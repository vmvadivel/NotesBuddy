package com.vadivel.notesbuddy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NoteDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notesdb";
    private static final String DATABASE_TABLE = "notestable";

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

        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE_TIME, KEY_COLOR}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note(cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_CONTENT)),
                cursor.getLong(cursor.getColumnIndex(KEY_DATE_TIME)),
                cursor.getString(cursor.getColumnIndex(KEY_COLOR)));

        cursor.close();
        db.close();
        return note;
    }

    public ArrayList<Note> getNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Note> allNotes = new ArrayList<>();

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