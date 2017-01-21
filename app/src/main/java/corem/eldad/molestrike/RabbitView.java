package corem.eldad.molestrike;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by EldadC on 21/01/2017.
 * This class is an ImageView with a few alterations
 * The Move function is actually the beginning of the behaviour I wanted
 * The RabbitView inside the GameActivity has a ValueAnimator that is in charge of moving the rabbit accross the screen
 * This animator receives a direction and a duration time. According to the direction he sets the correct sprite.
 * The move begins the animation, and on animation updated when (int % 2, that's the best speed for this sprite)
 * The view changes, which causing the view to move, and the sprite to complete.
 * since there are 7 frames, after seven frames I reset the "hit" value of the view.
 * I do that in order to make sure that if the rabbit is blocking the mole, it will hit first so the mole won't
 */

public class RabbitView extends ImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private ValueAnimator rabbitMover;
    private static final int FRAME_W = 80;
    // frame height
    private static final int FRAME_H = 80;
    // number of frames
    private static final int NB_FRAMES = 7;
    // frame duration
    // we can slow animation by changing frame duration
    private static final int FRAME_DURATION = 200; // in ms !
    // scale factor for each frame
    private static final int SCALE_FACTOR = 5;
    // stores each frame
    private Bitmap[] bmps;
    private Bitmap rightRabbitBitmap, leftRabbitBitmap;
    int frame = 0;
    private Context mContext;
    private AnimationDrawable animationDrawable;
    private Animation animation;
    boolean hit = false;

    public RabbitView(Context context) {
        super(context);
        mContext = context;
        leftRabbitBitmap = getBitmapFromAssets("rabbitspriteltr.png");
        rightRabbitBitmap = getBitmapFromAssets("rabbitspritertl.png");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(250,250);
        setLayoutParams(layoutParams);
    }

    public void Move(int duration, int direction){
        rabbitMover = new ValueAnimator();
        rabbitMover.setDuration(duration);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int currentFrame = 0;
        if (direction==0) {
            rabbitMover.setFloatValues(-100f, point.x);
            bmps = new Bitmap[NB_FRAMES];
            for (int i = 0; i < NB_FRAMES; i++) {
                bmps[i] = Bitmap.createBitmap(leftRabbitBitmap, FRAME_W
                        * i, 0, FRAME_W, FRAME_H);
                bmps[currentFrame] = Bitmap.createScaledBitmap(
                        bmps[currentFrame], FRAME_W * SCALE_FACTOR, FRAME_H
                                * SCALE_FACTOR, false);

                if (++currentFrame >= NB_FRAMES) {
                    break;
                }
            }
        }
        else {
            rabbitMover.setFloatValues(point.x + 100, -400f);
            bmps = new Bitmap[NB_FRAMES];
            currentFrame = 0;
            for (int i = NB_FRAMES-1; i >=0; i--) {
                bmps[currentFrame] = Bitmap.createBitmap(rightRabbitBitmap, FRAME_W
                        * i, 0, FRAME_W, FRAME_H);
                bmps[currentFrame] = Bitmap.createScaledBitmap(
                        bmps[currentFrame], FRAME_W * SCALE_FACTOR, FRAME_H
                                * SCALE_FACTOR, true);

                if (++currentFrame >= NB_FRAMES) {
                    break;
                }
            }
        }
        rabbitMover.setInterpolator(new LinearInterpolator());
        rabbitMover.setTarget(this);
        rabbitMover.addListener(this);
        rabbitMover.addUpdateListener(this);
        setImageBitmap(bmps[frame]);
        rabbitMover.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        setX((float) animation.getAnimatedValue());
        if ((int) (float) animation.getAnimatedValue() % 2 == 0) {
            if (++frame == NB_FRAMES)
                frame = 0;
            setImageBitmap(bmps[frame]);
        }
        if (frame==0)
            setHit(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setHit(true);
        }
        return super.onTouchEvent(event);
    }

    private Bitmap getBitmapFromAssets(String filepath) {
        AssetManager assetManager = mContext.getAssets();
        InputStream istr = null;
        Bitmap bitmap = null;

        try {
            istr = assetManager.open(filepath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException ioe) {
            // manage exception
        } finally {
            if (istr != null) {
                try {
                    istr.close();
                } catch (IOException e) {
                }
            }
        }
        return bitmap;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean getHit(){
        return hit;
    }
}
