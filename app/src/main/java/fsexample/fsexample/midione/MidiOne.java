package fsexample.fsexample.midione;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import fsexample.fsexample.common.Constant;
import fsexample.fsexample.midione.drivers.android.OneDriverDefault;
import fsexample.fsexample.midione.drivers.none.OneDriverNone;
import fsexample.fsexample.midione.drivers.fluid.OneDriverSoundFont;
import fsexample.fsexample.midione.v1.OneConnectionListener;
import fsexample.fsexample.midione.v1.OneDevice;
import fsexample.fsexample.midione.v1.OneDispatcher;
import fsexample.fsexample.midione.v1.OneDriver;
import fsexample.fsexample.midione.v1.OneInput;
import fsexample.fsexample.midione.v1.OneMessage;
import fsexample.fsexample.midione.v1.OneOutput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * このパッケージを司るマネージャー
 */

public  class MidiOne  implements OneDispatcher {
    public static final boolean isDebug = false;
    public Context _applicationContext;

    public Context getApplicationContext() {
        return _applicationContext;
    }

    public static void Thread_sleep(long time) {
        synchronized (MidiOne.class) {
            try {
                MidiOne.class.wait(time);
            } catch (Throwable ex) {

            }
        }
    }

    static MidiOne _instance = null;

    public static MidiOne getInstance() {
        if (_instance == null) {
            _instance = new MidiOne();
        }
        return _instance;
    }

    OneDriverNone _driverNone;
    OneDriverSoundFont _driverSfz;
    OneDriverDefault _driverAndroid;
    BluetoothAdapter _adapter;

    public BluetoothAdapter getBLEAdapter() {
        return _adapter;
    }

    public static ParcelUuid MIDI_SERVICE = ParcelUuid.fromString("03B80E5A-EDE8-4B33-A751-6CE34EC4C700");

    public OneDriverDefault getDriverAndroid() {
        return _driverAndroid;
    }

    public OneDriverSoundFont getDriverSFZ() {
        return _driverSfz;
    }

    public OneDriverNone getDriverNone() {
        return _driverNone;
    }

    OneInput _smfInput = null;


    boolean _initDone = false;

    MidiOne() {
    }

    public void init(Context context) {
        if (_initDone) {
            return;
        }
        _applicationContext = context;
        _initDone = true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //APIレベル22以前の機種の場合の処理
            BluetoothManager bluetoothManager = context.getSystemService(BluetoothManager.class);
            _adapter = bluetoothManager.getAdapter();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //APIレベル23以降の機種の場合の処理
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            _adapter = bluetoothManager.getAdapter();
        }


        try {
            _driverSfz = new OneDriverSoundFont(context, this);
            addDriver(_driverSfz);
        } catch (Throwable ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }

        try {
            _driverNone = new OneDriverNone(this);
            addDriver(_driverNone);
        } catch (Throwable ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }

