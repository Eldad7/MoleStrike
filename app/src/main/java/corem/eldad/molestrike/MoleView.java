package corem.eldad.molestrike;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by eldadc on 12/12/2016.
 */

public class MoleView extends View {

    private static final int FRAME_W = 86;
    private static final int FRAME_H = 86;
    Rect[] frames = new Rect[5];
    RectF gameZone;
    Bitmap moles;


    public MoleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(true);
        prepare();
        gameZone = new RectF(0,80,80,0);
        gameZone.offset(10, 10);
        moles = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mole1);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(moles,frames[0],gameZone,null);
        postInvalidateDelayed(60); // slower = more natural movement
    }

    private void prepare(){
        for (int i=0; i<5; i++)
            frames[i] = new Rect(0,80,80,0);
    }
}

