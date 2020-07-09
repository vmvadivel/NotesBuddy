package com.vadivel.notesbuddy.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.vadivel.notesbuddy.NotesConstants;
import com.vadivel.notesbuddy.R;
import com.vadivel.notesbuddy.adapter.Adapter;
import com.vadivel.notesbuddy.data.Note;
import com.vadivel.notesbuddy.data.NoteDatabase;
import com.vadivel.notesbuddy.utils.AppFeedBackUtil;
import com.vadivel.notesbuddy.utils.RecyclerTouchListener;
import com.vadivel.notesbuddy.utils.SwipeToDeleteItemSupportCallBack;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 111;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private static final String PREF_LAYOUT_MODE = "pref_layout_mode";
    private Adapter adapter;
    private ArrayList<Note> notes = new ArrayList<>();
    private RelativeLayout emptyLayout;
    private NoteDatabase db;
    private int lastNoteClickPosition = -1;
    private CoordinatorLayout coordinatorLayout;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppFeedBackUtil.appLaunched(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = findViewById(R.id.toolbar);

        Field mCollapseIcon = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mCollapseIcon = toolbar.getClass().getDeclaredField("mCollapseIcon");
                mCollapseIcon.setAccessible(true);
                ((Drawable) mCollapseIcon.get(toolbar)).setTint(Color.parseColor("#2C2C2C"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);

        db = new NoteDatabase(this);
        notes = db.getNotes();

        recyclerView = findViewById(R.id.listOfNotes);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager((getVerticalMode() ? 1 : 2), StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        emptyLayout = findViewById(R.id.emptyLayout);
        coordinatorLayout = findViewById(R.id.root_coordinator_layout);

        adapter = new Adapter(notes);
        recyclerView.setAdapter(adapter);
        toggleView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotes();
            }
        });

        attachSwipeToDeleteRecyclerView();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
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
        Snackbar.make(coordinatorLayout, Message, Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackBarWithButton(String Message) {
        Snackbar.make(coordinatorLayout, Message, Snackbar.LENGTH_SHORT)
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
                    showSnackBar("Note Deleted");
                }
                toggleView();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //  showSnackBar("Note Changes Discarded");
            }
        }
    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        MenuItem toggleMenuItem = menu.findItem(R.id.toggleLayout);
        toggleMenuItem.setIcon(getVerticalMode() ? R.drawable.ic_baseline_grid_24 : R.drawable.ic_baseline_linear_view_24);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);

        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        ImageView searchClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_search_close);

        EditText editText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setHintTextColor(Color.parseColor("#696969"));
        editText.setTextColor(Color.parseColor("#3D3D3D"));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toggleLayout) {
            //Toggle the image and layout
            if (staggeredGridLayoutManager.getSpanCount() == 1) {
                item.setIcon(R.drawable.ic_baseline_linear_view_24);
                staggeredGridLayoutManager.setSpanCount(2);
                setVerticalMode(false);
            } else {

                item.setIcon(R.drawable.ic_baseline_grid_24);
                staggeredGridLayoutManager.setSpanCount(1);
                setVerticalMode(true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean getVerticalMode() {
        return mSharedPreferences.getBoolean(PREF_LAYOUT_MODE, true);
    }

    private void setVerticalMode(Boolean mode) {
        mSharedPreferences.edit().putBoolean(PREF_LAYOUT_MODE, mode).apply();
    }

    private void attachSwipeToDeleteRecyclerView() {
        SwipeToDeleteItemSupportCallBack swipeToDeleteCallback = new SwipeToDeleteItemSupportCallBack(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Note item = adapter.getNotesFromAdapter().get(position);
                db.deleteNote(item.getID());
                adapter.removeNoteFromAdapter(position);
                showSnackBar("Note Deleted");
                toggleView();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}