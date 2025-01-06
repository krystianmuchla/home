package com.github.krystianmuchla.home.infrastructure.http.drive;

import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.domain.drive.DriveService;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationError;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;
import com.github.krystianmuchla.home.domain.drive.file.FileDto;
import com.github.krystianmuchla.home.domain.drive.file.FileService;
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

public class DriveApiController extends Controller {
    public static final DriveApiController INSTANCE = new DriveApiController();

    private final DriveService driveService = DriveService.INSTANCE;
    private final DirectoryService directoryService = DirectoryService.INSTANCE;
    private final FileService fileService = FileService.INSTANCE;

    public DriveApiController() {
        super("/api/drive");
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        if (request.file() != null) {
            try {
                fileService.remove(user.id, request.file());
            } catch (FileNotFoundException exception) {
                throw new NotFoundException();
            } catch (FileNotUpdatedException exception) {
                throw new ConflictException();
            }
        } else if (request.dir() != null) {
            try {
                directoryService.remove(user.id, request.dir());
            } catch (DirectoryNotFoundException exception) {
                throw new NotFoundException();
            } catch (DirectoryNotUpdatedException exception) {
                throw new ConflictException();
            }
        } else {
            throw new BadRequestException();
        }
        new ResponseWriter(exchange).status(204).write();
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        if (request.file() == null) {
            var list = driveService.listDirectory(user.id, request.dir());
            new ResponseWriter(exchange).json(list.stream().map(EntryResponse::new).toList()).write();
        } else {
            FileDto fileDto;
            try {
                fileDto = driveService.getFile(user.id, request.file());
            } catch (FileNotFoundException exception) {
                throw new NotFoundException();
            }
            new ResponseWriter(exchange).file(fileDto.name(), fileDto.file()).write();
        }
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readJson(exchange, CreateDirectoryRequest.class);
        try {
            directoryService.create(user.id, request.dir(), request.name());
        } catch (DirectoryNotFoundException exception) {
            throw new NotFoundException();
        } catch (DirectoryValidationException exception) {
            var errors = new MultiValueHashMap<String, ValidationError>();
            for (var error : exception.errors) {
                switch (error) {
                    case DirectoryValidationError.NullName ignored -> errors.add("name", ValidationError.nullValue());
                    case DirectoryValidationError.NameBelowMinLength e ->
                        errors.add("name", ValidationError.belowMinLength(e.minLength));
                    case DirectoryValidationError.NameAboveMaxLength e ->
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
        new ResponseWriter(exchange).status(201).write();
    }

    @Override
    protected void put(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var filter = RequestReader.readQuery(exchange, DriveFilterRequest::new);
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
        driveService.uploadFile(user.id, fileId, fileContent);
        try {
            fileService.upload(user.id, fileId);
        } catch (FileNotFoundException exception) {
            throw new InternalServerErrorException(exception);
        } catch (FileNotUpdatedException exception) {
            throw new ConflictException();
        }
        new ResponseWriter(exchange).status(204).write();
    }
}
