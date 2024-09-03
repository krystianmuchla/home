package com.github.krystianmuchla.home.worker;

import com.github.krystianmuchla.home.Config;
import com.github.krystianmuchla.home.drive.worker.DriveWorker;
import com.github.krystianmuchla.home.note.removed.worker.RemovedNoteWorker;

import java.util.Set;

public class WorkerConfig extends Config {
    public static final Set<Worker> WORKERS = Set.of(
        new DriveWorker(),
        new RemovedNoteWorker()
    );
}
