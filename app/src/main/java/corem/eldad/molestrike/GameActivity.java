package corem.eldad.molestrike;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static android.R.drawable.ic_media_play;
import static corem.eldad.molestrike.R.drawable.ic_pause;


/**
 * Created by eldadc on 20/12/2016.
 */

public class GameActivity extends AppCompatActivity {
    ImageView topLeft;
    ImageView topMiddle;
    ImageView topRight;
    ImageView bottomLeft;
    ImageView bottomMiddle;
    ImageView bottomRight;
    ImageView current;
    ImageView[] characters;
    long timer;
    static boolean lose;
    boolean firstTime = true;
    Timer counter;
    int j;
    ImageView countDownView, pausePlay;
    TextView count;
    AnimationDrawable moleAnimation;
    boolean started = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ImageView topLeft = (ImageView) findViewById(R.id.topLeftMole);
        ImageView bottomLeft = (ImageView) findViewById(R.id.bottomLeftMole);
        ImageView topMiddle = (ImageView) findViewById(R.id.topRightMole);
        ImageView bottomMiddle = (ImageView) findViewById(R.id.MiddleMole);
        ImageView topRight = (ImageView) findViewById(R.id.topMiddleMole);
        ImageView bottomRight = (ImageView) findViewById(R.id.bottomRightMole);
        pausePlay = (ImageView) findViewById(R.id.pause);
        j=0;
        count = (TextView) findViewById(R.id.count);
        count.setText("Score: " + String.valueOf(j));
        countDownView = (ImageView) findViewById(R.id.countDownView);
        characters = new ImageView[] {topLeft, bottomLeft, topMiddle, bottomMiddle, topRight, bottomRight};
        for (int i=0; i<6; i++)
            characters[i].setBackgroundResource(R.drawable.jmole1);
        timer=2500;
        lose = false;
        final int three = R.drawable.number3;
        final int two = R.drawable.number2;
        final int one = R.drawable.number1;
        new CountDownTimer(5000,1000){
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

    @Override
    protected void onResume(){
        super.onResume();
        if ((counter!=null) && (started)) {
            System.out.println("Paused");
            pausePlay.setImageResource(ic_pause);
            counter.start();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        pausePlay.setImageResource(ic_media_play);
        started = false;
        counter.cancel();
    }

    private void play() {
        System.out.println("Playing");
        int random = (int )(Math.random() * 500);
        if(!lose) {
            current = characters[random % 6];
            current.setBackgroundResource(R.drawable.goingup);
            startTransition(current);
        }
    }

    private void startTransition(ImageView current) {
        System.out.println("Transitioning");
        moleAnimation = (AnimationDrawable) current.getBackground();
        moleAnimation.start();
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameOn(counter);
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
            moleAnimation = (AnimationDrawable) current.getBackground();
            moleAnimation.start();
            if ((timer > 800) && (j%5==0))
                timer -= 100;
            counter.setClicked(true);
            count.setText("Score: " + String.valueOf(++j));
        }
    }

    public void pause(View view) {
        pausePlay.setImageResource(ic_media_play);
        started = false;
        current.setOnClickListener(null);
        pausePlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                current.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GameOn(counter);
                    }
                });
                started = true;
                onResume();
            }
        });
        counter.cancel();
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
            if (!clicked) {
                lose = true;
                setClicked(true);
                current.setBackgroundResource(R.drawable.goingdown);
                moleAnimation = (AnimationDrawable) current.getBackground();
                moleAnimation.start();
                countDownView.setImageResource(R.drawable.gameover);
                countDownView.setVisibility(View.VISIBLE);
                current.setImageResource(android.R.color.transparent);
                current.setOnClickListener(null);
            }
            else{
                current.setImageResource(android.R.color.transparent);
                play();
            }
            finished = true;
        }
    }
}
