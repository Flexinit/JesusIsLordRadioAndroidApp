package businessregistration.lightspace.com.radioapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import businessregistration.lightspace.com.radioapp.R;
import businessregistration.lightspace.com.radioapp.main.HomeFragment;

/**
 * Created by user on 10/8/2018.
 */

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase("DownloadUpdate")) {

            long downloadedBytes = intent.getLongExtra("BytesRead", 0);
            Log.e(HomeFragment.class.getName(), "BytesRead: " + downloadedBytes);
            String progressBytes = String.valueOf((downloadedBytes / 1024) * 100);

            HomeFragment.downloadProgressBar.setProgress((int) downloadedBytes);
            HomeFragment.recordingProgress.setText(progressBytes + R.string.bytes);


        }
    }
}