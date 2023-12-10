package com.github.krystianmuchla.skyr.note;

import com.github.krystianmuchla.skyr.InstantFactory;
import com.github.krystianmuchla.skyr.exception.NotFoundException;
import com.github.krystianmuchla.skyr.note.grave.NoteGraveDao;
import com.github.krystianmuchla.skyr.pagination.PaginatedResult;
import com.github.krystianmuchla.skyr.pagination.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteDao noteDao;
    private final NoteGraveDao noteGraveDao;

    public UUID add(final String title, final String content) {
        final var id = UUID.randomUUID();
        final var creationTime = InstantFactory.create();
        noteDao.create(id, title, content, creationTime, creationTime);
        return id;
    }

    @Transactional
    public void add(final Note note) {
        noteGraveDao.delete(note.id());
        noteDao.create(note.id(), note.title(), note.content(), note.creationTime(), note.modificationTime());
    }

    @Transactional
    public void remove(final UUID id) {
        final var result = noteDao.delete(id);
        if (!result) throw new NotFoundException("Note not found");
        noteGraveDao.create(id);
    }

    @Transactional
    public void remove(final Note note) {

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

    private void update(final UUID id,
                        final String title,
                        final String content,
                        final Instant creationTime,
                        final Instant modificationTime) {
        final var result = noteDao.update(id, title, content, creationTime, modificationTime);
        if (!result) throw new NotFoundException("Note not found");
    }
}
