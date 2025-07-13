package fsexample.fsexample.midione.drivers.none;

import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneDevice;
import fsexample.fsexample.midione.v1.OneEventCounter;
import fsexample.fsexample.midione.v1.OneInput;
import fsexample.fsexample.midione.v1.OneMessage;
import fsexample.fsexample.midione.v1.OneOutput;

public class OneDeviceNone extends OneDevice {
    public OneDeviceNone(OneDriverNone driver) {
        super(driver, "-None-", "-None-");
    }

    @Override
    public int getSortOrder() {
        return 0;
    }

    @Override
    public void startAccessDevice() {
        getEventCounter().countIt(OneEventCounter.EVENT_CONNECTED);
        MidiOne.getInstance().fireOnDeviceConnectionChanged(this);
    }

    @Override
    protected void onDispose() {

    }

    @Override
    public int countOutput() {
        return 1;
    }

    @Override
    public int countInput() {
        return 1;
    }

    @Override
    public OneOutput allocateOutput(int track) {
        OneOutput out = new OneOutput(this, track) {
            @Override
            public boolean dispatchOne(OneMessage one) {
                return true;
            }

            @Override
            public int getNameRes() {
                return 0;
            }

            @Override
            public String getNameText() {
                return "<None>";
            }

            @Override
            public void onClose() {
            }
        };
        return out;
    }

    @Override
    public OneInput allocateInput(int track) {
        return new OneInputNone(this);
    }
}
