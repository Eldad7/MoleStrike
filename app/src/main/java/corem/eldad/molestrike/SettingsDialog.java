package corem.eldad.molestrike;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

/**
 * Created by The Gate Keeper on 1/16/2017.
 */

public class SettingsDialog extends Dialog implements android.view.View.OnClickListener{


    public Activity c;
    public Dialog d;
    private EditText editText;
    private Context context;
    private SeekBar musicVolume, soundfxVolume;
    private Switch music, soundfx;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Music _music;

    public SettingsDialog(Context context, Music _music) {
        super(context);
        this.context = context;
        this._music = _music;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings_dialog);
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();
        String name = settings.getString("display_name", "DEFAULT");
        System.out.println("Username " + name);
        editText = (EditText) findViewById(R.id.display_name);
        editText.setText(name);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("display_name", String.valueOf(editText.getText()));
                editor.apply();
                changeUserName(String.valueOf(editText.getText()));
            }
        });
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
                    if (volume > 0.5f)
                        _music.setMusicVolume(volume);
                    else{
                        _music.setMusicVolume(0.5f);
                    }
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
        if (!music.isChecked())
            musicVolume.setProgress(0);
        else
            musicVolume.setProgress(volume);
        soundfxVolume = (SeekBar) findViewById(R.id.soundFXSeekbar);
        soundfxVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                _music.setFXRate(0.01f * seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putFloat("soundfx_volume", (0.01f * soundfxVolume.getProgress()));
                editor.apply();
            }
        });
        volume = (int) (100 * settings.getFloat("soundfx_volume", 1));
        if (!soundfx.isChecked())
            soundfxVolume.setProgress(0);
        else
            soundfxVolume.setProgress(volume);
    }

    private void changeUserName(String s) {
        MoleStrikeDB db = new MoleStrikeDB(context);
        final SQLiteDatabase dbHelper = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MoleStrikeDB.player.COLUMN_NAME, s);
        db.updateName(s, dbHelper);
    }

    @Override
    public void onClick(View v){
        editor = settings.edit();
        switch (v.getId()){
            case R.id.music:{
                editor.putBoolean("music", music.isChecked());
                if (music.isChecked()) {
                    musicVolume.setProgress(50);
                    editor.putFloat("music_volume", 0.5f);
                    _music.run();
                }
                else{
                    musicVolume.setProgress(0);
                    editor.putFloat("music_volume", 0f);
                    _music.pause();
                }
                editor.apply();
            }
            case R.id.soundfx: {
                editor.putBoolean("soundfx", soundfx.isChecked());
                if (soundfx.isChecked()) {
                    soundfxVolume.setProgress(50);
                    editor.putFloat("soundfx_volume", 0.5f);
                    _music.setFXRate(0.5f);
                }
                else{
                    soundfxVolume.setProgress(0);
                    editor.putFloat("soundfx_volume", 0f);
                }
                editor.apply();
            }
        }

        System.out.println(settings.getFloat("music_volume", 1));
    }

    private static Preference.OnPreferenceChangeListener
            sPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

//    private void initPref(String prefKey) {
//        //Preference preference = findPreference(prefKey);
//        SharedPreferences preference = this.getContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
//        preference.setOnPreferenceChangeListener(sPreferenceChangeListener);
//        sPreferenceChangeListener.onPreferenceChange(preference,
//                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), "(not set yet)"));
//
//    }
}
