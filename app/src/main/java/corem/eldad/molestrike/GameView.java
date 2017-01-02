package corem.eldad.molestrike;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.icu.text.BreakIterator;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.nio.channels.Selector;

/**
 * Created by eldadc on 12/12/2016.
 */

public class GameView extends View {

    private static final int FRAME_W = 86;
    private static final int FRAME_H = 86;
    Rect[] frames = new Rect[3];
    RectF gameZone;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
/*
        setWillNotDraw(false);
        Characters();
        gameZone = new RectF(0,250,300,0);
        gameZone.offset(10, 10);
    }
}

    private void Characters(){
        frame0 = BitmapFactory.decodeResource(getResources(), R.drawable.talpa1);
        frame1 = BitmapFactory.decodeResource(getResources(), R.drawable.talpa2);
        frame2 = BitmapFactory.decodeResource(getResources(), R.drawable.talpa4);
        frames = new Bitmap[]{frame0,frame1,frame2};

    }

    @Override
    protected void onDraw(Canvas canvas) {


        }

        postInvalidateDelayed(60); // slower = more natural movement
        postInvalidateOnAnimation(); // too fast
    }
    private void prepareCharacter() {
        talpas = BitmapFactory.decodeResource(getResources(), R.drawable.talpas);
        // setup the rects
        mCharWidth = 523;
        mCharHeight = 550;
        int i = 0; // rect index
        for (int y = 0; y < 6; y++) { // row
        for (int x = 0; x < 12; x++) { // column
        frames[i] = new Rect(x * mCharWidth, y * mCharHeight, (x + 1) * mCharWidth, (y + 1) * mCharHeight);
        i++;
        if (i >= NUM_FRAMES) {
        break;
        }
        }
        }
*/
}

