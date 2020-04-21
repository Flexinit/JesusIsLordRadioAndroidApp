package businessregistration.lightspace.com.radioapp.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import businessregistration.lightspace.com.radioapp.Configuration.Config;


public class RadioMediaPlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {

    //Variables
    private static boolean isPlaying = false;
    private MediaPlayer radioPlayer; //The media player instance
    private static int classID = 579; // just a number
    public static String START_PLAY = "START_PLAY";
    //Config
    Config settings = new Config(this);
    private AudioManager audioManager;
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    private InputStream inputStream;
    private OutputStream outputStream;
    private FileOutputStream fileOutputStream;
    private String outputSource = "record.mp3";


    public static final String ACTION_PLAY = "com.valdioveliu.valdio.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.valdioveliu.valdio.audioplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.valdioveliu.valdio.audioplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.valdioveliu.valdio.audioplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.valdioveliu.valdio.audioplayer.ACTION_STOP";

    public static ExoPlayer exoPlayer;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private BandwidthMeter bandwidthMeter;
    private DefaultDataSourceFactory dataSourceFactory;
    private DefaultExtractorsFactory extractorsFactory;
    private ExtractorMediaSource mediaSource;
    private TrackSelector trackSelector;
    private LoadControl loadControl;
    private SimpleExoPlayer simpleExoPlayer;


    @Override
    public void onCreate() {
        startForeground(1,new Notification());
        super.onCreate();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Starts the streaming service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        trackSelector = new DefaultTrackSelector();
        loadControl = new DefaultLoadControl();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector, loadControl);
        // simpleExoPlayer = new SimpleExoPlayer(new DefaultRenderersFactory(getApplicationContext(), null),trackSelector,loadControl);
        settings = new Config(this);
        bandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        extractorsFactory = new DefaultExtractorsFactory();
        mediaSource = new ExtractorMediaSource(Uri.parse(settings.getRadioStreamURL()), dataSourceFactory, extractorsFactory, null, null);
        exoPlayer.prepare(mediaSource);


        if (!requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
       // } else if (intent.getBooleanExtra(START_PLAY, false)) {

        } else{// if (intent.getBooleanExtra(START_PLAY, false)) {

            play();

               //listenToCallStates();
        }

        return super.onStartCommand(intent, flags, startId);
        // return Service.START_STICKY;
    }

    /**
     * Starts radio URL stream
     */
    @SuppressLint("NewApi")
    public static boolean play() {

        //Check if player already streaming
        if (!isPlaying) {
            isPlaying = true;

            exoPlayer.setPlayWhenReady(true);
        }
        return true;
    }

    /**
     * Stops the stream if activity destroyed
     */
    @Override
    public void onDestroy() {
        stop();
        exoPlayer.release();
    }


    public static void pauseRadio() {
        exoPlayer.getPlaybackState();
        exoPlayer.setPlayWhenReady(false);
    }

    public static boolean resumeRadio() {
        exoPlayer.setPlayWhenReady(true);
        return true;

    }

    /**
     * Stops audio from the active service
     */
    public void stop() {
        if (isPlaying) {
            isPlaying = false;
            exoPlayer.setPlayWhenReady(false);

            stopForeground(true);
        }

        removeAudioFocus();
    }


    /**
     * Checks if there is a data or internet connection before starting the stream.
     * Displays Toast warning if there is no connection
     *
     * @return online status boolean
     */
    public boolean internetIsAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    private void listenToCallStates() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        stop();

                        ongoingCall = true;

                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.

                        if (ongoingCall) {
                            ongoingCall = false;

                            play();

                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }


    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = 0;
        if (audioManager != null) {
            result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(RadioMediaPlayerService.this);
    }

    @Override
    public void onAudioFocusChange(int focusState) {

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //Invoked when the audio focus of the system is updated.
        if (focusState == AudioManager.AUDIOFOCUS_GAIN) {// resume playback
            if (!play()) resumeRadio();
            else if (!play()) play();
            //radioPlayer.setVolume(1.0f, 1.0f);

        } else if (focusState == AudioManager.AUDIOFOCUS_LOSS) {
            // Lost focus for an unbounded amount of time: stop playback and release media player
            if (play()) pauseRadio();


        } else if (focusState == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {// Lost focus for a short time, but we have to stop
            // playback. We don't release the media player because playback
            // is likely to resume
            if (play()) pauseRadio();


        } else if (focusState == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {// Lost focus for a short time, but it's ok to keep playing
            // at an attenuated level


            //   if (radioPlayer.isPlaying()) radioPlayer.setVolume(0.1f, 0.1f);

        }


    }
}

