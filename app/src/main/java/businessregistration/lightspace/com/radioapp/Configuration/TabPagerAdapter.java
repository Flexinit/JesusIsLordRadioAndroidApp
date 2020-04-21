package businessregistration.lightspace.com.radioapp.Configuration;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import businessregistration.lightspace.com.radioapp.main.HomeFragment;
import businessregistration.lightspace.com.radioapp.Notifications.NotificationsFragment;
import businessregistration.lightspace.com.radioapp.Webviewfragment;
import businessregistration.lightspace.com.radioapp.main.TranslateFragment;

public class TabPagerAdapter extends FragmentPagerAdapter implements FragmentManager.OnBackStackChangedListener {
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
        fm.addOnBackStackChangedListener(this);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:

                return new HomeFragment();

            case 1:

                return new Webviewfragment();

            case 2:

                return new NotificationsFragment();

            case 3:

                return new TranslateFragment();

            default:

                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }


    @Override
    public void onBackStackChanged() {

    }
}



