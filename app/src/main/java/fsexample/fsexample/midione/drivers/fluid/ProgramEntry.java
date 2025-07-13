package fsexample.fsexample.midione.drivers.fluid;

import fsexample.fsexample.common.MXMidiStatic;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneMessage;
import fsexample.fsexample.userchoice.UserChoiceElement;

public class ProgramEntry implements UserChoiceElement {
    public ProgramEntry(int prog, int bank, int bank32, String name) {
        _prog = prog;
        _bank = bank;
        _bank32 = bank32;
        _name = name;
    }

    public final int _prog;
    public final int _bank;
    public final int _bank32;
    public final String _name;

    @Override
    public int getNameRes() {
        return 0;
    }

    @Override
    public int getSubLabel() {
        return 0;
    }

    @Override
    public String getNameText() {
        return _name;
    }

    @Override
    public String getSubLabelText() {
        return "" + _prog + ":" +Integer.toHexString(_bank)+ "h";
    }

    public void send(int ch) {
        OneMessage bank = OneMessage.thisCodes(0, MXMidiStatic.COMMAND_CH_CONTROLCHANGE + ch, MXMidiStatic.DATA1_CC_BANKSELECT, _bank);
        MidiOne.getInstance().dispatchOne(bank);
        if (_bank32 >= 0) {
            OneMessage bank32 = OneMessage.thisCodes(0, MXMidiStatic.COMMAND_CH_CONTROLCHANGE + ch, MXMidiStatic.DATA1_CC_BANKSELECT, _bank32);
            MidiOne.getInstance().dispatchOne(bank32);
        }
        OneMessage prog = OneMessage.thisCodes(0, MXMidiStatic.COMMAND_CH_PROGRAMCHANGE + ch, _prog, 0);
        MidiOne.getInstance().dispatchOne(prog);
    }
}
