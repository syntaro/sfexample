package fsexample.fsexample.midione.drivers.android;

import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.util.Log;

import androidx.annotation.NonNull;

import fsexample.fsexample.common.Constant;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneDevice;
import fsexample.fsexample.midione.v1.OneEventCounter;
import fsexample.fsexample.midione.v1.OneInput;
import fsexample.fsexample.midione.v1.OneMessage;
import fsexample.fsexample.midione.v1.OneOutput;

import java.io.IOException;

public class OneDeviceDefault extends OneDevice {
    MidiDeviceInfo _info;
    MidiDevice _connected;
    boolean _isVirtual;

    public OneDeviceDefault(@NonNull OneDriverDefault service, @NonNull String name, @NonNull String uuid, MidiDeviceInfo info) {
        super(service, name, uuid);
        _info = info;
        if (name.startsWith("FSExample")) {
            _isVirtual = true;
        }
        _connected = null;
    }

    MidiInputPort[] _listSytemOutput;
    MidiOutputPort[] _listSystemInput;

    @Override
    public int getSortOrder() {
        return 0;
    }

    boolean _initCalled = false;

    @Override
    public void startAccessDevice() {
        if (_initCalled) {
            return;
        }
        _initCalled = true;
        try {
            MidiManager manager = ((OneDriverDefault) _driver)._midiMan;
            manager.openDevice(_info, new MidiManager.OnDeviceOpenedListener() {
                @Override
                public void onDeviceOpened(MidiDevice device) {
                    try {
                        _connected = device;
                        _info = device.getInfo();
                        _listSystemInput = new MidiOutputPort[_info.getOutputPortCount()];
                        _listSytemOutput = new MidiInputPort[_info.getInputPortCount()];
                        getEventCounter().countIt(OneEventCounter.EVENT_CONNECTED);
                        for (int i = 0; i < _info.getInputPortCount(); ++i) {
                            OneOutput out = getOutput(i);
                            _listSytemOutput[i] = device.openInputPort(i);
                            MidiOne.getInstance().fireOnOutputOpened(out);
                        }
                        for (int i = 0; i < _info.getOutputPortCount(); ++i) {
                            OneInput in = getInput(i);
                            _listSystemInput[i] = device.openOutputPort(i);
                            if (_listSystemInput[i] != null) {
                                _listSystemInput[i].connect(new MidiReceiver() {
                                    @Override
                                    public void onSend(byte[] msg, int offset, int count, long timestamp) throws IOException {
                                        OneMessage one = OneMessage.thisPart(0, msg, offset, count);
                                        one._messageSource = in;
                                        in.dispatchOne(one);
                                    }
                                });
                                MidiOne.getInstance().fireOnInputOpened(in);
                            }
                        }
                        MidiOne.getInstance().fireOnDeviceConnectionChanged(OneDeviceDefault.this);
                    } catch (Throwable ex) {
                        Log.e(Constant.TAG, ex.getMessage(), ex);
                        _stat._transferBroken++;
                        _stat.notifyStat();
                    }
                }
            }, null);
        } catch (Throwable ex) {
            Log.e(Constant.TAG, ex.getMessage());
            _stat._openError++;
            _stat.notifyStat();
            //TODO see CancelFlag
            /*
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                }catch (Exception ex) {
                    initialize();
                }
            });
            */
        }
    }

    @Override
    protected void onDispose() {
        _listSystemInput = null;
        _listSytemOutput = null;
        try {
            if (_connected != null) {
                _connected.close();
                _connected = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countOutput() {
        return _info.getInputPortCount();
    }

    @Override
    public int countInput() {
        return _info.getOutputPortCount();
    }

    @Override
    public OneOutput allocateOutput(int track) {
        return new OneOutput(this, track) {
            @Override
            public boolean dispatchOne(OneMessage one) {
                try {
                    if (_listSytemOutput != null && _listSytemOutput[track] != null) {
                        _listSytemOutput[track].send(one._data, 0, one._data.length);
                    }
                    return true;
                } catch (IOException ex) {
                }
                return false;
            }

            @Override
            public int getNameRes() {
                return 0;
            }

            @Override
            public String getNameText() {
                if (countOutput() >= 2) {
                    return _name + "(" + _info.getPorts()[track].getName() + ")";
                } else {
                    return _name;
                }
            }

            @Override
            public void onClose() {

            }
        };
    }

    @Override
    public OneInput allocateInput(int track) {
        OneInput oneInput = new OneInput(this, track) {

            @Override
            public int getNameRes() {
                return 0;
            }

            @Override
            public String getNameText() {
                if (countInput() >= 2) {
                    return _name + "(" + _info.getPorts()[track].getName() + ")";
                } else {
                    return _name;
                }
            }
        };
        oneInput.bindOnParsed(one -> {
            if (oneInput == MidiOne.getInstance().getPrimalIn() && MidiOne.getInstance().useMidiThru()) {
                return MidiOne.getInstance().dispatchOne(one);
            }
            return false;
        });
        return oneInput;
    }
}
