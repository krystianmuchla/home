package com.github.krystianmuchla.home.drive.worker;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.drive.DriveService;
import com.github.krystianmuchla.home.drive.file.FilePersistence;
import com.github.krystianmuchla.home.drive.file.FileService;
import com.github.krystianmuchla.home.drive.file.FileStatus;
import com.github.krystianmuchla.home.worker.Worker;

import java.nio.file.Files;

public class DriveWorker extends Worker {
    public DriveWorker() {
        super(DriveWorkerConfig.RATE);
    }

    @Override
    protected void work() {
        Transaction.run(this::syncUploadedFiles);
    }

    private void syncUploadedFiles() {
        var files = FilePersistence.readForUpdate(FileStatus.INITIATED);
        for (var file : files) {
            var path = DriveService.path(file.getUserId(), file.getId());
            if (Files.isRegularFile(path)) {
                FileService.upload(file);
            }
        }
    }
}
