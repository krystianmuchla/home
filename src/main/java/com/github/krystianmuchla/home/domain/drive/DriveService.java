package com.github.krystianmuchla.home.domain.drive;

import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;
import com.github.krystianmuchla.home.domain.drive.file.FileStatus;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.directory.DirectoryPersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.file.FilePersistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class DriveService {
    public static final DriveService INSTANCE = new DriveService();
    private static final Path DRIVE_PATH = Path.of(DriveConfig.LOCATION);

    static {
        if (Files.notExists(DRIVE_PATH)) {
            throw new IllegalStateException("Invalid drive location");
        }
    }

    public List<Entry> listDirectory(UUID userId, UUID directoryId) {
        var directories = DirectoryPersistence.readByParentIdAndStatus(userId, directoryId, DirectoryStatus.CREATED)
            .stream().map(directory -> new Entry(directory.id, EntryType.DIR, directory.name));
        var files = FilePersistence.readByDirectoryIdAndStatus(userId, directoryId, FileStatus.UPLOADED)
            .stream().map(file -> new Entry(file.id, EntryType.FILE, file.name));
        return Stream.concat(directories, files).toList();
    }

    public Path path(UUID userId, UUID fileId) {
        return path(userId).resolve(fileId.toString());
    }

    private Path path(UUID userId) {
        var path = DRIVE_PATH.resolve(userId.toString());
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        return path;
    }
}
