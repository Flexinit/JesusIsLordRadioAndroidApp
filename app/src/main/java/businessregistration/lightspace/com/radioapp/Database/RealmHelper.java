package businessregistration.lightspace.com.radioapp.Database;

/**
 * Created by user on 8/30/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import businessregistration.lightspace.com.radioapp.Configuration.Config;
import businessregistration.lightspace.com.radioapp.Notifications.TextNotification;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmHelper {

    private Realm realm;
  //  private SavedNotifications savedNotifications;
    private RealmConfiguration realmConfig;
    private Context context;
    private Config config;


    public RealmHelper(Realm realm) {
        this.realm = realm;

    }


    //WRITE
    public void save(Context context, final TextNotification textNotification ) {
      //  savedNotifications = new SavedNotifications();
        config = new Config(context);
        realm = config.setUpRealm(this);

        //  realm = Realm.getDefaultInstance(); // in onCreate() and if(realm != null) { realm.close(); } // in onDestroy()
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {

                TextNotification s = realm.copyToRealm(textNotification);
                realm.copyToRealm(textNotification);

            }
        });

    }


    //READ
    public ArrayList<String> retrieveTexts() {

        ArrayList<String> texts = new ArrayList<>();
        RealmResults<TextNotification> textNotificationResults = realm.where(TextNotification.class).findAll();

        for (TextNotification s : textNotificationResults) {
            texts.add(s.getText());
        }

        return texts;
    }
}