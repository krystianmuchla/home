package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.note.Note;

import java.util.List;
import java.util.UUID;

public record NoteSyncResult(UUID syncId, List<Note> modifiedNotes) {
}
