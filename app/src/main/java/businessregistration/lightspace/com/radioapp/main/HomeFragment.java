package businessregistration.lightspace.com.radioapp.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.ohoussein.playpause.PlayPauseView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import businessregistration.lightspace.com.radioapp.R;
import businessregistration.lightspace.com.radioapp.Recording.StreamActivity;
import businessregistration.lightspace.com.radioapp.Recording.VideoBroadcaster;
import businessregistration.lightspace.com.radioapp.Services.RadioMediaPlayerService;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ProgressDialog pDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PlayPauseView playPauseView;
    private OnFragmentInteractionListener mListener;
    private CoordinatorLayout coordinatorLayout;
    public static ProgressBar downloadProgressBar;

    private NotificationCompat.Builder mBuilder;
    private static boolean supportBigNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static boolean supportSmallNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private Notification notifyPlayer;
    private RemoteViews remoteViews;
    private File mOutputFile;
    private NestedScrollView scrollView;
    private ViewPager mViewPager;
    private Button closeApp;
    public static TextView recordingProgress;
    public static ImageView record_audio;
    public static String fileName;
    public static CircleImageView radio_logo;
    public LabeledSwitch labeledSwitch;
    /**
     * Called when the activity is first created.
     */


    public static boolean isRecording = false;
    private boolean radioIsOn = false;
    public static final int progress_bar_type = 0;

    long downloadedBytes;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_radio_fragment_layout, container, false);
        radio_logo = view.findViewById(R.id.logo);
        record_audio = view.findViewById(R.id.rec_audio);
        scrollView = view.findViewById(R.id.scrollView);
        coordinatorLayout = view.findViewById(R.id.coordinator);
        playPauseView = view.findViewById(R.id.play_pause_view);
        downloadProgressBar = view.findViewById(R.id.downloadProgressBar);
        recordingProgress = view.findViewById(R.id.recordingProgress);
       // labeledSwitch = view.findViewById(R.id.linkSwitch);
        closeApp = view.findViewById(R.id.close_app);


/*
        mViewPager =  view.findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(4);
*/
     //   setUpLinkSwitch();

