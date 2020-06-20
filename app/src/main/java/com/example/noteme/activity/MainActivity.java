package com.example.noteme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteme.NotesConstants;
import com.example.noteme.R;
import com.example.noteme.adapter.Adapter;
import com.example.noteme.data.Note;
import com.example.noteme.data.NoteDatabase;
import com.example.noteme.utils.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 111;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ArrayList<Note> notes = new ArrayList<>();
    private RelativeLayout emptyLayout;
    private NoteDatabase db;
    private int lastNoteClickPosition = -1;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        db = new NoteDatabase(this);
        notes = db.getNotes();

        recyclerView = findViewById(R.id.listOfNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyLayout = findViewById(R.id.emptyLayout);
        parentLayout = findViewById(android.R.id.content);

        adapter = new Adapter(this, notes);
        recyclerView.setAdapter(adapter);
        toggleView();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotes();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // Toast.makeText(MainActivity.this,"Position" + position,Toast.LENGTH_SHORT).show();
                lastNoteClickPosition = position;
                Intent intent = new Intent(MainActivity.this, NoteAddEditDeleteActivity.class);
                intent.putExtra(NotesConstants.KEY_NOTE_ID, notes.get(position).getID());
                intent.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_EDITED);
                startActivityForResult(intent, REQUEST_CODE);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void showSnackBar(String Message) {

        Snackbar.make(parentLayout, Message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackBarWithButton(String Message) {
        Snackbar.make(parentLayout, Message, Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //uNDO LOGIC
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void toggleView() {
        if (!notes.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    private void addNotes() {
        Intent i = new Intent(this, NoteAddEditDeleteActivity.class);
        i.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_ADDED);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getStringExtra(NotesConstants.KEY_IS_FROM).equals(NotesConstants.NOTE_ADDED)) {
                    notes.add(0, db.getNote(data.getLongExtra(NotesConstants.KEY_NOTE_ID, 0)));
                    adapter.notifyDataSetChanged();
                    showSnackBar("Note Added");
                } else if (data.getStringExtra(NotesConstants.KEY_IS_FROM).equals(NotesConstants.NOTE_EDITED)) {
                    notes.set(lastNoteClickPosition, db.getNote(data.getLongExtra(NotesConstants.KEY_NOTE_ID, 0)));
                    adapter.notifyItemChanged(lastNoteClickPosition);
                    showSnackBar("Note Modified");
                } else if (data.getStringExtra(NotesConstants.KEY_IS_FROM).equals(NotesConstants.NOTE_DELETED)) {
                    notes.remove(lastNoteClickPosition);
                    adapter.notifyItemRemoved(lastNoteClickPosition);
                    showSnackBarWithButton("Note deleted");
                }
                toggleView();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
    ///NEW


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        //searchViewItem.setIcon()

        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                Toast.makeText(MainActivity.this, "Into onQuery Text Change", Toast.LENGTH_LONG).show();
                //callSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void callSearch() {
        Toast.makeText(MainActivity.this, "within Call Search", Toast.LENGTH_LONG).show();
    }
    ///END NEW
}