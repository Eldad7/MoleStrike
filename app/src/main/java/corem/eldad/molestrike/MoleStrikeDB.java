package corem.eldad.molestrike;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by eldadc on 04/01/2017.
 */

public class MoleStrikeDB extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMBER_TYPE = " INTEGER";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + player.TABLE_NAME + " (" + player.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    player.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +  player.COLUMN_TOP_SCORE + NUMBER_TYPE + COMMA_SEP +
                    player.COLUMN_LEVEL + NUMBER_TYPE + COMMA_SEP + player.COLUMN_PLAYER + BOOLEAN_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + player.TABLE_NAME;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mole_strike.db";

    public MoleStrikeDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static class player implements BaseColumns {
        public static final String TABLE_NAME = "player";
        public static final String COLUMN_NAME = "fullName";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_TOP_SCORE = "topScore";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_PLAYER = "player";
    }

    public void updateTopScore(String fullName, int topScore, SQLiteDatabase db) {
        this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(player.COLUMN_TOP_SCORE, topScore);
        int rows = db.update(player.TABLE_NAME, values, player.COLUMN_NAME + " = ? ", new String[]{fullName});
    }

    public void updateLevel(String fullName, int Level, SQLiteDatabase db) {
        this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(player.COLUMN_LEVEL, Level);
        int rows = db.update(player.TABLE_NAME, values, player.COLUMN_NAME + " = ? ", new String[]{fullName});
    }

    public void updateName(String newName, SQLiteDatabase db) {
        this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(player.COLUMN_NAME, newName);
        int rows = db.update(player.TABLE_NAME, values, player.COLUMN_PLAYER + " = ? ", new String[]{"1"});
    }

    /*public void facebookLogin(String fullName, String email, String oldName, SQLiteDatabase db){
        this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(player.COLUMN_NAME, fullName);
        values.put(player.COLUMN_EMAIL, email);
        int rows = db.update(player.TABLE_NAME, values, player.COLUMN_NAME + " = ? ", new String[]{oldName});
    }
    * This is for next version
    * */
}
