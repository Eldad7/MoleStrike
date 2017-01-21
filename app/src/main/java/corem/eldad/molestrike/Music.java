package corem.eldad.molestrike;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.preference.PreferenceManager;

import java.io.IOException;

/**
 * @author ©EldadC
 *
 * In order to create a more "smooth" loop, I have created the function create next media player
 * Since each activity has it's own sound, you need to save a reference to the activity that called Music
 * To overcome null pointers, every function is checking if musicIsPlaying is true or false
 */
public class Music implements Runnable, MediaPlayer.OnCompletionListener{
    private static SoundPool soundPool;
    private int hitSound;
    private MediaPlayer mPlayer, mNextPlayer;
    private Activity activity;
    boolean musicIsPlaying = false;
    private Context appContext;
    private Float volumeLevel = 1.0f;
    private Float musicVolume;

    public Music(Context context, Activity activity) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        appContext = context;
        this.activity = activity;
        hitSound = soundPool.load(appContext, R.raw.hit,1);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        musicVolume = settings.getFloat("music_volume", 0.5f);
        volumeLevel = settings.getFloat("soundfx_volume", 0.5f);
    }

    public void loadHit(){
        hitSound = soundPool.load(appContext, R.raw.hit,1);
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
        soundPool.play(hitSound, volumeLevel, volumeLevel, 1, 0, 1.0f);
    }

    public void pause(){
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        setMusicIsPlaying(false);
    }

    public boolean getMusicIsPlaying(){ return musicIsPlaying;}
    public void setMusicIsPlaying(boolean _musicIsPlaying){musicIsPlaying = _musicIsPlaying;}

    @Override
    public void run() {
        if (musicIsPlaying) {
            mPlayer.stop();
            musicIsPlaying = false;
        } else {
            if (mPlayer == null) {
                if (activity.getClass() == GameActivity.class)
                    mPlayer = MediaPlayer.create(appContext, R.raw.game_music);
                else
                    mPlayer = MediaPlayer.create(appContext, R.raw.main_music);
                mPlayer.start();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mPlayer.start();
                    }
                });
            } else {
                try {
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            musicIsPlaying = true;
            createNextMediaPlayer();
        }
    }

    private void createNextMediaPlayer() {
        if (activity.getClass() == GameActivity.class)
            mNextPlayer = MediaPlayer.create(appContext, R.raw.game_music);
        else
            mNextPlayer = MediaPlayer.create(appContext, R.raw.main_music);
        mPlayer.setNextMediaPlayer(mNextPlayer);
        mPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (musicIsPlaying && mPlayer != null) {
            mPlayer.release();
            mPlayer = mNextPlayer;
            mPlayer.setVolume(musicVolume, musicVolume);
            mPlayer.start();
            createNextMediaPlayer();
        }
    }
}
