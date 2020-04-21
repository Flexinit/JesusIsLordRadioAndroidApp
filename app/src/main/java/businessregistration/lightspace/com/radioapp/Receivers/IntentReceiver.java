package businessregistration.lightspace.com.radioapp.Receivers;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.media.AudioManager;

import businessregistration.lightspace.com.radioapp.Services.RadioMediaPlayerService;

public class IntentReceiver extends BroadcastReceiver {

    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(
                AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Intent myIntent = new Intent(ctx, RadioMediaPlayerService.class);
            ctx.stopService(myIntent);
        }
    }
}
