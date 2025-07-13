package fsexample.fsexample.watchable;

import java.util.ArrayList;

public class MXWatchableGroup extends ArrayList<MXWatchable> {
    protected final int _nameRes;
    protected final String _nameText;

    protected MXWatchableListenerList _listener;

    public MXWatchableGroup(int nameRes) {
        this(nameRes, null);
    }

    public MXWatchableGroup(String nameText) {
        this(0, nameText);
    }

    public MXWatchableGroup(int nameRes, String nameText) {
        _nameRes = nameRes;
        _nameText = nameText;
        _listener = new MXWatchableListenerList();
        _listener.setupOwnerGroup(this);
    }

    public MXWatchableListenerList getListenerList() {
        return _listener;
    }

    public int getNameRes() {
        return _nameRes;
    }

    public String getNameText() {
        return _nameText;
    }

    public void install(MXWatchable param) {
        super.add(param);
        param.getListenerList().setupOwnerGroup(this);
        if (param instanceof MXWatchableCategory) {
            MXWatchableCategory cat = (MXWatchableCategory) param;
            cat.getListenerList().setupOwnerGroup(this);
            return;
        }
    }

    public boolean uninstall(MXWatchable param) {
        _listener.setupOwnerGroup(null);
        return super.remove(param);
    }

    public void setRequestValueHook(MXWatchableRequestHook requetValueHook) {
        for (MXWatchable seek : this) {
            seek.setRequestValueHook(requetValueHook);
        }
    }
}
