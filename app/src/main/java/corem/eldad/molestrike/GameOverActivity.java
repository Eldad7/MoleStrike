package corem.eldad.molestrike;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.google.android.gms.plus.PlusShare;

/**Created by Eldad Corem
 * The Handler is used in order to give the Gameover screen to be visible
 * Since only then I sent the broadcast to finish GameActivity, the onBackPressed is to fix side effects
 */

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences prefs;
    private ConstraintLayout cl;
    private ImageButton Continue;
    private Button share;
    private ImageView highScore, newLevel;
    boolean newHighScore,level;
    int background, top;
    private Context context;
    private Music music;
    private Intent intent;

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
        top = b.getInt("top");
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
                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (newHighScore) {
                                highScore.setVisibility(View.VISIBLE);
                                highScore.startAnimation(AnimationUtils.loadAnimation(context, R.anim.gameovertext));
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("top_score", top);
                                editor.apply();
                            }
                            if (level){
                                newLevel.setVisibility(View.VISIBLE);
                                newLevel.startAnimation(AnimationUtils.loadAnimation(context, R.anim.gameovertext));
                                share.setVisibility(View.VISIBLE);
                                share.setOnClickListener(GameOverActivity.this);
                                Continue.setVisibility(View.VISIBLE);
                                Continue.setOnClickListener(GameOverActivity.this);
                                MainActivity.dataChanged = true;
                            }
                            else{
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent("finish_activity");
                                        sendBroadcast(intent);
                                        finish();
                                    }
                                },3000);
                            }
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent("finish_activity");
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
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
