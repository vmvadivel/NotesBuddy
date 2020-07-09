package com.vadivel.notesbuddy.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vadivel.notesbuddy.NotesConstants;
import com.vadivel.notesbuddy.R;
import com.vadivel.notesbuddy.data.Note;
import com.vadivel.notesbuddy.data.NoteDatabase;
import com.vadivel.notesbuddy.utils.AppFeedBackUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NoteAddEditDeleteActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private CoordinatorLayout contentLayout;
    private AppCompatEditText noteTitle, noteDetails;
    private TextView dateText;
    private String noteColor = "#FAFAFA";
    private String isFrom;
    private long nid;
    private NoteDatabase db;
    private RadioGroup colorPickerRadioGroup;
    private LinearLayout colorSectionLinearLayout;
    private RadioButton defaultColorRadioButton;
    private RadioButton redColorRadioButton;
    private RadioButton orangeColorRadioButton;
    private RadioButton yellowColorRadioButton;
    private RadioButton greenColorRadioButton;
    private RadioButton cyanColorRadioButton;
    private RadioButton lightBlueColorRadioButton;
    private RadioButton darkBlueColorRadioButton;
    private RadioButton purpleColorRadioButton;
    private RadioButton pinkColorRadioButton;
    private RadioButton brownColorRadioButton;
    private RadioButton greyColorRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_delete);
        isFrom = getIntent().getStringExtra(NotesConstants.KEY_IS_FROM);

        db = new NoteDatabase(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to display back button

        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorImage), PorterDuff.Mode.SRC_ATOP);

        contentLayout = findViewById(R.id.contentParent);
        noteTitle = findViewById(R.id.noteTitle);
        noteDetails = findViewById(R.id.noteDetails);

        dateText = findViewById(R.id.last_modification_date);

        colorSectionLinearLayout = findViewById((R.id.note_actions_layout));
        colorPickerRadioGroup = findViewById((R.id.color_picker_radio_group));

        defaultColorRadioButton = findViewById(R.id.default_color_checkbox);
        redColorRadioButton = findViewById(R.id.red_color_checkbox);
        orangeColorRadioButton = findViewById(R.id.orange_color_checkbox);
        yellowColorRadioButton = findViewById(R.id.yellow_color_checkbox);
        greenColorRadioButton = findViewById(R.id.green_color_checkbox);
        cyanColorRadioButton = findViewById(R.id.cyan_color_checkbox);
        lightBlueColorRadioButton = findViewById(R.id.light_blue_color_checkbox);
        darkBlueColorRadioButton = findViewById(R.id.dark_blue_color_checkbox);
        purpleColorRadioButton = findViewById(R.id.purple_color_checkbox);
        pinkColorRadioButton = findViewById(R.id.pink_color_checkbox);
        brownColorRadioButton = findViewById(R.id.brown_color_checkbox);
        greyColorRadioButton = findViewById(R.id.grey_color_checkbox);

        (findViewById(R.id.note_types_button)).setOnClickListener(this);
        (findViewById(R.id.note_color_chooser)).setOnClickListener(this);

        //Edit
        if (!isFrom.equals(NotesConstants.NOTE_ADDED)) {
            nid = getIntent().getLongExtra(NotesConstants.KEY_NOTE_ID, 0);
            Note note = db.getNote(nid);
            noteTitle.setText(note.getTitle());
            noteDetails.setText(note.getContent());
            noteColor = note.getColor();
            dateText.setText("Edited " + getFormattedDate(note.getCurrentTimestamp()));
            setNoteSelectedColor(noteColor);

            Linkify.addLinks(noteTitle, Linkify.ALL);
            Linkify.addLinks(noteDetails, Linkify.ALL);

        } else {
            dateText.setText("Edited " + new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));
        }

        noteTitle.requestFocus();
        noteTitle.setSelection(noteTitle.getText().length());
        //noteDetails.requestFocus();

        setBackgroundColor(noteColor);

        colorPickerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.default_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteDefault);
                } else if (checkedId == R.id.red_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteRed);
                } else if (checkedId == R.id.orange_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteOrange);
                } else if (checkedId == R.id.yellow_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteYellow);
                } else if (checkedId == R.id.green_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteGreen);
                } else if (checkedId == R.id.cyan_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteCyan);
                } else if (checkedId == R.id.light_blue_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteLightBlue);
                } else if (checkedId == R.id.dark_blue_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteDarkBlue);
                } else if (checkedId == R.id.purple_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNotePurple);
                } else if (checkedId == R.id.pink_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNotePink);
                } else if (checkedId == R.id.brown_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteBrow);
                } else if (checkedId == R.id.grey_color_checkbox) {
                    noteColor = getResources().getString(R.string.colorNoteGrey);
                }
                setBackgroundColor(noteColor);
            }
        });
    }

    @SuppressLint("ResourceType")
    private void setNoteSelectedColor(String noteColor) {

        if (noteColor.equals(getResources().getString(R.string.colorNoteDefault))) {
            defaultColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteRed))) {
            redColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteOrange))) {
            orangeColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteYellow))) {
            yellowColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteGreen))) {
            greenColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteCyan))) {
            cyanColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteLightBlue))) {
            lightBlueColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteDarkBlue))) {
            darkBlueColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNotePurple))) {
            purpleColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNotePink))) {
            pinkColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteBrow))) {
            brownColorRadioButton.setChecked(true);
        } else if (noteColor.equals(getResources().getString(R.string.colorNoteGrey))) {
            greyColorRadioButton.setChecked(true);
        }
    }

    private void setBackgroundColor(String noteColor) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(noteColor));
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

        Intent returnIntent = new Intent();

        if (item.getItemId() == R.id.save) {

            if (isFrom.equals(NotesConstants.NOTE_ADDED)) {
                if (!TextUtils.isEmpty(noteTitle.getText().toString().trim()) || !TextUtils.isEmpty(noteDetails.getText().toString().trim())) {
                    Note note = new Note(noteTitle.getText().toString(), noteDetails.getText().toString(), System.currentTimeMillis(), noteColor);
                    long id = db.addNote(note);
                    returnIntent.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_ADDED);
                    returnIntent.putExtra(NotesConstants.KEY_NOTE_ID, id);
                    setResult(Activity.RESULT_OK, returnIntent);
                    onBackPressed();
                } else {
                    Toast.makeText(this, "Empty note cannot be saved", Toast.LENGTH_LONG).show();
                }
            } else { //Edit
                if (!TextUtils.isEmpty(noteTitle.getText().toString().trim()) || !TextUtils.isEmpty(noteDetails.getText().toString().trim())) {
                    Note note = new Note(nid, noteTitle.getText().toString(), noteDetails.getText().toString(), System.currentTimeMillis(), noteColor);
                    db.editNote(note);
                    returnIntent.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_EDITED);
                    returnIntent.putExtra(NotesConstants.KEY_NOTE_ID, nid);
                    setResult(Activity.RESULT_OK, returnIntent);
                    onBackPressed();
                } else {
                    Toast.makeText(this, "Empty note cannot be saved", Toast.LENGTH_LONG).show();
                }
            }
        } else if (item.getItemId() == R.id.delete) {
            db.deleteNote(nid);
            returnIntent.putExtra(NotesConstants.KEY_IS_FROM, NotesConstants.NOTE_DELETED);
            setResult(Activity.RESULT_OK, returnIntent);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.note_types_button:
                showBottomSheet();
                break;
            case R.id.note_color_chooser:
                if (colorSectionLinearLayout.getVisibility() == View.GONE) {

                    Animation animation;
                    animation = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.animation);
                    colorSectionLinearLayout.setAnimation(animation);
                    colorSectionLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    colorSectionLinearLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.share_email_whatsapp:
                shareNotesViaIntent();
                break;
            default:
                break;
        }
    }

    private void shareNotesViaIntent() {
        if (!TextUtils.isEmpty(noteDetails.getText().toString())) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, noteTitle.getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT, noteDetails.getText().toString());
            startActivity(Intent.createChooser(intent, "App Name"));
        } else {
            Toast.makeText(NoteAddEditDeleteActivity.this, "Cannot send empty notes.", Toast.LENGTH_LONG).show();
        }
    }

    private void showBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet, null);
        view.setBackgroundColor(Color.parseColor(noteColor));
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        (view.findViewById(R.id.share_email_whatsapp)).setOnClickListener(this);
    }

    private void showFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteAddEditDeleteActivity.this);

        builder.setMessage("If you enjoy using " + AppFeedBackUtil.APP_NAME + ", please take a moment to rate it. Thanks for your support!");
        builder.setPositiveButton("Rate Us",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AppFeedBackUtil.APP_PACKAGE_NAME)));
                        AppFeedBackUtil.goToMarket(NoteAddEditDeleteActivity.this);
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        });
        dialog.show();
    }
}