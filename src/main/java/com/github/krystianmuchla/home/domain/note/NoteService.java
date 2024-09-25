package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.infrastructure.http.exception.NotFoundException;

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
