package businessregistration.lightspace.com.radioapp.Receivers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;

import businessregistration.lightspace.com.radioapp.Configuration.Config;
import businessregistration.lightspace.com.radioapp.Database.RealmHelper;
import businessregistration.lightspace.com.radioapp.Notifications.TextNotification;
import businessregistration.lightspace.com.radioapp.Services.RadioMediaPlayerService;
import io.realm.Realm;

/**
 * Created by user on 8/7/2018.
 */


public class NotificationReceiver extends WakefulBroadcastReceiver {

    private static String NUMBER_OF_NOTIFICATIONS;
    private String message;
    private Realm realm;
    private Config config;


    @Override
    public void onReceive(Context context, Intent intent) {

        message = intent.getStringExtra("gcm.notification.body");

        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                Log.e("FirebaseDataReceiver", "Key: " + key + " Value: " + value);
            }
        }

        long timeInMillis = Calendar.getInstance().getTimeInMillis();

        String dateTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(timeInMillis);

        //String time = String.valueOf(timeInMillis);
        saveToDatabase(context, message + "Time: " + dateTime);
        Log.e(NotificationReceiver.class.getSimpleName(), "Inserting Message into Realm Database " + message);

/*
        Intent radioAutostartIntent = new Intent();
        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
        startActivity(radioAutostartIntent);
*/
        Intent radioIntent = new Intent(context, RadioMediaPlayerService.class);
        context.startService(radioIntent);
        playNotificationSound(context);
    }


    public void saveToDatabase(Context context, String message) {

        TextNotification notification = new TextNotification();
        notification.setText(message);

        config = new Config(context);
        realm = config.setUpRealm();

        RealmHelper helper = new RealmHelper(realm);
        helper.save(context, notification);

    }

    public void playNotificationSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}