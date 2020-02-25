package com.company.quicknote.undo;

import com.company.quicknote.viewModel.NoteViewModel;

public abstract class Command {
    protected NoteViewModel noteViewModel;

    public Command(NoteViewModel noteViewModel) {
        this.noteViewModel = noteViewModel;
    }

    public abstract void execute();
}
