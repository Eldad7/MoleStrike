package corem.eldad.molestrike;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


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
        timer=3600;
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
        System.out.println("Lose = " + String.valueOf(lose));
        current.setImageResource(R.drawable.talpa1);
        timer-=100;
        counter.setClicked(true);
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

        public void onTick(long millisUntilFinished) {

        }
        public void onFinish() {
            if (!clicked)
                lose=true;
            else{
                play();
            }
            finished = true;
        }
    }
}
