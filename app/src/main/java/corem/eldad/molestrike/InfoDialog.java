package corem.eldad.molestrike;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;

/**
 * Created by The Gate Keeper on 1/16/2017.
 */

public class InfoDialog extends Dialog{

    public Activity c;
    private Context context;
    private RadioButton forest, desert;
    private SharedPreferences settings;
    SharedPreferences.Editor editor;

    public InfoDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_dialog);
        ImageView credits = (ImageView) findViewById(R.id.credits);
        credits.startAnimation(AnimationUtils.loadAnimation(context, R.anim.imageclick));
        credits.getAnimation().setRepeatCount(Animation.INFINITE);
    }
}
