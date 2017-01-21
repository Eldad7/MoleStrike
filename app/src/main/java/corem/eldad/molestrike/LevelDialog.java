package corem.eldad.molestrike;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

/**
 * Created by The Gate Keeper on 1/16/2017.
 * This custom dialog let's the player choose which level he wants - Easy,Medium or Hard
 * The getLevelChosen is to make sure there are no side effects if the player dismisses the dialog but doesn't choose a level
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
            case R.id.medium: {
                level = 2;
                break;
            }
            case R.id.hard: {
                level = 3;
                break;
            }
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

    public void setLevels(int levels){
        if (levels>1) {
            medium.setBackgroundResource(R.drawable.medium);
            medium.setEnabled(true);
        } if (levels>2) {
            hard.setBackgroundResource(R.drawable.hard);
            hard.setEnabled(true);
        }
    }
}
