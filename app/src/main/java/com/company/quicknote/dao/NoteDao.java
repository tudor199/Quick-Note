package com.company.quicknote.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.company.quicknote.entity.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    public void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    public void delete(Note note);

    @Query("SELECT * FROM notes ORDER BY priority DESC")
    public LiveData<List<Note>> getAllNotes();
}
