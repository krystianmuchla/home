package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.api.RequestBody;

public record CreateNoteRequest(String title, String content) implements RequestBody {
    @Override
    public void validate() {
        if (title == null) throw new IllegalArgumentException();
        if (title.length() > Note.TITLE_MAX_LENGTH) throw new IllegalArgumentException();
        if (content == null) throw new IllegalArgumentException();
        if (content.length() > Note.CONTENT_MAX_LENGTH) throw new IllegalArgumentException();
    }
}
