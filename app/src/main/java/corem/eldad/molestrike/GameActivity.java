package corem.eldad.molestrike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by eldadc on 20/12/2016.
 *
 * This activity is the main game activity. the function Play is the beginning of each round.
 * setTransition assigns the animation to the correct view and starts the counter
 * gameOn is the function that is calls when the timer is over. it checks if you have clicked the mole or not.
 * If you didn't - you lose a life.
 * setTopScore actually sets the top score but also it is the one that fires the GameOverActivity call, in case you have lost.
 * The show functions (Mole\Devil) happens only the first time the user encounters a mole\devilMole, to explain to him what to do
 */

public class GameActivity extends AppCompatActivity {
    private ImageView current, lifeView;
    private ImageView[] characters;
    long timer, devilTimer,angelTimer;
    boolean lose = false;
    private Timer counter;
    int life=3, level, j=0,numberOfMoles, background, top=0;
    private DevilTimer dcounter;
    private AngelTimer acounter;
    private RabbitTimer rabbitTimer;
    float rabbitLocations[];
    private ImageView countDownView;
    private TextView count, topScore, instructions;
    private AnimationDrawable moleAnimation;
    boolean started, resumed = false, firstTime = true, devilFirstTime = true, angelFirstTime = true,hitSound;
    boolean firstGame, firstDevil,firstAngel,firstRabbit;
    private Music music;
    private CountDownTimer countDown;
    private SharedPreferences prefs;
    private ConstraintLayout cl;
    private MoleStrikeDB db;
    private ViewGroup contentView;
    private Context mContext;
    private RabbitView rb;
    long tick = 500;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = getIntent();
        Bundle b=intent.getExtras();
        level = b.getInt("level");
        top = b.getInt("top");
        switch (level){
            case 2: {
                numberOfMoles = 8;
                setContentView(R.layout.activity_game_medium);
                cl = (ConstraintLayout) findViewById(R.id.activity_game_medium);
                break;
            }
            case 3:{
                numberOfMoles = 10;
                setContentView(R.layout.activity_game_hard);
                cl = (ConstraintLayout) findViewById(R.id.activity_game_hard);
                break;
            }
            default:
                numberOfMoles = 6;
                setContentView(R.layout.activity_game);
                cl = (ConstraintLayout) findViewById(R.id.activity_game);
        }
        System.out.println(level);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = new MoleStrikeDB(this);
        music = new Music(this.getBaseContext(), this);
        setTopScore();
        countDownView = (ImageView) findViewById(R.id.countDownView);
        lifeView = (ImageView) findViewById(R.id.life);
        contentView = cl;
        new Thread(new Runnable() {
            @Override
            public void run() {
                initViews(numberOfMoles);
                getFirstTimers();
                for (int i = 0; i < numberOfMoles; i++)
                    characters[i].setBackgroundResource(R.drawable.jmole1);
            }
        }).run();
        devilTimer=angelTimer=timer=2500;
        final int three = R.drawable.number3;
        final int two = R.drawable.number2;
        final int one = R.drawable.number1;
        countDown = new CountDownTimer(5000,1000){
            @Override
            public void onTick(long l) {
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
                int rand = (int) (Math.random() * 10000);
                rabbitTimer = new RabbitTimer(((long) rand % 120000),1000);
                rabbitTimer.start();
                Play();
            }
        }.start();
        Fabric.with(this, new Crashlytics());
        logUser(prefs);
    }

    private void getFirstTimers(){
        if(prefs.getBoolean("first_time", true))
            firstGame = true;
        if(prefs.getBoolean("first_time_devil", true))
            firstDevil = true;
        if(prefs.getBoolean("first_time_angel", true))
            firstAngel = true;
        if(prefs.getBoolean("first_time_rabbit", true))
            firstRabbit = true;
    }

    private void logUser(SharedPreferences settings) {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserEmail(settings.getString("display_name", "Player1") + "@fabric.io");
        Crashlytics.setUserName(settings.getString("display_name", "Player1"));
    }

    private void setTopScore() {
        boolean newHigh = false;
        boolean newLevel = false;
        if (!lose && life < 3 && life > 0)
            Play();
        if (life == 0)
            lose = true;
        if (lose) {
            try {
                counter.cancel();
                dcounter.cancel();
                acounter.cancel();
                rabbitTimer.cancel();
            } catch (NullPointerException ignored) {

            }
        }
        SQLiteDatabase dbHelper = db.getWritableDatabase();
        if (lose && j > top) {
            newHigh = true;
            db.updateTopScore(prefs.getString("display_name", "Player1"), j, dbHelper);
            if ((j < 80) && (j > 29))
                if (level < 2) {
                    db.updateLevel(prefs.getString("display_name", "Player1"), 2, dbHelper);
                    newLevel = true;
                } else if (j > 79)
                    if (level < 3) {
                        db.updateLevel(prefs.getString("display_name", "Player1"), 3, dbHelper);
                        newLevel = true;
                    }
            top = j;
        }
        dbHelper.close();

        if (lose) {
            BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals("finish_activity")) {
                        if (music.getMusicIsPlaying())
                            music.pause();
                        finish();
                        unregisterReceiver(this);
                    }
                }
            };
            Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("highScore", newHigh);
            bundle.putBoolean("newLevel", newLevel);
            rabbitTimer.cancel();
            intent.putExtras(bundle);
            registerReceiver(broadcast_receiver, new IntentFilter("finish_activity"));
            startActivity(intent);
        }
    }


    private void initViews(int moles) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        rabbitLocations = new float[2];
        float screenHeight = metrics.heightPixels;
        ImageView topLeft = (ImageView) findViewById(R.id.topLeftMole);
        ImageView bottomLeft = (ImageView) findViewById(R.id.bottomLeftMole);
        ImageView topMiddle = (ImageView) findViewById(R.id.topRightMole);
        ImageView middleMole = (ImageView) findViewById(R.id.MiddleMole);
        ImageView topRight = (ImageView) findViewById(R.id.topMiddleMole);
        ImageView bottomRight = (ImageView) findViewById(R.id.bottomRightMole);
        rabbitLocations[0] = screenHeight - 300;
        rabbitLocations[1] = screenHeight - 600;
        if (moles>7) {
            ImageView bottomMiddleMole = (ImageView) findViewById(R.id.bottomMiddleMole);
            ImageView rightMiddleMole = (ImageView) findViewById(R.id.rightMiddleMole);
            if (moles == 10) {
                ImageView hardTop = (ImageView) findViewById(R.id.hardTop);
                ImageView hardBottom = (ImageView) findViewById(R.id.hardBottom);
                characters = new ImageView[]{topLeft, bottomLeft, topMiddle, middleMole, topRight, bottomRight, bottomMiddleMole, rightMiddleMole, hardBottom, hardTop};
            }
            else
                characters = new ImageView[]{topLeft, bottomLeft, topMiddle, middleMole, topRight, bottomRight, bottomMiddleMole, rightMiddleMole};
        }
        else {
            characters = new ImageView[]{topLeft, bottomLeft, topMiddle, middleMole, topRight, bottomRight};
            instructions = (TextView) findViewById(R.id.instructions);
        }
        rb = new RabbitView(mContext);
        count = (TextView) findViewById(R.id.count);
        count.setText(String.format(getResources().getString(R.string.score), j));
        topScore = (TextView) findViewById(R.id.topScore);
        topScore.setText(String.format(getResources().getString(R.string.top), top));
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
        else if (resumed) {
            Play();
            rabbitTimer.start();
        }
        resumed = false;
    }

    private void togglePrefs(SharedPreferences prefs) {
        if (prefs.getBoolean("music", true)) {
            if (!music.musicIsPlaying) {
                music.run();
                music.setMusicVolume(prefs.getFloat("music_volume", 1.0f));
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
        if (dcounter != null)
            dcounter.cancel();
        if (acounter != null)
            acounter.cancel();
        if (rabbitTimer!=null)
            rabbitTimer.cancel();
        for (int i=0; i<numberOfMoles; i++){
                characters[i].setImageResource(android.R.color.transparent);
                characters[i].setBackgroundResource(R.drawable.jmole1);
        }
    }

    private void Play() {
        if (firstGame){
            showMole();
            return;
        }
        if (!lose){
            started=true;
            lifeSet(life);
            int type = 1;
            int random = (int )(Math.random() * 2000);
            int character = random;
            if (character%77 == 0)
                type = 3;

            if (character%66 == 0){
                type = 2;
            }
            current = characters[random % numberOfMoles];
            switch (type) {
                case 2: {
                    if (firstDevil){
                        showDevil(current);
                        return;
                    }
                    current.setBackgroundResource(R.drawable.goingupdevil);
                    break;
                }
                case 3: {
                    if (firstAngel){
                        instructions.setText(R.string.angelHit);
                        instructions.setTextColor(Color.WHITE);
                        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/njnaruto.ttf");
                        instructions.setTextSize(30.0f);
                        instructions.setTypeface(custom_font);
                        instructions.setVisibility(View.VISIBLE);
                    }
                    current.setBackgroundResource(R.drawable.goingupangel);
                    break;
                }
                default:
                    current.setBackgroundResource(R.drawable.goingup);
            }
            startTransition(current, type);
        }
    }

    private void showMole() {
        current = characters[0];
        current.setBackgroundResource(R.drawable.goingup);
        moleAnimation = (AnimationDrawable) current.getBackground();
        moleAnimation.start();
        current.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound)) {
                        music.playHit();
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        instructions.setText(R.string.great);
                        current.setBackgroundResource(R.drawable.goingdown);
                        current.setImageResource(R.drawable.pow);
                        current.setOnTouchListener(null);
                        moleAnimation = (AnimationDrawable) current.getBackground();
                        moleAnimation.start();
                        firstGame = false;
                        editor = prefs.edit();
                        editor.putBoolean("first_time", false);
                        editor.apply();
                        new CountDownTimer(2000,1000){

                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                instructions.setVisibility(View.GONE);
                                current.setImageResource(android.R.color.transparent);
                                count.setText(String.format(getResources().getString(R.string.score), ++j));
                                Play();
                            }
                        }.start();
                    }
                    return true;
                }
            });
        instructions.setText(R.string.moleHit);
        instructions.setTextColor(Color.WHITE);
        Typeface custom_font = Typeface.createFromAsset(this.getAssets(),  "fonts/njnaruto.ttf");
        instructions.setTextSize(30.0f);
        instructions.setTypeface(custom_font);
        instructions.setVisibility(View.VISIBLE);
    }

    private void showDevil(final ImageView current){
        current.setBackgroundResource(R.drawable.goingupdevil);
        moleAnimation = (AnimationDrawable) current.getBackground();
        moleAnimation.start();
        final int[] devilHit = {0};
        current.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound)) {
                    music.playHit();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ++devilHit[0];
                    if (devilHit[0] ==3 ) {
                        instructions.setText(R.string.great);
                        current.setBackgroundResource(R.drawable.goingdowndevil);
                        current.setImageResource(R.drawable.pow);
                        current.setOnTouchListener(null);
                        moleAnimation = (AnimationDrawable) current.getBackground();
                        moleAnimation.start();
                        firstDevil = false;
                        editor = prefs.edit();
                        editor.putBoolean("first_time_devil", false);
                        editor.apply();
                        new CountDownTimer(2000, 1000) {

                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                instructions.setVisibility(View.GONE);
                                current.setImageResource(android.R.color.transparent);
                                j += 5;
                                count.setText(String.format(getResources().getString(R.string.score), j));
                                Play();
                            }
                        }.start();
                    }
                }
                return true;
            }
        });
        counter = new Timer(timer, tick);
        instructions.setText(R.string.hitDevil);
        instructions.setTextColor(Color.WHITE);
        Typeface custom_font = Typeface.createFromAsset(this.getAssets(),  "fonts/njnaruto.ttf");
        instructions.setTextSize(30.0f);
        instructions.setTypeface(custom_font);
        instructions.setVisibility(View.VISIBLE);
    }

    private void startTransition(ImageView current, int type) {
        moleAnimation = (AnimationDrawable) current.getBackground();
        moleAnimation.start();
        switch (type){
            case 1: {
                current.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (started) {
                            if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound)) {
                                music.playHit();
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP)
                                gameOn(1);
                            return true;
                        }
                        return false;
                    }
                });
                if (firstTime) {
                    firstTime = false;
                    counter = new Timer(timer, tick);
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
                            if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound) && (!rb.getHit())) {
                                music.playHit();
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP)
                                gameOn(2);
                            return true;
                        }
                        return false;
                    }
                });
                if (devilFirstTime) {
                    devilFirstTime = false;
                    dcounter = new DevilTimer(timer, tick);
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
                            if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound) && (!rb.getHit())) {
                                music.playHit();
                                Vibrator vibrate = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrate.vibrate(500);
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP)
                                gameOn(3);
                            return true;
                        }
                        return false;
                    }
                });
                if (angelFirstTime) {
                    angelFirstTime = false;
                    acounter = new AngelTimer(timer, tick);
                } else
                    acounter.setMillisInFuture(timer);
                acounter.setClicked(false);
                acounter.start();
                break;
            }
        }
    }

    private void gameOn(int type) {
        switch (type){
            case 1: {
                if (!counter.getClicked() && !rb.getHit()) {
                    current.setImageResource(R.drawable.pow);
                    current.setBackgroundResource(R.drawable.goingdown);
                    current.setOnTouchListener(null);
                    moleAnimation = (AnimationDrawable) current.getBackground();
                    moleAnimation.start();
                    if ((timer > 800) && (j % 3 == 0))
                        timer -= 100;
                    counter.setClicked(true);
                    count.setText(String.format(getResources().getString(R.string.score), ++j));
                }
            }
                break;
            case 2: {
                    if (dcounter.getClicked() && !rb.getHit()) {
                        current.setBackgroundResource(R.drawable.goingdowndevil);
                        current.setImageResource(R.drawable.pow);
                        current.setOnTouchListener(null);
                        moleAnimation = (AnimationDrawable) current.getBackground();
                        moleAnimation.start();
                        if (devilTimer > 800)
                            devilTimer -= 100;
                        j+=5;
                        count.setText(String.format(getResources().getString(R.string.score), j));
                    }
                dcounter.setClicked();
                    break;
                }
            case 3: {
                if (firstAngel) {
                    instructions.setVisibility(View.GONE);
                    firstAngel = false;
                    editor = prefs.edit();
                    editor.putBoolean("first_time_angel", false);
                    editor.apply();
                }
                if (!rb.getHit()) {
                    current.setImageResource(R.drawable.pow);
                    current.setBackgroundResource(R.drawable.goingdownangel);
                    current.setOnTouchListener(null);
                    moleAnimation = (AnimationDrawable) current.getBackground();
                    moleAnimation.start();
                    acounter.setClicked(true);
                }
            }
        }
            if (j>top)
                topScore.setText(R.string.newTop);
            if (hitSound)
                music.loadHit();
    }

    private void lifeSet(int life){
        switch(life){
            case 2:{
                lifeView.setImageResource(R.drawable.ic_heart_2);
                break;
            }
            case 1:{
                lifeView.setImageResource(R.drawable.ic_heart_1);
                break;
            }
            case 0:{
                lifeView.setImageResource(R.drawable.ic_heart_0);
                break;
            }
        }
    }

    @Override
    public void onBackPressed(){
        try {
            countDown.cancel();
            rabbitTimer.cancel();
            dcounter.cancel();
            acounter.cancel();
            counter.cancel();
        } catch (NullPointerException ignored) {

        }
        if (music.getMusicIsPlaying())
            music.pause();
        finish();
    }

    public void Pause(View view) {
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
            this.clicked = clicked;
        }

        boolean getClicked(){
            return clicked;
        }

        public void onTick(long millisUntilFinished) {

        }
        public void onFinish() {
            if ((!clicked) && (!resumed)) {
                if (hitSound) {
                    Vibrator vibrate = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrate.vibrate(500);
                }
                life--;
                setClicked(true);
                current.setBackgroundResource(R.drawable.goingdown);
                moleAnimation = (AnimationDrawable) current.getBackground();
                moleAnimation.start();
                current.setImageResource(android.R.color.transparent);
                current.setOnTouchListener(null);
                setTopScore();
            }
            else{
                current.setImageResource(android.R.color.transparent);
                Play();
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
            if (++hits==2)
                this.clicked = true;
        }

        boolean getClicked(){
            return clicked;
        }

        public void onTick(long millisUntilFinished) {

        }
        public void onFinish() {
            if ((!clicked) && (!resumed)) {
                if (hitSound) {
                    Vibrator vibrate = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrate.vibrate(500);
                }
                life--;
                resetHits();
                current.setBackgroundResource(R.drawable.goingdowndevil);
                moleAnimation = (AnimationDrawable) current.getBackground();
                moleAnimation.start();
                current.setImageResource(android.R.color.transparent);
                current.setOnTouchListener(null);
                setTopScore();
            }
            else{
                current.setImageResource(android.R.color.transparent);
                clicked=false;
                resetHits();
                Play();
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
            this.clicked = clicked;
            if (this.clicked) {
                life--;
                lifeSet(life);
            }
        }

        public void onTick(long millisUntilFinished) {

        }
        public void onFinish() {
            if ((clicked) && (!resumed)) {
                current.setImageResource(android.R.color.transparent);
                setTopScore();
            }
            else{
                if (firstAngel) {
                    instructions.setVisibility(View.GONE);
                    firstAngel = false;
                    editor = prefs.edit();
                    editor.putBoolean("first_time_angel", false);
                    editor.apply();
                }
                current.setBackgroundResource(R.drawable.goingdownangel);
                moleAnimation = (AnimationDrawable) current.getBackground();
                moleAnimation.start();
                current.setOnTouchListener(null);
                Play();
            }
            finished = true;
        }
    }

    private class RabbitTimer extends MyCountDownTimer {
        long mMillisInFuture;

        RabbitTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            mMillisInFuture = millisInFuture;
        }

        public void setMillisInFuture(long millisInFuture) {
            super.setMillisInFuture(millisInFuture);
            mMillisInFuture = millisInFuture;
        }

        public void onTick(long millisUntilFinished) {

        }

        public void onFinish() {
            int rand = (int) (Math.random() * 2000);
            if (rb!=null) {
                if (firstRabbit) {
                    final TextView rabbitInstructions = (TextView) findViewById(R.id.rabbitInstructions);
                    rabbitInstructions.setText(R.string.rabbitsHit);
                    rabbitInstructions.setTextColor(Color.WHITE);
                    Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/njnaruto.ttf");
                    rabbitInstructions.setTextSize(30.0f);
                    rabbitInstructions.setTypeface(custom_font);
                    rabbitInstructions.setVisibility(View.VISIBLE);
                    new CountDownTimer(4500, 1000){

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            firstRabbit = false;
                            editor = prefs.edit();
                            editor.putBoolean("first_time_rabbit", false);
                            editor.apply();
                            rabbitInstructions.setVisibility(View.GONE);
                        }
                    }.start();
                }
                rb = new RabbitView(mContext);
                rb.setY(rabbitLocations[rand % 2]);
                contentView.addView(rb);
                rb.Move(4500, rand % 2);
            }
            setMillisInFuture((rand * 1000) % 20000);
            start();
        }
    }
}
