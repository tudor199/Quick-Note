package com.company.quicknote.undo;

import com.company.quicknote.entity.Note;
import com.company.quicknote.viewModel.NoteViewModel;

public class DeleteCommand extends Command{
    private Note note;

    public DeleteCommand(NoteViewModel noteViewModel, Note note) {
        super(noteViewModel);
        this.note = note;
    }

    public void execute() {
        System.out.println(noteViewModel);
        noteViewModel.delete(note);
    }
}
