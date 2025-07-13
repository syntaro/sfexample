package fsexample.fsexample.ui.midiselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import fsexample.fsexample.common.AppBroadcast;
import fsexample.fsexample.R;
import fsexample.fsexample.common.Constant;
import fsexample.fsexample.common.MXMidiStatic;
import fsexample.fsexample.databinding.FragmentMidiSelectorBinding;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.MidiOneSetting;
import fsexample.fsexample.midione.drivers.fluid.FluidSetting;
import fsexample.fsexample.midione.drivers.fluid.FontFileEntry;
import fsexample.fsexample.midione.drivers.fluid.FontFileManager;
import fsexample.fsexample.midione.drivers.fluid.ProgramEntry;
import fsexample.fsexample.midione.v1.OneMessage;
import fsexample.fsexample.userchoice.BasicUserChoiceElement;
import fsexample.fsexample.userchoice.UserChoiceAdapter;
import fsexample.fsexample.userchoice.UserChoiceElement;
import fsexample.fsexample.userchoice.UserChoiceView;
import fsexample.fsexample.watchable.MXWatchable;
import fsexample.fsexample.watchable.MXWatchableGroup;
import fsexample.fsexample.watchable.MXWatchableGroupView;
import fsexample.fsexample.watchable.MXWatchableListener;
import fsexample.fsexample.watchable.withapp.MXWatchableSoundFontName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MidiSelectorFragment extends Fragment {
    public MidiSelectorFragment() {
        super();
    }

    MidiOne _one;

    private FragmentMidiSelectorBinding binding;

    Handler _myLooper = null;

    public void runInUIThread(Runnable run) {
        runInUIThread(run, 0);
    }

    public void runInUIThread(Runnable run, int time) {
        if (_myLooper == null) {
            _myLooper = new Handler(Looper.getMainLooper());
        }
        if (time == 0) {
            _myLooper.post(run);
        }
        else {
            _myLooper.postDelayed(run, time);
        }
    }
    AppBroadcast _broadcast;

    public void uninstallBroadcast() {
        if (_broadcast != null) {
            _broadcast.unregister();
            _broadcast = null;
        }
    }
    ActivityResultLauncher<Intent> _launchSfzBrowser;
    ActivityResultCallback<ActivityResult> activitySfzResult = result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {

            Intent intent = result.getData();
            if (intent == null) return;
            Context context = getContext();

            InputStream stream = null;

            try {
                Uri uri = intent.getData();
                String uriText = uri.toString();
                String soundFontPath = uri.getLastPathSegment();
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    String path = null;
                    if (cursor.moveToFirst()) {
                        soundFontPath = cursor.getString(0);
                    }
                    cursor.close();
                }

                try {
                    stream = context.getContentResolver().openInputStream(Uri.parse(uriText));
                } catch (Throwable ex) {
                    stream = new FileInputStream(new File(uriText));
                }
                if (stream != null) {
                    FontFileEntry newEntry = FontFileManager.getInstance().createFileFromStream(getContext(), soundFontPath, stream);
                    stream.close();
                    stream = null;
                    if (newEntry != null) {
                        FluidSetting fluidSetting = FluidSetting.getInstance();
                        fluidSetting._soundFont.setValue(newEntry.getNameText());
                    }
                }
            } catch (Throwable ex) {
                Log.e(Constant.TAG, ex.getMessage(), ex);
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    }catch(IOException ex) {

                    }
                    stream = null;
                }
            }
        }
    };

    public void installBroadcast() {
        if (_broadcast != null) {
            return;
        }
        _launchSfzBrowser = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activitySfzResult);

        _broadcast = new AppBroadcast(getContext(), new AppBroadcast.Callback() {

            @Override
            public void onEventInvoked(long id, int value, Parcelable userObject) {

                if (id == AppBroadcast.ID_LAUNCH_BROWSER) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    Intent req = Intent.createChooser(intent, "Open SoundFont File");
                    _launchSfzBrowser.launch(req);
                }
                if (id == AppBroadcast.ID_RESULT_BROWSER) {
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _one = MidiOne.getInstance();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    MXWatchableListener _fileListener = new MXWatchableListener() {
        @Override
        public void OnWatchableValueChanged(MXWatchableGroup paramSet, MXWatchable param) {
            runInUIThread(() -> {
                if (param instanceof MXWatchableSoundFontName) {
                    MXWatchableSoundFontName font =(MXWatchableSoundFontName)param;
                    FontFileManager manager = FontFileManager.getInstance();
                    if (font.getValue() != null) {
                        FontFileEntry entry = manager.findByName(font.getValue());
                        if (entry != null) {
                            if (manager.loadFontFile(getContext(), entry, false)) {
                                reloadProgramList(entry);
                                return;
                            }
                            else {
                                Toast.makeText(getContext(), "font file broken", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getContext(), "font file entry not found", Toast.LENGTH_SHORT).show();
                        }
                        reloadProgramList(null);
                    }
                }
            }, 500);
        }
    };

    public void reloadProgramList(FontFileEntry entry) {
        ArrayList<UserChoiceElement> set = new ArrayList<>();
        if (entry != null) {
            ArrayList<ProgramEntry> list = entry.listPrograms();
            if (list != null) {
                for (ProgramEntry e : list) {
                    e.send(0);
                    break;
                }
                set.addAll(list);
            }
        }
        binding.programList.getAdapter().setData(set);
        binding.programList.getAdapter().setOnUserChoiceListener((adapter, clicked) -> {
            if (clicked instanceof ProgramEntry) {
                ProgramEntry e = (ProgramEntry) clicked;
                e.send(0);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMidiSelectorBinding.inflate(getLayoutInflater());

        MidiOne.getInstance().postEnumerateDevicesForAll(getContext());
        installConfigView(MidiOneSetting.getInstance());
        installConfigView(FluidSetting.getInstance());
        FluidSetting.getInstance()._soundFont.getListenerList().addListner(_fileListener);

        UserChoiceAdapter adapter = binding.midiKeyboardView2.getAdapter();
        GridLayoutManager man = new GridLayoutManager(getContext(), 6);
        binding.midiKeyboardView2.setLayoutManager(man);
        ArrayList<UserChoiceElement> list = new ArrayList<>();
        for (int note = 48; note < 48 + 12; ++ note) {
            String name = MXMidiStatic.nameOfNote(note);
            BasicUserChoiceElement e = new BasicUserChoiceElement(name, 0, note);
            list.add(e);
        }
        adapter.setData(list);
        adapter.setOnUserChoiceListener((adapter1, clicked) -> {
            if (clicked instanceof  BasicUserChoiceElement) {
                BasicUserChoiceElement e = (BasicUserChoiceElement) clicked;
                int note = e.getValue();
                surroundNote(note);
            }
        });
        installBroadcast();
        binding.programList.setNoscrollMyself(true);

        return binding.getRoot();
    }
    public void onDestroyView() {
        super.onDestroyView();
        FluidSetting.getInstance()._soundFont.getListenerList().removeListener(_fileListener);
    }

    class ConfigView {
        TextView _text;
        UserChoiceView _userChoice;
        MXWatchableGroup _group;
        MXWatchableListener _listener;
        public ConfigView(Context context) {
            _userChoice = new UserChoiceView(context);
            _userChoice.setLayoutManager(new GridLayoutManager(context, 3));
            _text = new TextView(context);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(30, 8, 30, 0);
            _userChoice.setLayoutParams(params);
        }
        public void setGroup(Context context, MXWatchableGroup group) {
            _group = group;
            _text.setText(group.getNameRes());
            UserChoiceAdapter adapter = _userChoice.getAdapter();

            _listener = new MXWatchableListener() {
                @Override
                public void OnWatchableValueChanged(MXWatchableGroup paramSet, MXWatchable param) {
                    runInUIThread(() -> {
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                    });
                }
            };

            _group.getListenerList().addListner(_listener);

            ArrayList<UserChoiceElement> data = new ArrayList<>();
            for (int y = 0; y < group.size(); ++y) {
                MXWatchable seek = group.get(y);
                UserChoiceElement e = new BasicUserChoiceElement(seek.getNameRes(), new BasicUserChoiceElement.ShowSubLabel() {
                    @Override
                    public String show() {
                        return seek.toString();
                    }
                }, y);
                data.add(e);
            }

            adapter.setOnUserChoiceListener((adapter2, e1) -> {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    adapter.setUserChoiceResult(null);
                }, 10);
                int index = adapter.indexOf(e1);
                if (index >= 0) {
                    MXWatchable param = group.get(index);

                    AlertDialog.Builder builder =  new AlertDialog.Builder(context);
                    View view = param.createCustomEditor(context);
                    if (view != null) {
                        builder.setTitle(param.getNameRes());
                        param.saveRollbackPoint();
                        builder.setView(view);
                        builder.setPositiveButton(R.string.button_general_ok, (dialog, which) -> {
                        });
                        builder.setNegativeButton(R.string.button_general_cancel, (dialog, which) -> {
                            param.doRollback();
                        });
                        AlertDialog dialog = builder.show();
                    }
                }
            });
            adapter.setData(data);
        }

        public void uninstall() {
            _group.getListenerList().removeListener(_listener);
        }
    }

    public void installConfigView(MXWatchableGroup group) {
        ConfigView cur = new ConfigView(getContext());
        cur.setGroup(getContext(), group);

        binding.configContainer.addView(cur._text, ViewGroup.LayoutParams.WRAP_CONTENT);
        binding.configContainer.addView(cur._userChoice, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void surroundNote(int note) {
        int ch = 0;
        OneMessage on = OneMessage.thisCodes(0, MXMidiStatic.COMMAND_CH_NOTEON + ch, note, 100);
        OneMessage off = OneMessage.thisCodes(0, MXMidiStatic.COMMAND_CH_NOTEOFF + ch, note, 0);
        MidiOne.getInstance().dispatchOne(on);
        runInUIThread(() -> {
            MidiOne.getInstance().dispatchOne(off);
        }, 500);
    }
}
