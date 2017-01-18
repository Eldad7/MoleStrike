package corem.eldad.molestrike;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MoleStrikeDB db;
    private SharedPreferences prefs;
    String name;
    ConstraintLayout cl;
    int background;
    int userLevel = 1;
    RadioButton medium, hard;
    Music music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MoleStrikeDB(this);
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

    /*private void gameOver() {
        setContentView(R.layout.activity_game_over);
        Intent intent = getIntent();
        Bundle b=intent.getExtras();
        final boolean level = b.getBoolean("newLevel");
        final boolean newHighScore = b.getBoolean("highScore");
        final ImageView highScore = (ImageView) findViewById(R.id.highScore);
        final ImageView newLevel = (ImageView) findViewById(R.id.newLevel);
        final Context context = this;
        cl = (ConstraintLayout) findViewById(R.id.activity_game_over);
        togglePrefs(prefs);
        cl.setBackgroundResource(background);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (newHighScore) {
                    highScore.setVisibility(View.VISIBLE);
                    highScore.startAnimation(AnimationUtils.loadAnimation(context, R.anim.imageclick));
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (level){
                    newLevel.setVisibility(View.VISIBLE);
                    newLevel.startAnimation(AnimationUtils.loadAnimation(context, R.anim.imageclick));
                }
            }
        }).start();
    }
*/
    /*@Override
    public void onBackPressed(){
        setContentView(R.layout.activity_main);
        togglePrefs(prefs);
        cl = (ConstraintLayout) findViewById(R.layout.activity_main);
        cl.setBackgroundResource(background);
    }*/

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
}
