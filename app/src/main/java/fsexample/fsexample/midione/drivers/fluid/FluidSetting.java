package fsexample.fsexample.midione.drivers.fluid;

import android.content.Context;
import android.view.View;

import fsexample.fsexample.common.AppBroadcast;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneOutput;
import fsexample.fsexample.watchable.MXWatchable;
import fsexample.fsexample.watchable.MXWatchableGroup;
import fsexample.fsexample.watchable.MXWatchableListener;
import fsexample.fsexample.watchable.generic.MXWatchableBooleanYesNo;
import fsexample.fsexample.watchable.generic.MXWatchableString;
import fsexample.fsexample.watchable.withapp.MXWatchableSoundFontName;
import fsexample.fsexample.R;

public class FluidSetting extends MXWatchableGroup  {
    private static FluidSetting _fluid = null;


    public static synchronized FluidSetting getInstance() {
        if (_fluid != null) {
            return _fluid;
        }
        _fluid = new FluidSetting();
        return _fluid;
    }

    public static final String defaultFontName = "GeneralUser GS";
    public MXWatchableSoundFontName _soundFont = new MXWatchableSoundFontName(R.string.config_fluid_fontfile);
    public MXWatchableString _soundFontAdd = new MXWatchableString(R.string.button_general_add) {
        @Override
        public View createCustomEditor(Context context) {
            AppBroadcast.invokeHelper(context, AppBroadcast.ID_LAUNCH_BROWSER, 0, null);
            return null;
        }
    };

    public Context getAppContext() {
        return _appContext;
    }
    public MXWatchableBooleanYesNo _soundFontLowLatency = new MXWatchableBooleanYesNo(R.string.sfz_lowlatency);

    private String _prevLang = null;
    Context _appContext=  null;
    public void setupAppContext(Context context) {
        _appContext = context;
    }

    public FluidSetting() {
        super(R.string.config_group_fluid);
        _soundFont.setValue(null);
        _soundFontAdd.setValue(null);
        _soundFontLowLatency.setValue(true);

        install(_soundFont);
        install(_soundFontAdd);
        install(_soundFontLowLatency);
    }

    public void loadFont(boolean force) {
        FontFileManager manager = FontFileManager.getInstance();
        FontFileEntry e = manager.findByName(_soundFont.getValue());

        OneDeviceSoundFont device = (OneDeviceSoundFont) MidiOne.getInstance().getDriverSFZ().getDevice(0);
        OneOutput out = device.getOutput(0);

        if (e != null) {
            new Thread(() -> {
                manager.loadFontFile(_appContext, e, force);
                MidiOne.getInstance().launchOutput(out);
            }).start();
        }
    }

    boolean _readingJson = false;

    MXWatchableListener _listener = this::changeListener;
    public void installListener() {
        FluidSetting setting = FluidSetting.getInstance();
        setting.getListenerList().addListner(_listener);
    }

    public void changeListener(MXWatchableGroup paramSet, MXWatchable param) {
        if (_readingJson) {
            return;
        }
    }
}
