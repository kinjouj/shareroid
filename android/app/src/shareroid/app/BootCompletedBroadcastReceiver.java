package shareroid.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompletedBroadcastReceiver.class
                                        .getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive");
        context.startService(new Intent(context, ReadService.class));
    }
}