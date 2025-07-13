package fsexample.fsexample.midione.drivers.fluid;

import java.util.ArrayList;
import java.util.function.ObjIntConsumer;

import fsexample.fsexample.common.MXMidiStatic;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneMessage;
import fsexample.fsexample.userchoice.UserChoiceElement;

public class FontFileEntry implements UserChoiceElement {
    String _name;
    String _file;
    FontFileEntry(String name) {
        _name = name;
        _file = null;
    }

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
    public String getSubLabelText() { return ""; }

    public ArrayList<ProgramEntry> listPrograms() {
        return _listPrograms;
    }

    ArrayList<ProgramEntry> _listPrograms;

}
