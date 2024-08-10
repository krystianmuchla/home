package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.drive.directory.DirectoryPersistence;
import com.github.krystianmuchla.home.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.drive.file.FilePersistence;
import com.github.krystianmuchla.home.drive.file.FileService;
import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.http.ConflictException;
import com.github.krystianmuchla.home.exception.http.ForbiddenException;
import com.github.krystianmuchla.home.exception.http.NotFoundException;
import com.github.krystianmuchla.home.util.StreamService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        var directories = DirectoryPersistence.readByParentId(userId, directoryId).stream()
            .map(directory -> new Entry(directory.id(), EntryType.DIR, directory.getName()));
        var files = FilePersistence.readByDirectoryId(userId, directoryId).stream()
            .map(file -> new Entry(file.id(), EntryType.FILE, file.getName()));
        return Stream.concat(directories, files).toList();
    }

    public static void createDirectory(UUID userId, UUID directoryId, String name) {
        var userPath = path(userId);
        var path = entryPath(userPath, userId, directoryId, name);
        if (Files.exists(path)) {
            throw new ConflictException("DIRECTORY_ALREADY_EXISTS");
        }
        var relativeUri = userPath.toUri().relativize(path.toUri());
        DirectoryService.create(userId, directoryId, relativeUri.getPath());
        createDirectory(path);
    }

    public static void uploadFile(UUID userId, UUID directoryId, FileUpload fileUpload) {
        var userPath = path(userId);
        var path = entryPath(userPath, userId, directoryId, fileUpload.fileName());
        if (Files.notExists(path)) {
            var relativeUri = userPath.toUri().relativize(path.toUri());
            FileService.create(userId, directoryId, relativeUri.getPath());
        }
        try (var outputStream = new FileOutputStream(path.toString())) {
            try (var inputStream = fileUpload.inputStream()) {
                StreamService.copy(inputStream, outputStream);
            }
        } catch (IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static File getFile(UUID userId, UUID fileId) {
        var path = filePath(userId, fileId);
        var file = path.toFile();
        if (!file.exists()) {
            throw new NotFoundException();
        }
        if (!file.isFile()) {
            throw new BadRequestException();
        }
        return file;
    }

    private static Path entryPath(Path userPath, UUID userId, UUID directoryId, String entry) {
        if (directoryId == null) {
            return path(userPath, entry);
        }
        var directory = DirectoryService.get(userId, directoryId);
        return path(userPath, Path.of(directory.path()), entry);
    }

    private static Path filePath(UUID userId, UUID fileId) {
        var file = FileService.get(userId, fileId);
        return path(userId, Path.of(file.path()));
    }

    private static Path path(Path userPath, Path relativePath, String entry) {
        var path = userPath.resolve(relativePath).resolve(entry).normalize();
        checkAccess(userPath, path);
        return path;
    }

    private static Path path(Path userPath, String entry) {
        var path = userPath.resolve(entry).normalize();
        checkAccess(userPath, path);
        return path;
    }

    private static Path path(UUID userId, Path relativePath) {
        var userPath = path(userId);
        var path = userPath.resolve(relativePath).normalize();
        checkAccess(userPath, path);
        return path;
    }

    private static void checkAccess(Path userPath, Path path) {
        if (!path.startsWith(userPath)) {
            throw new ForbiddenException();
        }
    }

    private static Path path(UUID userId) {
        var userDrivePath = DRIVE_PATH.resolve(userId.toString());
        if (Files.notExists(userDrivePath)) {
            createDirectory(userDrivePath);
        }
        return userDrivePath;
    }

    private static void createDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (IOException exception) {
            throw new InternalException(exception);
        }
    }
}