//        liveStream.setOnClickListener(view12 -> startActivity(new Intent(getActivity(), VideoBroadcaster.class)));


        closeApp.setOnClickListener(v -> {

            new MaterialStyledDialog.Builder(getActivity())
                    .setTitle("CONFIRM?")
                    .setDescription("Are You Sure You Want To Exit?")
                    .setPositiveText(R.string.exit)
                    .setStyle(Style.HEADER_WITH_ICON)
                    .setNegativeText(R.string.cancel)
                    .setIcon(R.drawable.jesusislordradio2)
                    .setHeaderColor(R.color.colorPrimary)
                    .withIconAnimation(true)
                    .onPositive((dialog, which) -> {

                        Intent intent = new Intent(getActivity(), RadioMediaPlayerService.class);
                        getActivity().stopService(intent);


                        isRecording = false;

                        getActivity().finish();
                        System.exit(0);

                    }).onNegative((dialog, which) -> {
                dialog.dismiss();

            }).show();


        });

        record_audio.setOnClickListener(view1 -> {

            if (!isRecording) {

                setFileNameFromUser();

            } else {

                new MaterialStyledDialog.Builder(getActivity())
                        .setTitle("CONFIRM?")
                        .setDescription("Are You Sure You Want To Stop Recording?")
                        .setPositiveText(R.string.endRecording)
                        .setStyle(Style.HEADER_WITH_ICON)
                        .setNegativeText(R.string.cancel)
                        .setIcon(R.drawable.jesusislordradio2)
                        .setHeaderColor(R.color.colorPrimary)
                        .onPositive((dialog, which) -> {

                            Intent intent = new Intent(getActivity(), StreamActivity.class);
                            getActivity().stopService(intent);

                            //streamActivity.stopRecording();
                            record_audio.setImageResource(R.drawable.record);
                            downloadProgressBar.setVisibility(View.GONE);
                            recordingProgress.setVisibility(View.GONE);

                            isRecording = false;

                            Toast.makeText(getActivity(), R.string.stopRecording, Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "Audio file saved in" + mOutputFile.getPath(), Toast.LENGTH_LONG).show();
                            Log.e(HomeFragment.class.getName(), "audio file saved in" + mOutputFile.getPath());
                        }).onNegative((dialog, which) -> {
                    dialog.dismiss();

                }).show();

            }
        });


        playPauseView.setOnClickListener(v -> {

            if (!isNetworkAvailable()) {

                showErrorSnackbar();

            } else {

                playPauseView.toggle();

                if (!radioIsOn) {

                    Intent intent = new Intent(getActivity(), RadioMediaPlayerService.class);
                    intent.putExtra(RadioMediaPlayerService.START_PLAY, true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                    } else {
                        getActivity().startService(intent);

                    }
                    showCustomNotification();

                    radioIsOn = true;
                    animateLogo(radio_logo);
                    Log.e("Starting Radio Service", "Now");

                } else {

                    Intent intent = new Intent(getActivity(), RadioMediaPlayerService.class);
                    getActivity().stopService(intent);

                    radioIsOn = false;
                    stopAnimatingLogo(radio_logo);
                }

            }


        });
        // Inflate the layout for this fragment
        return view;
    }


    public void setUpLinkSwitch() {
        labeledSwitch.setLabelOff("OFF");
        labeledSwitch.setLabelOn("ON");
        labeledSwitch.setOn(false);
       // labeledSwitch.findViewById(R.id.linkSwitch);

        labeledSwitch.setOnToggledListener((labeledSwitch, isOn) -> {
            // Implement your switching logic here
        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

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

        return;
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


    // TODO: Rename method, update argument and hook method into UI event

    public boolean internetIsAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void animateLogo(CircleImageView view) {

        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_logo);
        view.startAnimation(rotate);
    }

    private void stopAnimatingLogo(CircleImageView view) {

        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_logo);
        view.clearAnimation();
    }

    private void setFileNameFromUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.jesusislordradio2);
        builder.setTitle(R.string.filename);
        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.rename_file_dialog, (ViewGroup) getView(), false);
        final EditText input = viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {

            if (input.getText().toString().trim().isEmpty()) {

                showEmptyEditTextSnackbar();
            }

            // fileName = input.getText().toString();

            try {

                // String recordingFileName = getFileNameFromUser();

                File storageDir = new File(Environment.getExternalStorageDirectory(), "/JesusIsLord");
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                storageDir.mkdirs(); // make sure you call mkdirs() and not mkdir()

                fileName = input.getText().toString();

                mOutputFile = new File(storageDir + File.separator + fileName + ".mp3");

                mOutputFile.createNewFile();

            } catch (Exception e) {

                e.printStackTrace();
            }


            if (!isNetworkAvailable()) {

                showErrorSnackbar();

            } else {

                Intent intent = new Intent(getActivity(), StreamActivity.class)
                        .putExtra(StreamActivity.START_REC, true)
                        .putExtra("file", mOutputFile.toString());
                getActivity().startService(intent);

                Log.e(HomeFragment.class.getName(), "Recording Service Started");

                downloadProgressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), R.string.startRecording, Toast.LENGTH_LONG).show();
                record_audio.setImageResource(R.drawable.stop_music);
                recordingProgress.setVisibility(View.VISIBLE);


                isRecording = true;

            }


            //   dialog.dismiss();

        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            isRecording = false;

            dialog.cancel();
        });
        builder.setCancelable(false);

        builder.show();


    }

    public void shutDownRadio() {


    }


    private void showCustomNotification() {

        RemoteViews mContentView = new RemoteViews(getActivity().getPackageName(), R.layout.custom_notifiation_layout);
        Intent intent = new Intent(getActivity(), TabbedActivityMain.class);

        if (supportBigNotifications) {
            remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.big_notification);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
        // Create Notification.
        mBuilder = new NotificationCompat.Builder(getActivity());
        notifyPlayer = mBuilder.setSmallIcon(R.drawable.jesusislordradio2)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();

// Assign remoteview to notification
        if (supportSmallNotifications) {
            notifyPlayer.contentView = mContentView;
            if (supportBigNotifications) {
                notifyPlayer.bigContentView = remoteViews;
            }
        } else {

            if (supportBigNotifications) {
                notifyPlayer.bigContentView = remoteViews;
            }
        }

// manage clicks of notification
        setRemoteViews(mContentView);
        if (supportBigNotifications) {
            setRemoteViews(remoteViews);
        }
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, notifyPlayer);

    }

    private void setRemoteViews(RemoteViews remoteViews) {

        // set Intent to open app on notification click.
        Intent openAppIntent = new Intent(getActivity(), TabbedActivityMain.class);

        // call broadcast when any control of notification is clicked.
        Intent closeNotification = new Intent(RadioMediaPlayerService.ACTION_STOP);
        Intent playIntent = new Intent(RadioMediaPlayerService.ACTION_PLAY);
        Intent previousInt = new Intent("previous_intent");
        Intent pauseIntent = new Intent(RadioMediaPlayerService.ACTION_PAUSE);

        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(getActivity(), 0, closeNotification, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(getActivity(), 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(getActivity(), 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingOpenIntent = PendingIntent.getActivity(getActivity(), 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Using RemoteViews to bind custom layouts into Notification
        remoteViews.setOnClickPendingIntent(R.id.btnStop, pendingCloseIntent);
        // remoteViews.setOnClickPendingIntent(R.id., pendingOpenIntent);
        remoteViews.setOnClickPendingIntent(R.id.btnPause, pendingPauseIntent);
        remoteViews.setOnClickPendingIntent(R.id.btnPlay, pendingPlayIntent);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}




