package corem.eldad.molestrike;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by eldadc on 20/12/2016.
 */

public class GameActivity extends AppCompatActivity {
    ImageView current;
    ImageView[] characters;
    long timer;
    static boolean lose;
    Timer counter;
    int j=0,numberOfMoles, background, top=0;
    ImageView countDownView, pausePlay;
    TextView count, topScore;
    AnimationDrawable moleAnimation;
    boolean started, resumed = false, firstTime = true, hitSound;
    private Music music;
    CountDownTimer countDown;
    private SharedPreferences prefs;
    ConstraintLayout cl;
    MoleStrikeDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = new MoleStrikeDB(this);
        music = new Music(this.getBaseContext(), this);
        setContentView(R.layout.activity_game);
        cl = (ConstraintLayout) findViewById(R.id.activity_game);
        Intent intent = getIntent();
        Bundle b=intent.getExtras();
        numberOfMoles = b.getInt("numberOfMoles");
        setTopScore();
        initViews(numberOfMoles);
        pausePlay = (ImageView) findViewById(R.id.pause);
        count = (TextView) findViewById(R.id.count);
        count.setText("Score: " + String.valueOf(j));
        countDownView = (ImageView) findViewById(R.id.countDownView);
        for (int i=0; i<numberOfMoles; i++)
            characters[i].setBackgroundResource(R.drawable.jmole1);
        timer=2500;
        lose = false;
        final int three = R.drawable.number3;
        final int two = R.drawable.number2;
        final int one = R.drawable.number1;
        countDown = new CountDownTimer(5000,1000){
            @Override
            public void onTick(long l) {
                System.out.println(l/1000);
                if (l/1000 == 3){
                    countDownView.setImageResource(three);
                    countDownView.setVisibility(View.VISIBLE);
                }
                else if (l/1000 == 2){
                    countDownView.setImageResource(two);
                }
                else if (l/1000 == 1){
                    countDownView.setImageResource(one);
                }
            }

            @Override
            public void onFinish() {
                countDownView.setVisibility(View.GONE);
                play();
            }
        }.start();
    }

    private void setTopScore(){
        SQLiteDatabase dbHelper = db.getWritableDatabase();
        if ((j==top) && (j==0)) {
            String whereClause = MoleStrikeDB.player.COLUMN_PLAYER + "=?";
            String[] whereArgs = {"1"};
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
            top = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_TOP_SCORE));
            System.out.println("top - " + top);
        }
        else if (j>top){
            db.updateTopScore(prefs.getString("display_name", "Player1"), j, dbHelper);
            if ((j < 80) && (j > 29))
                db.updateLevel(prefs.getString("display_name", "Player1"), 2, dbHelper);
            else if (j>79)
                db.updateLevel(prefs.getString("display_name", "Player1"), 3, dbHelper);
            else
                db.updateLevel(prefs.getString("display_name", "Player1"), 1, dbHelper);
            top=j;
        }
        dbHelper.close();
        topScore = (TextView) findViewById(R.id.topScore);
        topScore.setText("Top: " + String.valueOf(top));
    }

    private void initViews(int moles) {
        ImageView topLeft = (ImageView) findViewById(R.id.topLeftMole);
        ImageView bottomLeft = (ImageView) findViewById(R.id.bottomLeftMole);
        ImageView topMiddle = (ImageView) findViewById(R.id.topRightMole);
        ImageView middleMole = (ImageView) findViewById(R.id.MiddleMole);
        ImageView topRight = (ImageView) findViewById(R.id.topMiddleMole);
        ImageView bottomRight = (ImageView) findViewById(R.id.bottomRightMole);
        if (moles==9){
            ImageView leftMiddle = (ImageView) findViewById(R.id.leftMiddleMole);
            ImageView bottomMiddleMole = (ImageView) findViewById(R.id.bottomMiddleMole);
            ImageView rightMiddleMole = (ImageView) findViewById(R.id.rightMiddleMole);
            characters = new ImageView[] {topLeft, bottomLeft, topMiddle, middleMole, topRight, bottomRight, leftMiddle, bottomMiddleMole, rightMiddleMole};
        }
        else
            characters = new ImageView[] {topLeft, bottomLeft, topMiddle, middleMole, topRight, bottomRight};
    }

    @Override
    protected void onResume(){
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                togglePrefs(prefs);
            }
        }).run();
        cl.setBackgroundResource(background);
        if ((resumed) && (current==null))
            countDown.start();
        else if (resumed)
            play();
        resumed = false;
    }

    private void togglePrefs(SharedPreferences prefs) {
        System.out.println(prefs.getBoolean("soundfx", true));
        if (prefs.getBoolean("music", true)) {
            if (!music.musicIsPlaying) {
                music.run();
                music.setMusicVolume(100 * prefs.getFloat("music_volume", 1.0f));
            }
        }
        else{
            if (music.musicIsPlaying)
                music.pause();
        }
        if (prefs.getBoolean("soundfx", true)) {
            music.setFXVolume(100 * prefs.getFloat("soundfx_volume", 1.0f));
            hitSound = true;
        }

        else
            hitSound = false;

        if (prefs.getBoolean("forestbackground", true))
            background = R.drawable.forestbackground;
        else
            background = R.drawable.desertbackground;
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (counter != null)
            counter.cancel();
        for (int i=0; i<6; i++){
                characters[i].setImageResource(android.R.color.transparent);
                characters[i].setBackgroundResource(R.drawable.jmole1);
        }
    }

    private void play() {
        started=true;
        int random = (int )(Math.random() * 500);
        if (!lose) {
            current = characters[random % numberOfMoles];
            current.setBackgroundResource(R.drawable.goingup);
            startTransition(current);
        }
    }

    private void startTransition(ImageView current) {
        System.out.println("Transitioning");
        moleAnimation = (AnimationDrawable) current.getBackground();
        moleAnimation.start();
        current.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (started) {
                    if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound!=false))
                        music.playHit();
                    if (event.getAction() == MotionEvent.ACTION_UP)
                        GameOn(counter);
                    return true;
                }
                return false;
            }
        });
        if (firstTime) {
            firstTime = false;
            counter = new Timer(timer, 1000);
            counter.start();
        }
        else{
            System.out.println(timer);
            counter.setMillisInFuture(timer);
            counter.start();
        }
            counter.setClicked(false);
    }

    private void GameOn(Timer counter) {
        if (!counter.getClicked()) {
            System.out.println("Lose = " + String.valueOf(lose));
            current.setImageResource(R.drawable.pow);
            current.setBackgroundResource(R.drawable.goingdown);
            current.setOnTouchListener(null);
            moleAnimation = (AnimationDrawable) current.getBackground();
            moleAnimation.start();
            if ((timer > 800) && (j%3==0))
                timer -= 100;
            counter.setClicked(true);
            count.setText("Score: " + String.valueOf(++j));
            if (j>top)
                topScore.setText("New top!");
        }
    }

    public void pause(View view) {
        pausePlay.setBackgroundResource(R.drawable.play);
        resumed = true;
        if (current != null)
            current.setOnTouchListener(null);
        else if (counter != null)
            counter.cancel();
        else{
            countDown.cancel();
        }
        countDownView.setVisibility(View.GONE);
        InGameDialog igd = new InGameDialog(this,music);
        igd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onResume();
            }
        });
        igd.show();
        onStop();
    }

    private class Timer extends MyCountDownTimer {
        boolean clicked = false;
        boolean finished = false;
        long mMillisInFuture;

        Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            mMillisInFuture = millisInFuture;
        }

        public void setMillisInFuture(long millisInFuture) {
            super.setMillisInFuture(millisInFuture);
            mMillisInFuture = millisInFuture;
        }

        void setClicked(boolean clicked){
            System.out.println("Clicked = "+clicked);
            this.clicked = clicked;
        }

        boolean getClicked(){
            return clicked;
        }

        public void onTick(long millisUntilFinished) {

        }
        public void onFinish() {
            if ((!clicked) && (!resumed)) {
                lose = true;
                setClicked(true);
                current.setBackgroundResource(R.drawable.goingdown);
                moleAnimation = (AnimationDrawable) current.getBackground();
                moleAnimation.start();
                countDownView.setImageResource(R.drawable.gameover);
                countDownView.setVisibility(View.VISIBLE);
                current.setImageResource(android.R.color.transparent);
                current.setOnTouchListener(null);
                setTopScore();
            }
            else{
                current.setImageResource(android.R.color.transparent);
                play();
            }
            finished = true;
        }
    }
}
