package corem.eldad.molestrike;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

/**
 * Created by The Gate Keeper on 1/16/2017.
 *
 */

public class SettingsDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public boolean signIn = false, facebookSignIn = false;
    private EditText editText;
    private Context context;
    private SignInButton signInButton;
    private SeekBar musicVolume, soundfxVolume;
    private Switch music, soundfx;
    private Button showAchievements;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Music _music;
    String newName = null;
    private LoginButton login;
    CallbackManager callbackManager;

    public SettingsDialog(Context context, Music _music) {
        super(context);
        this.context = context;
        this._music = _music;
    }

    public boolean getSignIn(){return signIn;}

    public boolean getFacebookSignIn(){return facebookSignIn;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings_dialog);
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();
        editText = (EditText) findViewById(R.id.display_name);
        editText.setText(settings.getString("display_name", "DEFAULT"));
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        showAchievements = (Button) findViewById(R.id.show_achievements);
        login = (LoginButton) findViewById(R.id.login_button);
        login.setReadPermissions("public_profile email");
        login.setOnClickListener(this);
        if (!settings.getBoolean("google_play_services", false)){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    newName = String.valueOf(editText.getText());
                    changeUserName(String.valueOf(editText.getText()));
                }
            });
        }
        else {
            signInButton.setVisibility(View.GONE);
            signInButton.setOnClickListener(null);
            editText.setEnabled(false);
        }
        if (settings.getBoolean("facebook", false))
            login.setVisibility(View.GONE);
        music = (Switch) findViewById(R.id.music);
        music.setOnClickListener(this);
        music.setChecked(settings.getBoolean("music", true));
        soundfx = (Switch) findViewById(R.id.soundfx);
        soundfx.setOnClickListener(this);
        soundfx.setChecked(settings.getBoolean("soundfx", true));
        musicVolume = (SeekBar) findViewById(R.id.musicSeekbar);
        musicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (_music.musicIsPlaying) {
                    float volume = (0.01f * seekBar.getProgress());
                    _music.setMusicVolume(volume);
                    if (!music.isChecked())
                        music.setChecked(true);
                }
                if (seekBar.getProgress() == 0) {
                    editor.putBoolean("music", false);
                    editor.apply();
                    music.setChecked(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putFloat("music_volume", (0.01f * musicVolume.getProgress()));
                editor.apply();
            }
        });
        int volume = (int) (100 * settings.getFloat("music_volume", 1));
        if (!music.isChecked()) {
            musicVolume.setProgress(0);
            musicVolume.setFocusable(false);
            musicVolume.setEnabled(false);
        } else
            musicVolume.setProgress(volume);
        soundfxVolume = (SeekBar) findViewById(R.id.soundFXSeekbar);
        soundfxVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                _music.setFXVolume(0.01f * seekBar.getProgress());
                if (seekBar.getProgress() == 0) {
                    editor.putBoolean("soundfx", false);
                    editor.apply();
                    soundfx.setChecked(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putFloat("soundfx_volume", (0.01f * soundfxVolume.getProgress()));
                editor.apply();
            }
        });
        volume = (int) (100 * settings.getFloat("soundfx_volume", 1));
        if (!soundfx.isChecked()) {
            soundfxVolume.setProgress(0);
            soundfxVolume.setFocusable(false);
            soundfxVolume.setEnabled(false);
        } else
            soundfxVolume.setProgress(volume);
    }

    public void setCallbackManager(CallbackManager callbackManager){
        this.callbackManager = callbackManager;
        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                Log.d("Molestrike", "Success facebook");
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("Molestrike", "cancel facebook");
            }

            @Override
            public void onError(final FacebookException exception) {
                // App code
                Log.d("Molestrike", "fail facebook");
            }
        });
    }

    @Override
    public void onDetachedFromWindow(){
        if (newName != null) {
            editor.putString("display_name", String.valueOf(editText.getText()));
            editor.apply();
        }
    }

    private void changeUserName(String name) {
        MoleStrikeDB db = new MoleStrikeDB(context);
        final SQLiteDatabase dbHelper = db.getWritableDatabase();
        db.updateName(name, dbHelper);
        MainActivity.dataChanged=true;
    }

    @Override
    public void onClick(View v){
        editor = settings.edit();
        switch (v.getId()){
            case R.id.music:{
                editor.putBoolean("music", music.isChecked());
                if (music.isChecked()) {
                    musicVolume.setFocusable(true);
                    musicVolume.setEnabled(true);
                    musicVolume.setProgress(50);
                    editor.putFloat("music_volume", 0.5f);
                    if (_music.getMusicIsPlaying())
                        _music.setMusicIsPlaying(false);
                    _music.run();
                    _music.setMusicVolume(0.5f);
                }
                else{
                    musicVolume.setProgress(0);
                    editor.putFloat("music_volume", 0f);
                    _music.pause();
                    musicVolume.setFocusable(false);
                    musicVolume.setEnabled(false);
                }
                editor.apply();
                break;
            }
            case R.id.soundfx: {
                editor.putBoolean("soundfx", soundfx.isChecked());
                if (soundfx.isChecked()) {
                    soundfxVolume.setProgress(50);
                    editor.putFloat("soundfx_volume", 0.5f);
                    _music.setFXVolume(0.5f);
                    soundfxVolume.setFocusable(true);
                    soundfxVolume.setEnabled(true);
                }
                else{
                    soundfxVolume.setProgress(0);
                    editor.putFloat("soundfx_volume", 0f);

                    soundfxVolume.setFocusable(false);
                    soundfxVolume.setEnabled(false);
                }
                editor.apply();
                break;
            }
            case R.id.sign_in_button: {
                signIn = true;
                dismiss();
                break;
            }

            case R.id.login_button: {
                facebookSignIn = true;
                dismiss();
                break;
            }
        }
    }
}
