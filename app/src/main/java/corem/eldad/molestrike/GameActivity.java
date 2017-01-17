package corem.eldad.molestrike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;


/**
 * Created by eldadc on 20/12/2016.
 */

public class GameActivity extends AppCompatActivity {
    /*ImageView topLeft;
    ImageView topMiddle;
    ImageView topRight;
    ImageView bottomLeft;
    ImageView bottomMiddle;
    ImageView bottomRight;*/
    ImageView current;
    ImageView[] characters;
    long timer;
    static boolean lose;
    boolean firstTime = true;
    Timer counter;
    int j,numberOfMoles;
    ImageView countDownView, pausePlay;
    TextView count;
    AnimationDrawable moleAnimation;
    boolean started;
    boolean resumed = false;
    private Music hitSound, music;
    CountDownTimer countDown;
    private SharedPreferences prefs;
    int background;
    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        music = new Music(this.getBaseContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                togglePrefs(prefs);
            }
        }).start();
        setContentView(R.layout.activity_game);
        cl = (ConstraintLayout) findViewById(R.id.activity_game);
        Intent intent = getIntent();
        Bundle b=intent.getExtras();
//        music = (Music) intent.getSerializableExtra("music");
        numberOfMoles = b.getInt("numberOfMoles");
        initViews(numberOfMoles);
        pausePlay = (ImageView) findViewById(R.id.pause);
        j=0;
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
        }).start();
        cl.setBackgroundResource(background);
        pausePlay.setBackgroundResource(R.drawable.pause);
        if ((resumed) && (current==null)){
            countDown.start();
            System.out.println("Countdown");
        }
        if (resumed) {
            System.out.println("Resumed");
            play();
        }
        resumed = false;
    }

    private void togglePrefs(SharedPreferences prefs) {
        /*System.out.println(prefs.getBoolean("soundFX", true));
        if (prefs.getBoolean("soundFX", true)){
            System.out.println("true");
            hitSound = new Music(this.getBaseContext());
        }
        else{
            System.out.println("false");
            hitSound = null;
        }
        if (prefs.getBoolean("music", true)) {
            if (!music.musicIsPlaying)
                music.run();
        }
        else{
            if (music.musicIsPlaying)
                music.pause();
        }*/
        if (prefs.getBoolean("forestbackground", true))
            background = R.drawable.forestbackground;
        else
            background = R.drawable.desertbackground;
        if (prefs.getBoolean("music", true))
            music.run();
    }

    @Override
    protected void onStop(){
        super.onStop();
        pausePlay.setBackgroundResource(R.drawable.play);
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
                    if ((event.getAction() == MotionEvent.ACTION_DOWN) && (hitSound!=null))
                        hitSound.playHit();
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
        Intent intent = new Intent(getBaseContext(), Settings.class);
        startActivity(intent);
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
            }
            else{
                current.setImageResource(android.R.color.transparent);
                play();
            }
            finished = true;
        }
    }
}
