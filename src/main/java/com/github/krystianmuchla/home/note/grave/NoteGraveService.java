package com.github.krystianmuchla.home.note.grave;

import com.github.krystianmuchla.home.exception.http.NotFoundException;

public class NoteGraveService {
    public static void update(final NoteGrave noteGrave) {
        final var result = NoteGraveSql.update(noteGrave);
        if (!result) {
            throw new NotFoundException();
        }
    }
}
