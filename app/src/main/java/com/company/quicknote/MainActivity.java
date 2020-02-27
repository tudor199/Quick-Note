package com.company.quicknote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
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
import com.company.quicknote.common.Constant;
import com.company.quicknote.common.UserAction;
import com.company.quicknote.entity.Note;
import com.company.quicknote.undo.AddCommand;
import com.company.quicknote.undo.CommandStack;
import com.company.quicknote.undo.EditCommand;
import com.company.quicknote.viewModel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    private CommandStack commandStack;

    private boolean doubleBackToExitPressedOnce;

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

        final FloatingActionButton addButton = findViewById(R.id.fab_add_note);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton.setEnabled(false);
                Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
                intent.putExtra(Constant.KEY_ACTION, UserAction.Add);
                startActivityForResult(intent, Constant.REQUEST_CODE_ADD_NOTE);
                addButton.setEnabled(true);
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
                intent.putExtra(Constant.KEY_ACTION, UserAction.Edit);
                intent.putExtra(Constant.KEY_OLD_NOTE, note);
                startActivityForResult(intent, Constant.REQUEST_CODE_EDIT_NOTE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.undo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_undo:
                if (!commandStack.executeLastCommand()) {
                    Toast.makeText(this, "There are no more actions to undo!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constant.REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            Note note = data.getParcelableExtra(Constant.KEY_NEW_NOTE);

            noteViewModel.insert(note);
            commandStack.clear();

            Toast.makeText(this, "Note has been saved successfully!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == Constant.REQUEST_CODE_EDIT_NOTE && resultCode == RESULT_OK) {
            Note note = data.getParcelableExtra(Constant.KEY_NEW_NOTE);
            noteViewModel.update(note);
            commandStack.push(new EditCommand(noteViewModel, (Note) data.getParcelableExtra(Constant.KEY_OLD_NOTE)));

            Toast.makeText(this, "Note has been updated successfully!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "No modification has been made!", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, Constant.DOUBLE_BACK_DELAY_MS);
    }
}
