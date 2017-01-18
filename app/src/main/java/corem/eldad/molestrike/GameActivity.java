package corem.eldad.molestrike;

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
    ImageView current, devil, angel;
    ImageView[] characters;
    long timer, devilTimer,angelTimer;
    static boolean lose=false;
    Timer counter;
    DevilTimer dcounter;
    AngelTimer acounter;
    int j=0,numberOfMoles, background, top=0;
    ImageView countDownView, pausePlay;
    TextView count, topScore;
    AnimationDrawable moleAnimation;
    boolean started, resumed = false, firstTime = true, devilFirstTime = true, angelFirstTime = true, hitSound;
    private Music music;
    CountDownTimer countDown;
    private SharedPreferences prefs;
    ConstraintLayout cl;
    MoleStrikeDB db;
    int level;

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
        devilTimer=angelTimer=timer=2500;
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
        boolean newHigh = false;
        boolean newLevel = false;
        if (lose) {
            try {
                dcounter.cancel();
                acounter.cancel();
                counter.cancel();
            } catch (NullPointerException ignored){

            }
        }
        SQLiteDatabase dbHelper = db.getWritableDatabase();
        if ((j==top) && (j==0)) {
            String whereClause = MoleStrikeDB.player.COLUMN_PLAYER + "=?";
            String[] whereArgs = {"1"};
            String[] projection = {
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
            level = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_LEVEL));
        }
        else if (j>top){
            newHigh = true;
            db.updateTopScore(prefs.getString("display_name", "Player1"), j, dbHelper);
            if ((j < 80) && (j > 29))
                if (level<2) {
                    db.updateLevel(prefs.getString("display_name", "Player1"), 2, dbHelper);
                    newLevel = true;
                }
            else if (j>79)
                    if (level<3) {
                        db.updateLevel(prefs.getString("display_name", "Player1"), 3, dbHelper);
                        newLevel = true;
                    }
            top=j;
        }
        dbHelper.close();
        topScore = (TextView) findViewById(R.id.topScore);
        topScore.setText("Top: " + String.valueOf(top));
        if (lose){
            Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("numberOfMoles", numberOfMoles);
            bundle.putBoolean("highScore", newHigh);
            bundle.putBoolean("newLevel",newLevel);
            intent.putExtras(bundle);
            startActivity(intent);
            if (music.getMusicIsPlaying())
                music.pause();
            finish();
        }

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
            while ((devil == current) || (current==angel))
                current = characters[random % numberOfMoles];
            current.setBackgroundResource(R.drawable.goingup);
            startTransition(current, 1);
        }
    }

    private void devilPlay(){
        int random = (int )(Math.random() * 500);
        if (!lose) {
            devil = characters[random % numberOfMoles];
            while ((devil == current) || (devil==angel))
                devil = characters[random % numberOfMoles];
            devil.setBackgroundResource(R.drawable.goingupdevil);
            startTransition(devil, 2);
        }
    }

    private void AngelPlay(){
        int random = (int )(Math.random() * 500);
        if (!lose) {
            angel = characters[random % numberOfMoles];
            while ((angel == current) || (devil==angel))
                angel = characters[random % numberOfMoles];
            angel.setBackgroundResource(R.drawable.goingupangel);
            startTransition(angel, 3);
        }
    }

    private void startTransition(ImageView current, int type) {
        System.out.println("Transitioning");
        moleAnimation = (AnimationDrawable) current.getBackground();
        moleAnimation.start();
        switch (type){
            case 1: {
                System.out.println("Case 1");
                current.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (started) {
                            if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound))
                                music.playHit();
                            if (event.getAction() == MotionEvent.ACTION_UP)
                                GameOn(1);
                            return true;
                        }
                        return false;
                    }
                });
                if (firstTime) {
                    firstTime = false;
                    counter = new Timer(timer, 1000);

                } else
                    counter.setMillisInFuture(timer);
                counter.setClicked(false);
                counter.start();
                break;
            }
            case 2: {
                current.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (started) {
                            if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound))
                                music.playHit();
                            if (event.getAction() == MotionEvent.ACTION_UP)
                                GameOn(2);
                            return true;
                        }
                        return false;
                    }
                });
                if (firstTime) {
                    firstTime = false;
                    dcounter = new DevilTimer(timer, 1000);
                    dcounter.resetHits();
                } else {
                    dcounter.setMillisInFuture(timer);
                }
                dcounter.start();
                break;
            }
            case 3:{
                current.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (started) {
                            if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound))
                                music.playHit();
                            if (event.getAction() == MotionEvent.ACTION_UP)
                                GameOn(3);
                            return true;
                        }
                        return false;
                    }
                });
                if (firstTime) {
                    firstTime = false;
                    acounter = new AngelTimer(timer, 1000);
                } else
                    acounter.setMillisInFuture(timer);
                acounter.start();
                break;
            }
        }
    }

    private void GameOn(int type) {
        switch (type){
            case 1: {
                if (!counter.getClicked()) {
                    current.setImageResource(R.drawable.pow);
                    current.setBackgroundResource(R.drawable.goingdown);
                    current.setOnTouchListener(null);
                    moleAnimation = (AnimationDrawable) current.getBackground();
                    moleAnimation.start();
                    if ((timer > 800) && (j % 3 == 0))
                        timer -= 100;
                    counter.setClicked(true);
                    count.setText("Score: " + String.valueOf(++j));
                }
            }
                break;
            case 2: {
                if (dcounter.getClicked()) {
                    devil.setImageResource(R.drawable.pow);
                    devil.setBackgroundResource(R.drawable.goingdowndevil);
                    devil.setOnTouchListener(null);
                    moleAnimation = (AnimationDrawable) devil.getBackground();
                    moleAnimation.start();
                    if (devilTimer > 800)
                        devilTimer -= 100;
                } else {
                    dcounter.setClicked();
                }
                count.setText("Score: " + String.valueOf(++j+4));
                break;
            }
            case 3:
                if (!acounter.getClicked()) {
                    angel.setBackgroundResource(R.drawable.goingdownangel);
                    angel.setOnTouchListener(null);
                    moleAnimation = (AnimationDrawable) angel.getBackground();
                    moleAnimation.start();
                    acounter.setClicked(true);
                }
                break;
        }
            if (j>top)
                topScore.setText("New top!");
            if (hitSound)
                music.loadHit();
    }

    public void pause(View view) {
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
                System.out.println("Lose");
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
                System.out.println("Still playing");
                play();
            }
            finished = true;
        }
    }

    private class DevilTimer extends MyCountDownTimer {
        boolean clicked = false;
        boolean finished = false;
        long mMillisInFuture;
        int hits=0;

        DevilTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            mMillisInFuture = millisInFuture;
        }

        public void setMillisInFuture(long millisInFuture) {
            super.setMillisInFuture(millisInFuture);
            mMillisInFuture = millisInFuture;
        }

        void setClicked(){
            if (++hits==3)
                this.clicked = true;
        }

        boolean getClicked(){
            return clicked;
        }

        public void onTick(long millisUntilFinished) {

        }
        public void onFinish() {
            if ((!clicked) && (!resumed)) {
                lose = true;
                hits=3;
                setClicked();
                devil.setBackgroundResource(R.drawable.goingdowndevil);
                moleAnimation = (AnimationDrawable) devil.getBackground();
                moleAnimation.start();
                countDownView.setImageResource(R.drawable.gameover);
                countDownView.setVisibility(View.VISIBLE);
                devil.setImageResource(android.R.color.transparent);
                devil.setOnTouchListener(null);
                setTopScore();
            }
            else{
                devil.setImageResource(android.R.color.transparent);
                play();
            }
            finished = true;
        }

        public void resetHits() {
            this.hits=0;
        }
    }

    private class AngelTimer extends MyCountDownTimer {
        boolean clicked = false;
        boolean finished = false;
        long mMillisInFuture;

        AngelTimer(long millisInFuture, long countDownInterval) {
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
            if ((clicked) && (!resumed)) {
                lose = true;
                setClicked(true);
                angel.setBackgroundResource(R.drawable.goingdownangel);
                moleAnimation = (AnimationDrawable) angel.getBackground();
                moleAnimation.start();
                countDownView.setImageResource(R.drawable.gameover);
                countDownView.setVisibility(View.VISIBLE);
                angel.setImageResource(android.R.color.transparent);
                angel.setOnTouchListener(null);
                setTopScore();
            }
            else{
                angel.setImageResource(android.R.color.transparent);
                play();
            }
            finished = true;
        }
    }
}
