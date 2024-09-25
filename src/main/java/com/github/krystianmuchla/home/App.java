package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.application.changelog.ChangelogService;
import com.github.krystianmuchla.home.application.worker.Worker;
import com.github.krystianmuchla.home.application.worker.WorkerConfig;
import com.github.krystianmuchla.home.infrastructure.http.Http;
import com.github.krystianmuchla.home.infrastructure.http.HttpConfig;

public class App {
    public static void main(String... args) {
        ChangelogService.update();
        Http.startServer(HttpConfig.PORT);
        Worker.start(WorkerConfig.WORKERS);
    }
}
