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
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DriveApiController extends Controller {
    public static final DriveApiController INSTANCE = new DriveApiController();

    public DriveApiController() {
        super("/api/drive");
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        if (request.file() != null) {
            Transaction.run(() -> {
                try {
                    FileService.remove(user.id, request.file());
                } catch (FileNotFoundException exception) {
                    throw new NotFoundException();
                } catch (FileNotUpdatedException exception) {
                    throw new ConflictException();
                }
            });
        } else if (request.dir() != null) {
            Transaction.run(() -> {
                try {
                    DirectoryService.remove(user.id, request.dir());
                } catch (DirectoryNotFoundException exception) {
                    throw new NotFoundException();
                } catch (DirectoryNotUpdatedException exception) {
                    throw new ConflictException();
                }
            });
        } else {
            throw new BadRequestException();
        }
        ResponseWriter.write(exchange, 204);
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        if (request.file() == null) {
            var list = DriveService.listDirectory(user.id, request.dir());
            ResponseWriter.writeJson(exchange, 200, list.stream().map(EntryResponse::new).toList());
        } else {
            FileDto fileDto;
            try {
                fileDto = DriveService.getFile(user.id, request.file());
            } catch (FileNotFoundException exception) {
                throw new NotFoundException();
            }
            ResponseWriter.writeFile(exchange, 200, fileDto.name(), fileDto.file());
        }
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readJson(exchange, CreateDirectoryRequest.class);
        Transaction.run(() -> {
            try {
                DirectoryService.create(user.id, request.dir(), request.name());
            } catch (DirectoryNotFoundException exception) {
                throw new NotFoundException();
            } catch (DirectoryValidationException exception) {
                var errors = new MultiValueHashMap<String, ValidationError>();
                for (var error : exception.errors) {
                    switch (error) {
                        case DirectoryValidationError.NullName ignored ->
                            errors.add("name", ValidationError.nullValue());
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
        });
        ResponseWriter.write(exchange, 201);
    }

    @Override
    protected void put(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var filter = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        var request = RequestReader.readHeaders(exchange, UploadFileRequest::new);
        var fileContent = RequestReader.readStream(exchange);
        var fileId = Transaction.run(() -> {
            try {
                return FileService.create(user.id, filter.dir(), request.fileName());
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
        });
        DriveService.uploadFile(user.id, fileId, fileContent);
        Transaction.run(() -> {
            try {
                FileService.upload(user.id, fileId);
            } catch (FileNotFoundException exception) {
                throw new InternalServerErrorException(exception);
            } catch (FileNotUpdatedException exception) {
                throw new ConflictException();
            }
        });
        ResponseWriter.write(exchange, 204);
    }
}
