/**
 * Config for app including URLS and other features.
 */

package businessregistration.lightspace.com.radioapp.Configuration;

import android.content.Context;

import businessregistration.lightspace.com.radioapp.Database.RealmHelper;
import businessregistration.lightspace.com.radioapp.main.HomeFragment;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class Config {

    private Realm realm;
    private RealmConfiguration realmConfig;
    private Context context;


    /********ALL EDITABLE SETTINGS ARE HERE *****/


    public Config(Context context) {
        this.context = context;
    }

    public static final String STREAM_URL = "rtsp://192.168.43.233:1935/live/android_test";
    public static final String PUBLISHER_USERNAME = "ravi";
    public static final String PUBLISHER_PASSWORD = "passtemp";
    //Name of radio station
    private final String radioName = "Radio";

    //URL of the radio stream
    // private String radioStreamURL = "http://comet.shoutca.st:9169/stream";
    //private String radioStreamURL = "http://cast5.servcast.net:1346";
    private String radioStreamURL = "https://s3.radio.co/s97f38db97/listen";

    public String getRadioWebsite() {
        return radioWebsite;
    }

    public void setRadioWebsite(String radioWebsite) {
        this.radioWebsite = radioWebsite;
    }

    //URL of webcam (or YouTube link maybe)
    private String radioWebsite = "https://www.jesusislordradio.info";

    //URL for the advertising / message banner. For no banner leave blank, i.e: ""
    private String adBannerURL = "http://www.aironair.co.uk/wp-content/uploads/2013/09/App-Banner.png";

    //Contact button twitter
    private String twitterAddress = "https://twitter.com/search?q=%40jesusiscoming_2&src=typd";

    //Contact button email address
    private String emailAddress = "jesusislord.fmradio@gmail.com";

    //Contact button phone number
    private String phoneNumber = "+254774667722";
    private String phoneNumber_2 = "+254774445851";

    //Contact button website URL
    private String websiteURL = "http://aironair.co.uk";
    public static String youtubeLink_1 = "https://www.youtube.com/user/repentancechannel";
    public static String youtubeLink_2 = "https://m.youtube.com/channel/UCqdgi-yU4fVlOhKZLrz24rw";
    public static String youtubeLink_3 = "https://m.youtube.com/channel/UCwOqIopR59qtuVxSH1XuGug";

    //Contact button SMS number
    private String smsNumber = "+254727503030";

    //Message to be shown in notification center whilst playing
    private String mainNotificationMessage = "You're listening to Radio";

    //TOAST notification when play button is pressed
    private String playNotificationMessage = "Starting Radio";

    //Play store URL (not known until published
    private String playStoreURL = "http://play.google.com/";

    //Enable console output for streaming info (Buffering etc) - Disable = false
    private boolean allowConsole = true;

    public Config(HomeFragment homeFragment) {
    }

    /********END OF EDITABLE SETTINGS**********/


    /********DO NOT EDIT BELOW THIS LINE*******/
    public String getRadioName() {
        return radioName;
    }

    public String getRadioStreamURL() {
        return radioStreamURL;
    }

    public String getRadioWebsiteURL() {
        return radioWebsite;
    }

    public String getAdBannerURL() {
        return adBannerURL;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getTwitterAddress() {
        return twitterAddress;
    }

    public String getPhoneNumber() {
        String appendPhoneNumber = "tel:" + phoneNumber;
        return appendPhoneNumber;
    }

    public String getPhoneNumber_2() {
        String appendPhoneNumber = "tel:" + phoneNumber_2;
        return appendPhoneNumber;
    }


    public String getSmsNumber() {
        return smsNumber;
    }

    public String getMainNotificationMessage() {
        return mainNotificationMessage;
    }

    public String getPlayNotificationMessage() {
        return playNotificationMessage;
    }

    public String getPlayStoreURL() {
        return playStoreURL;
    }

    public boolean getAllowConsole() {
        return allowConsole;
    }

    public Realm setUpRealm() {

        Realm.init(context);
        realmConfig = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(realmConfig);

        return realm;

    }


    public Realm setUpRealm(RealmHelper realmHelper) {

        return setUpRealm();
    }
}



