package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.exception.NotFoundException;
import com.github.krystianmuchla.skyr.note.Note;
import com.github.krystianmuchla.skyr.note.NoteDao;
import com.github.krystianmuchla.skyr.note.NoteService;
import com.github.krystianmuchla.skyr.note.grave.NoteGrave;
import com.github.krystianmuchla.skyr.note.grave.NoteGraveDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NoteSyncService {
    private final NoteDao noteDao;
    private final NoteService noteService;
    private final NoteGraveDao noteGraveDao;

    @Transactional
    public List<Note> sync(final List<Note> externalNotes) {
        if (externalNotes == null || externalNotes.isEmpty()) {
            return noteDao.read();
        }
        final var notes = toMap(noteDao.readWithLock(), noteGraveDao.read());
        final var overriddenNotes = sync(notes, externalNotes);
        overriddenNotes.forEach(notes::remove);
        return List.copyOf(notes.values());
    }

    private List<UUID> sync(final Map<UUID, Note> notes, final List<Note> externalNotes) {
        final var overriddenNotes = new ArrayList<UUID>();
        for (final var externalNote : externalNotes) {
            final UUID id = externalNote.id();
            final var note = notes.get(id);
            if (note == null) {
                if (externalNote.hasContent()) {
                    noteService.add(externalNote);
                }
            } else if (note.modificationTime().isBefore(externalNote.modificationTime())) {
                if (externalNote.hasContent()) {
                    if (note.hasContent()) {
                        noteService.update(externalNote);
                    } else {
                        noteService.add(externalNote);
                    }
                } else {
                    if (note.hasContent()) {
                        removeNote(id);
                        noteGraveDao.create(externalNote);
                    }
                }
                overriddenNotes.add(id);
            }
        }
        return overriddenNotes;
    }

    private void removeNote(final UUID id) {
        final var result = noteDao.delete(id);
        if (!result) throw new NotFoundException("Note not found");
    }

    private Map<UUID, Note> toMap(final List<Note> notes, final List<NoteGrave> noteGraves) {
        return Stream.concat(notes.stream(), noteGraves.stream().map(NoteGrave::toNote))
                .collect(Collectors.toMap(Note::id, Function.identity()));
    }
}
