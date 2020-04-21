/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package businessregistration.lightspace.com.radioapp.Recording;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import businessregistration.lightspace.com.radioapp.Configuration.Config;
import businessregistration.lightspace.com.radioapp.R;

public class StreamActivity extends Service {


    private RxPermissions mPermissions;
    private Thread recordingThread;

    private Config configuration;
    private long bytesRead;
    private volatile boolean isDownloading;
    private InputStream inputStream;
    private FileOutputStream fileOutputStream;

    public static String START_REC;
    private File myFile;
    private String file = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(StreamActivity.class.getName(), "Recording started");

        // file = intent.getStringExtra("file");

        if (intent.getBooleanExtra(START_REC, false) && intent.getStringExtra("file") != null) {

            String fileName = intent.getStringExtra("file");
            Log.e(StreamActivity.class.getName(),fileName);
            myFile = new File(fileName);

            getStream(myFile);
        }

        return Service.START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public Thread getStream(File fileOut) {

        Toast.makeText(this, R.string.startRecording, Toast.LENGTH_LONG).show();

        Runnable runnable = () -> {

            try {

                configuration = new Config(StreamActivity.this);

                System.setProperty("http.keepAlive", "false");
                URL url = new URL(configuration.getRadioStreamURL());
                inputStream = url.openStream();

                Log.e(StreamActivity.class.getName(), "url.openStream()");

               // fileOutputStream = new FileOutputStream(fileOut, true);

                FileUtils.copyURLToFile(url,fileOut);

                Log.e(StreamActivity.class.getName(), "FileOutputStream: " + fileOut);
/*
                int c;

                while ((c = inputStream.read()) != -1) {

                    Log.e(StreamActivity.class.getName(), "bytesRead=" + bytesRead);
                    fileOutputStream.write(c);
                    bytesRead++;
*/
                    sendUpdate(FileUtils.byteCountToDisplaySize(3));


            } catch (IOException e) {

                e.printStackTrace();
            }

        };

        //  try {

        recordingThread = new Thread(runnable);
        recordingThread.start();

        //  throw new InterruptedException();

        // } catch (InterruptedException e) {
        //      e.printStackTrace();
        //   }

        return recordingThread;
    }


    private void stopRecording() {

        //4 getStream(myFile).interrupt();
    }


    /*
        private RxPermissions getRxPermissions() {
            if (mPermissions == null) {
                mPermissions = new RxPermissions(StreamActivity.this);
            }
            return mPermissions;
        }
        */
    public void playNotificationSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getMyFile(Intent intent) {

        file = intent.getStringExtra("file");

        if (file != null) {

            myFile = new File(file);

            SharedPreferences sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("file", file);
            editor.apply();

        } else {

            SharedPreferences sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);
            String fileName = sharedPref.getString("file", null);

            if (fileName != null) {
                myFile = new File(fileName);
            }
        }

        return myFile;
    }

    private void sendUpdate(String downloadProgress) {

        Intent intent = new Intent("DownloadUpdate");
        intent.putExtra("BytesRead", downloadProgress);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stopSelf();
        Toast.makeText(this, R.string.stopRecording, Toast.LENGTH_LONG).show();
        Log.e(StreamActivity.class.getName(), "Recording Stopped");
        //   stopRecording();
    }

}
