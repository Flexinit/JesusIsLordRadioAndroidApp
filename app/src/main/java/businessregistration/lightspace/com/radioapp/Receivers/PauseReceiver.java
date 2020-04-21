package businessregistration.lightspace.com.radioapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import businessregistration.lightspace.com.radioapp.Services.RadioMediaPlayerService;

import static businessregistration.lightspace.com.radioapp.Services.RadioMediaPlayerService.pauseRadio;

/**
 * Created by user on 9/4/2018.
 */

public class PauseReceiver extends BroadcastReceiver {

    private RadioMediaPlayerService radioMediaPlayerService;


    @Override
    public void onReceive(Context context, Intent intent) {

        //radioMediaPlayerService = new RadioMediaPlayerService();


       // Intent radioAudio = new Intent(context, RadioMediaPlayerService.class);
        //intent.putExtra(RadioMediaPlayerService.START_PLAY, true);
      //  context.stopService(radioAudio);
           pauseRadio();



        // RecordMain.buttonPlay.setEnabled(false);
        Toast.makeText(context, "Pausing radio", Toast.LENGTH_LONG).show();
    }
}

