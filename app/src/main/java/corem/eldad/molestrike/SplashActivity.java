package corem.eldad.molestrike;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by The Gate Keeper on 1/15/2017.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        if(!(settings.getBoolean("active", false))) {
            System.out.println("in here");
            editor.putBoolean("active", true);
            editor.putBoolean("forestbackground", true);
            editor.putBoolean("desertbackground", false);
            editor.putBoolean("music", true);
            editor.putFloat("music_volume", 1.0f);
            editor.putBoolean("soundfx", true);
            editor.putFloat("soundfx_volume", 1.0f);
            editor.putString("display_name", "Player1");
            editor.apply();
            createUsers();
            System.out.println("First time!");
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

    private void createUsers() {
        MoleStrikeDB db = new MoleStrikeDB(this);
        final SQLiteDatabase dbHelper = db.getWritableDatabase();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String names[] = new String[]{"John", "David", "Yaniv", "Tal", "Ben", "Jenny", "Matt", "Oliver", "Amy", "Samuel"};
        ContentValues values = new ContentValues();
        values.put(MoleStrikeDB.player.COLUMN_NAME, settings.getString("display_name", "Player1"));
        values.put(MoleStrikeDB.player.COLUMN_EMAIL, "");
        values.put(MoleStrikeDB.player.COLUMN_LEVEL, 1);
        values.put(MoleStrikeDB.player.COLUMN_TOP_SCORE, 0);
        values.put(MoleStrikeDB.player.COLUMN_PLAYER, 1);
        dbHelper.insert(MoleStrikeDB.player.TABLE_NAME, null, values);
        for (int i=0; i<10; i++){
            values.put(MoleStrikeDB.player.COLUMN_NAME, names[i]);
            values.put(MoleStrikeDB.player.COLUMN_EMAIL, "");
            if ((i+1)*10 < 40)
                values.put(MoleStrikeDB.player.COLUMN_LEVEL, 1);
            else if (((i+1)*10 < 80) && ((i+1)*10 > 30))
                values.put(MoleStrikeDB.player.COLUMN_LEVEL, 2);
            else
                values.put(MoleStrikeDB.player.COLUMN_LEVEL, 3);
            values.put(MoleStrikeDB.player.COLUMN_TOP_SCORE, (i+1)*10);
            values.put(MoleStrikeDB.player.COLUMN_PLAYER, 0);
            dbHelper.insert(MoleStrikeDB.player.TABLE_NAME, null, values);
        }

        dbHelper.close();
    }
}
