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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import java.util.ArrayList;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    MoleStrikeDB db;
    private SharedPreferences prefs;
    String name;
    ConstraintLayout cl;
    int background;
    int userLevel = 1;
    RadioButton medium, hard;
    Music music;
    private ListView mListView;
    private ArrayList<String> mList;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MoleStrikeDB(this);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mList = b.getStringArrayList("list");
        System.out.println(mList.size());
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
        medium = (RadioButton) findViewById(R.id.medium);
        hard = (RadioButton) findViewById(R.id.hard);
        Toast.makeText(this, "Hello " + name, Toast.LENGTH_SHORT).show();
        Fabric.with(this, new Crashlytics());
        logUser(prefs);
        mListView = (ListView) findViewById(R.id.list);
        LeaderBoardAdapter listAdapter = new LeaderBoardAdapter(MainActivity.this, R.layout.leader_board, mList);
        mListView.setAdapter(listAdapter);
        button = (Button) findViewById(R.id.button);
        Typeface custom_font = Typeface.createFromAsset(this.getAssets(),  "fonts/njnaruto.ttf");
        button.setTypeface(custom_font);
        button.setTextColor(Color.BLACK);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (intent.hasExtra("newLevel")) {
            //gameOver();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                togglePrefs(prefs);
            }
        }).run();
        cl.setBackgroundResource(background);
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
        System.out.println(c.getCount());
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
                System.out.println("Play");
            }
    }

    public void chooseLevel(View view){
        if (userLevel==2) {
            medium.setBackgroundResource(R.drawable.medium);
            medium.setEnabled(true);
        } else if (userLevel>2) {
            hard.setBackgroundResource(R.drawable.hard);
            hard.setEnabled(true);
        }
        final LevelDialog ldd=new LevelDialog(MainActivity.this);
        ldd.show();
        ldd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (ldd.levelChosen)
                    play(ldd.getLevel());
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (music.getMusicIsPlaying())
            music.setMusicIsPlaying(false);
    }

    @Override
    public void finish(){
        if (music.getMusicIsPlaying())
            music.setMusicIsPlaying(false);
    }

    public void play(int level) {
        int numberOfMoles = 6;
        switch (level){
            case 1:break;
            case 2:numberOfMoles = 9;
        }
        System.out.println(level);
        if (music.getMusicIsPlaying())
            music.pause();
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("numberOfMoles", numberOfMoles);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void settingsMenu(View view) {
        SettingsDialog cdd=new SettingsDialog(MainActivity.this, music);
        cdd.show();
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
        button.setText("Hide Scoreboard");
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imageclick));
        if(mListView.getVisibility() == View.GONE) {
            mListView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.leaderboard_animation_in));
            mListView.setVisibility(View.VISIBLE);
        }
        else{
            mListView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.leaderboard_animation_out));
            mListView.setVisibility(View.GONE);
        }
    }
}
