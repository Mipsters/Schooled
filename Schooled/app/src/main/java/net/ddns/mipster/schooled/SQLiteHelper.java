package net.ddns.mipster.schooled;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Chen on 18/03/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "data.db";

    public final static String NOTE_TABLE = "note_table",
                                NOTE_COL_X1 = "x1",
                                NOTE_COL_Y1 = "y1",
                                NOTE_COL_X2 = "x2",
                                NOTE_COL_Y2 = "y2",
                                NOTE_COL_TEXT = "text";


    public final static String ANNOUNCEMENT_TABLE = "announcement_table",
                                ANNOUNCEMENT_COL_TITLE = "title",
                                ANNOUNCEMENT_COL_TEXT = "text",
                                ANNOUNCEMENT_COL_DATE = "date",
                                ANNOUNCEMENT_COL_URL = "url";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NOTE_TABLE + " (" +
                NOTE_COL_X1   + " INT NOT NULL," +
                NOTE_COL_Y1   + " INT NOT NULL," +
                NOTE_COL_X2   + " INT NOT NULL," +
                NOTE_COL_Y2   + " INT NOT NULL," +
                NOTE_COL_TEXT + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + ANNOUNCEMENT_TABLE + " (" +
                ANNOUNCEMENT_COL_TITLE + " TEXT," +
                ANNOUNCEMENT_COL_TEXT  + " TEXT," +
                ANNOUNCEMENT_COL_DATE  + " TEXT," +
                ANNOUNCEMENT_COL_URL   + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ANNOUNCEMENT_TABLE);

        onCreate(db);
    }

    public void resetAnnouncement(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + ANNOUNCEMENT_TABLE);

        db.execSQL("CREATE TABLE " + ANNOUNCEMENT_TABLE + " (" +
                ANNOUNCEMENT_COL_TITLE + " TEXT," +
                ANNOUNCEMENT_COL_TEXT  + " TEXT," +
                ANNOUNCEMENT_COL_DATE  + " TEXT," +
                ANNOUNCEMENT_COL_URL   + " TEXT)");
    }

    public void resetNote() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);

        db.execSQL("CREATE TABLE " + NOTE_TABLE + " (" +
                NOTE_COL_X1   + " INT NOT NULL," +
                NOTE_COL_Y1   + " INT NOT NULL," +
                NOTE_COL_X2   + " INT NOT NULL," +
                NOTE_COL_Y2   + " INT NOT NULL," +
                NOTE_COL_TEXT + " TEXT NOT NULL)");
    }

    public boolean insertDataNote(int x1, int y1, int x2, int y2, String text){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(NOTE_COL_X1, x1);
        cv.put(NOTE_COL_Y1, y1);
        cv.put(NOTE_COL_X2, x2);
        cv.put(NOTE_COL_Y2, y2);
        cv.put(NOTE_COL_TEXT, text);

        return db.insert(NOTE_TABLE, null, cv) != -1;
    }

    public boolean insertDataAnnouncement(String title, String text, String date, String url){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ANNOUNCEMENT_COL_TITLE, title);
        cv.put(ANNOUNCEMENT_COL_TEXT, text);
        cv.put(ANNOUNCEMENT_COL_DATE, date);
        cv.put(ANNOUNCEMENT_COL_URL, url);

        return db.insert(ANNOUNCEMENT_TABLE, null, cv) != -1;
    }

    public Cursor select(String table, String[] col, String condition){
        return getReadableDatabase().query(table, col, condition, null, null, null, null);
    }

    public  Cursor getAllData(String table){
        return getReadableDatabase().rawQuery("SELECT * FROM " + table, null);
    }
}