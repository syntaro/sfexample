package fsexample.fsexample.midione.drivers.fluid;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

import fsexample.fsexample.common.Constant;
import fsexample.fsexample.common.MXMidiStatic;
import fsexample.fsexample.common.MXQueue;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneDevice;
import fsexample.fsexample.midione.v1.OneEventCounter;
import fsexample.fsexample.midione.v1.OneInput;
import fsexample.fsexample.midione.v1.OneMessage;
import fsexample.fsexample.midione.v1.OneOutput;

public class OneDeviceSoundFont extends OneDevice {
    public OneDeviceSoundFont(@NonNull OneDriverSoundFont service) {
        super(service, "<SoundFont>", "sfz");
    }

    MXQueue<OneMessage> _queue = new MXQueue<>();
    boolean useThread = false;
    FontFileManager _manager = FontFileManager.getInstance();
    public void prepareOutput() {
        if (!_manager.isFluidUsable()) {
            return;
        }

        if (_manager.isFluidReadyForPlay()) {
            OneOutput out = getOutput(0);
            getEventCounter().countIt(OneEventCounter.EVENT_CONNECTED);
            MidiOne.getInstance().fireOnDeviceConnectionChanged(this);
            MidiOne.getInstance().fireOnOutputOpened(out);
            return;
        }
        else {
            new Thread(()->{
                Context context = FluidSetting.getInstance().getAppContext();
                if (_manager.loadFontFile(context,  _manager.getDefault(), false)) {
                    OneOutput out = getOutput(0);
                    getEventCounter().countIt(OneEventCounter.EVENT_CONNECTED);
                    MidiOne.getInstance().fireOnDeviceConnectionChanged(this);
                    MidiOne.getInstance().fireOnOutputOpened(out);
                }
                else {
                    MidiOne.getInstance().cancelIfPrimalOut(getOutput(0));
                }
            }).start();
        }
    }


    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    public void startAccessDevice() {
        prepareOutput();
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
        return 0;
    }

    @Override
    public OneOutput allocateOutput(int track) {
        OneOutput out = new OneOutput(this, track) {
            @Override
            public boolean dispatchOne(OneMessage one) {
                if (one != null && _manager.isFluidReadyForPlay()) {
                    if (useThread) {
                        launchInfinity();
                        _queue.push(one);
                    } else {
                        if (one.isBinaryMessage()) {
                            _manager.sendLongMessage(one.getBinary());
                        } else {
                            int status = one.getStatus();
                            int command = status & 0xf0;
                            int data1 = one.getData1();
                            int data2 = one.getData2();

                            if (command >= 0x80 && command <= 0xe0) {
                                if (command == MXMidiStatic.COMMAND_CH_CONTROLCHANGE) {
                                    int x = -1;
                                    if (data1 == MXMidiStatic.DATA1_CC_SOUND_RESONANCE) {
                                        x = 22;
                                    }
                                    if (data1 == MXMidiStatic.DATA1_CC_SOUND_BLIGHTNESS) {
                                        x = 21;
                                    }
                                    if (data1 == MXMidiStatic.DATA1_CC_SOUND_ATTACKTIME) {
                                        x = 11;
                                    }
                                    if (data1 == MXMidiStatic.DATA1_CC_SOUND_RELEASETIME) {
                                        x = 15;
                                    }
                                    if (x >= 0) {
                                        int channel = status & 0x0f;
                                        int cc = MXMidiStatic.COMMAND_CH_CONTROLCHANGE + channel;
                                        _manager.sendShortMessage(cc, MXMidiStatic.DATA1_CC_NRPN_MSB, 127);
                                        _manager.sendShortMessage(cc, MXMidiStatic.DATA1_CC_NRPN_LSB, x);
                                        _manager.sendShortMessage(cc, MXMidiStatic.DATA1_CC_DATAENTRY, data2);
                                        _manager.sendShortMessage(cc, MXMidiStatic.DATA1_CC_DATAENTRY2, data2);
                                        //Log.e(TAG, "@CC " + data1 +"  will replase to NPRN " + x + " -> " + data2);
                                        return true;
                                    }
                                }
                                _manager.sendShortMessage(status, data1, data2);
                            }
                        }
                    }
                    return true;
                }
                return false;
            }

            @Override
            public int getNameRes() {
                return 0;
            }

            @Override
            public String getNameText() {
                FluidSetting params = FluidSetting.getInstance();
                if (params._soundFont.getValue() == null || params._soundFont.getValue().isEmpty()) {
                    return _name + "(" + FluidSetting.defaultFontName + ")";
                } else {
                    return _name + "(" + params._soundFont + ")";
                }
            }

            @Override
            public void onClose() {
            }
        };
        return out;
    }

    @Override
    public OneInput allocateInput(int track) {
        return null;
    }

    Thread _infinity;

    public void launchInfinity() {
        if (_infinity != null) {
            return;
        }
        Thread t = new Thread() {
            public void run() {
                _infinity = Thread.currentThread();
                try {
                    while (true) {
                        OneMessage one = _queue.pop();
                        if (one != null) {
                            if (one.isBinaryMessage()) {
                                _manager.sendLongMessage(one.getBinary());

                                if (one.isReset()) {
                                    //retuneAgain();
                                }
                            } else {
                                int status = one.getStatus();
                                int data1 = one.getData1();
                                int data2 = one.getData2();
                                if (status >= 0x80 && status <= 0xef) {
                                    int command = status & 0xf0;
                                    int channel = status & 0x0f;

                                    _manager.sendShortMessage(status, data1, data2);
                                }
                            }
                        } else {
                            break;
                        }
                    }
                } catch (Throwable ex) {
                    Log.e(Constant.TAG, "PostMessageError", ex);
                } finally {
                    _infinity = null;
                }
            }
        };
        t.start();
        try {
            while (_infinity == null) {
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {

        }
    }
}