        try {
            _driverAndroid = new OneDriverDefault(context, this);
            addDriver(_driverAndroid);
        } catch (Throwable ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }
    }

    public ArrayList<OneDevice> listAllDevices() {
        ArrayList<OneDevice> result = new ArrayList<>();

        for (OneDriver seek : _installedDrivers) {
            ArrayList<OneDevice> segment = new ArrayList<>();
            int cnt = seek.countDevices();
            for (int x = 0; x < cnt; ++x) {
                segment.add(seek.getDevice(x));
            }
            segment.sort(new Comparator<OneDevice>() {
                @Override
                public int compare(OneDevice o1, OneDevice o2) {
                    int s1 = o1.getSortOrder();
                    int s2 = o2.getSortOrder();
                    if (s1 == s2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return (s1 < s2) ? -1 : 1;
                }
            });
            result.addAll(segment);
        }
        return result;
    }

    public final List<OneDriver> _installedDrivers = new ArrayList<>();

    public void addDriver(OneDriver service) {
        synchronized (_installedDrivers) {
            if (_installedDrivers.contains(service)) {
                return;
            }
            _installedDrivers.add(service);
        }
    }

    public void removeDriver(OneDriver service) {
        synchronized (_installedDrivers) {
            if (_installedDrivers.contains(service)) {
                _installedDrivers.remove(service);
            }
        }
    }

    static boolean _useInfo = true;

    public void stopAllScan() {
        /*
        Log.e(TAG, "stopAllScan");
        synchronized (_installedServices) {
            for (MihMidiDriver driver : _installedServices) {
                Log.e(TAG, "stopAllScan1 - " + driver._prefix);
                //driver.stopDeepScan(_context);
            }
        }
        Log.e(TAG, "stopAllScan - 2");
         */
    }

    public void postEnumerateDevicesForAll(Context viewContext) {
        ArrayList<OneDriver> copy;
        synchronized (_installedDrivers) {
            copy = new ArrayList<>(_installedDrivers);
        }
        synchronized (copy) {
            for (OneDriver seek : copy) {
                try {
                    seek.startFirstContact(viewContext);
                } catch (Throwable ex) {
                    Log.e(Constant.TAG, ex.getMessage(), ex);
                }
            }
        }
    }

    public ArrayList<OneInput> enumInput() {
        ArrayList<OneInput> result = new ArrayList<>();
        ArrayList<OneDevice> list = listAllDevices();
        for (OneDevice seek : list) {
            int count = seek.countInput();
            for (int i = 0; i < count; ++i) {
                result.add(seek.getInput(i));
            }
        }
        return result;
    }

    public ArrayList<OneOutput> enumOutput() {
        ArrayList<OneOutput> result = new ArrayList<>();
        ArrayList<OneDevice> list = listAllDevices();
        for (OneDevice seek : list) {
            int count = seek.countOutput();
            for (int i = 0; i < count; ++i) {
                result.add(seek.getOutput(i));
            }
        }
        return result;
    }

    LinkedList<OneInput> _reservedIn = new LinkedList<>();
    LinkedList<OneOutput> _reservedOut = new LinkedList<>();

    private OneOutput _primalOut = null;
    private OneInput _primalIn = null;

    public OneOutput getPrimalOut() {
        if (_primalOut == null) {
            return null;
        }

        return _primalOut;
    }

    public OneInput getPrimalIn() {
        if (_primalIn == null) {
            return null;
        }

        return _primalIn;
    }

    public synchronized void reservePrimalIn(OneInput in) {
        _reservedIn.remove(in);
        _reservedIn.add(in);
    }

    public synchronized void reservePrimalOut(OneOutput out) {
        _reservedOut.remove(out);
        _reservedOut.add(out);
    }

    public synchronized void cancelIfPrimalIn(OneInput in) {
        _reservedIn.remove(in);
    }

    public synchronized void cancelIfPrimalOut(OneOutput out) {
        _reservedOut.remove(out);
    }

    public synchronized void clearPrimalOut() {
        _reservedOut.clear();
        _primalOut = null;
    }

    public synchronized void clearPrimalIn() {
        _reservedIn.clear();
        _primalIn = null;
    }

    public void fireOnDeviceAdded(OneDevice device) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "fireOnDeviceAdded " + device.getName());
        }
        ArrayList<OneConnectionListener> copy;
        synchronized (_listeners) {
            copy = new ArrayList<>(_listeners);
        }
        for (OneConnectionListener seek : copy) {
            seek.onDeviceAdded(device);
        }
    }

    public void fireOnDeviceConnectionChanged(OneDevice device) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "fireOnDeviceConnectionChanged " + device.getName() + " -> " + device.getEventCounter()._currentConnected);
        }
        ArrayList<OneConnectionListener> copy;
        synchronized (_listeners) {
            copy = new ArrayList<>(_listeners);
        }
        if (device.getEventCounter()._currentConnected == false) {
            if (_primalIn != null && _primalIn.getDevice() == device) {
                clearPrimalIn();
            }
            if (_primalOut != null && _primalOut.getDevice() == device) {
                clearPrimalOut();
            }
        }
        for (OneConnectionListener seek : copy) {
            seek.onDeviceChanged(device);
        }
    }

    public void fireOnDeviceLost(OneDevice device) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "fireOnDeviceLost " + device.getName());
        }
        ArrayList<OneConnectionListener> copy;
        synchronized (_listeners) {
            copy = new ArrayList<>(_listeners);
        }
        if (device.getEventCounter()._currentConnected == false) {
            if (_primalIn.getDevice() == device) {
                clearPrimalIn();
            }
            if (_primalOut.getDevice() == device) {
                clearPrimalOut();
            }
        }
        for (OneConnectionListener seek : copy) {
            seek.onDeviceLost(device);
        }
    }
    public void fireOnInputOpened(OneInput in) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "fireOnInputOpened " + in.getNameText());
        }
        int index = _reservedIn.lastIndexOf(in);
        if (index < 0) {
            return;
        }
        for (int i = 0; i <= index; ++i) {
            _reservedIn.removeFirst();
        }
        _primalIn = in;
        MidiOneSetting.getInstance()._input.setValue(in.getDevice().getDriver().getPrefix() + ":" + in.getDevice().getName());

        ArrayList<OneConnectionListener> copy;
        synchronized (_listeners) {
            copy = new ArrayList<>(_listeners);
        }
        for (OneConnectionListener seek : copy) {
            seek.onInputOpened(in);
        }
    }

    public void fireOnOutputOpened(OneOutput out) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "fireOnOutputOpened " + out.getNameText());
        }
        int index = _reservedOut.lastIndexOf(out);
        if (index < 0) {
            return;
        }
        for (int i = 0; i <= index; ++i) {
            _reservedOut.removeFirst();
        }
        _primalOut = out;
        MidiOneSetting.getInstance()._output.setValue(out.getDevice().getDriver().getPrefix() + ":" + out.getDevice().getName());
        ArrayList<OneConnectionListener> copy;
        synchronized (_listeners) {
            copy = new ArrayList<>(_listeners);
        }
        for (OneConnectionListener seek : copy) {
            seek.onOutputOpened(out);
        }
    }

    public void fireOnInputClosed(OneInput input) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "fireOnInputClosed " + input.getNameText());
        }
        ArrayList<OneConnectionListener> copy;
        synchronized (_listeners) {
            copy = new ArrayList<>(_listeners);
        }
        for (OneConnectionListener seek : copy) {
            seek.onInputClosed(input);
        }
    }

    public void fireOnOutputClosed(OneOutput output) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "fireOnOutputClosed " + output.getNameText());
        }
        ArrayList<OneConnectionListener> copy;
        synchronized (_listeners) {
            copy = new ArrayList<>(_listeners);
        }
        for (OneConnectionListener seek : copy) {
            seek.onOutputClosed(output);
        }
    }

    public void launchKnownConnection(Context viewContext) {
        if (_initialOut != null && _primalOut == null) {
            launchOutput(_initialOutDriver, _initialOut, _initialOutTrack);
        }
        if (_initialIn != null && _primalIn== null) {
            launchInput(_initialInDriver, _initialIn, _initialInTrack);
        }
    }

    public void launchOutput(OneOutput clicked) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "launchOutput " + clicked.getNameText());
        }
        reservePrimalOut(clicked);
        clicked.getDevice().startAccessDevice();
        if (clicked.getDevice().getEventCounter()._currentConnected) {
            fireOnOutputOpened(clicked);
        }
    }

    public void launchInput(OneInput clicked) {
        if (MidiOne.isDebug) {
            Log.e(Constant.TAG, "launchInput " + clicked.getNameText());
        }
        reservePrimalIn(clicked);
        clicked.getDevice().startAccessDevice();
        if (clicked.getDevice().getEventCounter()._currentConnected) {
            fireOnInputOpened(clicked);
        }
    }

    public int indexOfReserveList(OneInput input) {
        return _reservedIn.indexOf(input);
    }

    public int indexOfReserveList(OneOutput output) {
        return _reservedOut.indexOf(output);
    }

    LinkedList<OneConnectionListener> _listeners = new LinkedList<>();

    public synchronized void addConnectionListener(OneConnectionListener listener) {
        if (_listeners.contains(listener)) {
            return;
        }
        _listeners.add(listener);
    }

    public synchronized void removeConnectionListener(OneConnectionListener listener) {
        _listeners.remove(listener);
    }

    public void resetConnection() {
        _primalOut = null;
        _primalIn = null;
    }

    public boolean useMidiThru() {
        return _midiThru;
    }

    public void setMidiThru(boolean thru) {
        _midiThru = thru;
    }

    boolean _midiThru = true;
    String _initialOut = null;
    String _initialIn = null;
    String _initialOutDriver = null;
    String _initialInDriver = null;
    int _initialInTrack = 0;
    int _initialOutTrack = 0;

    public void launchOutput(String driver, String name, int track) {
        //Log.e("-", "output d[" +  driver + "] name[" + name + "]");
        for (OneDriver seek : _installedDrivers) {
            //Log.e(TAG, "seek d[" + seek.getPrefix() + "]");
            if (seek.getPrefix().equals(driver)) {
                for (OneDevice device : seek._one.listAllDevices()) {
                    //Log.e(TAG, "seek name[" + device.getName() +"]");
                    if (device.getName().equals(name)) {
                        if (track < device.countOutput()) {
                            launchOutput(device.getOutput(track));
                            return;
                        }
                    }
                }
            }
        }
    }

    public void launchInput(String driver, String name, int track) {
        for (OneDriver seek : _installedDrivers) {
            if (seek.getPrefix().equals(driver)) {
                for (OneDevice device : seek._one.listAllDevices()) {
                    if (device.getName().equals(name)) {
                        if (track < device.countInput()) {
                            launchInput(device.getInput(track));
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public synchronized boolean dispatchOne(OneMessage one) {
        OneOutput out = getPrimalOut();
        if (out != null) {
            return out.dispatchOne(one);
        }
        return false;
    }
}
