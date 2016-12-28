package corem.eldad.molestrike;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import static android.os.Looper.prepare;


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
    int timer;
    static boolean lose;
    boolean firstTime = true;
    MyCountDownTimer counter;
    int j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ImageView topLeft = (ImageView) findViewById(R.id.topLeftMole);
        ImageView bottomLeft = (ImageView) findViewById(R.id.bottomLeftMole);
        ImageView topMiddle = (ImageView) findViewById(R.id.topMiddleMole);
        ImageView bottomMiddle = (ImageView) findViewById(R.id.bottomMiddleMole);
        ImageView topRight = (ImageView) findViewById(R.id.topRightMole);
        ImageView bottomRight = (ImageView) findViewById(R.id.bottomRightMole);
        characters = new ImageView[] {topLeft, bottomLeft, topMiddle, bottomMiddle, topRight, bottomRight};
        timer=1;
        lose = false;
        for (int i=0; i<6; i++)
            characters[i].setVisibility(View.VISIBLE);
        play();
    }

        private void play() {
            System.out.println("Playing");
            int random = (int )(Math.random() * 500);
            if(!lose) {
                System.out.println("Round " + ++j);
                current = characters[random % 6];
                startTransition(current);
            }
        }

    private void startTransition(ImageView current) {
        System.out.println("Transitioning");
        current.setImageResource(R.drawable.talpa4);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameOn(counter);
            }
        });
        if (firstTime) {
            firstTime = false;
            counter = new MyCountDownTimer(timer*4000, 1000);
            counter.start();
        }
        else{
            counter.start();
        }
            counter.setClicked(false);
    }
    //NEW CLASS THAT EXTENDS COUNTDOWN

    private void GameOn(MyCountDownTimer counter) {
        System.out.println("Lose = " + String.valueOf(lose));
        current.setImageResource(R.drawable.talpa1);
        timer-=0.1;
        counter.setClicked(true);
    }

    private class MyCountDownTimer extends CountDownTimer {
        boolean clicked = false;
        boolean finished = false;

        void setClicked(boolean clicked){
            System.out.println("Clicked = "+clicked);
            this.clicked = clicked;
        }

        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }



        public void onTick(long millisUntilFinished) {
            System.out.println("seconds remaining: " + millisUntilFinished / 1000);
        }
        public void onFinish() {
            System.out.println("Time Over");
            if (!clicked)
                lose=true;
            else{
                play();
            }
            finished = true;
        }
    }
}
