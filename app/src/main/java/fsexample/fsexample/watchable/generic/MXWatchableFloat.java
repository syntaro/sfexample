package fsexample.fsexample.watchable.generic;

import fsexample.fsexample.watchable.MXWatchable;

public class MXWatchableFloat extends MXWatchable<Float> {
    Float _min;
    Float _max;

    public MXWatchableFloat(int name, Float min, Float max) {
        super(name);
        _min = min;
        _max = max;
    }

    @Override
    public void setValue(Float value) {
        if (_min != null && value < _min) {
            return;
        }
        if (_max != null && value > _max) {
            return;
        }
        super.setValue(value);
    }
}