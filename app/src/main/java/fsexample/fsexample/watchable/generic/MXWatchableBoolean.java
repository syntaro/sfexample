package fsexample.fsexample.watchable.generic;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.slider.Slider;

import fsexample.fsexample.namedvalue.NamedValueList;
import fsexample.fsexample.watchable.MXWatchable;


public class MXWatchableBoolean extends MXWatchable<Boolean> {
    public MXWatchableBoolean(int name) {
        super(name);
    }

    @Override
    protected NamedValueList<Boolean> createNameTable() {
        NamedValueList<Boolean> table = new NamedValueList<>();
        table.addNameAndValue("False", false);
        table.addNameAndValue("True", true);
        return table;
    }

    @Override
    public View createCustomEditor(Context context) {
        View inherits = super.createCustomEditor(context);
        if (inherits == null) {
            SwitchCompat view = new SwitchCompat(context);
            view.setChecked(_value == null ? false : _value.booleanValue());
            view.setOnCheckedChangeListener((compoundButton, b) -> {
                setValue(b);
            });
            return view;
        }
        return inherits;
    }
}
