package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.http.ForbiddenException;
import com.github.krystianmuchla.home.exception.http.NotFoundException;
import com.github.krystianmuchla.home.util.StreamService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class DriveService {
    private static final Path DRIVE_PATH = Path.of(DriveConfig.LOCATION);

    static {
        if (Files.notExists(DRIVE_PATH)) {
            throw new InternalException("Invalid drive location");
        }
    }

    public static List<File> listDirectory(final UUID userId, final List<String> directories) {
        final var path = path(userId, directories);
        try (final var paths = Files.list(path)) {
            return paths.map(Path::toFile).toList();
        } catch (final NotDirectoryException exception) {
            throw new BadRequestException();
        } catch (final NoSuchFileException exception) {
            throw new NotFoundException();
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static void uploadFile(final UUID userId, final List<String> directories, final FileUpload fileUpload) {
        final var path = path(userId, directories, fileUpload.fileName());
        try (final var outputStream = new FileOutputStream(path.toString())) {
            try (final var inputStream = fileUpload.inputStream()) {
                StreamService.copy(inputStream, outputStream);
            }
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static File getFile(final UUID userId, final List<String> directories, final String fileName) {
        final var path = path(userId, directories, fileName);
        final var file = path.toFile();
        if (!file.exists()) {
            throw new NotFoundException();
        }
        if (!file.isFile()) {
            throw new BadRequestException();
        }
        return file;
    }

    private static Path path(final UUID userId, final List<String> directories) {
        final var userDrivePath = userDrivePath(userId);
        final var path = dirPath(userDrivePath, directories).normalize();
        authorization(userDrivePath, path);
        return path;
    }

    private static Path path(final UUID userId, final List<String> directories, final String fileName) {
        final var userDrivePath = userDrivePath(userId);
        final var path = filePath(userDrivePath, directories, fileName).normalize();
        authorization(userDrivePath, path);
        return path;
    }

    private static Path filePath(final Path userDrivePath, final List<String> directories, final String fileName) {
        return dirPath(userDrivePath, directories).resolve(fileName);
    }

    private static Path dirPath(final Path userDrivePath, final List<String> directories) {
        return Path.of(userDrivePath.toString(), directories.toArray(String[]::new));
    }

    private static void authorization(final Path userDrivePath, final Path path) {
        if (!path.startsWith(userDrivePath)) {
            throw new ForbiddenException();
        }
    }

    private static Path userDrivePath(final UUID userId) {
        final var userDrivePath = DRIVE_PATH.resolve(userId.toString());
        if (Files.notExists(userDrivePath)) {
            try {
                Files.createDirectory(userDrivePath);
            } catch (final IOException exception) {
                throw new InternalException(exception);
            }
        }
        return userDrivePath;
    }
}
