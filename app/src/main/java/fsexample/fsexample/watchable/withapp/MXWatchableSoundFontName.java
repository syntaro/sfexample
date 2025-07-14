package fsexample.fsexample.watchable.withapp;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import fsexample.fsexample.midione.drivers.fluid.FluidSetting;
import fsexample.fsexample.midione.drivers.fluid.FontFileEntry;
import fsexample.fsexample.midione.drivers.fluid.FontFileManager;
import fsexample.fsexample.userchoice.UserChoiceElement;
import fsexample.fsexample.userchoice.UserChoiceView;
import fsexample.fsexample.watchable.MXWatchable;

public class MXWatchableSoundFontName extends MXWatchable<String> {
    public MXWatchableSoundFontName(int name) {
        super(name);
    }

    public String toString() {
        return _value;
    }
    UserChoiceView _view;

    public void doUIAfterCommitState() {
        FluidSetting.getInstance().loadFont(false);
    }

    public View createCustomEditor(Context context) {
        _view = new UserChoiceView(context);
        ArrayList<UserChoiceElement> data = new ArrayList<>();
        FontFileManager manager = FontFileManager.getInstance();
        UserChoiceElement sel = null;
        for (FontFileEntry e : manager.getList()) {
            data.add(e);
            if (e.getNameText().equals(getValue())) {
                sel = e;
            }
        }
        _view.setData(data);
        if (sel != null) {
            _view.getAdapter().setUserChoiceResult(sel);
        }
        _view.getAdapter().setOnUserChoiceListener((adapter, clicked) -> {
            setValue(clicked.getNameText());
        });
        return _view;
    }
}
