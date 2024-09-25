package com.github.krystianmuchla.home.domain.drive.worker;

import com.github.krystianmuchla.home.application.worker.Worker;
import com.github.krystianmuchla.home.domain.drive.DriveService;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryPersistence;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;
import com.github.krystianmuchla.home.domain.drive.file.FilePersistence;
import com.github.krystianmuchla.home.domain.drive.file.FileService;
import com.github.krystianmuchla.home.domain.drive.file.FileStatus;
import com.github.krystianmuchla.home.infrastructure.persistence.Transaction;
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
        Transaction.run(this::removeFilesAndDirectories);
        Transaction.run(this::deleteRemovedFiles);
        Transaction.run(this::deleteRemovedDirectories);
    }

    private void syncUploadedFiles() {
        var files = FilePersistence.readByStatus(FileStatus.UPLOADING);
        for (var file : files) {
            var path = DriveService.path(file.userId, file.id);
            if (Files.isRegularFile(path)) {
                FileService.upload(file);
            }
        }
    }

    private void removeFilesAndDirectories() {
        var directories = DirectoryPersistence.readByStatus(DirectoryStatus.REMOVED);
        for (var directory : directories) {
            var subdirectories = DirectoryPersistence.readByParentIdAndStatus(directory.userId, directory.id, DirectoryStatus.CREATED);
            for (var subdirectory : subdirectories) {
                DirectoryService.remove(subdirectory);
            }
            var files = FilePersistence.readByDirectoryIdAndStatus(directory.userId, directory.id, FileStatus.UPLOADED);
            for (var file : files) {
                FileService.remove(file);
            }
        }
    }

    private void deleteRemovedFiles() {
        var files = FilePersistence.readByStatus(FileStatus.REMOVED);
        for (var file : files) {
            var path = DriveService.path(file.userId, file.id);
            if (Files.exists(path)) {
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

    private void deleteRemovedDirectories() {
        var directories = DirectoryPersistence.readByStatus(DirectoryStatus.REMOVED);
        for (var directory : directories) {
            var subdirectories = DirectoryPersistence.readByParentId(directory.userId, directory.id);
            if (!subdirectories.isEmpty()) {
                continue;
            }
            var files = FilePersistence.readByDirectoryId(directory.userId, directory.id);
            if (!files.isEmpty()) {
                continue;
            }
            DirectoryService.delete(directory);
        }
    }
}
