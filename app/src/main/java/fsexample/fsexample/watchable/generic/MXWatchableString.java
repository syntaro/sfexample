package fsexample.fsexample.watchable.generic;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import fsexample.fsexample.watchable.MXWatchable;

public class MXWatchableString extends MXWatchable<String> {
    public MXWatchableString(int name) {
        super(name);
    }
    @Override
    public View createCustomEditor(Context context) {
        View intherits = super.createCustomEditor(context);
        if (intherits == null) {
            EditText view = new EditText(context);
            view.setOnEditorActionListener((textView, i, keyEvent) -> {
                setValue(view.getText().toString());
                return true;
            });
            view.setText(_value != null ? _value : "");
            return view;
        }
        return intherits;
    }
}

