package com.company.quicknote.undo;

import com.company.quicknote.entity.Note;
import com.company.quicknote.viewModel.NoteViewModel;

public class EditCommand extends Command{
    private Note note;

    public EditCommand(NoteViewModel noteViewModel, Note note) {
        super(noteViewModel);
        this.note = note;
    }

    public void execute() {
        System.out.println(noteViewModel);
        noteViewModel.update(note);
    }
}
