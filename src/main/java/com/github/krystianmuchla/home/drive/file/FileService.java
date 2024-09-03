package com.github.krystianmuchla.home.drive.file;

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
        var file = FilePersistence.read(userId, fileId);
        if (file == null) {
            throw new NotFoundException();
        }
        return file;
    }

    public static void upload(UUID userId, UUID fileId) {
        var file = FilePersistence.readForUpdate(userId, fileId);
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

    public static void update(File file) {
        var modificationTime = InstantFactory.create();
        file.setModificationTime(modificationTime);
        var result = FilePersistence.update(file);
        if (!result) {
            throw new NotFoundException();
        }
    }
}
