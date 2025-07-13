package fsexample.fsexample.watchable.generic;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import fsexample.fsexample.R;
import fsexample.fsexample.namedvalue.NamedValue;
import fsexample.fsexample.namedvalue.NamedValueList;
import fsexample.fsexample.userchoice.BasicUserChoiceElement;
import fsexample.fsexample.userchoice.UserChoiceElement;
import fsexample.fsexample.userchoice.UserChoiceView;

public class MXWatchableTabled extends MXWatchableInteger {
    NamedValueList<Integer> _table;
    UserChoiceView _view = null;

    public MXWatchableTabled(int name, NamedValueList<Integer> table) {
        super(name, 0, 0, 0);
        _table = table;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < table.getSize(); ++i) {
            NamedValue<Integer> var = table.get(i);
            int x = var._value;
            if (max < x) max = x;
            if (min > x) min = x;
        }
        setMax(max);
        setMin(min);
    }

    public String toStringForList() {
        return _table.nameOfValue(getValue());
    }

    ArrayList<UserChoiceElement> _list;

    @Override
    public View createCustomEditor(Context context) {
        _view = new UserChoiceView(context);
        _list = new ArrayList<>();
        UserChoiceElement focus = null;
        for (NamedValue<Integer> seek : _table) {
            UserChoiceElement e = new BasicUserChoiceElement(seek._name, "" + seek._value, seek._value);
            if (seek._value == getValue()) {
                focus = e;
            }
            _list.add(e);
        }
        _view.setData(_list);
        _view.setUserChoiceResult(focus);
        _view.getAdapter().setOnUserChoiceListener((adapter, clicked) -> {
            BasicUserChoiceElement b = (BasicUserChoiceElement) clicked;
            if (b != null) {
                super.requestValueByUI(b.getValue());
            }
        });
        return _view;
    }
}
