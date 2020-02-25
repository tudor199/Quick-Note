package com.company.quicknote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.quicknote.adapter.NoteAdapter;
import com.company.quicknote.common.Constants;
import com.company.quicknote.entity.Note;
import com.company.quicknote.undo.AddCommand;
import com.company.quicknote.undo.CommandStack;
import com.company.quicknote.undo.DeleteCommand;
import com.company.quicknote.undo.EditCommand;
import com.company.quicknote.viewModel.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    private CommandStack commandStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commandStack = new CommandStack();

        RecyclerView recyclerView = findViewById(R.id.note_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        findViewById(R.id.fab_add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
                startActivityForResult(intent, Constants.REQUEST_CODE_ADD_NOTE);
            }
        });
        findViewById(R.id.fab_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commandStack.executeLastCommand();
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note note = adapter.getNote(viewHolder.getAdapterPosition());
                commandStack.push(new AddCommand(noteViewModel, note));
                noteViewModel.delete(note);
                Toast.makeText(MainActivity.this, "Note has been deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListerne(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
                intent.putExtra(Constants.KEY_ID, note.getId());
                intent.putExtra(Constants.KEY_TITLE, note.getTitle());
                intent.putExtra(Constants.KEY_DESCRIPTION, note.getDescription());
                intent.putExtra(Constants.KEY_PRIORITY, note.getPriority());
                startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_NOTE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            String title = data.getStringExtra(Constants.KEY_TITLE);
            String description = data.getStringExtra(Constants.KEY_DESCRIPTION);
            int priority = data.getIntExtra(Constants.KEY_PRIORITY, 1);

            Note note = new Note(title, description, priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "Note has been saved successful!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == Constants.REQUEST_CODE_EDIT_NOTE && resultCode == RESULT_OK) {
            int id = data.getIntExtra(Constants.KEY_ID, -1);
            String title = data.getStringExtra(Constants.KEY_TITLE);
            String description = data.getStringExtra(Constants.KEY_DESCRIPTION);
            int priority = data.getIntExtra(Constants.KEY_PRIORITY, 1);

            Note note = new Note(title, description, priority);
            note.setId(id);
            noteViewModel.update(note);

            Toast.makeText(this, "Note has been updated successful!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "No modification has been made!", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
