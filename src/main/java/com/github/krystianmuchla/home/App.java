package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.db.changelog.ChangelogService;
import com.github.krystianmuchla.home.http.Http;
import com.github.krystianmuchla.home.http.HttpConfig;
import com.github.krystianmuchla.home.worker.Worker;
import com.github.krystianmuchla.home.worker.WorkerConfig;

public class App {
    public static void main(String... args) {
        ChangelogService.update();
        Http.startServer(HttpConfig.PORT);
        Worker.start(WorkerConfig.WORKERS);
    }
}
