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
    private SharedPreferences settings;
    SharedPreferences.Editor editor;

    public ThemesDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        editor = settings.edit();
        switch (v.getId()) {
            case R.id.forest:
                if (forest.isChecked()) {
                    desert.setChecked(false);
                    editor.putBoolean("desertbackground", false);
                    editor.putBoolean("forestbackground", true);
                }
            case R.id.desert:
                if (desert.isChecked()) {
                    forest.setChecked(false);
                    editor.putBoolean("desertbackground", true);
                    editor.putBoolean("forestbackground", false);
                }
        }
        editor.apply();
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.themes_dialog);
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        forest = (RadioButton) findViewById(R.id.forest);
        forest.setOnClickListener(this);
        desert = (RadioButton) findViewById(R.id.desert);
        desert.setOnClickListener(this);
    }
}
