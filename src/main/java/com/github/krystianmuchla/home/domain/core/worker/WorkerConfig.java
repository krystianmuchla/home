package com.github.krystianmuchla.home.domain.core.worker;

import com.github.krystianmuchla.home.application.Config;
import com.github.krystianmuchla.home.domain.drive.worker.DriveWorker;
import com.github.krystianmuchla.home.domain.note.removed.worker.RemovedNoteWorker;

import java.util.Set;

public class WorkerConfig extends Config {
    public static final Set<Worker> WORKERS = Set.of(
        new DriveWorker(),
        new RemovedNoteWorker()
    );
}
