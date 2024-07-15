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

    public static List<Entry> listDirectory(final UUID userId, final UUID directoryId) {
        final var directories = DirectoryPersistence.readByParentId(userId, directoryId).stream()
            .map(directory -> new Entry(directory.id(), EntryType.DIR, directory.getName()));
        final var files = FilePersistence.readByDirectoryId(userId, directoryId).stream()
            .map(file -> new Entry(file.id(), EntryType.FILE, file.getName()));
        return Stream.concat(directories, files).toList();
    }

    public static void createDirectory(final UUID userId, final UUID directoryId, final String name) {
        final var userPath = path(userId);
        final var path = entryPath(userPath, userId, directoryId, name);
        if (Files.exists(path)) {
            throw new ConflictException("DIRECTORY_ALREADY_EXISTS");
        }
        final var relativeUri = userPath.toUri().relativize(path.toUri());
        DirectoryService.create(userId, directoryId, relativeUri.getPath());
        createDirectory(path);
    }

    public static void uploadFile(final UUID userId, final UUID directoryId, final FileUpload fileUpload) {
        final var userPath = path(userId);
        final var path = entryPath(userPath, userId, directoryId, fileUpload.fileName());
        if (Files.notExists(path)) {
            final var relativeUri = userPath.toUri().relativize(path.toUri());
            FileService.create(userId, directoryId, relativeUri.getPath());
        }
        try (final var outputStream = new FileOutputStream(path.toString())) {
            try (final var inputStream = fileUpload.inputStream()) {
                StreamService.copy(inputStream, outputStream);
            }
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static File getFile(final UUID userId, final UUID fileId) {
        final var path = filePath(userId, fileId);
        final var file = path.toFile();
        if (!file.exists()) {
            throw new NotFoundException();
        }
        if (!file.isFile()) {
            throw new BadRequestException();
        }
        return file;
    }

    private static Path entryPath(final Path userPath, final UUID userId, final UUID directoryId, final String entry) {
        if (directoryId == null) {
            return path(userPath, entry);
        }
        final var directory = DirectoryService.get(userId, directoryId);
        return path(userPath, Path.of(directory.path()), entry);
    }

    private static Path filePath(final UUID userId, final UUID fileId) {
        final var file = FileService.get(userId, fileId);
        return path(userId, Path.of(file.path()));
    }

    private static Path path(final Path userPath, final Path relativePath, final String entry) {
        final var path = userPath.resolve(relativePath).resolve(entry).normalize();
        checkAccess(userPath, path);
        return path;
    }

    private static Path path(final Path userPath, final String entry) {
        final var path = userPath.resolve(entry).normalize();
        checkAccess(userPath, path);
        return path;
    }

    private static Path path(final UUID userId, final Path relativePath) {
        final var userPath = path(userId);
        final var path = userPath.resolve(relativePath).normalize();
        checkAccess(userPath, path);
        return path;
    }

    private static void checkAccess(final Path userPath, final Path path) {
        if (!path.startsWith(userPath)) {
            throw new ForbiddenException();
        }
    }

    private static Path path(final UUID userId) {
        final var userDrivePath = DRIVE_PATH.resolve(userId.toString());
        if (Files.notExists(userDrivePath)) {
            createDirectory(userDrivePath);
        }
        return userDrivePath;
    }

    private static void createDirectory(final Path path) {
        try {
            Files.createDirectory(path);
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }
}
