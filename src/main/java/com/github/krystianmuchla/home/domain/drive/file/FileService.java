package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.NotFoundException;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.FilePersistence;

import java.util.UUID;

public class FileService {
    public static UUID create(UUID userId, UUID directoryId, String name) {
        checkDirectoryExistence(userId, directoryId);
        var file = new File(userId, directoryId, name);
        FilePersistence.create(file);
        return file.id;
    }

    public static File get(UUID userId, UUID fileId) {
        var file = FilePersistence.readByIdAndStatus(userId, fileId, FileStatus.UPLOADED);
        if (file == null) {
            throw new NotFoundException();
        }
        return file;
    }

    public static void upload(UUID userId, UUID fileId) {
        var file = FilePersistence.readByIdAndStatus(userId, fileId, FileStatus.UPLOADING);
        if (file == null) {
            throw new NotFoundException();
        }
        upload(file);
    }

    public static void upload(File file) {
        if (!file.isUploaded()) {
            file.updateStatus(FileStatus.UPLOADED);
            update(file);
        }
    }

    public static void remove(UUID userId, UUID fileId) {
        var file = FilePersistence.readByIdAndStatus(userId, fileId, FileStatus.UPLOADED);
        if (file == null) {
            throw new NotFoundException();
        }
        remove(file);
    }

    public static void remove(File file) {
        if (!file.isRemoved()) {
            file.updateStatus(FileStatus.REMOVED);
            update(file);
        }
    }

    public static void update(File file) {
        var result = FilePersistence.update(file);
        if (!result) {
            throw new NotFoundException();
        }
    }

    public static void delete(File file) {
        if (!file.isRemoved()) {
            throw new InternalException(
                "Attempting to delete file %s in wrong status %s".formatted(file.id, file.status)
            );
        }
        var result = FilePersistence.delete(file);
        if (!result) {
            throw new NotFoundException();
        }
    }

    private static void checkDirectoryExistence(UUID userId, UUID directoryId) {
        if (directoryId != null) {
            DirectoryService.get(userId, directoryId);
        }
    }
}
