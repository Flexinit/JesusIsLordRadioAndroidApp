package businessregistration.lightspace.com.radioapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static businessregistration.lightspace.com.radioapp.Services.RadioMediaPlayerService.resumeRadio;

/**
 * Created by user on 9/3/2018.
 */

public class PlayReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

      //  Intent radioAudio = new Intent(context, RadioMediaPlayerService.class);
      //  radioAudio.putExtra(RadioMediaPlayerService.START_PLAY, true);
      //  context.startService(radioAudio);

         resumeRadio();
        //  play();

            /*Intent radioAudio = new Intent(context, RadioMediaPlayerService.class);
            intent.putExtra(RadioMediaPlayerService.START_PLAY, true);
            context.startService(radioAudio);
*/
        // RecordMain.buttonPlay.setEnabled(false);
        Toast.makeText(context, "Play Song", Toast.LENGTH_LONG).show();
    }


}
