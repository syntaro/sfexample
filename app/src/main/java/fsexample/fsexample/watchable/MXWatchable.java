package fsexample.fsexample.watchable;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import fsexample.fsexample.midione.v1.OneMessage;
import fsexample.fsexample.namedvalue.NamedValueList;
import fsexample.fsexample.userchoice.BasicUserChoiceElement;
import fsexample.fsexample.userchoice.UserChoiceElement;
import fsexample.fsexample.userchoice.UserChoiceView;

public class MXWatchable<T> implements UserChoiceElement {
    public static final OneMessage NOT_CHAINED_JUST_SETTINGUP = null;
    public static final OneMessage NOT_CHAINED = null;
    public static final OneMessage NOT_CHAINED_JUST_ROLLBACK = null;
    public static final OneMessage NOT_CHAINED_JUST_BYHAND = null;

    public MXWatchable(int nameRes) {
        this(nameRes, null);
    }

    public MXWatchable(String nameText){
        this(0, nameText);
    }

    public MXWatchable(int nameRes, String nameText) {
        _nameText = nameText;
        _nameRes = nameRes;
        _value = null;
        _nameTable = null;
        _listener =  new MXWatchableListenerList();
        _nameTable = createNameTable();
    }

    public NamedValueList<T> getNameTable(){
        return _nameTable;
    }
    public void setNameTable(NamedValueList<T> nameTable){
        _nameTable = nameTable;
    }
    protected NamedValueList<T> createNameTable() {
        return null;
    }
    protected MXWatchableListenerList _listener;
    protected int _nameRes;
    protected String _nameText;
    protected T _value;

    public T getValue() {
        return _value;
    }

    static byte[] empty = new byte[0];

    MXWatchableRequestHook _requetValueHook;

    public void setRequestValueHook(MXWatchableRequestHook requetValueHook) {
        _requetValueHook = requetValueHook;
    }

    public void requestValueByUI(T newValue) {
        if (_requetValueHook != null) {
            _requetValueHook.requestByUIHook(this, newValue);
        }
        else {
            setValue(newValue);
        }
    }

    public void setValue(T value) {
        if (_value == value) {
            return;
        }
        if (_value != null && value != null) {
            if (_value.getClass() == value.getClass()) {
                if (_value.equals(value)) {
                    return;
                }
            }
        }
        _value = value;
        _listener.invokeChanging(this);
        checkAcceptableAndEnableButton();
    }

    public MXWatchableListenerList getListenerList() {
        return _listener;
    }
    private NamedValueList<T> _nameTable;

    @NonNull
    @Override
    public String toString() {
        return toStringForList();
    }

    public View createCustomEditor(Context context) {
        if (_nameTable != null) {
            return createListEditor(context);
        }
        return null;
    }

    public View createListEditor(Context context) {
        ArrayList<UserChoiceElement> data = new ArrayList<>();
        for (int i = 0; i < _nameTable.size(); ++i) {
            String name = _nameTable.nameOfIndex(i);
            BasicUserChoiceElement e = new BasicUserChoiceElement(name, 0, i);
            data.add(e);
        }
        UserChoiceView userView = new UserChoiceView(context);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins( 10, 10, 10, 10);
        userView.getAdapter().setData(data);
        for (int i = 0; i < _nameTable.size(); ++i) {
            T var = _nameTable.valueOfIndex(i);
            if (var == getValue()) {
                userView.getAdapter().setUserChoiceResult(i);
                break;
            }
        }
        userView.getAdapter().setOnUserChoiceListener((view, clicked) -> {
            if (clicked == null) {
            } else {
                int y = ((BasicUserChoiceElement) clicked).getValue();
                if (y < 0) {
                    requestValueByUI(null);
                }
                else {
                    requestValueByUI(_nameTable.valueOfIndex(y));
                }
            }
        });
        return userView;
    }

    T _editingValue = null;

    public T getEditingValueForEditor() {
        return _editingValue;
    }

    public void setEditingValueForEditor(T editingValue) {
        _editingValue = editingValue;
    }


    protected T _rollbackValue;

    public void saveRollbackPoint() {
        _rollbackValue = getValue();
    }

    public void doRollback() {
        setValue(_rollbackValue);
    }

    Button[] _listAcceptButtons = null;

    public void checkAcceptableAndEnableButton() {
        if (_listAcceptButtons != null) {
            boolean ok = isValueAcceptable();
            for (Button seek : _listAcceptButtons) {
                seek.setEnabled(ok);
            }
        }
    }
    public void bindAcceptButton(Button[] listButtons) {
        _listAcceptButtons = listButtons;
        checkAcceptableAndEnableButton();
    }
    public boolean isValueAcceptable() {
        return true;
    }

    @Override
    public int getNameRes() {
        return _nameRes;
    }

    @Override
    public String getNameText() {
        return _nameText;
    }

    @Override
    public int getSubLabel() {
        return 0;
    }

    @Override
    public String getSubLabelText() {
        return toString();
    }

    public String toStringForList() {
        if (_nameTable == null) {
            if (_value == null) {
                return "-";
            }
            return String.valueOf(_value);
        } else {
            String name = _nameTable.nameOfValue(_value);
            if (name == null) {
                if (_value == null) {
                    return "-";
                }
                return "(" + _value + ")";
            }
            return name;
        }
    }

    public String toStringForButton() {
        return toStringForList();
    }
    public boolean isEditorViewEqualFragment() {
        return false;
    }

    boolean _disableEditor = false;

    public void disableEditor(boolean readonly) {
        _disableEditor = readonly;
    }

    public boolean isDisableEditor() {
        return _disableEditor;
    }
}
