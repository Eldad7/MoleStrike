package corem.eldad.molestrike;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;

/**
 * Created by The Gate Keeper on 1/16/2017.
 */

public class ThemesDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    private Context context;
    private RadioButton forest, desert;

    public ThemesDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.themes_dialog);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        forest = (RadioButton) findViewById(R.id.forest);
        forest.setChecked(settings.getBoolean("forestbackground", true));
        desert = (RadioButton) findViewById(R.id.desert);
        desert.setChecked(settings.getBoolean("desertbackground", false));
    }
}
