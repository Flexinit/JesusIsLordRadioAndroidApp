package businessregistration.lightspace.com.radioapp.Notifications;

import io.realm.RealmObject;

/**
 * Created by user on 8/27/2018.
 */

public class TextNotification extends RealmObject {

    public String text;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
