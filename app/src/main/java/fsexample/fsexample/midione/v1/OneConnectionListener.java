package fsexample.fsexample.midione.v1;

public interface OneConnectionListener {
    void onDeviceAdded(OneDevice device);

    void onDeviceChanged(OneDevice device);

    void onDeviceLost(OneDevice device);

    void onInputOpened(OneInput input);

    void onOutputOpened(OneOutput input);

    void onInputClosed(OneInput input);

    void onOutputClosed(OneOutput input);
}
