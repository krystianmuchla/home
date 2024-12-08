package com.github.krystianmuchla.home.domain.drive;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.application.util.StreamService;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.DirectoryPersistence;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;
import com.github.krystianmuchla.home.domain.drive.file.FileDto;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.FilePersistence;
import com.github.krystianmuchla.home.domain.drive.file.FileService;
import com.github.krystianmuchla.home.domain.drive.file.FileStatus;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.NotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class DriveService {
    private static final Path DRIVE_PATH = Path.of(DriveConfig.LOCATION);

    static {
        if (Files.notExists(DRIVE_PATH)) {
            throw new InternalException("Invalid drive location");
        }
    }

    public static List<Entry> listDirectory(UUID userId, UUID directoryId) {
        var directories = DirectoryPersistence.readByParentIdAndStatus(userId, directoryId, DirectoryStatus.CREATED)
            .stream().map(directory -> new Entry(directory.id, EntryType.DIR, directory.name));
        var files = FilePersistence.readByDirectoryIdAndStatus(userId, directoryId, FileStatus.UPLOADED)
            .stream().map(file -> new Entry(file.id, EntryType.FILE, file.name));
        return Stream.concat(directories, files).toList();
    }

    public static void uploadFile(UUID userId, UUID fileId, InputStream fileContent) {
        var path = path(userId, fileId);
        try (var outputStream = new FileOutputStream(path.toString())) {
            try (var inputStream = fileContent) {
                StreamService.copy(inputStream, outputStream);
            }
        } catch (IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static FileDto getFile(UUID userId, UUID fileId) {
        var file = FileService.get(userId, fileId);
        var actualFile = actualFile(userId, fileId);
        return new FileDto(file.name, actualFile);
    }

    public static Path path(UUID userId, UUID fileId) {
        return path(userId).resolve(fileId.toString());
    }

    private static File actualFile(UUID userId, UUID fileId) {
        var path = path(userId, fileId);
        var file = path.toFile();
        if (!file.isFile()) {
            throw new NotFoundException();
        }
        return file;
    }

    private static Path path(UUID userId) {
        var path = DRIVE_PATH.resolve(userId.toString());
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException exception) {
                throw new InternalException(exception);
            }
        }
        return path;
    }
}
