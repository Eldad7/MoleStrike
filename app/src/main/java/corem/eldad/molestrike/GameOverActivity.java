package corem.eldad.molestrike;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.facebook.*;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.plus.PlusShare;

/**Created by Eldad Corem
 * The Handler is used in order to give the Gameover screen to be visible
 * Since only then I sent the broadcast to finish GameActivity, the onBackPressed is to fix side effects
 */

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences prefs;
    ConstraintLayout cl;
    ImageButton Continue;
    Button share;
    ImageView highScore, newLevel;
    boolean newHighScore,level, handler=true;
    int background;
    Context context;
    Music music;
    CallbackManager callbackManager;
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
        share = (Button) findViewById(R.id.share_button);
        Continue = (ImageButton) findViewById(R.id.Continue);
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
                if (level){
                    handler=false;
                    newLevel.setVisibility(View.VISIBLE);
                    newLevel.startAnimation(AnimationUtils.loadAnimation(context, R.anim.gameovertext));
                    share.setVisibility(View.VISIBLE);
                    share.setOnClickListener(GameOverActivity.this);
                    Continue.setVisibility(View.VISIBLE);
                    Continue.setOnClickListener(GameOverActivity.this);
                }
                if (newHighScore) {
                    highScore.setVisibility(View.VISIBLE);
                    highScore.startAnimation(AnimationUtils.loadAnimation(context, R.anim.gameovertext));
                    MainActivity.dataChanged = true;
                }
            }
        }).start();
        if (handler){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent("finish_activity");
                    sendBroadcast(intent);
                    finish();
                }
            },4000);
        }
    }

    @Override
    public void onBackPressed(){
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Continue:{
                Intent intent = new Intent("finish_activity");
                sendBroadcast(intent);
                finish();
                break;
            }
            case R.id.share_button:{
                Intent shareIntent = new PlusShare.Builder(this)
                        .setType("text/plain")
                        .setText("I just scored " + newHighScore + " on Mole Strike! Think you can beat me??")
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=corem.eldad.molestrike"))
                        .getIntent();

                startActivityForResult(shareIntent, 0);
            }
        }

    }
}
