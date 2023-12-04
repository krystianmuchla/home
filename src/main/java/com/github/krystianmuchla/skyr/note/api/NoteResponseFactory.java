package com.github.krystianmuchla.skyr.note.api;

import com.github.krystianmuchla.skyr.note.Note;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoteResponseFactory {
    public static NoteResponse create(final Note note) {
        return new NoteResponse(note.id(), note.title(), note.content(), note.creationTime(), note.modificationTime());
    }

    public static List<NoteResponse> create(final List<Note> notes) {
        return notes.stream().map(NoteResponseFactory::create).toList();
    }
}
