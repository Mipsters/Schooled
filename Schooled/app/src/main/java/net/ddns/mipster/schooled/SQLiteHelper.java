package net.ddns.mipster.schooled;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import org.apache.xmlbeans.StringEnumAbstractBase;

/**
 * Created by Chen on 18/03/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "SQL_DATA.db";

    public enum Tables {
        NOTE,
        ANNOUNCEMENT,
        SCHEDULE,
        CLASS;

        @Override
        public String toString() {
            switch (this){
                case NOTE:
                    return "note_table";
                case ANNOUNCEMENT:
                    return "announcement_table";
                case SCHEDULE:
                    return "schedule_table";
                case CLASS:
                    return "class_table";
            }
            return null;
        }
    }

    private final static String NOTE_COL_X1 = "x1",
                                NOTE_COL_Y1 = "y1",
                                NOTE_COL_X2 = "x2",
                                NOTE_COL_Y2 = "y2",
                                NOTE_COL_TEXT = "text";


    private final static String ANNOUNCEMENT_COL_TITLE = "title",
                               ANNOUNCEMENT_COL_TEXT = "text",
                               ANNOUNCEMENT_COL_DATE = "date",
                               ANNOUNCEMENT_COL_URL = "url";

    private final static String SCHEDULE_COL_GENERIC = "col";

    private final static String CLASS_COL = "classes";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        onUpgrade(getWritableDatabase(), 1, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.NOTE + " (" +
                NOTE_COL_X1   + " INT NOT NULL," +
                NOTE_COL_Y1   + " INT NOT NULL," +
                NOTE_COL_X2   + " INT NOT NULL," +
                NOTE_COL_Y2   + " INT NOT NULL," +
                NOTE_COL_TEXT + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.ANNOUNCEMENT + " (" +
                ANNOUNCEMENT_COL_TITLE + " TEXT," +
                ANNOUNCEMENT_COL_TEXT  + " TEXT," +
                ANNOUNCEMENT_COL_DATE  + " TEXT," +
                ANNOUNCEMENT_COL_URL   + " TEXT)");

        db.execSQL("CREATE TABLE " + Tables.CLASS + " (" +
                CLASS_COL + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ANNOUNCEMENT);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CLASS);

        onCreate(db);
    }

    public void createScheduleTable(int size){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULE);

        String command = "CREATE TABLE " + Tables.SCHEDULE + " (" ;
        for(int i = 0; i < size - 1; i++)
            command += SCHEDULE_COL_GENERIC + i + " TEXT,";
        command += SCHEDULE_COL_GENERIC + (size - 1) + " TEXT)";

        db.execSQL(command);
    }

    public void resetAnnouncement(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + Tables.ANNOUNCEMENT);

        db.execSQL("CREATE TABLE " + Tables.ANNOUNCEMENT + " (" +
                ANNOUNCEMENT_COL_TITLE + " TEXT," +
                ANNOUNCEMENT_COL_TEXT  + " TEXT," +
                ANNOUNCEMENT_COL_DATE  + " TEXT," +
                ANNOUNCEMENT_COL_URL   + " TEXT)");
    }

    public void resetNote() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + Tables.NOTE);

        db.execSQL("CREATE TABLE " + Tables.NOTE + " (" +
                NOTE_COL_X1   + " INT NOT NULL," +
                NOTE_COL_Y1   + " INT NOT NULL," +
                NOTE_COL_X2   + " INT NOT NULL," +
                NOTE_COL_Y2   + " INT NOT NULL," +
                NOTE_COL_TEXT + " TEXT NOT NULL)");
    }

    public void resetClass() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + Tables.CLASS);

        db.execSQL("CREATE TABLE " + Tables.CLASS + " (" +
                CLASS_COL + " TEXT NOT NULL)");
    }

    public void insertDataNote(int x1, int y1, int x2, int y2, String text){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(NOTE_COL_X1, x1);
        cv.put(NOTE_COL_Y1, y1);
        cv.put(NOTE_COL_X2, x2);
        cv.put(NOTE_COL_Y2, y2);
        cv.put(NOTE_COL_TEXT, text);

        db.insert(Tables.NOTE.toString(), null, cv);
    }

    public void insertDataAnnouncement(String title, String text, String date, String url){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ANNOUNCEMENT_COL_TITLE, title);
        cv.put(ANNOUNCEMENT_COL_DATE, date);
        cv.put(ANNOUNCEMENT_COL_TEXT, text);
        cv.put(ANNOUNCEMENT_COL_URL, url);

        db.insert(Tables.ANNOUNCEMENT.toString(), null, cv);
    }

    public void insertDataSchedule(String[] text){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        for(int i = 0; i < text.length; i++)
            cv.put(SCHEDULE_COL_GENERIC + i, text[i]);

        db.insert(Tables.SCHEDULE.toString(), null, cv);
    }

    public void insertDataClass(String cls){
        ContentValues cv = new ContentValues();
        cv.put(CLASS_COL, cls);
        getWritableDatabase().insert(Tables.CLASS.toString(), null, cv);
    }

    public Cursor getAllData(Tables table){
        Cursor out = null;
        try {
            out = getReadableDatabase().rawQuery("SELECT * FROM " + table, null);
        } catch (Exception e){
            e.printStackTrace();
        }

        return out;
    }
}