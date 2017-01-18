package corem.eldad.molestrike;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Switch;

/**
 * Created by The Gate Keeper on 1/16/2017.
 */

public class InGameDialog extends Dialog implements android.view.View.OnClickListener{


    public Activity c;
    public Dialog d;
    private Switch music, soundfx;
    private ImageButton play;
    private Context context;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Music _music;

    public InGameDialog(Context context, Music _music) {
        super(context);
        this.context = context;
        this._music = _music;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ingame_dialog);
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        music = (Switch) findViewById(R.id.musicInGame);
        music.setOnClickListener(this);
        if (settings.getBoolean("music", true)) {
            music.setChecked(true);
            music.setBackgroundResource(R.drawable.music);
        }
        else{
            music.setChecked(false);
            music.setBackgroundResource(R.drawable.musicdisabled);
        }
        soundfx = (Switch) findViewById(R.id.soundfxInGame);
        soundfx.setOnClickListener(this);
        if (settings.getBoolean("soundfx", true)) {
            soundfx.setChecked(true);
            soundfx.setBackgroundResource(R.drawable.soundfx);
        }
        else{
            soundfx.setChecked(false);
            soundfx.setBackgroundResource(R.drawable.soundfxdisabled);
        }
        play = (ImageButton) findViewById(R.id.resumeGame);
        play.setOnClickListener(this);
    }

    @Override
    public void onDetachedFromWindow(){
        dismiss();
    }

    @Override
    public void onClick(View v){
        editor = settings.edit();
        switch (v.getId()){
            case R.id.musicInGame:{
                editor.putBoolean("music", music.isChecked());
                if (music.isChecked()) {
                    v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.imageclick));
                    _music.run();
                    editor.putFloat("music_volume", 0.5f);
                    v.setBackgroundResource(R.drawable.music);
                }
                else{
                    v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.imageclick));
                    editor.putFloat("music_volume", 0f);
                    _music.pause();
                    v.setBackgroundResource(R.drawable.musicdisabled);
                }
                editor.apply();
                break;
            }
            case R.id.soundfxInGame: {
                editor.putBoolean("soundfx", soundfx.isChecked());
                if (soundfx.isChecked()) {
                    v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.imageclick));
                    _music.setFXVolume(0.5f);
                    editor.putFloat("soundfx_volume", 0.5f);
                    _music.loadHit();
                    v.setBackgroundResource(R.drawable.soundfx);
                }
                else{
                    v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.imageclick));
                    editor.putFloat("soundfx_volume", 0f);
                    v.setBackgroundResource(R.drawable.soundfxdisabled);
                }
                editor.apply();
                break;
            }
            case R.id.resumeGame: {
                dismiss();
                break;
            }
        }
    }
}
