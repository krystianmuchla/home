package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileNotFoundException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;
import com.github.krystianmuchla.home.domain.drive.file.error.IllegalFileStatusException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.file.FilePersistence;

import java.util.UUID;

public class FileService {
    public static final FileService INSTANCE = new FileService(DirectoryService.INSTANCE);

    private final DirectoryService directoryService;

    public FileService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    public UUID create(UUID userId, UUID directoryId, String name) throws DirectoryNotFoundException, FileValidationException {
        if (directoryId != null) {
            directoryService.checkExistence(userId, directoryId);
        }
        var file = new File(userId, directoryId, name);
        Transaction.run(() -> FilePersistence.create(file));
        return file.id;
    }

    public File get(UUID userId, UUID fileId) throws FileNotFoundException {
        var file = FilePersistence.readByIdAndStatus(userId, fileId, FileStatus.UPLOADED);
        if (file == null) {
            throw new FileNotFoundException();
        }
        return file;
    }

    public void upload(UUID userId, UUID fileId) throws FileNotFoundException, FileValidationException, FileNotUpdatedException {
        var file = FilePersistence.readByIdAndStatus(userId, fileId, FileStatus.UPLOADING);
        if (file == null) {
            throw new FileNotFoundException();
        }
        upload(file);
    }

    public void upload(File file) throws FileValidationException, FileNotUpdatedException {
        if (!file.isUploaded()) {
            file.updateStatus(FileStatus.UPLOADED);
            update(file);
        } else {
            throw new FileNotUpdatedException();
        }
    }

    public void remove(UUID userId, UUID fileId) throws FileNotFoundException, FileValidationException, FileNotUpdatedException {
        var file = get(userId, fileId);
        remove(file);
    }

    public void remove(File file) throws FileValidationException, FileNotUpdatedException {
        if (!file.isRemoved()) {
            file.updateStatus(FileStatus.REMOVED);
            update(file);
        }
    }

    public void update(
        UUID userId,
        UUID fileId,
        FileUpdate update
    ) throws FileNotFoundException, FileValidationException, FileNotUpdatedException {
        var file = get(userId, fileId);
        if (update.directoryId() != null) {
            file.updateDirectoryId(update.directoryId());
        }
        if (update.unsetDirectoryId()) {
            file.updateDirectoryId(null);
        }
        if (update.name() != null) {
            file.updateName(update.name());
        }
        update(file);
    }

    public void update(File file) throws FileValidationException, FileNotUpdatedException {
        file.updateModificationTime(new Time());
        file.updateVersion(file.version + 1);
        var result = Transaction.run(() -> FilePersistence.update(file));
        if (!result) {
            throw new FileNotUpdatedException();
        }
    }

    public void delete(File file) throws IllegalFileStatusException, FileNotUpdatedException {
        if (!file.isRemoved()) {
            throw new IllegalFileStatusException(file.status);
        }
        var result = Transaction.run(() -> FilePersistence.delete(file));
        if (!result) {
            throw new FileNotUpdatedException();
        }
    }
}
