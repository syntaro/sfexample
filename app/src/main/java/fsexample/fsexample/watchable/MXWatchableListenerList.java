package fsexample.fsexample.watchable;

import java.util.ArrayList;

public class MXWatchableListenerList {
    ArrayList<MXWatchableListener> _list = null;
    static long _totalAdded = 0;
    static long _totalInvoke = 0;
    public MXWatchableListenerList() {
    }

    // package private
    public void setupOwnerGroup(MXWatchableGroup group) {
        _ownerGroup = group;
    }
    MXWatchableGroup _ownerGroup;

    public synchronized void addListner(MXWatchableListener listener) {
        if (_list == null) {
            _list = new ArrayList<>();
        }
        if (_list.contains(listener)) {
            //Log.e(TAG, "already "+ listener.toString(), new Throwable());
            return;
        }
        _list.add(listener);
        _totalAdded ++;
        //Log.e(TAG, "lister added " + listener.toString() + " , " + _totalAdded);
    }

    public synchronized void removeListener(MXWatchableListener listener) {
        if (_list == null) {
            return;
        }
        _list.remove(listener);
    }
    public synchronized void invokeChanging(MXWatchable value) {
        if (_ownerGroup != null) {
            if (_ownerGroup.getListenerList() == this) {
                //same process as after lines following synchronized(this)
            }
            else {
                _ownerGroup.getListenerList().invokeChanging(value);
            }
        }
        if (_list != null) {
            for (MXWatchableListener seek : _list) {
                if (seek != null) {
                    seek.OnWatchableValueChanged(_ownerGroup, value);
                    _totalInvoke++;
                }
            }
        }
    }
}
