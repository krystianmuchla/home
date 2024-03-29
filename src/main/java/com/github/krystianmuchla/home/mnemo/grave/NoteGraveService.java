package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.error.exception.MissingResourceException;

public class NoteGraveService {
    public static void update(final NoteGrave noteGrave) {
        final var result = NoteGraveSql.update(noteGrave);
        if (!result) {
            throw new MissingResourceException();
        }
    }
}
