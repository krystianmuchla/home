package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.exception.http.NotFoundException;

public class NoteService {
    public static void update(Note note) {
        var result = NotePersistence.update(note);
        if (!result) {
            throw new NotFoundException();
        }
    }

    public static void delete(Note note) {
        var result = NotePersistence.delete(note);
        if (!result) {
            throw new NotFoundException();
        }
    }
}
