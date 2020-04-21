package businessregistration.lightspace.com.radioapp.Services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.firebase.messaging.FirebaseMessagingService;

import businessregistration.lightspace.com.radioapp.Configuration.Config;
import businessregistration.lightspace.com.radioapp.Database.RealmHelper;
import businessregistration.lightspace.com.radioapp.Notifications.RecyclerAdapter;
import businessregistration.lightspace.com.radioapp.Notifications.TextNotification;
import io.realm.Realm;

/**
 * Created by user on 8/6/2018.
 */

public class MessagingService extends FirebaseMessagingService {

    Bitmap imageBitmap;
    Bitmap bitmap;
    String message;
    String imageUri;

    private Realm realm;
    private RealmHelper helper;
   // private SavedNotifications savedNotifications;
    private ArrayList<String> textLists;
    private RecyclerAdapter recyclerAdapter;
    private Config config;

    public static final String ACTION_UNREAD_NOTIFICATION = "unreadNotification";
    public static final String EXTRA_NOTIFICATION_BODY = "notification_body";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        message = remoteMessage.getNotification().getBody();
        // db = new DatabaseHelper(getApplicationContext());
        //db.insertMsg(message);
        long time = remoteMessage.getSentTime();


     //   saveToDatabase(message + "  " + time);


        Log.e(MessagingService.class.getSimpleName(), "Inserting Message into Realm Database ");
        //showNotification(remoteMessage, 1);
    }



    public void saveToDatabase(String message) {

        TextNotification notification = new TextNotification();
        notification.setText(message);

        //Realm.init(this);
        // realm = Realm.getDefaultInstance(); // in onCreate() and if(realm != null) { realm.close(); } // in onDestroy()
        config = new Config(getApplicationContext());
        realm = config.setUpRealm();

        RealmHelper helper = new RealmHelper(realm);
        helper.save(this, notification);

       // SavedNotifications.textLists = helper.retrieveTexts();
        //SavedNotifications.adapter = new RecyclerAdapter(this, textLists);
        // recyclerView.setAdapter(adapter);

    }

/*
    private void showNotification(RemoteMessage messageBody, int ID) {

     //   Intent i = new Intent(this, SavedNotifications.class);
     //   i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("PREPARE THE WAY")
                .setContentText(messageBody.getNotification().getBody())
                .setAutoCancel(true)
                // .setLargeIcon(image)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                .setColor(Color.BLUE)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID, notificationBuilder.build());

    }
*/
    public Bitmap getBitmapFromUri(String imageURL) {

        try {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);

            return bitmap;

        } catch (Exception ex) {
            ex.printStackTrace();

            return null;

        }

    }
}
