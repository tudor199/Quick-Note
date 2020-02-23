package com.company.quicknote;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> notes;

    public NoteRepository(Application application) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
        notes = noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

    public void insert(Note note) {
        new InsertAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteAsyncTask(noteDao).execute(note);
    }

    private static class InsertAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        InsertAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        DeleteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }
}
