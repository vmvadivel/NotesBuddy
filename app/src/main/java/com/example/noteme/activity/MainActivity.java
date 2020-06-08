package com.example.noteme.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.noteme.adapter.Adapter;
import com.example.noteme.data.Note;
import com.example.noteme.data.NoteDatabase;
import com.example.noteme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Adapter adapter;
    List<Note> notes;
    TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        setSupportActionBar(toolbar);

        NoteDatabase db = new NoteDatabase(this);
        notes = db.getNotes();

        recyclerView = findViewById(R.id.listOfNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyText = findViewById(R.id.emptyText);
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 0));

        adapter = new Adapter(this, notes);

        if(!notes.isEmpty()){
            emptyText.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
        }
        else{
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotes();
            }
        });
    }

    private void addNotes(){
        Intent i = new Intent(this,AddNote.class);
        startActivity(i);
    }
}