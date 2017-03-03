package corem.eldad.molestrike;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

import static com.google.android.gms.games.GamesStatusCodes.STATUS_OK;

/**
 * @author ©EldadC
 *
 * The main activity is the first screen, so I seperated a lot of views and data initialization to seperate threads
 * to ease on the main thread.
 * Toggle prefs is to set any shared preferences - Background,music,soundfx etc. and so it has to run on two occasions:
 * When resuming to app (hence - it's inside onResume) and when changing settings.
 * The settings, about and choose themes are all seperated dialogs, and they are initialized inside their own functions,
 * which are triggered by onClick.
 * getUpdatedList is started only when the flag dataChanged is on true, which is triggered or by the settings dialog
 * (changed your user name) or when you've reached a new high score and unlocked a new level
 */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    MoleStrikeDB db;
    private SharedPreferences prefs;
    private String name;
    private ConstraintLayout cl;
    int background;
    int userLevel = 1;
    int score;
    private Music music;
    private ListView mListView;
    private Button button;
    private TextView topScore;
    private ArrayList<String> mList;
    public static boolean dataChanged = false, gps = false;
    private LeaderBoardAdapter listAdapter;
    public static GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    ArrayList<String> list;
    boolean unlocked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MoleStrikeDB(this);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mList = b.getStringArrayList("list");
        music = new Music(this.getBaseContext(), this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cl = (ConstraintLayout) findViewById(R.id.activity_main);
        name = prefs.getString("display_name", "Player1");
        list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                pullFromDB();
            }
        }).start();
        Toast.makeText(this, "Hello " + name, Toast.LENGTH_SHORT).show();
        Fabric.with(this, new Crashlytics());
        logUser(prefs);
        mListView = (ListView) findViewById(R.id.list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                listAdapter = new LeaderBoardAdapter(MainActivity.this, R.layout.leader_board, mList);
                mListView.setAdapter(listAdapter);
                mListView.setAdapter(listAdapter);
            }
        }).run();
        button = (Button) findViewById(R.id.button);
        Typeface custom_font = Typeface.createFromAsset(this.getAssets(),  "fonts/njnaruto.ttf");
        button.setTypeface(custom_font);
        button.setTextColor(Color.WHITE);
        topScore = (TextView) findViewById(R.id.topScore);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                list.add(getString(R.string.achievement_junior));
                list.add(getString(R.string.achievement_advanced));
                list.add(getString(R.string.achievement_expert));
                list.add(getString(R.string.achievement_junior));
                list.add(getString(R.string.achievement_junior));
            }
        }).start();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (music.getMusicIsPlaying())
            music.pause();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume(){
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                togglePrefs(prefs);
                cl.setBackgroundResource(background);
            }
        }).run();

    }

    private void pullFromDB(){
        SQLiteDatabase dbHelper = db.getReadableDatabase();
        String whereClause = MoleStrikeDB.player.COLUMN_PLAYER+"=?";
        String [] whereArgs = {"1"};
        String[] projection = {
                MoleStrikeDB.player.COLUMN_NAME,
                MoleStrikeDB.player.COLUMN_EMAIL,
                MoleStrikeDB.player.COLUMN_TOP_SCORE,
                MoleStrikeDB.player.COLUMN_LEVEL,
                MoleStrikeDB.player.COLUMN_PLAYER
        };
        Cursor c = dbHelper.query(
                MoleStrikeDB.player.TABLE_NAME,                     // The table to query
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        c.moveToFirst();
        userLevel = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_LEVEL));
        score = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_TOP_SCORE));
        dbHelper.close();
    }

    private void togglePrefs(SharedPreferences prefs) {
        if (prefs.getBoolean("forestbackground", true))
            background = R.drawable.forestbackground;
        else
            background = R.drawable.desertbackground;
        if (prefs.getBoolean("music", true))
            if (!music.getMusicIsPlaying()){
                music.run();
                music.setMusicVolume(prefs.getFloat("music_volume", 1.0f));
            }
        if (dataChanged){
            getUpdatedList();
        }
        topScore.setText(String.format(getResources().getString(R.string.top), score));
        if (prefs.getBoolean("google_play_services", false) && !mGoogleApiClient.isConnected()){
            gps=true;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope(Scopes.GAMES))
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Games.API)
                    .addScope(Games.SCOPE_GAMES)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
        }
    }

    public void chooseLevel(View view){
        final LevelDialog ldd=new LevelDialog(MainActivity.this);
        ldd.show();
        ldd.setLevels(userLevel);
        ldd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (ldd.getLevelChosen())
                    play(ldd.getLevel());
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (music.getMusicIsPlaying()) {
            music.pause();
        }
        moveTaskToBack(true);
    }

    @Override
    public void finish(){
        if (music.getMusicIsPlaying())
            music.setMusicIsPlaying(false);
    }

    public void play(int level) {
        if (music.getMusicIsPlaying())
            music.pause();
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("top", score);
        bundle.putInt("level", level);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void settingsMenu(View view) {
        final SettingsDialog cdd=new SettingsDialog(MainActivity.this, music);
        cdd.show();
        cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (dataChanged)
                    getUpdatedList();
                if (cdd.getSignIn())
                    signIn();
                if (cdd.showAch){
                    if (mGoogleApiClient.isConnected() && mGoogleApiClient.hasConnectedApi(Games.API))
                        startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1);
                    else
                        Toast.makeText(getBaseContext(), "Not connected", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void signIn() {
        try {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                System.out.println("acct is not null");
                name = acct.getDisplayName();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("display_name", name);
                editor.putBoolean("google_play_services", true);
                editor.apply();
                gps=true;
                MoleStrikeDB db = new MoleStrikeDB(this);
                final SQLiteDatabase dbHelper = db.getWritableDatabase();
                db.updateName(name, dbHelper);
                MainActivity.dataChanged=true;
                db.close();
                if (mGoogleApiClient.isConnected())
                    mGoogleApiClient.disconnect();
                togglePrefs(prefs);
            }
        }
    }

    public void themesDialog(View view) {
        ThemesDialog cdd=new ThemesDialog(MainActivity.this);
        cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onResume();
            }
        });
        cdd.show();
    }

    public void about(View view) {
        InfoDialog cdd=new InfoDialog(MainActivity.this);
        cdd.show();
    }

    private void logUser(SharedPreferences settings) {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserEmail(settings.getString("display_name", "Player1") + "@fabric.io");
        Crashlytics.setUserName(settings.getString("display_name", "Player1"));
    }

    public void showscoreboard(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imageclick));
        if(mListView.getVisibility() == View.GONE) {
            mListView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.leaderboard_animation_in));
            mListView.setVisibility(View.VISIBLE);
            button.setText(R.string.hsb);
        }
        else{
            mListView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.leaderboard_animation_out));
            mListView.setVisibility(View.GONE);
            button.setText(R.string.ssb);
        }
    }

    public void getUpdatedList() {
        mList.clear();
        String name;
        int level, _score;
        MoleStrikeDB db = new MoleStrikeDB(this);
        SQLiteDatabase dbHelper = db.getReadableDatabase();
        String[] projection = {
                MoleStrikeDB.player.COLUMN_NAME,
                MoleStrikeDB.player.COLUMN_TOP_SCORE,
                MoleStrikeDB.player.COLUMN_LEVEL
        };
        Cursor c = dbHelper.query(
                MoleStrikeDB.player.TABLE_NAME,                     // The table to query
                projection,
                null,
                null,
                null,
                null,
                "topScore DESC"
        );
        c.moveToFirst();
        for (int i=0; i<c.getCount(); i++){
            name = c.getString(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_NAME));
            level = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_LEVEL));
            _score = c.getInt(c.getColumnIndexOrThrow(MoleStrikeDB.player.COLUMN_TOP_SCORE));
            mList.add(i,name + "+" + String.valueOf(level) + "+" + String.valueOf(_score));
            if (name.equals(prefs.getString("display_name", "Player1"))) {
                score = _score;
                userLevel = level;
            }
            c.moveToNext();
        }
        dbHelper.close();
        ((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
        dataChanged = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Not connected!",Toast.LENGTH_LONG).show();
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this,ConnectionResult.SIGN_IN_REQUIRED);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
            return;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show();
        //After leaderboard is created - get top result from there!!! ------------------->>>>>>>>>>>>>>>>>
        new Thread(new Runnable() {
            @Override
            public void run() {
                int moles = prefs.getInt("total_moles", 0);
                if (moles>=50){
                    Log.d("MoleStrike", "Moles>50");
                    PendingResult p = Games.Achievements.load(mGoogleApiClient, false);
                    Achievements.LoadAchievementsResult r = (Achievements.LoadAchievementsResult) p.await(3, TimeUnit.SECONDS);
                    int status = r.getStatus().getStatusCode();
                    Log.d("MoleStrike", String.valueOf(r.getStatus().getStatusCode() + " " + r.getStatus().hasResolution()));
                    if (status != STATUS_OK) {
                        r.release();
                        return;
                    }
                    AchievementBuffer buf = r.getAchievements();
                    for (int i = 0; i < list.size(); i++) {
                        Achievement ach = buf.get(i);
                        Log.d("MoleStrike", "Buffer count - " + String.valueOf(buf.getCount()));
                        Log.d("MoleStrike", "i = " + String.valueOf(i) + " achievement is - " + ach.getAchievementId());
                        Log.d("MoleStrike", String.valueOf(ach.getState() + " " + Achievement.STATE_UNLOCKED));
                        switch (i) {
                            case 0:
                                if (ach.getState() != Achievement.STATE_UNLOCKED) {
                                    Games.Achievements.unlock(mGoogleApiClient, list.get(i));
                                    unlocked = true;
                                    Log.d("MoleStrike", "Unlocked");
                                }
                                break;
                            case 1:
                                if (ach.getState() != Achievement.STATE_UNLOCKED && moles > 124) {
                                    Games.Achievements.unlock(mGoogleApiClient, list.get(i));
                                    unlocked = true;
                                }
                                break;
                            case 2:
                                if (ach.getState() != Achievement.STATE_UNLOCKED && moles > 249) {
                                    Games.Achievements.unlock(mGoogleApiClient, list.get(i));
                                    unlocked = true;
                                }
                                break;
                            case 3:
                                if (ach.getState() != Achievement.STATE_UNLOCKED && prefs.getBoolean("man_machine", true)) {
                                    Games.Achievements.unlock(mGoogleApiClient, list.get(i));
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("man_machine", false);
                                    editor.apply();
                                    unlocked = true;
                                }
                                break;
                            case 4:
                                if (ach.getState() != Achievement.STATE_UNLOCKED && prefs.getInt("devil_moles", 0) > 149) {
                                    Games.Achievements.unlock(mGoogleApiClient, list.get(i));
                                    unlocked = true;
                                }
                                break;
                        }
                    }
                    r.release();
                    buf.release();
                }
                if (unlocked){

                }
            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

}
