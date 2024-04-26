package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.error.exception.AuthorizationException;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.error.exception.MissingResourceException;
import com.github.krystianmuchla.home.error.exception.validation.ValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DriveService {
    private static final Path DRIVE_PATH = Path.of(DriveConfig.LOCATION);

    static {
        if (Files.notExists(DRIVE_PATH)) {
            throw new InternalException("Invalid drive location");
        }
    }

    public static Set<String> listDirectory(final UUID userId, final String[] directories) {
        final var path = path(userId, directories);
        try (final var paths = Files.list(path)) {
            return paths
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toSet());
        } catch (final NotDirectoryException exception) {
            throw new ValidationException();
        } catch (final NoSuchFileException exception) {
            throw new MissingResourceException();
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static void uploadFile(final UUID userId, final String[] directories, final FileUpload fileUpload) {
        final var path = path(userId, directories, fileUpload.fileName());
        try (final var outputStream = new FileOutputStream(path.toString())) {
            try (final var inputStream = fileUpload.inputStream()) {
                final var buffer = new byte[512];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            outputStream.flush();
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static File getFile(final UUID userId, final String[] directories, final String fileName) {
        final var path = path(userId, directories, fileName);
        final var file = path.toFile();
        if (!file.exists()) {
            throw new MissingResourceException();
        }
        if (!file.isFile()) {
            throw new ValidationException();
        }
        return file;
    }

    private static Path path(final UUID userId, final String[] directories) {
        final var userDrivePath = userDrivePath(userId);
        final var path = dirPath(userDrivePath, directories).normalize();
        authorization(userDrivePath, path);
        return path;
    }

    private static Path path(final UUID userId, final String[] directories, final String fileName) {
        final var userDrivePath = userDrivePath(userId);
        final var path = filePath(userDrivePath, directories, fileName).normalize();
        authorization(userDrivePath, path);
        return path;
    }

    private static Path filePath(final Path userDrivePath, final String[] directories, final String fileName) {
        return dirPath(userDrivePath, directories).resolve(fileName);
    }

    private static Path dirPath(final Path userDrivePath, final String[] directories) {
        return Path.of(userDrivePath.toString(), directories);
    }

    private static void authorization(final Path userDrivePath, final Path path) {
        if (!path.startsWith(userDrivePath)) {
            throw new AuthorizationException();
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
