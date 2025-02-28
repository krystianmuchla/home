package com.github.krystianmuchla.home.infrastructure.http.drive.file;

import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.file.FileDto;
import com.github.krystianmuchla.home.domain.drive.file.FileService;
import com.github.krystianmuchla.home.domain.drive.file.FileUpdate;
import com.github.krystianmuchla.home.domain.drive.file.error.FileNotFoundException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationError;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.error.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.UUID;

public class FileApiController extends Controller {
    public static final FileApiController INSTANCE = new FileApiController();

    private final FileService fileService = FileService.INSTANCE;

    public FileApiController() {
        super("/api/drive/files");
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, FileFilterRequest::new);
        if (request.file() == null) {
            throw new BadRequestException("file", ValidationError.nullValue());
        }
        try {
            fileService.markAsRemoved(user.id, request.file());
        } catch (FileNotFoundException exception) {
            throw new NotFoundException();
        } catch (FileValidationException exception) {
            throw new InternalServerErrorException(exception);
        } catch (FileNotUpdatedException exception) {
            throw new ConflictException();
        }
        new ResponseWriter(exchange).status(204).write();
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, FileFilterRequest::new);
        if (request.file() == null) {
            throw new BadRequestException("file", ValidationError.nullValue());
        }
        FileDto fileDto;
        try {
            fileDto = fileService.getDto(user.id, request.file());
        } catch (FileNotFoundException exception) {
            throw new NotFoundException();
        }
        new ResponseWriter(exchange).file(fileDto.name(), fileDto.file()).write();
    }

    @Override
    protected void patch(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var filter = RequestReader.readQuery(exchange, FileFilterRequest::new);
        if (filter.file() == null) {
            throw new BadRequestException("file", ValidationError.nullValue());
        }
        var request = RequestReader.readJson(exchange, UpdateFileRequest.class);
        try {
            fileService.update(user.id, filter.file(), map(request));
        } catch (FileValidationException exception) {
            var errors = new MultiValueHashMap<String, ValidationError>();
            for (var error : exception.errors) {
                switch (error) {
                    case FileValidationError.NameBelowMinLength e ->
                        errors.add("name", ValidationError.belowMinLength(e.minLength));
                    case FileValidationError.NameAboveMaxLength e ->
                        errors.add("name", ValidationError.aboveMaxLength(e.maxLength));
                    default -> {
                    }
                }
            }
            if (errors.isEmpty()) {
                throw new InternalServerErrorException(exception);
            } else {
                throw new BadRequestException(errors);
            }
        } catch (FileNotFoundException exception) {
            throw new NotFoundException();
        } catch (FileNotUpdatedException exception) {
            throw new ConflictException();
        }
        new ResponseWriter(exchange).status(204).write();
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var filter = RequestReader.readQuery(exchange, DirectoryFilterRequest::new);
        var request = RequestReader.readHeaders(exchange, UploadFileRequest::new);
        var fileContent = RequestReader.readStream(exchange);
        UUID fileId;
        try {
            fileId = fileService.create(user.id, filter.dir(), request.fileName());
        } catch (DirectoryNotFoundException exception) {
            throw new NotFoundException();
        } catch (FileValidationException exception) {
            var errors = new MultiValueHashMap<String, ValidationError>();
            for (var error : exception.errors) {
                switch (error) {
                    case FileValidationError.NullName ignored -> errors.add("name", ValidationError.nullValue());
                    case FileValidationError.NameBelowMinLength e ->
                        errors.add("name", ValidationError.belowMinValue(e.minLength));
                    case FileValidationError.NameAboveMaxLength e ->
                        errors.add("name", ValidationError.aboveMaxLength(e.maxLength));
                    default -> {
                    }
                }
            }
            if (errors.isEmpty()) {
                throw new InternalServerErrorException(exception);
            } else {
                throw new BadRequestException(errors);
            }
        }
        try {
            fileService.upload(user.id, fileId, fileContent);
        } catch (FileNotFoundException | FileValidationException exception) {
            throw new InternalServerErrorException(exception);
        } catch (FileNotUpdatedException exception) {
            throw new ConflictException();
        }
        new ResponseWriter(exchange).status(204).write();
    }

    private static FileUpdate map(UpdateFileRequest request) {
        return new FileUpdate(request.directoryId(), request.unsetDirectoryId(), request.name());
    }
}
