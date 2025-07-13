package fsexample.fsexample.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AppBroadcast extends BroadcastReceiver {
    public static final int ID_LAUNCH_BROWSER = 101;
    public static final int ID_RESULT_BROWSER= 102;
    private static final String ACTION_INVOKED = "fsexample.fsexample.ACTION_INVOKED";
    private static final String ID = "ID";
    private static final String VALUE = "VALUE";
    private static final String USER_OBJECT = "USER_OBJECT";
    public interface Callback {
        void onEventInvoked(long id, int value, Parcelable userObject);
    }

    private Callback callback;
    private LocalBroadcastManager manager;

    public AppBroadcast(@NonNull Context context, @NonNull Callback callback) {
        super();
        this.callback = callback;
        manager = LocalBroadcastManager.getInstance(context.getApplicationContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INVOKED);

        manager.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        String action = intent.getAction();

        if (ACTION_INVOKED.equals(action)) {
            long id = intent.getLongExtra(ID, 0);
            int value = intent.getIntExtra(VALUE, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Parcelable userObject = intent.getParcelableExtra(USER_OBJECT, Parcelable.class);
                callback.onEventInvoked(id, value, userObject);
            } else {
                Object userObject = intent.getParcelableExtra(USER_OBJECT);
                callback.onEventInvoked(id, value, (Parcelable) userObject);
            }
        }
    }

    public static void invokeHelper(@NonNull Context context, long id, int value, Parcelable userObject) {
        Intent intent = new Intent(ACTION_INVOKED);
        intent.putExtra(ID, id);
        intent.putExtra(VALUE, value);
        intent.putExtra(USER_OBJECT, userObject);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        manager.sendBroadcast(intent);
    }

    public void unregister() {
        manager.unregisterReceiver(this);
    }
}