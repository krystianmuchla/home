package com.github.krystianmuchla.home.drive.file;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.http.NotFoundException;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.util.UUID;

public class FileService {
    public static UUID create(UUID userId, UUID directoryId, String name) {
        var id = UUID.randomUUID();
        var status = FileStatus.INITIATED;
        var creationTime = InstantFactory.create();
        var file = new File(id, userId, status, directoryId, name, creationTime);
        FilePersistence.create(file);
        return file.getId();
    }

    public static File get(UUID userId, UUID fileId) {
        var file = FilePersistence.readByIdAndStatus(userId, fileId, FileStatus.UPLOADED);
        if (file == null) {
            throw new NotFoundException();
        }
        return file;
    }

    public static void upload(UUID userId, UUID fileId) {
        var file = FilePersistence.readByIdAndStatusForUpdate(userId, fileId, FileStatus.INITIATED);
        if (file == null) {
            throw new NotFoundException();
        }
        upload(file);
    }

    public static void upload(File file) {
        if (!file.isUploaded()) {
            file.upload();
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
            file.remove();
            update(file);
        }
    }

    public static void update(File file) {
        var modificationTime = InstantFactory.create();
        file.setModificationTime(modificationTime);
        var result = FilePersistence.update(file);
        if (!result) {
            throw new NotFoundException();
        }
    }

    public static void delete(File file) {
        if (!file.isRemoved()) {
            throw new InternalException(
                "Attempting to delete file %s in wrong status %s".formatted(file.getId(), file.getStatus())
            );
        }
        var result = FilePersistence.delete(file);
        if (!result) {
            throw new NotFoundException();
        }
    }
}
