package corem.eldad.molestrike;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    MoleStrikeDB MSdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void play(View view) {
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        startActivity(intent);
    }

    /*@Override
    protected void onResume(){
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDBData();
            }
        }).start();
    }*/

    private void getDBData() {
        final SQLiteDatabase db = MSdb.getWritableDatabase();
        String[] projection = {
                MoleStrikeDB.player.COLUMN_NAME,
                MoleStrikeDB.player.COLUMN_MINIMUM_LEVEL,
                MoleStrikeDB.player.COLUMN_TOP_SCORE
        };
        Cursor c = db.query(
        MoleStrikeDB.player.TABLE_NAME,           // The table to query
        projection,                               // The columns to return
        null,                                     // The columns for the WHERE clause
        null,                                     // The values for the WHERE clause
        null,                                     // don't group the rows
        null,                                     // don't filter by row groups
        null                                      // The sort order
        );
        c.moveToFirst();
    }

    public void settings(View view) {

    }

    //Need to make 2 image views with Z-index with an algorithm
}
