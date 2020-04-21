package businessregistration.lightspace.com.radioapp.main;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


import businessregistration.lightspace.com.radioapp.Configuration.Config;
import businessregistration.lightspace.com.radioapp.Configuration.TabPagerAdapter;
import businessregistration.lightspace.com.radioapp.Database.RealmHelper;
import businessregistration.lightspace.com.radioapp.Notifications.NotificationsFragment;
import businessregistration.lightspace.com.radioapp.R;
import businessregistration.lightspace.com.radioapp.Recording.DownloadActivity;
import businessregistration.lightspace.com.radioapp.Recording.RecordingsLibrary;
import businessregistration.lightspace.com.radioapp.Recording.StreamActivity;
import businessregistration.lightspace.com.radioapp.Services.FirebaseInstanceIdService;
import businessregistration.lightspace.com.radioapp.Services.RadioMediaPlayerService;
import businessregistration.lightspace.com.radioapp.Webviewfragment;
import io.realm.Realm;

import static android.Manifest.permission.CALL_PHONE;
import static businessregistration.lightspace.com.radioapp.main.HomeFragment.downloadProgressBar;
import static businessregistration.lightspace.com.radioapp.main.HomeFragment.isRecording;
import static businessregistration.lightspace.com.radioapp.main.HomeFragment.recordingProgress;


