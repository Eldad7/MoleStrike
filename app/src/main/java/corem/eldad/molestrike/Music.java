package corem.eldad.molestrike;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.preference.PreferenceManager;

import java.io.IOException;

/**
 * @author ©EldadC
 */
public class Music implements Runnable, MediaPlayer.OnCompletionListener {
    private static SoundPool soundPool;
    private int hitSound;
    MediaPlayer mPlayer;
    Activity activity;
    boolean musicIsPlaying = false;
    Context appContext;
    Float volumeLevel = 1.0f;
    Float rate = 1.0f;
    Float musicVolume = 1.0f;

    public Music(Context context, Activity activity) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        hitSound = soundPool.load(context, R.raw.hit,1);
        appContext = context;
        this.activity = activity;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        musicVolume = settings.getFloat("music_volume", 1.0f);
        volumeLevel = settings.getFloat("soundfx_volume", 1.0f);
    }

    public void setFXRate(float rate){
        this.rate = rate;
        soundPool.setRate(hitSound,rate);
    }

    public void setFXVolume(float volume){
        this.volumeLevel = volume;
        soundPool.setVolume(hitSound, volumeLevel,volumeLevel);
    }

    public void setMusicVolume(float volume){
        musicVolume = volume;
        mPlayer.setVolume(volume, volume);
    }

    public void playHit() {
        soundPool.play(hitSound, volumeLevel, volumeLevel, 1, 0, rate);
    }

    public void pause(){
        mPlayer.stop();
    }

    public boolean getMusicIsPlaying(){ return musicIsPlaying;}

    @Override
    public void run() {
        if (musicIsPlaying) {
            mPlayer.stop();
            musicIsPlaying = false;
        } else {
            if (mPlayer == null) {
                if (activity.getClass() == MainActivity.class)
                    mPlayer = MediaPlayer.create(appContext, R.raw.main_music);
                else
                    mPlayer = MediaPlayer.create(appContext, R.raw.game_music);
                mPlayer.setLooping(true);
                mPlayer.start();
                mPlayer.setOnCompletionListener(this); // MediaPlayer.OnCompletionListener
            } else {
                try {
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            musicIsPlaying = true;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (musicIsPlaying && mPlayer != null) {
            mPlayer.start();
        }
    }
}
