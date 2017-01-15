package corem.eldad.molestrike;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.preference.PreferenceFragmentCompat;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private static Music sounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sounds = new Music(this.getBaseContext());
        //sounds.run();
    }

    public void play(View view) {
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        startActivity(intent);
    }

    public static Music pauseMusic(){
        return sounds;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.settings) {
            System.out.println("selected");
        }
        return super.onOptionsItemSelected(item);
    }

    public void settingsMenu(View view) {
        Intent intent = new Intent(getBaseContext(), Settings.class);
        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.activity_main);

        //if (cl.getBackground().toString() == "")
        startActivity(intent);
    }
}
