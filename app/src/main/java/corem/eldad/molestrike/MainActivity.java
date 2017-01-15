package corem.eldad.molestrike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    //private Music sounds;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //sounds = new Music(this.getBaseContext());
    }

    public void play(View view) {
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putSerializable("music", sounds);
        startActivity(intent);
    }

    public void settingsMenu(View view) {
        Intent intent = new Intent(getBaseContext(), Settings.class);
        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.activity_main);
        startActivity(intent);
    }
}
