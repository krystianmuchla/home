package com.github.krystianmuchla.skyr.note;

import com.github.krystianmuchla.skyr.InstantFactory;
import com.github.krystianmuchla.skyr.exception.NotFoundException;
import com.github.krystianmuchla.skyr.pagination.PaginatedResult;
import com.github.krystianmuchla.skyr.pagination.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteDao noteDao;

    public UUID add(final String title, final String content) {
        final var id = UUID.randomUUID();
        noteDao.create(id, title, content);
        return id;
    }

    public void remove(final UUID id) {
        final var result = noteDao.delete(id);
        if (!result) throw new NotFoundException("Note not found");
    }

    public Note get(final UUID id) {
        final var note = noteDao.read(id);
        if (note == null) throw new NotFoundException("Note not found");
        return note;
    }

    public PaginatedResult<Note> get(final Pagination pagination) {
        return noteDao.read(pagination);
    }

    public void update(final Note note) {
        update(note.id(), note.title(), note.content(), note.creationTime(), note.modificationTime());
    }

    public void update(final UUID id, final String title, final String content) {
        update(id, title, content, null, InstantFactory.create());
    }

    private void update(final UUID noteId,
                        final String title,
                        final String content,
                        final Instant creationTime,
                        final Instant modificationTime) {
        final var result = noteDao.update(noteId, title, content, creationTime, modificationTime);
        if (!result) throw new NotFoundException("Note not found");
    }
}
