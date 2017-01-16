package corem.eldad.molestrike;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MoleStrikeDB db;
    private SharedPreferences prefs;
    boolean firstRun;
    String name;
    ConstraintLayout cl;
    int background;
    int userLevel = 1;
    RadioButton medium, hard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MoleStrikeDB(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        name = prefs.getString("display_name", "0");
        cl = (ConstraintLayout) findViewById(R.id.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                togglePrefs(prefs);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                pullFromDB();
            }
        }).start();
        if (firstRun)
            createUser();
        else
            Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
        medium = (RadioButton) findViewById(R.id.medium);
        hard = (RadioButton) findViewById(R.id.hard);
    }

    private void createUser() {
        final SQLiteDatabase dbHelper = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MoleStrikeDB.player.COLUMN_NAME, prefs.getString("display_name", "0"));
        values.put(MoleStrikeDB.player.COLUMN_EMAIL, "");
        values.put(MoleStrikeDB.player.COLUMN_LEVEL, 1);
        values.put(MoleStrikeDB.player.COLUMN_TOP_SCORE, 0);
        dbHelper.insert(MoleStrikeDB.player.TABLE_NAME, null, values);
        Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        cl.setBackgroundResource(background);
        new Thread(new Runnable() {
            @Override
            public void run() {
                togglePrefs(prefs);
            }
        }).start();
    }

    private void pullFromDB(){
        SQLiteDatabase dbHelper = db.getReadableDatabase();
        String[] projection = {
                MoleStrikeDB.player.COLUMN_NAME,
                MoleStrikeDB.player.COLUMN_EMAIL,
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
                null
        );
        c.moveToFirst();
        if (c.getCount()==0)
            firstRun=true;
        else{
            firstRun=false;
            userLevel = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_LEVEL));
        }
        dbHelper.close();
    }

    private void togglePrefs(SharedPreferences prefs) {
        String newName = prefs.getString("display_name", "0");
        if (!(newName.equals(name))) {
            final SQLiteDatabase dbHelper = db.getWritableDatabase();
            db.updateName(newName, name, dbHelper);
            name = newName;
            dbHelper.close();
        }
        if (prefs.getString("themes", "0").equals("forestbackground"))
            background = R.drawable.forestbackground;
        else if (prefs.getString("themes", "0").equals("desertbackground"))
            background = R.drawable.desertbackground;

    }

    public void chooseLevel(View view){
        ConstraintLayout level = (ConstraintLayout) findViewById(R.id.level);
        level.setVisibility(View.VISIBLE);
        if (userLevel==2) {
            medium.setBackgroundResource(R.drawable.medium);
            medium.setEnabled(true);
        } else if (userLevel>2) {
            hard.setBackgroundResource(R.drawable.hard);
            hard.setEnabled(true);
        }

        ImageButton button = (ImageButton) findViewById(R.id.imageButton);
        button.setVisibility(View.GONE);
    }

    public void play(View view) {
        int level = 0;
        int numberOfMoles = 6;
        System.out.println(view.getResources().getResourceName(view.getId()));
        if (view.getResources().getResourceName(view.getId()).equals("corem.eldad.molestrike:id/easy")) {
            level = R.id.activity_game;
            numberOfMoles = 6;
        }
        else if(view.getResources().getResourceName(view.getId()).equals("corem.eldad.molestrike:id/medium")) {
            level = R.id.activity_game_medium;
            numberOfMoles = 9;
        }
        System.out.println(level);
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putSerializable("music", sounds);
        bundle.putInt("level", level);
        bundle.putInt("numberOfMoles", numberOfMoles);
        bundle.putInt("background", background);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void settingsMenu(View view) {
        Intent intent = new Intent(getBaseContext(), Settings.class);
        startActivity(intent);
    }
}
