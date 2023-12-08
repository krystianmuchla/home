package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.note.Note;
import com.github.krystianmuchla.skyr.note.NoteDao;
import com.github.krystianmuchla.skyr.note.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteSyncService {
    private final SyncIdService syncIdService;
    private final NoteDao noteDao;
    private final NoteService noteService;

    @Transactional
    public NoteSyncResult sync(final UUID externalSyncId, final List<Note> externalNotes) {
        var syncId = syncIdService.getWithLock();
        if (Objects.equals(syncId, externalSyncId)) return NoteSyncResultFactory.create(syncId);
        final var notes = noteDao.readWithLock().stream().collect(Collectors.toMap(Note::id, Function.identity()));
        var externalNoteAdded = false;
        final var excludedIds = new ArrayList<UUID>();
        for (final var externalNote : externalNotes) {
            final var note = notes.get(externalNote.id());
            if (note == null) {
                if (externalNote.hasContent()) {
                    noteDao.create(externalNote);
                    externalNoteAdded = true;
                }
            } else if (note.modificationTime().isBefore(externalNote.modificationTime())) {
                if (externalNote.hasContent()) {
                    noteService.update(externalNote);
                } else {
                    noteService.remove(externalNote.id());
                }
                excludedIds.add(externalNote.id());
            }
        }
        if (externalNoteAdded || !excludedIds.isEmpty()) {
            excludedIds.forEach(notes::remove);
            syncId = UUID.randomUUID();
            syncIdService.update(syncId);
        }
        return NoteSyncResultFactory.create(syncId, List.copyOf(notes.values()));
    }
}
