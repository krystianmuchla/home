package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.domain.core.worker.Worker;
import com.github.krystianmuchla.home.domain.core.worker.WorkerConfig;
import com.github.krystianmuchla.home.infrastructure.http.core.Http;
import com.github.krystianmuchla.home.infrastructure.http.core.HttpConfig;
import com.github.krystianmuchla.home.infrastructure.persistence.core.changelog.ChangelogService;

public class App {
    public static void main(String... args) {
        ChangelogService.update();
        Http.startServer(HttpConfig.PORT);
        Worker.start(WorkerConfig.WORKERS);
    }
}
