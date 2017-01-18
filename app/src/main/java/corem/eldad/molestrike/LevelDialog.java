package corem.eldad.molestrike;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;

/**
 * Created by The Gate Keeper on 1/16/2017.
 */

public class LevelDialog extends Dialog implements android.view.View.OnClickListener {

    private Context context;
    private RadioButton easy, medium, hard;
    boolean levelChosen = false;
    int level;

    public LevelDialog(Context context) {
        super(context);
        this.context = context;
    }

    public boolean getLevelChosen(){
        return levelChosen;
    }

    public int getLevel(){
        return level;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.medium:
                level = 2;
            case R.id.hard:
                level = 3;
            default: level=1;

        }
        levelChosen = true;
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.level_dialog);
        easy = (RadioButton) findViewById(R.id.easy);
        easy.setOnClickListener(this);
        medium = (RadioButton) findViewById(R.id.medium);
        medium.setOnClickListener(this);
        hard = (RadioButton) findViewById(R.id.hard);
        hard.setOnClickListener(this);
    }
}
