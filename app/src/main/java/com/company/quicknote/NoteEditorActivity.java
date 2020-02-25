package com.company.quicknote;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.company.quicknote.common.Constants;
import com.company.quicknote.entity.Note;

public class NoteEditorActivity extends AppCompatActivity {
    private long id;

    Intent intent;

    private EditText texTitle;
    private EditText textDescription;
    private EditText textPriority;
    private SeekBar seekPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        texTitle = findViewById(R.id.title);
        textDescription = findViewById(R.id.description);
        textPriority = findViewById(R.id.priority_text);
        seekPriority = findViewById(R.id.priority_seekbar);

        seekPriority.setMax(10);
        seekPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textPriority.setText(String.valueOf(progress));
                textPriority.setSelection(textPriority.getEditableText().toString().length());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        textPriority.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    seekPriority.setProgress(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    seekPriority.setProgress(0);
                }
            }
        });

        seekPriority.setMax(10);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        intent = getIntent();
        Note note = intent.getParcelableExtra(Constants.KEY_OLD_NOTE);
        if (note == null) {
            setTitle("Add Note");
            seekPriority.setProgress(5);
            texTitle.requestFocus();
        } else {
            setTitle("Edit Note");
            id = note.getId();
            texTitle.setText(note.getTitle());
            textDescription.setText(note.getDescription());
            seekPriority.setProgress(note.getPriority());
            textDescription.requestFocus();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void saveNote() {
        String title = texTitle.getText().toString();
        String description = textDescription.getText().toString();
        int priority = seekPriority.getProgress();

        if (title.trim().isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note(title, description, priority);
        note.setId(id);
        intent.putExtra(Constants.KEY_NEW_NOTE, note);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
