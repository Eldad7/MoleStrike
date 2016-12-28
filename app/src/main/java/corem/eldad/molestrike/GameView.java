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
import android.widget.ImageView;

import java.nio.channels.Selector;

/**
 * Created by eldadc on 12/12/2016.
 */

public class GameView extends android.support.constraint.ConstraintLayout {

    private static final int FRAME_W = 86;
    private static final int FRAME_H = 86;
    Rect[] characters;
    static Boolean gameOn = true;
    RectF gameZone;
    Bitmap frame0;
    Bitmap frame1;
    Bitmap frame2;
    Bitmap[] frames;
    Bitmap talpas;
    Paint topRectPaint = new Paint();
    Rect bottomLeft;
    Rect topLeft;
    Rect bottomMiddle;
    Rect topMiddle;
    Rect bottomRight;
    Rect topRight;
    int mCharHeight;
    int mCharWidth;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setWillNotDraw(false);
//        Characters();
//        gameZone = new RectF(0,250,300,0);
//        gameZone.offset(10, 10);
    }
}
//
//    private void Characters(){
//        frame0 = BitmapFactory.decodeResource(getResources(), R.drawable.talpa1);
//        frame1 = BitmapFactory.decodeResource(getResources(), R.drawable.talpa2);
//        frame2 = BitmapFactory.decodeResource(getResources(), R.drawable.talpa4);
//        frames = new Bitmap[]{frame0,frame1,frame2};
//        Rect bottomLeft = new Rect(10,100,96,14);
//        Rect topLeft = new Rect(10,200,96,114);
//        Rect bottomMiddle = new Rect(110,100,196,14);
//        Rect topMiddle = new Rect(110,200,196,114);
//        Rect bottomRight = new Rect(210,100,296,14);
//        Rect topRight = new Rect(210,200,296,114);
//        characters = new Rect[]{topLeft, topMiddle, topRight, bottomLeft, bottomMiddle, bottomRight};
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//
//        for (int i=0; i<6; i++) {
//            canvas.drawBitmap(frame0, characters[i], gameZone, null);
//        }
//
//        postInvalidateDelayed(60); // slower = more natural movement
////        postInvalidateOnAnimation(); // too fast
//    }
//    private void prepareCharacter() {
//        talpas = BitmapFactory.decodeResource(getResources(), R.drawable.talpas);
//        // setup the rects
//        mCharWidth = 523;
//        mCharHeight = 550;
//        Selector select;
//
//}
//
////OLD
////        ImageView topLeft = (ImageView) findViewById(R.id.topLeftMole);
////        ImageView bottomLeft = (ImageView) findViewById(R.id.bottomLeftMole);
////        ImageView topMiddle = (ImageView) findViewById(R.id.topMiddleMole);
////        ImageView bottomMiddle = (ImageView) findViewById(R.id.bottomMiddleMole);
////        ImageView topRight = (ImageView) findViewById(R.id.topRightMole);
////        ImageView bottomRight = (ImageView) findViewById(R.id.bottomRightMole);
////ImageButton button = (ImageButton) findViewById(R.id.imageButton);
////button.setVisibility(View.INVISIBLE);
////characters = new ImageView[] {topLeft, bottomLeft, topMiddle, bottomMiddle, topRight, bottomRight};
////play();

