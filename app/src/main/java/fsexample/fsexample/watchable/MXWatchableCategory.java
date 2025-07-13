package fsexample.fsexample.watchable;

import android.content.Context;
import android.view.View;

public class MXWatchableCategory extends MXWatchable<MXWatchableGroup> {
    static Context _context;

    public static void setupAppContext(Context context) {
        _context = context;
    }
    public MXWatchableCategory(int name) {
        super(name);
        _value =  new MXWatchableGroup(name);
    }

    public String toStringForList() {
        StringBuilder result = new StringBuilder();
        for (MXWatchable seek : _value) {
            if (seek.getValue() != null) {
                int titleId = seek.getNameRes();
                String title = _context.getResources().getString(titleId);
                String value = seek.toString();
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append(title);
                result.append("=");
                result.append(value);
            }
        }
        return result.toString();
    }

    @Override
    public View createCustomEditor(Context context) {
        MXWatchableGroupView view = new MXWatchableGroupView(context, _value);
        return view;
    }

    @Override
    public void setRequestValueHook(MXWatchableRequestHook requetValueHook) {
        _value.setRequestValueHook(requetValueHook);
    }
}
