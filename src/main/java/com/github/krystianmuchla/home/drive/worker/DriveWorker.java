package com.github.krystianmuchla.home.drive.worker;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.drive.DriveService;
import com.github.krystianmuchla.home.drive.file.FilePersistence;
import com.github.krystianmuchla.home.drive.file.FileService;
import com.github.krystianmuchla.home.drive.file.FileStatus;
import com.github.krystianmuchla.home.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

public class DriveWorker extends Worker {
    private static final Logger LOG = LoggerFactory.getLogger(DriveWorker.class);

    public DriveWorker() {
        super(DriveWorkerConfig.RATE);
    }

    @Override
    protected void work() {
        Transaction.run(this::syncUploadedFiles);
        Transaction.run(this::deleteRemovedFiles);
    }

    private void syncUploadedFiles() {
        var files = FilePersistence.readByStatusForUpdate(FileStatus.INITIATED);
        for (var file : files) {
            var path = DriveService.path(file.getUserId(), file.getId());
            if (Files.isRegularFile(path)) {
                FileService.upload(file);
            }
        }
    }

    private void deleteRemovedFiles() {
        var files = FilePersistence.readByStatusForUpdate(FileStatus.REMOVED);
        for (var file : files) {
            var path = DriveService.path(file.getUserId(), file.getId());
            if (!Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException exception) {
                    LOG.warn("{}", exception.getMessage(), exception);
                    continue;
                }
            }
            FileService.delete(file);
        }
    }
}
