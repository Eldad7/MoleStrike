package corem.eldad.molestrike;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import java.util.ArrayList;
import io.fabric.sdk.android.Fabric;

/**
 * @author ©EldadC
 *
 * The main activity is the first screen, so I seperated a lot of views and data initialization to seperate threads
 * to ease on the main thread.
 * Toggle prefs is to set any shared preferences - Background,music,soundfx etc. and so it has to run on two occasions:
 * When resuming to app (hence - it's inside onResume) and when changing settings.
 * The settings, about and choose themes are all seperated dialogs, and they are initialized inside their own functions,
 * which are triggered by onClick.
 * getUpdatedList is started only when the flag dataChanged is on true, which is triggered or by the settings dialog
 * (changed your user name) or when you've reached a new high score and unlocked a new level
 */

public class MainActivity extends AppCompatActivity {
    MoleStrikeDB db;
    private SharedPreferences prefs;
    private String name;
    private ConstraintLayout cl;
    int background;
    int userLevel = 1;
    int score;
    private Music music;
    private ListView mListView;
    private Button button;
    private TextView topScore;
    private ArrayList<String> mList;
    public static boolean dataChanged = false;
    private LeaderBoardAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MoleStrikeDB(this);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mList = b.getStringArrayList("list");
        music = new Music(this.getBaseContext(), this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cl = (ConstraintLayout) findViewById(R.id.activity_main);
        name = prefs.getString("display_name", "Player1");
        new Thread(new Runnable() {
            @Override
            public void run() {
                pullFromDB();
            }
        }).start();
        Toast.makeText(this, "Hello " + name, Toast.LENGTH_SHORT).show();
        Fabric.with(this, new Crashlytics());
        logUser(prefs);
        mListView = (ListView) findViewById(R.id.list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                listAdapter = new LeaderBoardAdapter(MainActivity.this, R.layout.leader_board, mList);
                mListView.setAdapter(listAdapter);
                mListView.setAdapter(listAdapter);
            }
        }).run();
        button = (Button) findViewById(R.id.button);
        Typeface custom_font = Typeface.createFromAsset(this.getAssets(),  "fonts/njnaruto.ttf");
        button.setTypeface(custom_font);
        button.setTextColor(Color.WHITE);
        topScore = (TextView) findViewById(R.id.topScore);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (music.getMusicIsPlaying())
            music.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                togglePrefs(prefs);
                cl.setBackgroundResource(background);
            }
        }).run();

    }

    private void pullFromDB(){
        SQLiteDatabase dbHelper = db.getReadableDatabase();
        String whereClause = MoleStrikeDB.player.COLUMN_PLAYER+"=?";
        String [] whereArgs = {"1"};
        String[] projection = {
                MoleStrikeDB.player.COLUMN_NAME,
                MoleStrikeDB.player.COLUMN_EMAIL,
                MoleStrikeDB.player.COLUMN_TOP_SCORE,
                MoleStrikeDB.player.COLUMN_LEVEL,
                MoleStrikeDB.player.COLUMN_PLAYER
        };
        Cursor c = dbHelper.query(
                MoleStrikeDB.player.TABLE_NAME,                     // The table to query
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        c.moveToFirst();
        userLevel = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_LEVEL));
        score = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_TOP_SCORE));
        dbHelper.close();
    }

    private void togglePrefs(SharedPreferences prefs) {
        if (prefs.getBoolean("forestbackground", true))
            background = R.drawable.forestbackground;
        else
            background = R.drawable.desertbackground;
        if (prefs.getBoolean("music", true))
            if (!music.getMusicIsPlaying()){
                music.run();
                music.setMusicVolume(prefs.getFloat("music_volume", 1.0f));
            }
        if (dataChanged){
            getUpdatedList();
        }
        topScore.setText(String.format(getResources().getString(R.string.top), score));
    }

    public void chooseLevel(View view){
        final LevelDialog ldd=new LevelDialog(MainActivity.this);
        ldd.show();
        ldd.setLevels(userLevel);
        ldd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (ldd.getLevelChosen())
                    play(ldd.getLevel());
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (music.getMusicIsPlaying()) {
            music.pause();
        }
    }

    @Override
    public void finish(){
        if (music.getMusicIsPlaying())
            music.setMusicIsPlaying(false);
    }

    public void play(int level) {
        if (music.getMusicIsPlaying())
            music.pause();
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("top", score);
        bundle.putInt("level", level);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void settingsMenu(View view) {
        SettingsDialog cdd=new SettingsDialog(MainActivity.this, music);
        cdd.show();
        cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (dataChanged)
                    getUpdatedList();
            }
        });
    }

    public void themesDialog(View view) {
        ThemesDialog cdd=new ThemesDialog(MainActivity.this);
        cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onResume();
            }
        });
        cdd.show();
    }

    public void about(View view) {
        InfoDialog cdd=new InfoDialog(MainActivity.this);
        cdd.show();
    }

    private void logUser(SharedPreferences settings) {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserEmail(settings.getString("display_name", "Player1") + "@fabric.io");
        Crashlytics.setUserName(settings.getString("display_name", "Player1"));
    }

    public void showscoreboard(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imageclick));
        if(mListView.getVisibility() == View.GONE) {
            mListView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.leaderboard_animation_in));
            mListView.setVisibility(View.VISIBLE);
            button.setText(R.string.hsb);
        }
        else{
            mListView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.leaderboard_animation_out));
            mListView.setVisibility(View.GONE);
            button.setText(R.string.ssb);
        }
    }

    public void getUpdatedList() {
        mList.clear();
        String name;
        int level, _score;
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
            _score = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_TOP_SCORE));
            mList.add(i,name + "+" + String.valueOf(level) + "+" + String.valueOf(_score));
            if (name.equals(prefs.getString("display_name", "Player1"))) {
                score = _score;
                userLevel = level;
            }
            c.moveToNext();
        }
        dbHelper.close();
        ((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
        dataChanged = false;
    }
}
