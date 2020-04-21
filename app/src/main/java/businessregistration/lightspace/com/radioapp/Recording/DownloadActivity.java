package businessregistration.lightspace.com.radioapp.Recording;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;


import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import businessregistration.lightspace.com.radioapp.R;
import businessregistration.lightspace.com.radioapp.main.TabbedActivityMain;


public class DownloadActivity extends Activity {
    private static String youtubeLink = null;

    private LinearLayout mainLayout;
    private ProgressBar mainProgressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_download);

        mainLayout = findViewById(R.id.main_layout);
        mainProgressBar = findViewById(R.id.prgrBar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        changeStatusBarColor();

        //  setSupportActionBar(toolbar)

        Intent getLink = getIntent();
        youtubeLink = getLink.getStringExtra("link");

        if (youtubeLink != null) {
            Toast.makeText(this, "Youtube download starting...", Toast.LENGTH_LONG).show();
            getYoutubeDownloadUrl(youtubeLink);

        } else {

            Toast.makeText(this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
          //  finish();
        }
    }


    @SuppressLint("StaticFieldLeak")

    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                mainProgressBar.setVisibility(View.GONE);

                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    Toast.makeText(DownloadActivity.this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
                    // finish();
                    return;
                }
                // Iterate over itags
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        addButtonToMainLayout(vMeta.getTitle(), ytFile);
                    }
                }
            }
        }.extract(youtubeLink, true, false);
    }

    private void changeStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.israeli_blue));
        }
    }

    private void addButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
        // Display some buttons and let the user choose the format
        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
                ytfile.getFormat().getHeight() + "p";
        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
        Button btn = new Button(this);
        btn.setText(btnText);
        btn.setOnClickListener(v -> {
            String filename;
            if (videoTitle.length() > 55) {
                filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
            } else {
                filename = videoTitle + "." + ytfile.getFormat().getExt();
            }
            filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
            downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
            finish();
        });
        mainLayout.addView(btn);
    }

    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
        }

        Toast.makeText(DownloadActivity.this, "The Downloaded File has been saved in " + Environment.DIRECTORY_DOWNLOADS, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, TabbedActivityMain.class));
        backwardAnimation(this);
    }

    public void backwardAnimation(Activity activity) {

        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}


