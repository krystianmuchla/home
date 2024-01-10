package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.api.RequestBody;

public record UpdateNoteRequest(String title, String content) implements RequestBody {
    @Override
    public void validate() {
        if (title != null && title.length() > Note.TITLE_MAX_LENGTH) throw new IllegalArgumentException();
        if (content != null && content.length() > Note.CONTENT_MAX_LENGTH) throw new IllegalArgumentException();
    }
}
