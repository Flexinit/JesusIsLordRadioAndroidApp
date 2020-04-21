package businessregistration.lightspace.com.radioapp.Services;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;


/**
 * Created by user on 8/6/2018.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String REG_TOKEN = "REG_TOKEN";

    @Override
    public void onTokenRefresh() {
      //  super.onTokenRefresh();

        String recent_token = FirebaseInstanceId.getInstance().getToken();
        storeRegIdToSharedPreferences(recent_token);
//        Toast.makeText(getApplicationContext(),"REG_TOKEN"+ recent_token, Toast.LENGTH_LONG).show();
        Log.e(REG_TOKEN, recent_token);

        sendRegIdToServer(recent_token);
/*
        Intent registrationComp = new Intent("registrationComp");
        registrationComp.putExtra("token", recent_token);

        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComp);

        */
    }

    private void sendRegIdToServer(final String token){

        Log.e("TAG","SendRegiistrationToServer: "+token);

    }

    private void storeRegIdToSharedPreferences(String token){
        SharedPreferences tokenPref = getApplicationContext().getSharedPreferences("tokenPref", 0);
        SharedPreferences.Editor editor = tokenPref.edit();
        editor.putString("RegID", token);
        editor.apply();

    }
}
 