package fsexample.fsexample.midione.drivers.fluid;

import android.util.Log;

public class JFluid {
    private boolean _loaded = false;

    JFluid() {
        try {
            System.loadLibrary("native-lib");
            _loaded = true;
        } catch (Throwable e) {
            System.err.println("App can't load native-lib - (FluidSynth).");
            e.printStackTrace();
        }
    }


    public void log(String str) {
        Log.e("Fluid", str);
    }
    public boolean isUsable() {
        return _loaded;
    }

    public synchronized native int open(String font, boolean lowlatency);

    public native boolean isSoundFont(String font);
    public synchronized native String listProgram(int handle);

    public synchronized native void close(int handle);

    public synchronized native void retune(int handle, float hzamust, boolean equalTemp, int baseKey);

    public synchronized native void sendShortMessage(int handle, int status, int data1, int data2);
    public synchronized native void sendLongMessage(int handle, byte[] data);
}
