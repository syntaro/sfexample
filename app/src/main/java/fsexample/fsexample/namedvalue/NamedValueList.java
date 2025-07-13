/*
 * Copyright 2023 Syntarou YOSHIDA.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fsexample.fsexample.namedvalue;

import java.util.ArrayList;
import java.util.List;

import fsexample.fsexample.common.MXUtil;

/**
 * @author Syntarou YOSHIDA
 */
public class NamedValueList<T> extends ArrayList<NamedValue<T>> {

    boolean _ignoreCase = false;

    public NamedValueList() {
        super();
    }

    public boolean ignoreCase() {
        return _ignoreCase;
    }

    public void setIgnoreCase(boolean ignore) {
        _ignoreCase = ignore;
    }

    public List<String> nameList() {
        ArrayList<String> list = new ArrayList<>();
        for (NamedValue<T> e : this) {
            list.add(e._name);
        }
        return list;
    }

    public List<T> valueList() {
        ArrayList<T> list = new ArrayList<>();
        for (NamedValue<T> e : this) {
            list.add(e._value);
        }
        return list;
    }

    public boolean existsName(String name) {
        if (_ignoreCase) {
            for (NamedValue<T> e : this) {
                if (e._name.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        } else {
            for (NamedValue<T> e : this) {
                if (e._name.equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    public int indexOfName(String name) {
        if (_ignoreCase) {
            int x = 0;
            for (NamedValue<T> e : this) {
                if (e._name.equalsIgnoreCase(name)) {
                    return x;
                }
                x++;
            }
            return -1;
        } else {
            int x = 0;
            for (NamedValue<T> e : this) {
                if (e._name.equals(name)) {
                    return x;
                }
                x++;
            }
            return -1;
        }
    }

    public int indexOfNameShrink(String name) {
        name = MXUtil.shrinkText(name);
        if (_ignoreCase) {
            int x = 0;
            for (NamedValue<T> e : this) {
                String name2 = MXUtil.shrinkText(e._name);
                if (name2.equalsIgnoreCase(name)) {
                    return x;
                }
                x++;
            }
            return -1;
        } else {
            int x = 0;
            for (NamedValue<T> e : this) {
                String name2 = MXUtil.shrinkText(e._name);
                if (name2.equalsIgnoreCase(name)) {
                    return x;
                }
                x++;
            }
            return -1;
        }
    }

    public int indexOfValue(T value) {
        int x = 0;
        for (NamedValue<T> e : this) {
            if (e._value == value) {
                return x;
            }
            x++;
        }
        x = 0;
        for (NamedValue<T> e : this) {
            if (e._value != null && value != null) {
                if (e._value.equals(value)) {
                    return x;
                }
            }
            x++;
        }
        return -1;
    }

    public String nameOfIndex(int x) {
        return get(x)._name;
    }

    public T valueOfIndex(int x) {
        return get(x)._value;
    }

    public T valueOfName(String name) {
        int x = indexOfName(name);
        if (x < 0) {
            return null;
        }
        return get(x)._value;
    }

    public String nameOfValue(T value) {
        int x = indexOfValue(value);
        if (x < 0) {
            return null;
        }
        return get(x)._name;
    }

    public boolean addNameAndValue(String name, T value) {
        boolean x = add(new NamedValue<>(name, value));
        return x;
    }

    //@Override
    public boolean add(NamedValue<T> value) {
        boolean x = super.add(value);
        fireIntervalAdded(this, size() - 1, size() - 1);
        return x;
    }

    //@Override
    public int getSize() {
        return size();
    }

    //@Override
    public Object getElementAt(int index) {
        return get(index);
    }

    //List<ListDataListener> _listeners = new ArrayList<>();

    Object _selected = null;

    /*
    @Override
    public void addListDataListener(ListDataListener l) {
        _listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        _listeners.remove(l);
    }
    */
    protected void fireContentsChanged(Object source, int index0, int index1) {
    }

    protected void fireIntervalAdded(Object source, int index0, int index1) {
    }

    protected void fireIntervalRemoved(Object source, int index0, int index1) {
    }

    //@Override
    public void setSelectedItem(Object o) {
        _selected = o;
    }

    //@Override
    public Object getSelectedItem() {
        if (_selected == null) {
            //　初期値
            if (size() > 0) {
                _selected = get(0);
            }
        }
        return _selected;
    }

    public String[] getNamesForAlert() {
        String[] ret = new String[size()];
        for (int i = 0; i < size(); ++i) {
            ret[i] = nameOfIndex(i);
        }
        return ret;
    }
}
