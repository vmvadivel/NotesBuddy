package com.vadivel.notesbuddy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NoteDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notesdb"; //Vadivel Learning: initially had string lowercase s which throwed an error cannot resolve method super
    private static final String DATABASE_TABLE = "notestable"; //Vadivel Learning: initially had string lowercase s which throwed an error cannot resolve method super


    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE_TIME = "date_time";
    private static final String KEY_COLOR = "color";

    public NoteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DATABASE_TABLE + "( " + KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_TITLE + " TEXT," +
                KEY_CONTENT + " TEXT," +
                KEY_DATE_TIME + " LONG," +
                KEY_COLOR + " TEXT " + ")";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public long addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(KEY_TITLE, note.getTitle());
        c.put(KEY_CONTENT, note.getContent());
        c.put(KEY_DATE_TIME, note.getCurrentTimestamp());
        c.put(KEY_COLOR, note.getColor());

        long ID = db.insert(DATABASE_TABLE, null, c);

        db.close();
        return ID;
    }

    public Note getNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        //String Query = "SELECT KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_TIME FROM " + DATABASE_TABLE + " WHERE KEY_ID = " + id;
        //db.execSQL(Query);

        //Vadivel - its a pointer to a specific record in our table.
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE_TIME, KEY_COLOR}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        //cursor will start with -1 index so we need to move to the first record manually
        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note(cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_CONTENT)),
                cursor.getLong(cursor.getColumnIndex(KEY_DATE_TIME)),
                cursor.getString(cursor.getColumnIndex(KEY_COLOR)));

        //Vadivel: Things typed like the one below and opted for Inline variable from the tip (bulb icon) converts to what it looks in the subsequent line
        //Note note = new Note(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        cursor.close();
        db.close();
        return note;
    }

    //Its a generic List
    public ArrayList<Note> getNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Note> allNotes = new ArrayList<>();

        // String Query = "SELECT KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_TIME FROM " + DATABASE_TABLE;
        String Query = "SELECT  * FROM " + DATABASE_TABLE + " ORDER BY " + KEY_ID + " DESC";

        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setID(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                note.setCurrentTimestamp(cursor.getLong(cursor.getColumnIndex(KEY_DATE_TIME)));
                note.setColor(cursor.getString(cursor.getColumnIndex(KEY_COLOR)));
                allNotes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return allNotes;
    }

    public long editNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, note.getTitle());
        contentValues.put(KEY_CONTENT, note.getContent());
        contentValues.put(KEY_DATE_TIME, note.getCurrentTimestamp());
        contentValues.put(KEY_COLOR, note.getColor());

        long id = db.update(DATABASE_TABLE, contentValues, KEY_ID + "=?", new String[]{String.valueOf(note.getID())});
        db.close();
        return id;
    }

    public void deleteNote(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}