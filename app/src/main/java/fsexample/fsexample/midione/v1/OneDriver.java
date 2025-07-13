package fsexample.fsexample.midione.v1;

import android.content.Context;

import fsexample.fsexample.midione.MidiOne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 複数あるドライバの基底クラス
 * この１つ下に、デバイス一覧がつながっている
 * デバイスの下のポートは、IN1OUT1としてあつかう、2つ以上あっても認識しない
 * MIDIなど、開く前からわかるもののみ、INとOUTの有無をチェックしていて、
 * 開かないとわからないものはチェックしない
 */
public abstract class OneDriver {
    protected String _prefix;
    public final MidiOne _one;

    public String getPrefix() {
        return _prefix;
    }

    protected List<OneDevice> _listDevices = Collections.synchronizedList(new ArrayList<>());

    protected OneDriver(String prefix, MidiOne manager) {
        _one = manager;
        _prefix = prefix;
    }

    public int indexOfDevice(OneDevice device) {
        for (int i = 0; i < _listDevices.size(); ++i) {
            if (_listDevices.get(i) == device) {
                return i;
            }
        }
        return -1;
    }

    public OneDevice findDeviceInfoByUUID(String uuid) {
        synchronized (_listDevices) {
            for (OneDevice seek : _listDevices) {
                if (seek._uuid.equals(uuid)) {
                    return seek;
                }
            }
        }
        return null;
    }

    public OneDevice getDevice(int x) {
        return _listDevices.get(x);
    }

    public void addDevice(OneDevice info) {
        if (_listDevices.contains(info)) {
            return;
        }
        _listDevices.add(info);
        _one.fireOnDeviceAdded(info);
    }

    public void removeDevice(OneDevice info) {
        if (_listDevices.contains(info)) {
            _listDevices.remove(info);
            _one.fireOnDeviceLost(info);
        }
    }

    public int countDevices() {
        return _listDevices.size();
    }

    protected void terminateAllDevices() {
        List<OneDevice> copy;
        synchronized (_listDevices) {
            copy = new ArrayList<>(_listDevices);
        }
        for (OneDevice info : copy) {
            info.closeDevice();
        }
    }

    public abstract void startFirstContact(Context context);

    public abstract void startDeepScan(Context context);

    public abstract void stopDeepScan(Context context);
}
