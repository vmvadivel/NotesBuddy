package com.example.noteme.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.noteme.NotesConstants;
import com.example.noteme.R;
import com.example.noteme.data.Note;
import com.example.noteme.data.NoteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class NoteAddEditDeleteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CoordinatorLayout contentLayout;
    private EditText noteTitle, noteDetails;
    private TextView dateText;
    private String noteColor = "#ffffff";
    private String isFrom;
    private long nid;
    private NoteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_delete);
        isFrom = getIntent().getStringExtra(NotesConstants.KEY_IS_FROM);

        db = new NoteDatabase(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to display back button

        contentLayout = findViewById(R.id.contentParent);
        noteTitle = findViewById(R.id.noteTitle);
        noteDetails = findViewById(R.id.noteDetails);
        dateText = findViewById(R.id.last_modification_date);

        //Edit
        if (!isFrom.equals(NotesConstants.NOTE_ADDED)) {
            nid = getIntent().getLongExtra(NotesConstants.KEY_NOTE_ID, 0);
            Note note = db.getNote(nid);
            noteTitle.setText(note.getTitle());
            noteDetails.setText(note.getContent());
            noteColor = note.getColor();
            dateText.setText("Edited " + getFormattedDate(note.getCurrentTimestamp()));
        } else {
            dateText.setText("Edited " + new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));
        }

        toolbar.setBackgroundColor(Color.parseColor(noteColor));
        contentLayout.setBackgroundColor(Color.parseColor(noteColor));
    }


    public String getFormattedDate(long timeInMilis) {
        Calendar noteUpdatedTime = Calendar.getInstance(Locale.ENGLISH);
        noteUpdatedTime.setTimeInMillis(timeInMilis);
        Calendar now = Calendar.getInstance(Locale.ENGLISH);
        final String timeFormatString = "h:mm aa";
        boolean isSameMonthYear = (now.get(Calendar.MONTH) == noteUpdatedTime.get(Calendar.MONTH)) &&
                (now.get(Calendar.YEAR) == noteUpdatedTime.get(Calendar.YEAR));

        if (now.get(Calendar.DATE) == noteUpdatedTime.get(Calendar.DATE) && isSameMonthYear) {
            return "Today " + DateFormat.format(timeFormatString, noteUpdatedTime);
        } else if ((now.get(Calendar.DATE) - noteUpdatedTime.get(Calendar.DATE) == 1) && isSameMonthYear) {
            return "Yesterday " + DateFormat.format(timeFormatString, noteUpdatedTime);
        } else {
            return DateFormat.format("MMM d', 'yyyy", noteUpdatedTime).toString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        //Add
        if (isFrom.equals(NotesConstants.NOTE_ADDED)) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        setPageColor(); ///Bottomsheet

        Intent returnIntent = new Intent();
        if (item.getItemId() == R.id.save) {
            //ADD
            if (isFrom.equals(NotesConstants.NOTE_ADDED)) {
                Note note = new Note(noteTitle.getText().toString(), noteDetails.getText().toString(), System.currentTimeMillis(), noteColor);
                long id = db.addNote(note);
                returnIntent.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_ADDED);
                returnIntent.putExtra(NotesConstants.KEY_NOTE_ID, id);
                setResult(Activity.RESULT_OK, returnIntent);
                onBackPressed();
            } else { //Edit
                Note note = new Note(nid, noteTitle.getText().toString(), noteDetails.getText().toString(), System.currentTimeMillis(), noteColor);
                db.editNote(note);
                returnIntent.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_EDITED);
                returnIntent.putExtra(NotesConstants.KEY_NOTE_ID, nid);
                setResult(Activity.RESULT_OK, returnIntent);
                onBackPressed();
            }
        } else if (item.getItemId() == R.id.delete) {
            db.deleteNote(nid);
            returnIntent.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_DELETED);
            setResult(Activity.RESULT_OK, returnIntent);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPageColor() {
        Random rn = new Random();
        int iRnd = rn.nextInt(6);

        if (iRnd == 0) {
            noteColor = "#A7FEEB"; //Cyan
        } else if (iRnd == 1) {
            noteColor = "#80D8FE";
        } else if (iRnd == 2) {
            noteColor = "#F9BBD0";
        } else if (iRnd == 3) {
            noteColor = "#D7CCC8";
        } else if (iRnd == 4) {
            noteColor = "#FF8A80";
        } else {
            noteColor = "#CFD8DD";
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}