public class TabbedActivityMain extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        Webviewfragment.OnFragmentInteractionListener, NotificationsFragment.OnFragmentInteractionListener,
        TranslateFragment.OnFragmentInteractionListener {

    private ProgressBar playSeekBar;
    public static Button buttonPlay;
    private Button buttonStopPlay;
    private Button buttonRecord;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    private MediaPlayer player;
    private RadioMediaPlayerService radioMediaPlayerService;
    private final long MEDIA_LENGTH_IN_KBS = 1677;
    private final long MEDIA_LENGTH_IN_SECONDS = 217;

    private InputStream inputStream;
    private OutputStream outputStream;
    private FileOutputStream fileOutputStream;
    private File outputSource = new File("record.mp3");
    private DrawerLayout mDrawerLayout;
    boolean doubleBackToExitPressedOnce = false;


    public Config settings = new Config(this);
    private static final int REQUEST_CALL_PHONE = 0;
    private static final int REQUEST_READ_CONTACTS = 1;

    TextView textCartItemCount;

    private NotificationCompat.Builder mBuilder;
    private static boolean supportBigNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static boolean supportSmallNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private Notification notifyPlayer;
    private RemoteViews remoteViews;

    private MenuItem notifications;
    /**
     * Called when the activity is first created.
     */

    private static final int RECORD_REQUEST_CODE = 101;
    private static final int WRITE_STORAGE_REQUEST_CODE = 102;
    private static final int CAMERA_REQUEST_CODE = 103;
    private static final int READ_STORAGE_REQUEST_CODE = 104;
    private static final int MEDIA_CONTROL_REQUEST_CODE = 105;


    public static int offScreenLimit = 4;


    Realm realm;
    RealmHelper realmHelper;

    private String youtubeLink;
    public String[] permissions = {

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tabbed_activity_main_with_drawer);

        initializeUIElements();
        setUpTabs();
        getMediaPermissions();
        radioMediaPlayerService = new RadioMediaPlayerService();
        // checkIfInternetIsAvailable();
        Intent instanceID = new Intent(this, FirebaseInstanceIdService.class);
        startService(instanceID);

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    private void changeStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.israeli_blue));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        //notifications = menu.findItem(R.id.action_notifications);
        final MenuItem musicLibrary = menu.findItem(R.id.action_music_library);
        final MenuItem downloads = menu.findItem(R.id.action_youtube_download);


        View actionView = MenuItemCompat.getActionView(musicLibrary);
        // actionView.findViewById(R.id.action_music_library);
        /*
        actionView.setOnClickListener(view -> {

            onOptionsItemSelected(musicLibrary);
        });
*/
        // textCartItemCount = actionView.findViewById(R.id.notification_badge);

        // actionView.setOnClickListener(v -> onOptionsItemSelected(notifications));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //open the drawer
                mDrawerLayout.openDrawer(GravityCompat.START);

                return true;

            case R.id.action_music_library:
                //go to recordings Library
                startActivity(new Intent(TabbedActivityMain.this, RecordingsLibrary.class));
                forwardAnimation(this);

                return true;

            case R.id.action_youtube_download:
                //go to recordings Library
                if (!isNetworkAvailable()) {
                    Toast.makeText(this, "Sorry, No network connection", Toast.LENGTH_LONG).show();
                    // showErrorSnackbar();
                } else {

                    downloadVideo();

                }


                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    protected void requestPermission(String permissionType, int
            requestCode) {
        int permission = ContextCompat.checkSelfPermission(getApplicationContext(),
                permissionType);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    /*
                    Toast.makeText(this,
                            "Camera permission required",
                            Toast.LENGTH_LONG).show();
                            */

                    getMediaPermissions();
                }
                break;
            }

            case REQUEST_CALL_PHONE: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    // recordButton.setEnabled(false);
                    Toast.makeText(this,
                            "Call Phone permission required",
                            Toast.LENGTH_LONG).show();

                }
                break;
            }
            case WRITE_STORAGE_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Read External Storage permission required",
                            Toast.LENGTH_LONG).show();
                    getMediaPermissions();

                }
                break;
            }
            case READ_STORAGE_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Read External Storage permission required",
                            Toast.LENGTH_LONG).show();

                    getMediaPermissions();

                }
                break;
            }
            case MEDIA_CONTROL_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Media Control permission required",
                            Toast.LENGTH_LONG).show();
                }
                break;
            }

            default:
                return;
        }
    }

    public void forwardAnimation(Activity activity) {

        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    private void setUpTabs() {

        TabLayout tabLayout = (findViewById(R.id.tab_layout));
        tabLayout.setTabTextColors(Color.parseColor("#63D1F4"), Color.WHITE);
        tabLayout.animate();

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_home).setText(R.string.home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_link).setText(R.string.website));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_updates).setText(R.string.updates));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_translate).setText(R.string.translate));
        // tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_file_download));
        // tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_videocam));

        /*
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_home).setText(R.string.home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_link).setText(R.string.website));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_notification).setText(R.string.updates));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_translate).setText(R.string.translate));
        */


        getFragmentManager().addOnBackStackChangedListener(() -> {


        });

        final ViewPager viewPager =
                findViewById(R.id.pager);
        final PagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(offScreenLimit);


        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                viewPager.animate();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewPager.setOnScrollChangeListener((view, i, i1, i2, i3) -> {

            });
        }
    }

    private void initializeUIElements() {
        changeStatusBarColor();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_drawer);


        NavigationView navigationView = findViewById((R.id.navigation_header_container));
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    settings = new Config(TabbedActivityMain.this);
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    if (menuItem.getItemId() == R.id.call) {

                        String phoneNum = settings.getPhoneNumber();
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNum));

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return mayMakeCall();
                        }
                        startActivity(phoneIntent);
                    }
                    if (menuItem.getItemId() == R.id.call_2) {

                        String phoneNum_2 = settings.getPhoneNumber_2();
                        Intent phoneIntent_2 = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNum_2));

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return mayMakeCall();
                        }
                        startActivity(phoneIntent_2);

                    }

                    if (menuItem.getItemId() == R.id.sms) {

                        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + settings.getSmsNumber()));
                        smsIntent.putExtra("sms_body", "text_message ");
                        startActivity(smsIntent);

                    }
                    if (menuItem.getItemId() == R.id.whatsapp) {

                        String url = "https://api.whatsapp.com/send?phone=+254727503030";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }

                    if (menuItem.getItemId() == R.id.sendEmail) {

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{settings.getEmailAddress()});

                        try {

                            startActivity(Intent.createChooser(emailIntent, "Send email..."));

                        } catch (ActivityNotFoundException ex) {
                            Toast.makeText(TabbedActivityMain.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }


                    }

                    if (menuItem.getItemId() == R.id.tweet) {

                        Intent twitterIntent = new Intent()
                                .setType("text/plain")
                                .setAction(Intent.ACTION_SEND)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        final PackageManager packageManager = getPackageManager();
                        List<ResolveInfo> list = packageManager.queryIntentActivities(twitterIntent, PackageManager.MATCH_DEFAULT_ONLY);

                        for (ResolveInfo resolveInfo : list) {
                            String packageName = resolveInfo.activityInfo.packageName;

                            //In case that the app is installed, lunch it.
                            if (packageName != null && packageName.equals("com.twitter.android")) {
                                try {
                                    // String formattedTwitterAddress = "twitter://user/";
                                    String formattedTwitterAddress = settings.getTwitterAddress();
                                    Intent browseTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse(formattedTwitterAddress))
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    long twitterId = 0;
                                    //browseTwitter.putExtra("user_id", twitterId);
                                    browseTwitter.putExtra("user_name", settings.getTwitterAddress());
                                    startActivity(browseTwitter);

                                } catch (Exception e) {

                                    e.printStackTrace();

                                }
                            }
                        }

                        //If it gets here it means that the twitter app is not installed. Therefor, lunch the browser.
                        try {
                            String twitterName = settings.getTwitterAddress();
                            String formattedTwitterAddress = "https://twitter.com/search?q=%40jesusiscoming_2&src=typd";
                            Intent browseTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse(formattedTwitterAddress));
                            startActivity(browseTwitter);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }

                    if (menuItem.getItemId() == R.id.youtubeChannel_1) {

                        goToRepentanceChannel1(Config.youtubeLink_1);

                    }
                    if (menuItem.getItemId() == R.id.youtubeChannel_2) {

                        goToRepentanceChannel1(Config.youtubeLink_2);

                    }
                    if (menuItem.getItemId() == R.id.youtubeChannel_3) {

                        goToRepentanceChannel1(Config.youtubeLink_3);

                    }

                    if (menuItem.getItemId() == R.id.share) {
                        shareAppLink();
                    }


                    return true;
                });


        mDrawerLayout.animate();
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

    }


    private void setRemoteViews(RemoteViews remoteViews) {

        // set Intent to open app on notification click.
        Intent openAppIntent = new Intent(this, TabbedActivityMain.class);

        // call broadcast when any control of notification is clicked.


        Intent previousInt = new Intent("previous_intent");


        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(this, 0, getStopIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(this, 0, getPlayIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, 0, getPauseIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingOpenIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Using RemoteViews to bind custom layouts into Notification
        remoteViews.setOnClickPendingIntent(R.id.btnStop, pendingCloseIntent);
        // remoteViews.setOnClickPendingIntent(R.id., pendingOpenIntent);
        remoteViews.setOnClickPendingIntent(R.id.btnPause, pendingPauseIntent);
        remoteViews.setOnClickPendingIntent(R.id.btnPlay, pendingPlayIntent);

    }

    private Intent getPauseIntent() {

        Intent pauseIntent = new Intent(RadioMediaPlayerService.ACTION_PAUSE);

        return pauseIntent;
    }

    private Intent getPlayIntent() {

        if (!isMyServiceRunning(RadioMediaPlayerService.class)) {
            buttonPlay.setEnabled(false);
            buttonStopPlay.setEnabled(true);

            Intent playIntent = new Intent(RadioMediaPlayerService.ACTION_PLAY);

            return playIntent;
        } else {
            Toast.makeText(this, R.string.playing, Toast.LENGTH_LONG).show();
        }

        return null;
    }

    private Intent getStopIntent() {

        if (isMyServiceRunning(RadioMediaPlayerService.class)) {
            buttonPlay.setEnabled(true);
            buttonStopPlay.setEnabled(false);

            Intent stopIntent = new Intent(RadioMediaPlayerService.ACTION_STOP);

            return stopIntent;
        } else {

            return null;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {

        if (isRecording) {

            new MaterialStyledDialog.Builder(this)
                    .setTitle("CONFIRM?")
                    .setDescription("Are You Sure You Want To Stop Recording?")
                    .setPositiveText(R.string.endRecording)
                    .setStyle(Style.HEADER_WITH_ICON)
                    .setNegativeText(R.string.cancel)
                    .setIcon(R.drawable.jesusislordradio2)
                    .onPositive((dialog, which) -> {

                        Intent intent = new Intent(this, StreamActivity.class);
                        stopService(intent);

                        //streamActivity.stopRecording();
                        HomeFragment.record_audio.setImageResource(R.drawable.record);
                        downloadProgressBar.setVisibility(View.GONE);
                        recordingProgress.setVisibility(View.GONE);

                        Toast.makeText(this, R.string.stopRecording, Toast.LENGTH_LONG).show();

                        isRecording = false;

                    }).onNegative((dialog, which) -> {
                dialog.dismiss();

            }).show();

        } else {

            super.onDestroy();
        }
    }

    private void downloadVideo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.paste_here);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.paste_link_dialog, findViewById(R.id.link_dialog), false);
// Set up the input
        final EditText input = viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {

            if (input.getText().toString().trim().isEmpty()) {

                Toast.makeText(this, "Please paste a Youtube link", Toast.LENGTH_LONG).show();
                //  showEmptyEditTextSnackbar();
            } else {

                youtubeLink = input.getText().toString();

                Intent downloadIntent = new Intent(TabbedActivityMain.this, DownloadActivity.class);
                downloadIntent.putExtra("link", youtubeLink);
                startActivity(downloadIntent);
                forwardAnimation(TabbedActivityMain.this);
            }
        });


        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showEmptyEditTextSnackbar() {

        Snackbar mSnackBar = Snackbar.make(coordinatorLayout, R.string.input_fileName, Snackbar.LENGTH_LONG);
        TextView mainTextView = (mSnackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        mainTextView.setTextColor(Color.RED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mSnackBar.show();

        return;
    }

    /*
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
*/

    private void showErrorSnackbar() {

        Snackbar mSnackBar = Snackbar.make(coordinatorLayout, R.string.networkUnavailable, Snackbar.LENGTH_LONG);
        TextView mainTextView = (mSnackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        mainTextView.setTextColor(Color.RED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mSnackBar.show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void goToRepentanceChannel1(String youtubeURL) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setPackage("com.google.android.youtube");

        startActivity(appIntent);

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(youtubeURL));
        try {

            startActivity(appIntent);

        } catch (ActivityNotFoundException ex) {

            startActivity(webIntent);
        }
    }

    public void goToRepentanceChannel2(String youtubeURL) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setPackage("com.google.android.youtube");

        startActivity(appIntent);

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(youtubeURL));
        try {

            startActivity(appIntent);

        } catch (ActivityNotFoundException ex) {

            startActivity(webIntent);
        }
    }

    public void goToRepentanceChannel3(String youtubeURL) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setPackage("com.google.android.youtube");

        startActivity(appIntent);

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(youtubeURL));
        try {

            startActivity(appIntent);

        } catch (ActivityNotFoundException ex) {

            startActivity(webIntent);
        }
    }

    public void getMediaPermissions() {

        //   requestPermission(Manifest.permission.CAMERA,CAMERA_REQUEST_CODE);

        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                WRITE_STORAGE_REQUEST_CODE);

        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_STORAGE_REQUEST_CODE);
/*
        requestPermission(Manifest.permission.MEDIA_CONTENT_CONTROL,
                MEDIA_CONTROL_REQUEST_CODE);
*/
    }


    private boolean mayMakeCall() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
        }
        return false;
    }


    private void shareAppLink() {

        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=businessregistration.lightspace.com.radioapp \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
