package fsexample.fsexample.midione.drivers.none;

import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneDevice;
import fsexample.fsexample.midione.v1.OneDispatcher;
import fsexample.fsexample.midione.v1.OneInput;
import fsexample.fsexample.midione.v1.OneMessage;

public class OneInputNone extends OneInput {
    public OneInputNone(OneDevice device) {
        super(device, 0);
        bindOnParsed(new OneDispatcher() {
            @Override
            public boolean dispatchOne(OneMessage one) {
                one._messageSource = OneInputNone.this;
                MidiOne.getInstance().dispatchOne(one);
                return true;
            }
        });
    }

    @Override
    public int getNameRes() {
        return 0;
    }

    @Override
    public String getNameText() {
        return "<None>";
    }
}
