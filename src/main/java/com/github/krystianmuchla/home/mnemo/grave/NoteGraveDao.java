package com.github.krystianmuchla.home.mnemo.grave;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.github.krystianmuchla.home.Dao;
import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.mnemo.Note;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoteGraveDao extends Dao {
    public static final NoteGraveDao INSTANCE = new NoteGraveDao();

    public void create(final UUID id) {
        create(id, InstantFactory.create());
    }

    public void create(final Note note) {
        create(note.id(), note.modificationTime());
    }

    private void create(final UUID id, final Instant creationTime) {
        executeUpdate(
            "INSERT INTO note_grave VALUES (?, ?)",
            id.toString(),
            timestamp(creationTime).toString()
        );
    }

    public List<NoteGrave> readWithLock() {
        return executeQuery("SELECT * FROM note_grave FOR UPDATE", NoteGrave::new);
    }

    public void delete(final UUID id) {
        executeUpdate("DELETE FROM note_grave WHERE id = ?", id.toString());
    }

    public void delete(final Instant creationTimeThreshold) {
        executeUpdate(
            "DELETE FROM note_grave WHERE creation_time < ?",
            timestamp(creationTimeThreshold).toString()
        );
    }
}
