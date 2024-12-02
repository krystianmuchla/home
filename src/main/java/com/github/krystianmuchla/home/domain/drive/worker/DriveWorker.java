package com.github.krystianmuchla.home.domain.drive.worker;

import com.github.krystianmuchla.home.application.worker.Worker;
import com.github.krystianmuchla.home.domain.drive.DriveService;
import com.github.krystianmuchla.home.domain.drive.directory.Directory;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;
import com.github.krystianmuchla.home.domain.drive.directory.exception.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.directory.exception.DirectoryNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.directory.exception.IllegalDirectoryStatusException;
import com.github.krystianmuchla.home.domain.drive.file.File;
import com.github.krystianmuchla.home.domain.drive.file.FileService;
import com.github.krystianmuchla.home.domain.drive.file.FileStatus;
import com.github.krystianmuchla.home.domain.drive.file.exception.FileNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.file.exception.IllegalFileStatusException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.DirectoryPersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.FilePersistence;
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
        uploadFiles();
        removeDirectoriesContent();
        deleteFiles();
        deleteDirectories();
    }

    private void uploadFiles() {
        var files = FilePersistence.readByStatus(FileStatus.UPLOADING);
        for (var file : files) {
            uploadFile(file);
        }
    }

    private void uploadFile(File file) {
        var path = DriveService.path(file.userId, file.id);
        if (Files.isRegularFile(path)) {
            Transaction.run(() -> {
                try {
                    FileService.upload(file);
                } catch (FileNotUpdatedException exception) {
                    LOG.warn("{}", exception.getMessage(), exception);
                }
            });
        }
    }

    private void removeDirectoriesContent() {
        var directories = DirectoryPersistence.readByStatus(DirectoryStatus.REMOVED);
        for (var directory : directories) {
            removeDirectoryContent(directory);
        }
    }

    private void removeDirectoryContent(Directory directory) {
        var directories = DirectoryPersistence.readByParentIdAndStatus(
            directory.userId,
            directory.id,
            DirectoryStatus.CREATED
        );
        for (var subdirectory : directories) {
            removeDirectory(subdirectory);
        }
        var files = FilePersistence.readByDirectoryIdAndStatus(directory.userId, directory.id, FileStatus.UPLOADED);
        for (var file : files) {
            Transaction.run(() -> {
                try {
                    FileService.remove(file);
                } catch (FileNotUpdatedException exception) {
                    LOG.warn("{}", exception.getMessage(), exception);
                }
            });
        }
    }

    private void removeDirectory(Directory directory) {
        removeDirectoryContent(directory);
        Transaction.run(() -> {
            try {
                DirectoryService.remove(directory);
            } catch (DirectoryNotUpdatedException exception) {
                LOG.warn("{}", exception.getMessage(), exception);
            }
        });
    }

    private void deleteFiles() {
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
            Transaction.run(() -> {
                try {
                    FileService.delete(file);
                } catch (IllegalFileStatusException | FileNotUpdatedException exception) {
                    LOG.warn("{}", exception.getMessage(), exception);
                }
            });
        }
    }

    private void deleteDirectories() {
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
            Transaction.run(() -> {
                try {
                    DirectoryService.delete(directory);
                } catch (IllegalDirectoryStatusException | DirectoryNotFoundException exception) {
                    LOG.warn("{}", exception.getMessage(), exception);
                }
            });
        }
    }
}
