package fsexample.fsexample.midione.v1;

import java.util.LinkedList;

public class OneMessageFactory {
    LinkedList<OneMessage> _pool1 = new LinkedList<>();
    LinkedList<OneMessage> _pool2 = new LinkedList<>();
    LinkedList<OneMessage> _pool3 = new LinkedList<>();

    public synchronized OneMessage makeBuffer(int size) {
        /*
        LinkedList<OneMessage> pool = null;
        switch (size) {
            case 1:
                pool = _pool1;
                break;
            case 2:
                pool = _pool2;
                break;
            case 3:
                pool = _pool3;
                break;
        }
        if (pool == null || pool.isEmpty()) {*/
            return new OneMessage(0, size);
        /*}
        OneMessage one = _pool1.removeFirst();
        return one;*/
    }

    synchronized void returnBuffer(OneMessage one) {
        LinkedList<OneMessage> pool = null;
        int size = one._data.length;
        switch (size) {
            case 1:
                pool = _pool1;
                break;
            case 2:
                pool = _pool2;
                break;
            case 3:
                pool = _pool3;
                break;
        }
        if (pool == null || pool.isEmpty()) {
            return;
        }
        if (pool.size() < 20) {
            pool.add(one);
        }
    }

    public static int getSuggestedLength(int midiEvent) {
        switch (midiEvent & 0xf0) {
            case 0:
                return 0;
            case 0xf0: {
                switch (midiEvent) {
                    case 0xf0: //sysex
                    case 0xf7: //sysex special
                        return -1;

                    case 0xf1: //midi time code
                    case 0xf3: //song select
                        return 2;

                    case 0xf2: //song position
                        return 3;

                    case 0xf6: //tune request
                    case 0xf8: //timeing clock
                    case 0xfa: //start
                    case 0xfb: //continue
                    case 0xfc: //stop
                    case 0xfe: //active sencing
                        return 1;
                    case 0xff: //system reset
                        return 1;
                }
            }
            return 1;
            case 0x80:
            case 0x90:
            case 0xa0:
            case 0xb0:
            case 0xe0:
                return 3;

            case 0xc0: // program change
            case 0xd0: // channel after-touch
                return 2;
        }
        return 3;
    }
}
