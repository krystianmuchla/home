package com.github.krystianmuchla.home.domain.drive.worker;

import com.github.krystianmuchla.home.domain.core.worker.Worker;
import com.github.krystianmuchla.home.domain.drive.DriveService;
import com.github.krystianmuchla.home.domain.drive.directory.Directory;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;
import com.github.krystianmuchla.home.domain.drive.directory.error.IllegalDirectoryStatusException;
import com.github.krystianmuchla.home.domain.drive.file.File;
import com.github.krystianmuchla.home.domain.drive.file.FileService;
import com.github.krystianmuchla.home.domain.drive.file.FileStatus;
import com.github.krystianmuchla.home.domain.drive.file.error.FileNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;
import com.github.krystianmuchla.home.domain.drive.file.error.IllegalFileStatusException;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.directory.DirectoryPersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.file.FilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

public class DriveWorker extends Worker {
    private static final Logger LOG = LoggerFactory.getLogger(DriveWorker.class);

    private final DriveService driveService = DriveService.INSTANCE;
    private final DirectoryService directoryService = DirectoryService.INSTANCE;
    private final FileService fileService = FileService.INSTANCE;

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
        var path = driveService.path(file.userId, file.id);
        if (Files.isRegularFile(path)) {
            try {
                fileService.upload(file);
            } catch (FileValidationException | FileNotUpdatedException exception) {
                LOG.warn("{}", exception.getMessage(), exception);
            }
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
            try {
                fileService.remove(file);
            } catch (FileValidationException | FileNotUpdatedException exception) {
                LOG.warn("{}", exception.getMessage(), exception);
            }
        }
    }

    private void removeDirectory(Directory directory) {
        removeDirectoryContent(directory);
        try {
            directoryService.remove(directory);
        } catch (DirectoryValidationException | DirectoryNotUpdatedException exception) {
            LOG.warn("{}", exception.getMessage(), exception);
        }
    }

    private void deleteFiles() {
        var files = FilePersistence.readByStatus(FileStatus.REMOVED);
        for (var file : files) {
            var path = driveService.path(file.userId, file.id);
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException exception) {
                    LOG.warn("{}", exception.getMessage(), exception);
                    continue;
                }
            }
            try {
                fileService.delete(file);
            } catch (IllegalFileStatusException | FileNotUpdatedException exception) {
                LOG.warn("{}", exception.getMessage(), exception);
            }
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
            try {
                directoryService.delete(directory);
            } catch (IllegalDirectoryStatusException | DirectoryNotFoundException exception) {
                LOG.warn("{}", exception.getMessage(), exception);
            }
        }
    }
}
