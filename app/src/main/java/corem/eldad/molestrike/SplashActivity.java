package corem.eldad.molestrike;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.settings.Settings;

/**
 * Created by The Gate Keeper on 1/15/2017.
 */
public class SplashActivity extends Activity {

    int level, score;
    String name;
    ArrayList list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        logUser(settings);
        if(!(settings.getBoolean("active", false))) {
            editor.putBoolean("active", true);
            editor.putString("display_name", "Player1");
            editor.putBoolean("forestbackground", true);
            editor.putBoolean("desertbackground", false);
            editor.putBoolean("music", true);
            editor.putFloat("music_volume", 1.0f);
            editor.putBoolean("soundfx", true);
            editor.putFloat("soundfx_volume", 1.0f);
            editor.putBoolean("first_time", true);
            editor.putBoolean("first_time_devil", true);
            editor.putBoolean("first_time_angel", true);
            editor.putBoolean("first_time_rabbit", true);
            editor.apply();
            createUsers();
        }
        list = new ArrayList();
        getUsers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putStringArrayList("list",list);
                intent.putExtras(b);
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

    private void logUser(SharedPreferences settings) {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserEmail(settings.getString("display_name", "Player1") + "@fabric.io");
        Crashlytics.setUserName(settings.getString("display_name", "Player1"));
    }

    public void getUsers() {
        MoleStrikeDB db = new MoleStrikeDB(this);
        SQLiteDatabase dbHelper = db.getReadableDatabase();
        String[] projection = {
                MoleStrikeDB.player.COLUMN_NAME,
                MoleStrikeDB.player.COLUMN_TOP_SCORE,
                MoleStrikeDB.player.COLUMN_LEVEL
        };
        Cursor c = dbHelper.query(
                MoleStrikeDB.player.TABLE_NAME,                     // The table to query
                projection,
                null,
                null,
                null,
                null,
                "topScore DESC"
        );
        c.moveToFirst();
        for (int i=0; i<c.getCount(); i++){
            name = c.getString(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_NAME));
            level = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_LEVEL));
            score = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_TOP_SCORE));
            list.add(i,name + "+" + String.valueOf(level) + "+" + String.valueOf(score));
            c.moveToNext();
        }
        dbHelper.close();
    }
}
