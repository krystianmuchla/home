package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.db.changelog.ChangelogService;
import com.github.krystianmuchla.home.http.Http;
import com.github.krystianmuchla.home.http.HttpConfig;
import com.github.krystianmuchla.home.note.grave.NoteGraveCleaner;

public class App {
    public static void main(String... args) {
        ChangelogService.update();
        Http.startServer(HttpConfig.PORT);
        startJobs();
    }

    private static void startJobs() {
        var noteGraveCleaner = new NoteGraveCleaner();
        new Thread(noteGraveCleaner).start();
    }
}
