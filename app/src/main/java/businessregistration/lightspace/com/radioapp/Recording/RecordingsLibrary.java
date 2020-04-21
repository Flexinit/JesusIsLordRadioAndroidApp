package businessregistration.lightspace.com.radioapp.Recording;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


import businessregistration.lightspace.com.radioapp.R;
import businessregistration.lightspace.com.radioapp.Services.MediaPlayerService;
import businessregistration.lightspace.com.radioapp.main.TabbedActivityMain;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class RecordingsLibrary extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MusicRecyclerAdapter musicAdapter;

    private List<AudioModel> tempAudioList;

    private MediaPlayerService player;
    boolean serviceBound = false;
    ArrayList<AudioFilesList> audioFilesListList;
    private ArrayList<RecordingFiles> listContentArr;

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.radiostream.audioplayer.PlayNewAudio";
// Change to your package name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings_library);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //   loadAudioFiles();
//play the first audio in the ArrayList
        //   playAudio(audioFilesListList.get(0).getData());

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        musicAdapter = new MusicRecyclerAdapter(this, this);

        String path = Environment.getExternalStorageDirectory().toString()+"/JesusIsLord/";
        File f = new File(path);

       // File storageDir = new File(Environment.getExternalStorageDirectory(), "JesusIsLord");

        populateRecyclerViewValues(Environment.getExternalStorageDirectory().toString());

        changeStatusBarColor();
        //setSupportActionBar(toolbar);

        // playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");

        //  playAudio(7);
    }

    private void changeStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.israeli_blue));
        }
    }

    public void populateRecyclerViewValues(String fileName) {


        listContentArr = new ArrayList<>();
        FileList l = new FileList(fileName);

        if (l.getFileList().equals(null)) {

            Toast.makeText(this, R.string.noRecentRecord, Toast.LENGTH_LONG).show();

        } else {

            String[][] data = l.getFileList();


            for (int i = 0; i < data.length; i++) {

                RecordingFiles recordingFiles = new RecordingFiles();
                //Values are binded using set method of the POJO class
                recordingFiles.setFileName(data[i][0]);
                recordingFiles.setDetail(data[i][1]);
                recordingFiles.setFileImage(data[i][2]);

                listContentArr.add(recordingFiles);
            }

            //setting the data for adapter.
            musicAdapter.setListContent(listContentArr);
            recyclerView.setAdapter(musicAdapter);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (musicAdapter.goBack())
            moveTaskToBack(true);

        startActivity(new Intent(this, TabbedActivityMain.class));
        backwardAnimation(this);

    }

    // if file is clicked this method is called, it creates a new intent and open the activity related to the extension
    // of the file.
    public void playFile(String clickedFile, String type) {
        File file = new File(clickedFile);
        Intent target = new Intent(Intent.ACTION_VIEW);
        if (type.compareTo("pdf") == 0)
            target.setDataAndType(Uri.fromFile(file), "application/pdf");
        else if (type.compareTo("mp3") == 0)
            target.setDataAndType(Uri.fromFile(file), "audio/*");
        else if (type.compareTo("txt") == 0)
            target.setDataAndType(Uri.fromFile(file), "text/plain");
        else
            target.setDataAndType(Uri.fromFile(file), "image/*");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));

        } catch (ActivityNotFoundException e) {

            Toast.makeText(this, R.string.applicationNotfound, Toast.LENGTH_LONG).show();
            Log.d(RecordingsLibrary.class.getName(), e.toString());
            e.printStackTrace();

        }
    }


    public void backwardAnimation(Activity activity) {

        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    private void loadList(List<ApplicationInfo> apps) {

        Observable.fromIterable(apps)
                .subscribe(new Observer<ApplicationInfo>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApplicationInfo applicationInfo) {


                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }


                });
    }


    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(RecordingsLibrary.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    /*
        private void playAudio(int audioIndex) {
            //Check is service is active
            if (!serviceBound) {
                //Store Serializable audioFilesListList to SharedPreferences
                StorageUtil storage = new StorageUtil(getApplicationContext());
                storage.storeAudio(audioFilesListList);
                storage.storeAudioIndex(audioIndex);

                Intent playerIntent = new Intent(this, MediaPlayerService.class);
                startService(playerIntent);
                bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                //Store the new audioIndex to SharedPreferences
                StorageUtil storage = new StorageUtil(getApplicationContext());
                storage.storeAudioIndex(audioIndex);

                //Service is active
                //Send a broadcast to the service -> PLAY_NEW_AUDIO
                Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
                sendBroadcast(broadcastIntent);
            }
        }
    */
    private void loadAudioFiles() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioFilesListList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                // Save to audioFilesListList
                audioFilesListList.add(new AudioFilesList(data, title, album, artist));
            }
        }
        cursor.close();
    }

    public List<AudioModel> getAllAudioFromDevice(final Context context) {
        tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%utm%"}, null);

        if (c != null) {
            while (c.moveToNext()) {
                AudioModel audioModel = new AudioModel();
                String path = c.getString(0);
                String name = c.getString(1);
                String album = c.getString(2);
                String artist = c.getString(3);

                audioModel.setaName(name);
                audioModel.setaAlbum(album);
                audioModel.setaArtist(artist);
                audioModel.setaPath(path);


                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);
                ArrayList<String> recordingNames = new ArrayList<>();
                // recordingNames = audioModel.getaName();
            }
            c.close();
        }
        return tempAudioList;
    }
}


