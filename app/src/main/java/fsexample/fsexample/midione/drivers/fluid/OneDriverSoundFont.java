package fsexample.fsexample.midione.drivers.fluid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import fsexample.fsexample.common.Constant;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneDispatcher;
import fsexample.fsexample.midione.v1.OneDriver;

/**
 * Bluetooth（セントラル用）のMidiNetService
 * 複数デバイスに対応
 */
public class OneDriverSoundFont extends OneDriver {
    @Override
    public void startDeepScan(Context context) {
    }

    @Override
    public void stopDeepScan(Context context) {

    }

    boolean _usable = true;

    @SuppressLint("NewApi")
    public OneDriverSoundFont(Context context, MidiOne manager) {
        super("SFZ", manager);
    }

    OneDeviceSoundFont _sfz;

    boolean _alreadyEnum = false;

    @Override
    public void startFirstContact(Context context) {
        if (_alreadyEnum) {
            return;
        }
        if (!_usable) {
            return;
        }
        try {
            _alreadyEnum = true;
            if (_sfz == null) {
                _sfz = new OneDeviceSoundFont(this);
                addDevice(_sfz);
            }
        } catch (Throwable ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
            _alreadyEnum = false;
        }
    }

    @Override
    public void terminateAllDevices() {
        super.terminateAllDevices();
    }

    OneDispatcher _onRead = null;
}
