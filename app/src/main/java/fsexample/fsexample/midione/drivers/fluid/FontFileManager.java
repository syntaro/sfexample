package fsexample.fsexample.midione.drivers.fluid;

import android.content.Context;
import android.graphics.fonts.Font;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import fsexample.fsexample.common.Constant;
import fsexample.fsexample.common.MXMidiStatic;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.v1.OneMessage;
import kotlin.io.LineReader;

public class FontFileManager   {
    static JFluid _jfluid;
    static {
        try {
            _jfluid = new JFluid();
        }catch (Throwable ex) {
            Log.e("FontFileManager", ex.getMessage(), ex);
        }
    }
    ArrayList<FontFileEntry> _listEntries;
    FontFileEntry _currentFile = null;
    static FontFileManager _instance;
    int _handle = -1;

    public synchronized static FontFileManager getInstance() {
        if (_instance == null) {
            _instance = new FontFileManager();
        }
        return _instance;
    }

    public FontFileEntry getDefault() {
        return _listEntries.get(0);
    }
    public FontFileEntry getCurrent() {
        return _currentFile;
    }
    public FontFileManager() {
        _listEntries = new ArrayList<>();
    }

    public void initList(Context context)  {
        _listEntries = new ArrayList<>();
        _listEntries.addAll(defaultForList(context));
    }

    public ArrayList<FontFileEntry> getList() {
        return _listEntries;
    }

    public FontFileEntry findByName(String name) {
        for (FontFileEntry e : _listEntries) {
            if (e.getNameText().equals(name)) {
                return e;
            }
        }
        return null;
    }

    ArrayList<FontFileEntry> defaultForList(Context context) {
        File dir = context.getFilesDir();
        Log.e(Constant.TAG, "dir = " + dir);
        ArrayList<FontFileEntry> ret = new ArrayList<>();
        String[] list = dir.list();
        if (list != null && list.length > 0) {
            for (String sf2 : list) {
                if (sf2.toLowerCase().endsWith(".sf2")) {
                    FontFileEntry e = new FontFileEntry(sf2);
                    e._name = sf2;
                    e._file = sf2;
                    ret.add(e);
                }
            }
        }
        return  ret;
    }
    void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[2048];
        while(true){
            int x = input.read(buffer, 0, buffer.length);
            if (x <= 0) {
                break;
            }
            output.write(buffer, 0, x);
        }
    }

    public static boolean isFluidUsable() {
        return _jfluid.isUsable();
    }

    public boolean isFluidReadyForPlay() {
        if(isFluidUsable()) {
            return _handle >= 0;
        }
        return false;
    }

    public FontFileEntry createFileFromStream(Context context, String name, InputStream input) throws  IOException {
        String fname = name;
        if (!fname.toLowerCase().endsWith(".sf2")) {
            fname += ".sf2";
        }
        for (int i = 0; i < _listEntries.size(); ++ i) {
            if (_listEntries.get(i).getNameText().equals(name)) {
                _listEntries.remove(i);
                i --;
                continue;
            }
        }
        OutputStream output = context.openFileOutput(fname, Context.MODE_PRIVATE);
        if (output != null) {
            try {
                copyStream(input, output);
                output.close();
                output = null;

                File file = new File(context.getFilesDir(), fname);
                if (!_jfluid.isSoundFont(file.getPath())) {
                    Log.e(Constant.TAG, "Not soundfont");
                    context.deleteFile(name);
                    return null;
                }
                FontFileEntry e = new FontFileEntry(name);
                e._name = name;
                e._file = fname;
                _listEntries.add(e);
                //JsonHelper.write(context, this);
                return e;
            }finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                }catch (IOException ex) {
                    Log.e(Constant.TAG, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }
    public boolean loadFontFile(Context context, FontFileEntry entry, boolean forceReload) {
        FluidSetting params = FluidSetting.getInstance();
        if (_currentFile == entry && !forceReload) {
            return true;
        }
        if (isFluidUsable() == false) {
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, "Can't use SoundFont for Now", Toast.LENGTH_LONG).show();
            });
            return false;
        }
        boolean lowLatency = params._soundFontLowLatency.getValue();

        synchronized (_jfluid) {
            if (entry._file == null || entry._file.isBlank()) {
                entry._file = entry._name + "_.sf2";
            }
            File file = new File(context.getFilesDir(), entry._file);
            if (file.exists() == false) {
                return false;
            }
            if (_handle >= 0) {
                //_fluid.close(_handle);
                _handle = -1;
            }
            try {
                _handle = _jfluid.open(file.getPath(), lowLatency);
                if (_handle < 0) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "this font file broken or too large to load.", Toast.LENGTH_SHORT).show();
                    });
                    return false;
                }

            } catch (Throwable ex) {
                Log.e(Constant.TAG, ex.getMessage(), ex);
            }

            _currentFile = entry;

            FluidSetting.getInstance()._soundFont.setValue(entry._name);

            byte[] reset = new byte[]{(byte) 0xF0, (byte) 0x7e, (byte) 0x7f, (byte) 0x09, (byte) 0x01, (byte) 0xf7};
            _jfluid.sendLongMessage(_handle, reset);

            entry._listPrograms = listProgramimpl();

            return true;
        }
    }

    protected ArrayList<ProgramEntry> listProgramimpl() {
        if (!isFluidReadyForPlay()) {
            return null;
        }
        ArrayList<ProgramEntry> result = new ArrayList<>();
        String list = _jfluid.listProgram(_handle);
        for (String line : list.split("\n")) {
            int progEnd = line.indexOf(',');
            if (progEnd < 0) {
                continue;
            }
            int bankEnd = line.indexOf(',', progEnd + 1);
            if (bankEnd < 0) {
                continue;
            }
            int bank32End = line.indexOf(',', bankEnd + 1);
            if (bank32End < 0) {
                continue;
            }
            try {
                String prog = line.substring(0, progEnd);
                String bank = line.substring(progEnd + 1, bankEnd);
                String bank32 = line.substring(bankEnd + 1, bank32End);
                String name = line.substring(bank32End + 1);

                ProgramEntry e = new ProgramEntry(
                        Integer.parseInt(prog),
                        Integer.parseInt(bank),
                        Integer.parseInt(bank32),
                        name
                );
                result.add(e);
            }catch (Exception ex) {
                Log.e(Constant.TAG, ex.getMessage(), ex);
            }
        }
        return result;
    }

    public void sendShortMessage(int status, int data1, int data2) {
        if (_jfluid != null && _handle >= 0) {
            _jfluid.sendShortMessage(_handle, status, data1, data2);
        }
    }
    public void sendLongMessage(byte[] data) {
        if (_jfluid != null && _handle >= 0) {
            _jfluid.sendLongMessage(_handle, data);
        }
    }
}
