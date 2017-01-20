package corem.eldad.molestrike;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by The Gate Keeper on 1/16/2017.
 */

public class LeaderBoardAdapter extends ArrayAdapter{

    private Context mContext;
    private int id;
    private List<String> items ;
    SharedPreferences prefs;
    String user;

    public LeaderBoardAdapter(Context context, int textViewResourceId , List<String> list )
    {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        items = list ;
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        user = prefs.getString("display_name", "Player1");
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView name = (TextView) mView.findViewById(R.id.name);
        TextView level = (TextView) mView.findViewById(R.id.level);
        TextView score = (TextView) mView.findViewById(R.id.score);

        if(items.get(position) != null )
        {;
            Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(),  "fonts/njnaruto.ttf");
            String s = items.get(position);
            int first = s.indexOf("+");
            int second = s.lastIndexOf("+");
            String player = s.substring(0,first);
            String playerLevel = s.substring(first+1,second);
            String playerScore = s.substring(second+1,s.length()-1);
            name.setTypeface(custom_font);
            name.setTextColor(Color.RED);
            name.setTextSize(15.0f);
            name.setText(player);
            name.setHighlightColor(Color.RED);
            name.setBackgroundResource(R.drawable.boardcover);
            level.setTypeface(custom_font);
            level.setTextColor(Color.RED);
            level.setTextSize(15.0f);
            level.setText(playerLevel);
            level.setHighlightColor(Color.RED);
            level.setBackgroundResource(R.drawable.boardcover);
            score.setTypeface(custom_font);
            score.setTextColor(Color.RED);
            score.setTextSize(15.0f);
            score.setText(playerScore);
            score.setHighlightColor(Color.RED);
            score.setBackgroundResource(R.drawable.boardcover);
            if (player.equals(user)) {
                name.setTextSize(16.0f);
                level.setTextSize(16.0f);
                score.setTextSize(16.0f);
            }

        }

        return mView;
    }
}
