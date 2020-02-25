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

public class NoteEditorActivity extends AppCompatActivity {
    private int id;

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

        Intent intent = getIntent();
        id = intent.getIntExtra(Constants.KEY_ID, -1);
        if (id == -1) {
            setTitle("Add Note");
            seekPriority.setProgress(5);
            texTitle.requestFocus();
        } else {
            setTitle("Edit Note");
            texTitle.setText(intent.getStringExtra(Constants.KEY_TITLE));
            textDescription.setText(intent.getStringExtra(Constants.KEY_DESCRIPTION));
            seekPriority.setProgress(intent.getIntExtra(Constants.KEY_PRIORITY, 1));
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

        Intent data = new Intent();
        data.putExtra(Constants.KEY_ID, id);
        data.putExtra(Constants.KEY_TITLE, title);
        data.putExtra(Constants.KEY_DESCRIPTION, description);
        data.putExtra(Constants.KEY_PRIORITY, priority);
        setResult(RESULT_OK, data);
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
