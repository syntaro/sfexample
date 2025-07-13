package fsexample.fsexample.midione.v1;

import android.util.Log;

import fsexample.fsexample.common.Constant;
import fsexample.fsexample.midione.MidiOne;

import java.util.ArrayList;

public class OneEventCounter {
    OneDevice _device;

    public OneEventCounter(OneDevice device) {
        _device = device;

    }

    public static final int EVENT_CONNECTED = 1;
    public static final int EVENT_DISCONNECTED = 2;
    public static final int EVENT_ERR_CONNECT = 3;
    public static final int EVENT_ERR_TRANSFER = 4;
    public static final int EVENT_ERR_RECIEVE = 5;

    public void countIt(int event) {
        switch (event) {
            case EVENT_CONNECTED:
                if (MidiOne.isDebug) {
                    Log.e(Constant.TAG, "current connected " + _device.getName());
                }
                _currentConnected = true;
                ++_countConnected;
                break;
            case EVENT_DISCONNECTED:
                if (MidiOne.isDebug) {
                    Log.e(Constant.TAG, "current disconnected " + _device.getName());
                }
                _currentConnected = false;
                ++_countDisconnected;
                break;
            case EVENT_ERR_CONNECT:
                //Log.e(TAG, "connect error " + _device.getName());
                ++_errCountConnect;
                break;
            case EVENT_ERR_TRANSFER:
                //Log.e(TAG, "err transfer " + _device.getName());
                ++_errCountTransfer;
                break;
            case EVENT_ERR_RECIEVE:
                //Log.e(TAG, "err receive " + _device.getName());
                ++_errCountReceive;
                break;
        }
    }

    public String createSubLabelText() {
        StringBuilder builder = new StringBuilder();
        String alive = _currentConnected ? "On" : "Off";
        String addr = "";
        builder.append(alive);
        if (addr != null && addr.length() > 0) {
            builder.append(":");
            builder.append(addr);
        }
        ArrayList<String> errText = new ArrayList<>();
        if (_errCountConnect > 0) {
            errText.add("connect=" + _errCountConnect);
        }
        if (_errCountTransfer > 0) {
            errText.add("transfer=" + _errCountTransfer);
        }
        if (_errCountReceive > 0) {
            errText.add("receive=" + _errCountReceive);
        }
        if (errText.size() > 0) {
            builder.append("(error = ");
            builder.append(errText.toString());
            builder.append(")");
        }
        return builder.toString();
    }

    public boolean _currentConnected;
    public int _countConnected;
    public int _countDisconnected;
    public int _errCountConnect;
    public int _errCountTransfer;
    public int _errCountReceive;
}
