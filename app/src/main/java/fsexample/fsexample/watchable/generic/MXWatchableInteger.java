package fsexample.fsexample.watchable.generic;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.material.slider.Slider;

import fsexample.fsexample.common.Constant;
import fsexample.fsexample.watchable.MXWatchable;

public class MXWatchableInteger extends MXWatchable<Integer> {
    int _min;
    int _max;
    int _sendOffset;

    public MXWatchableInteger(MXWatchableInteger copyFrom) {
        this(copyFrom._nameRes, copyFrom._min, copyFrom._max, copyFrom._sendOffset);
        _value = copyFrom._value;
    }

    public MXWatchableInteger(int name, int min, int max, int sendOffset) {
        super(name);
        _min = min;
        _max = max;
        _value = null;
        _sendOffset = sendOffset;
    }

    public void setAllFrom(MXWatchableInteger from) {
        _min = from._min;
        _max = from._max;
        _sendOffset = from._sendOffset;
        _value = from._value;
        setNameTable(from.getNameTable());
    }

    public int getMin() {
        return _min;
    }

    public int getMax() {
        return _max;
    }

    public int getSendOffset() {
        return _sendOffset;
    }
    public void setMin(int min) {
        _min = min;
    }

    public void setMax(int max) {
        _max = max;
    }
    public void setSendOffset(int offset) {
        _sendOffset = offset;
    }
    public int getSendValue() {
        if (_value == null) {
            return _sendOffset;
        }
        return _value + _sendOffset;
    }
    @Override
    public void setValue(Integer value) {
        if (value != null) {
            if (value < _min) {
                return;
            }
            if (value > _max) {
                return;
            }
        }
        super.setValue(value);
    }
    @Override
    public String toStringForButton() {
        String send;
        if (_sendOffset > 0) {
            send = "-" + _sendOffset;
        } else if (_sendOffset < 0) {
            send = "+" + _sendOffset;
        } else {
            send = "";
        }
        String minMax = "";
        if (_min == 0 && _max == 127) {

        } else if (_min == 0 && _max == 16383) {
            minMax = "(14bit)";
        } else {
            minMax = "(" + _min + " to " + _max + ")";
        }
        String disp = Integer.toString(_value);
        String dump = String.valueOf(getValue());
        if (disp.equalsIgnoreCase(dump)) {
            return disp + minMax + send;
        }
        return disp + "=" + dump + minMax + send;
    }

    @Override
    public View createCustomEditor(Context context) {
        View intherits = super.createCustomEditor(context);
        if (intherits == null) {
            Slider view = new Slider(context);
            view.setValueFrom(_min);
            view.setValueTo(_max);
            view.setValue(_value == null ? (_min + _max) / 2 : _value.intValue());
            view.addOnChangeListener((slider, value, fromUser) -> {
                setValue((int)value);
            });
            return view;
        }
        return intherits;
    }

    Integer _rollbackValue;
    int _rollbackMin;
    int _rollbackMax;
    int _rollbackSendoffset;

    @Override
    public void doUISaveState() {
        Log.e(Constant.TAG, "set rollback from " + getValue());
        _rollbackMin = getMin();
        _rollbackMax = getMax();
        _rollbackSendoffset = _sendOffset;
        _rollbackValue = getValue();
    }

    @Override
    public void doUIRollbackState() {
        Log.e(Constant.TAG, "rollback from " + getValue() + "-> " + _rollbackValue);
        _min = _rollbackMin;
        _max = _rollbackMax;
        _sendOffset = _rollbackSendoffset;
        setValue(_rollbackValue);
    }

    @Override
    public boolean isValueAcceptable() {
        return _value >= _min && _value <= _max;
    }
}
