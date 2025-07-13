package fsexample.fsexample;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import fsexample.fsexample.common.Constant;
import fsexample.fsexample.midione.MidiOne;
import fsexample.fsexample.midione.drivers.fluid.FluidSetting;
import fsexample.fsexample.midione.drivers.fluid.FontFileManager;
import fsexample.fsexample.ui.midiselector.MidiSelectorFragment;
import fsexample.fsexample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MidiOne.getInstance().init(getApplicationContext());
        MidiOne.getInstance().postEnumerateDevicesForAll(this);
        FluidSetting.getInstance().setupAppContext(getApplicationContext());
        FluidSetting.getInstance().installListener();

        FontFileManager.getInstance().initList(this);

        if (binding == null) {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            showFragment();
        }
    }

    public void showFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.containerMain, new MidiSelectorFragment(), "MidiSelectorFragment");
        transaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
