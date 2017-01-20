package corem.eldad.molestrike;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class GameOverActivity extends AppCompatActivity {

    SharedPreferences prefs;
    ConstraintLayout cl;
    ImageView highScore, newLevel;
    boolean newHighScore,level;
    int background;
    Context context;
    Music music;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_game_over);
        cl = (ConstraintLayout) findViewById(R.id.activity_game_over);
        intent = getIntent();
        Bundle b=intent.getExtras();
        level = b.getBoolean("newLevel");
        newHighScore = b.getBoolean("highScore");
        highScore = (ImageView) findViewById(R.id.highScore);
        newLevel = (ImageView) findViewById(R.id.newLevel);
        context = this;
        music = new Music(this.getBaseContext(), this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (prefs.getBoolean("forestbackground", true))
            background = R.drawable.forestbackground;
        else
            background = R.drawable.desertbackground;
        cl.setBackgroundResource(background);
        highScore.startAnimation(AnimationUtils.loadAnimation(context, R.anim.gameovertext));
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (newHighScore) {
                    highScore.setVisibility(View.VISIBLE);
                    highScore.startAnimation(AnimationUtils.loadAnimation(context, R.anim.gameovertext));
                    MainActivity.dataChanged = true;
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (level){
                    newLevel.setVisibility(View.VISIBLE);
                    newLevel.startAnimation(AnimationUtils.loadAnimation(context, R.anim.gameovertext));
                }
            }
        }).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent("finish_activity");
                sendBroadcast(intent);
                finish();
            }
        },4000);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
