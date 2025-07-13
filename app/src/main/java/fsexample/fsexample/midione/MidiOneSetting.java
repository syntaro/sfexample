package fsexample.fsexample.midione;

import fsexample.fsexample.midione.v1.OneConnectionListener;
import fsexample.fsexample.midione.v1.OneDevice;
import fsexample.fsexample.midione.v1.OneInput;
import fsexample.fsexample.midione.v1.OneOutput;
import fsexample.fsexample.namedvalue.NamedValueList;
import fsexample.fsexample.watchable.MXWatchableGroup;
import fsexample.fsexample.watchable.generic.MXWatchableBooleanYesNo;
import fsexample.fsexample.watchable.generic.MXWatchableString;
import fsexample.fsexample.R;

import java.util.List;

public class MidiOneSetting extends MXWatchableGroup {
    static MidiOneSetting _instance = new MidiOneSetting();

    public static MidiOneSetting getInstance() {
        return _instance;
    }

    public MidiOneSetting() {
        super(fsexample.fsexample.R.string.title_midi);
        MidiOne.getInstance().addConnectionListener(new OneConnectionListener() {
            @Override
            public void onDeviceAdded(OneDevice device) {
                updateList();
            }

            @Override
            public void onDeviceChanged(OneDevice device) {

            }

            @Override
            public void onDeviceLost(OneDevice device) {
                updateList();
            }

            @Override
            public void onInputOpened(OneInput input) {

            }

            @Override
            public void onOutputOpened(OneOutput input) {

            }

            @Override
            public void onInputClosed(OneInput input) {

            }

            @Override
            public void onOutputClosed(OneOutput input) {

            }
        });
        _output.getListenerList().addListner((paramSet, param) -> {
            String value = (String)param.getValue();
            int prefixZone = value.indexOf(':');
            if (prefixZone >= 0) {
                String prefix = value.substring(0, prefixZone);
                String name = value.substring(prefixZone + 1);
                MidiOne.getInstance().launchOutput(prefix, name, 0);
            }
        });
        _input.getListenerList().addListner((paramSet, param) -> {
            String value = (String)param.getValue();
            int prefixZone = value.indexOf(':');
            if (prefixZone >= 0) {
                String prefix = value.substring(0, prefixZone);
                String name = value.substring(prefixZone + 1);
                MidiOne.getInstance().launchInput(prefix, name, 0);
            }
        });
        _thru.getListenerList().addListner((paramSet, param) -> {
            MidiOne.getInstance().setMidiThru((boolean)param.getValue());
        });
        install(_output);
        install(_input);
        install(_thru);
        updateList();
    }

    NamedValueList<String> _inputTable = new NamedValueList<>();
    NamedValueList<String> _outputTable = new NamedValueList<>();

    public final MXWatchableString _input = new MXWatchableString(R.string.config_midi_input);
    public final MXWatchableString _output = new MXWatchableString(R.string.config_midi_output);
    public final MXWatchableBooleanYesNo _thru = new MXWatchableBooleanYesNo(R.string.config_midi_thru);

    protected synchronized void updateList() {
        List<OneDevice> listDevice = MidiOne.getInstance().listAllDevices();
        _inputTable = new NamedValueList<>();
        _outputTable = new NamedValueList<>();
        for (OneDevice seek : listDevice) {
            if (seek.countInput() > 0) {
                _inputTable.addNameAndValue(seek._name, seek._driver.getPrefix() +":" + seek._name);
            }
            if (seek.countOutput() > 0) {
                _outputTable.addNameAndValue(seek._name, seek._driver.getPrefix() +":" + seek._name);
            }
        }
        _input.setNameTable(_inputTable);
        _output.setNameTable(_outputTable);
    }
}
