package com.vadivel.notesbuddy.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
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
import java.util.Collections;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private final int REQUEST_CODE = 111;
    private RecyclerView recyclerView;
    private ScrollView emptyLayout;
    private SearchView searchView;
    private CoordinatorLayout coordinatorLayout;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private Toolbar multiSelectToolbar;
    private AppCompatTextView multiSelectCountTxt;
    private int lastClickPosition = -1;
    private int totalSelected = 0;
    private static final String PREF_LAYOUT_MODE = "pref_layout_mode";
    private boolean isMultiSelect;

    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayList<Long> toBeDeletedNoteIDs = new ArrayList<>();

    private Adapter adapter;
    private NoteDatabase db;
    private SharedPreferences mSharedPreferences;
    private ItemTouchHelper itemTouchhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        AppFeedBackUtil.appLaunched(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        multiSelectToolbar = findViewById(R.id.multiSelectToolbar);
        multiSelectToolbar.setVisibility(View.GONE);

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

        db = new NoteDatabase(this);
        notes = db.getNotes();

        recyclerView = findViewById(R.id.listOfNotes);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        staggeredGridLayoutManager = new StaggeredGridLayoutManager((getVerticalMode() ? 1 : 2), StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        attachSwipeToDeleteRecyclerView();
        emptyLayout = findViewById(R.id.emptyLayout);
        coordinatorLayout = findViewById(R.id.root_coordinator_layout);

        (findViewById(R.id.multiSelectClose)).setOnClickListener(this);
        (findViewById(R.id.multiSelectDelete)).setOnClickListener(this);
        multiSelectCountTxt = findViewById(R.id.multiSelectCount);

        adapter = new Adapter(notes, this);
        recyclerView.setAdapter(adapter);
        toggleView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addNotes());

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (!isMultiSelect) {
                    lastClickPosition = position;
                    Intent intent = new Intent(MainActivity.this, NoteAddEditDeleteActivity.class);
                    intent.putExtra(NotesConstants.KEY_NOTE_ID, adapter.getNotesFromAdapter().get(position).getID());
                    intent.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_EDITED);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    multiSelectEnabledClick(position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (!isMultiSelect) {
                    itemTouchhelper.attachToRecyclerView(null);
                    resetMultiSelectToolbar(true);
                }

                multiSelectEnabledClick(position);
            }
        }));
    }

    private void multiSelectEnabledClick(int position) {
        if (adapter.getNotesFromAdapter().get(position).isSelected() == false) {
            adapter.getNotesFromAdapter().get(position).setSelected(true);
            totalSelected++;
            toBeDeletedNoteIDs.add(adapter.getNotesFromAdapter().get(position).getID());
        } else {
            adapter.getNotesFromAdapter().get(position).setSelected(false);
            totalSelected--;
            toBeDeletedNoteIDs.remove(adapter.getNotesFromAdapter().get(position).getID());
        }

        if (totalSelected != 0) {
            multiSelectCountTxt.setText(totalSelected + " Selected");
        } else {
            resetMultiSelectToolbar(false);
        }
        adapter.notifyDataSetChanged();
    }

    private void resetMultiSelectToolbar(boolean isEnabled) {
        multiSelectToolbar.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        toolbar.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        isMultiSelect = isEnabled;
        itemTouchhelper.attachToRecyclerView(isEnabled ? null : recyclerView);
        toBeDeletedNoteIDs.clear();
    }

    private void showSnackBar(String Message) {
        Snackbar.make(coordinatorLayout, Message, Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackBarWithButton(Note note, int position) {
        Snackbar.make(coordinatorLayout, "Note Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.softDelete(note.getID(), false);
                        notes.add(position, note);

                        adapter.updateSearchResultNotes(notes);

                        if (searchView.getQuery().toString().trim().length() > 0) {
                            adapter.getFilter().filter(searchView.getQuery());

                            Collections.sort(notes, (s1, s2) ->
                                    Long.compare(s2.getID(), s1.getID()));
                        }


                        adapter.notifyDataSetChanged();
                        toggleView();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
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
                    if (data.getBooleanExtra(NotesConstants.KEY_NOTE_NOT_MODIFIED, false) == false) {
                        long id = data.getLongExtra(NotesConstants.KEY_NOTE_ID, 0);
                        setUpdatedNote(notes, id);
                        setUpdatedNote(adapter.getNotesFromAdapter(), id);
                        if (searchView.getQuery().toString().trim().length() > 0) {
                            adapter.getFilter().filter(searchView.getQuery());
                        }
                        adapter.notifyDataSetChanged();
                        showSnackBar("Note Modified");
                    }
                } else if (data.getStringExtra(NotesConstants.KEY_IS_FROM).equals(NotesConstants.NOTE_DELETED)) {
                    long id = data.getLongExtra(NotesConstants.KEY_NOTE_ID, 0);
                    removeNoteFromList(id);
                    showSnackBarWithButton(db.getNote(id), lastClickPosition);
                }
                toggleView();
            }
            if (resultCode == Activity.RESULT_CANCELED) {

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

        searchView = (SearchView) searchViewItem.getActionView();

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toggleLayout) {
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

                db.softDelete(item.getID(), true);
                removeNoteFromList(item.getID());
                showSnackBarWithButton(item, position);
                toggleView();
            }
        };
        itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    private void removeNoteFromList(long id) {
        removeNote(notes, id);

        if (searchView.getQuery().toString().trim().length() > 0) {
            adapter.getFilter().filter(searchView.getQuery());
        }

        adapter.notifyDataSetChanged();
    }

    private void setUpdatedNote(ArrayList<Note> notes, Long noteID) {
        Note updateNote = db.getNote(noteID);
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getID() == noteID) {
                notes.set(i, updateNote);
                break;
            }
        }
    }

    private void removeNote(ArrayList<Note> notes, Long noteID) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getID() == noteID) {
                notes.remove(i);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.multiSelectClose:
                unSelectAll();
                break;
            case R.id.multiSelectDelete:

                String strList = TextUtils.join(",", toBeDeletedNoteIDs);
                //Todo: softdelete operations to be done here

                for (Iterator<Note> iter = notes.iterator(); iter.hasNext(); ) {
                    Note element = iter.next();
                    if (element.isSelected()) {
                        iter.remove();
                        //Todo: to be deleted after fixing line 373
                        db.softDelete(element.getID(), true);
                    }
                }

                adapter.updateSearchResultNotes(notes);

                if (searchView.getQuery().toString().trim().length() > 0) {
                    adapter.getFilter().filter(searchView.getQuery());
                }

                SnackBarMultiSelectDelete();
                itemTouchhelper.attachToRecyclerView(recyclerView);
                adapter.notifyDataSetChanged();
                totalSelected = 0;
                multiSelectToolbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                isMultiSelect = false;
                toggleView();
                break;
            default:
                break;
        }
    }

    private void SnackBarMultiSelectDelete() {
        Snackbar.make(coordinatorLayout, "Note Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", view -> {
                    //Todo: softdelete
                    for (Long undoID : toBeDeletedNoteIDs) {
                        db.softDelete(undoID, false);
                        notes.add(db.getNote(undoID));
                    }
                    toBeDeletedNoteIDs.clear();

                    Collections.sort(notes, (s1, s2) ->
                            Long.compare(s2.getID(), s1.getID()));

                    adapter.updateSearchResultNotes(notes);

                    if (searchView.getQuery().toString().trim().length() > 0) {
                        adapter.getFilter().filter(searchView.getQuery());
                    }
                    adapter.notifyDataSetChanged();
                    toggleView();
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private void unSelectAll() {
        resetMultiSelectToolbar(false);
        totalSelected = 0;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).isSelected()) {
                notes.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
        //Todo: Swipe delete to be disabled
        //Todo: backpress during search
    }

    @Override
    public void onBackPressed() {
        if (isMultiSelect) {
            unSelectAll();
        } else {
            super.onBackPressed();
        }

    }
}