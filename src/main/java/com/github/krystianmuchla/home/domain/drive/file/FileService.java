package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.util.StreamService;
import com.github.krystianmuchla.home.domain.drive.DriveService;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileNotFoundException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;
import com.github.krystianmuchla.home.domain.drive.file.error.IllegalFileStatusException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.file.FilePersistence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FileService {
    public static final FileService INSTANCE = new FileService(DriveService.INSTANCE, DirectoryService.INSTANCE);

    private final DriveService driveService;
    private final DirectoryService directoryService;

    public FileService(DriveService driveService, DirectoryService directoryService) {
        this.driveService = driveService;
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

    public FileDto getDto(UUID userId, UUID fileId) throws FileNotFoundException {
        var file = get(userId, fileId);
        return new FileDto(file.name, getFile(userId, fileId));
    }

    public void upload(UUID userId, UUID fileId, InputStream fileContent) throws FileNotFoundException, FileValidationException, FileNotUpdatedException {
        var file = FilePersistence.readByIdAndStatus(userId, fileId, FileStatus.UPLOADING);
        if (file == null) {
            throw new FileNotFoundException();
        }
        var path = driveService.path(userId, file.id);
        try (var outputStream = new FileOutputStream(path.toString())) {
            try (var inputStream = fileContent) {
                StreamService.copy(inputStream, outputStream);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        markAsUploaded(file);
    }

    public void markAsUploaded(File file) throws FileValidationException, FileNotUpdatedException {
        if (!file.isUploaded()) {
            file.updateStatus(FileStatus.UPLOADED);
            update(file);
        } else {
            throw new FileNotUpdatedException();
        }
    }

    public void markAsRemoved(UUID userId, UUID fileId) throws FileNotFoundException, FileValidationException, FileNotUpdatedException {
        var file = get(userId, fileId);
        markAsRemoved(file);
    }

    public void markAsRemoved(File file) throws FileValidationException, FileNotUpdatedException {
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

    private java.io.File getFile(UUID userId, UUID fileId) {
        var path = driveService.path(userId, fileId);
        var file = path.toFile();
        if (!file.isFile()) {
            throw new IllegalStateException();
        }
        return file;
    }
}
