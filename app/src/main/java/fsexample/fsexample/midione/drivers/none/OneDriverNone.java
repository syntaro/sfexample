package fsexample.fsexample.midione.drivers.none;

import android.content.Context;

import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneDriver;

public class OneDriverNone extends OneDriver {
    public OneDriverNone(MidiOne manager) {
        super("-", manager);
        OneDeviceNone device = new OneDeviceNone(this);
        addDevice(device);
    }

    @Override
    public void startFirstContact(Context context) {
    }

    @Override
    public void startDeepScan(Context context) {

    }

    @Override
    public void stopDeepScan(Context context) {

    }
}
