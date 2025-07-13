package fsexample.fsexample.midione.v1;

import fsexample.fsexample.userchoice.UserChoiceElement;

public abstract class OneOutput implements OneDispatcher, UserChoiceElement {
    boolean _transmitterRunning;
    OneDevice _device;
    int _track;

    public OneOutput(OneDevice device, int track) {
        _device = device;
        _track = track;
        _transmitterRunning = true;
    }

    @Override
    public abstract boolean dispatchOne(OneMessage one);

    @Override
    public abstract int getNameRes();

    @Override
    public abstract String getNameText();

    public String getDeviceAddress() {
        return null;
    }

    @Override
    public int getSubLabel() {
        return 0;
    }

    @Override
    public String getSubLabelText() {
        OneEventCounter counter = _device.getEventCounter();
        return counter.createSubLabelText();
    }

    public boolean isTransmitterRunning() {
        return _transmitterRunning;
    }

    public void startTransmittere() {
        _transmitterRunning = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public void stopTransmitterAndThread() {
        _transmitterRunning = false;
        synchronized (this) {
            notifyAll();
        }
    }
    public abstract void onClose();

    public OneDevice getDevice() {
        return _device;
    }

    public int getTrack() { return _track; }
}
