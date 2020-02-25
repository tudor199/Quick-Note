package com.company.quicknote;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class NoteEditorActivity extends AppCompatActivity {
    private int id;

    private EditText texTitle;
    private EditText textDescription;
    private NumberPicker pickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        texTitle = findViewById(R.id.title);
        textDescription = findViewById(R.id.description);
        pickerPriority = findViewById(R.id.picker_priority);

        pickerPriority.setMinValue(1);
        pickerPriority.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        id = intent.getIntExtra(Constants.KEY_ID, -1);
        if (id == -1) {
            setTitle("Add Note");
            pickerPriority.setValue(5);
            texTitle.requestFocus();
        } else {
            setTitle("Edit Note");
            texTitle.setText(intent.getStringExtra(Constants.KEY_TITLE));
            textDescription.setText(intent.getStringExtra(Constants.KEY_DESCRIPTION));
            pickerPriority.setValue(intent.getIntExtra(Constants.KEY_PRIORITY, 1));
            textDescription.requestFocus();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void saveNote() {
        String title = texTitle.getText().toString();
        String description = textDescription.getText().toString();
        int priority = pickerPriority.getValue();

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
