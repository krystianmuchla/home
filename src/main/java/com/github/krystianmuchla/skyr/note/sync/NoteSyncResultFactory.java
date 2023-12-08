package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.note.Note;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoteSyncResultFactory {
    public static NoteSyncResult create(final UUID syncId) {
        return create(syncId, List.of());
    }

    public static NoteSyncResult create(final UUID syncId, final List<Note> modifiedNotes) {
        return new NoteSyncResult(syncId, modifiedNotes);
    }
}
