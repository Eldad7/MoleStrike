package corem.eldad.molestrike;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

/**
 * Created by The Gate Keeper on 1/15/2017.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        if(!settings.getBoolean("active", true)) {
            editor.putBoolean("active", true);
            editor.putBoolean("forestbackground", true);
            editor.putBoolean("desertbackground", false);
            editor.putBoolean("music", true);
            editor.putFloat("music_volume", 1);
            editor.putBoolean("soundfx", true);
            editor.putFloat("soundfx_volume", 1);
            editor.apply();
        }
        if (!settings.getString("display_name", "0").equals("Player1")) {
            editor.putString("display_name", "Player1"); // value to store
            editor.apply();
        } else if ((!settings.getString("display_name" , "0").equals("Player1")) && (!!settings.getString("display_name" , "0").equals("0"))) {}
        else{
            editor.putString("display_name", "Player1"); // value to store
            editor.apply();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}
