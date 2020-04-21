package businessregistration.lightspace.com.radioapp.Database;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Created by user on 8/31/2018.
 */

public class Migration implements RealmMigration {

    // Migration logic...

    @Override
    public int hashCode() {
        return 37;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Migration);
    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

    }
}