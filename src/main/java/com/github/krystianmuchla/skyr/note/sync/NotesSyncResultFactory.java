package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.note.Note;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotesSyncResultFactory {
    public static NotesSyncResult create(final NotesSync notesSync) {
        return create(notesSync, List.of());
    }

    public static NotesSyncResult create(final NotesSync notesSync, final List<Note> modifiedNotes) {
        return new NotesSyncResult(notesSync.syncId(), notesSync.syncTime(), modifiedNotes);
    }
}
