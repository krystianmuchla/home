package com.github.krystianmuchla.skyr.note;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record Note(UUID id, String title, String content, Instant creationTime, Instant modificationTime) {
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int CONTENT_MAX_LENGTH = 65535;

    public Note {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (title != null && title.length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("Note title exceeded max length of " + TITLE_MAX_LENGTH);
        }
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException("Note content exceeded max length of " + CONTENT_MAX_LENGTH);
        }
        if (modificationTime == null) {
            throw new IllegalArgumentException("Modification time cannot be null");
        }
    }

    public boolean hasContent() {
        return content != null;
    }
}
