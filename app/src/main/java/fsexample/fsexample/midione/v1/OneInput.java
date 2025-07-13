package fsexample.fsexample.midione.v1;

import android.util.Log;

import fsexample.fsexample.common.Constant;
import fsexample.fsexample.userchoice.UserChoiceElement;

public abstract class OneInput implements UserChoiceElement, OneDispatcher {
    protected OneDispatcher _onParsed;
    boolean _parserRunning;
    OneDevice _device;

    int _track;

    public OneInput(OneDevice device, int track) {
        _track = track;
        _device = device;
        _parserRunning = true;
    }

    public void bindOnParsed(OneDispatcher onRead) {
        _onParsed = onRead;
    }

    public boolean isParserRunning() {
        return _parserRunning;
    }

    public void startParser() {
        _parserRunning = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public void stopParserAndThread() {
        _parserRunning = false;

        synchronized (this) {
            notifyAll();
        }
    }

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

    int lengthOfMessage(byte[] data, int offset, int count, int pos) {
        if (pos >= count) {
            return -1;
        }
        int ch = data[offset + pos] & 0xff;
        if (ch == 0xff) {
            int type = data[offset + pos + 1] & 0xff;
            int bodylen = data[offset + pos + 2] & 0xff;
            int totallen = 3 + bodylen;
            return totallen;
        }
        if (ch == 0xf0) {
            for (int i = pos + 1; i < count; ++i) {
                int ch2 = data[offset + i] & 0xff;
                if (ch2 == 0xf7) {
                    return i + 1 - pos;
                }
            }
            return count - pos;
        }
        return OneMessageFactory.getSuggestedLength(ch);
    }

    @Override
    public boolean dispatchOne(OneMessage one) {
        if (one == null) {
            return  false;
        }
        if (_onParsed == null) {
            return false;
        }

        try {
            byte[] data = one._data;
            int offset = 0;
            int count = data.length;
            int start = 0;
            while (true) {
                int len = lengthOfMessage(data, offset, count, start);
                //Log.e(TAG, "length  " + len);
                if (len == 0) {
                    start++;
                    continue;
                }
                if (len < 0) {
                    break;
                }
                OneMessage seg = OneMessage.thisPart(one._tick, data, offset + start, len);
                if (seg.isMetaTempo()) {
                    _onParsed.dispatchOne(seg);
                }
                else if (seg.isMetaMessage()) {
                    //Log.e(TAG, "Meta " + seg +" -> ignore");
                }
                else {
                    _onParsed.dispatchOne(seg);
                }
                start += len;
            }
        } catch (Throwable ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public void onClose() {

    }

    public int getTrack() { return _track; }

    public OneDevice getDevice() {
        return _device;
    }

    public String toString() {
        return getNameText() + "/" + getSubLabelText();
    }
}
