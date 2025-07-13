package fsexample.fsexample.midione.drivers.android;

import android.content.Context;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiManager;
import android.os.Build;

import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneDevice;
import fsexample.fsexample.midione.v1.OneDriver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 普通のAndroidMIDIAPIを用いる
 * 複数デバイス・アプリサービスに対応MidiNetService
 */
public class OneDriverDefault extends OneDriver {
    MidiManager _midiMan;
    MidiManager.DeviceCallback _callback;

    public OneDriverDefault(Context context, MidiOne manager) {
        super("And", manager);
        _midiMan = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
    }

    boolean _alreadyEnum = false;

    @Override
    public void startFirstContact(Context context) {
        if (_alreadyEnum) {
            return;
        }
        _alreadyEnum = true;
        Set<MidiDeviceInfo> infoList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            infoList = _midiMan.getDevicesForTransport(MidiManager.TRANSPORT_MIDI_BYTE_STREAM);
        } else {
            infoList = new HashSet<>();
            Collections.addAll(infoList, _midiMan.getDevices());
        }

        for (MidiDeviceInfo device : infoList) {
            String name = device.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
            if (name == null) {
                name = "?";
            }
            OneDevice info = findDeviceInfoByUUID(name);
            if (info == null) {
                info = new OneDeviceDefault(this, name, name, device);
                addDevice(info);
            }
        }
        _callback = new MidiManager.DeviceCallback() {
            @Override
            public void onDeviceAdded(MidiDeviceInfo device) {
                String name = device.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
                if (name == null) {
                    name = "?";
                }
                for (int i = 0; i < countDevices(); ++i) {
                    OneDevice dev = getDevice(i);
                    if (dev.getName().equals(name)) {
                        return;
                    }
                }
                OneDevice info = new OneDeviceDefault(OneDriverDefault.this, name, name, device);
                addDevice(info);
            }

            @Override
            public void onDeviceRemoved(MidiDeviceInfo info) {
                String name = info.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
                if (name != null) {
                    for (int i = 0; i < countDevices(); ++i) {
                        OneDevice dev = getDevice(i);
                        if (dev.getName().equals(name)) {
                            removeDevice(dev);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onDeviceStatusChanged(MidiDeviceStatus status) {
                super.onDeviceStatusChanged(status);
            }
        };
        _midiMan.registerDeviceCallback(_callback, null);
    }

    public void startDeepScan(Context context) {

    }

    @Override
    public void terminateAllDevices() {
        super.terminateAllDevices();
        _midiMan.unregisterDeviceCallback(_callback);
    }

    @Override
    public void stopDeepScan(Context context) {
    }
}
