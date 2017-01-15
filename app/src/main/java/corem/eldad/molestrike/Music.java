package corem.eldad.molestrike;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;

/**
 * @author ©EldadC
 */
public class Music implements Runnable, MediaPlayer.OnCompletionListener {
    private static SoundPool soundPool;
    private int hitSound;
    MediaPlayer mPlayer;
    boolean musicIsPlaying = false;
    Context appContext;

    public Music(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        hitSound = soundPool.load(context, R.raw.hit,1);
        appContext = context;
    }

    public void playHit() {
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void pause(){
        mPlayer.stop();
    }

    @Override
    public void run() {
        if (musicIsPlaying) {
            mPlayer.stop();
            musicIsPlaying = false;
        } else {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(appContext, R.raw.backgroundmusic);
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
        System.out.println("done playing");
        if (musicIsPlaying && mPlayer != null) {
            mPlayer.start();
        }
    }
}
