package com.example.noteme.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noteme.data.Note;
import com.example.noteme.data.NoteDatabase;
import com.example.noteme.R;

import java.util.Calendar;

public class Edit extends AppCompatActivity {

    Toolbar toolbar;
    EditText noteTitle, noteDetails;
    Calendar c;
    String todaysDate;
    String currentTime;
    long nid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent i = getIntent();
        nid = i.getLongExtra("ID",0);

        NoteDatabase db = new NoteDatabase(this);
        Note note = db.getNote(nid);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white)); //white color style added in styles.xml file
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(note.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to display back button

        noteTitle = findViewById(R.id.noteTitle);
        noteDetails = findViewById(R.id.noteDetails);

        noteTitle.setText(note.getTitle());
        noteDetails.setText(note.getContent());

        //When we type the note title for it to appear in the toolbar
        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    getSupportActionBar().setTitle(s);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Fetch the current date & time to be used while a new note is getting added
        c = Calendar.getInstance();

        //Month is 0 based so add 1 to it
        todaysDate = c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH);

        //If hours/Minutes is less than 10 lets pad it with 0
        currentTime = padZero(c.get(Calendar.HOUR)) + ":" + padZero(c.get(Calendar.MINUTE));

        //Log to cross check whether date and time is taken correctly
        Log.d("Calendar", "Data and Time: " + todaysDate + " " + currentTime);
    }

    private String padZero(int i) {
        if (i < 10)
            return ("0"+i);
        return String.valueOf(i);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.save){
            Note note = new Note(nid, noteTitle.getText().toString(), noteDetails.getText().toString(), todaysDate, currentTime);
          //  Log.d("EDITED", "edited: before saving id -> " + note.getID());
            NoteDatabase sDB = new NoteDatabase(getApplicationContext());
            long id = sDB.editNote(note);
            Log.d("EDITED", "EDIT: id " + id);
            goToMain();
            Toast.makeText(this, "Note Edited.", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.delete){
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(this, "On back pressed", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //Toast.makeText(this, "On support back pressed", Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }
}