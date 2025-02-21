package com.github.krystianmuchla.home.infrastructure.http.drive.directory;

import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryUpdate;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationError;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.error.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DirectoryApiController extends Controller {
    public static final DirectoryApiController INSTANCE = new DirectoryApiController();

    private final DirectoryService directoryService = DirectoryService.INSTANCE;

    public DirectoryApiController() {
        super("/api/drive/directories");
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, DirectoryFilterRequest::new);
        if (request.dir() == null) {
            throw new BadRequestException("dir", ValidationError.nullValue());
        }
        try {
            directoryService.remove(user.id, request.dir());
        } catch (DirectoryNotFoundException exception) {
            throw new NotFoundException();
        } catch (DirectoryValidationException exception) {
            throw new InternalServerErrorException(exception);
        } catch (DirectoryNotUpdatedException exception) {
            throw new ConflictException();
        }
        new ResponseWriter(exchange).status(204).write();
    }

    @Override
    protected void patch(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var filter = RequestReader.readQuery(exchange, DirectoryFilterRequest::new);
        if (filter.dir() == null) {
            throw new BadRequestException("dir", ValidationError.nullValue());
        }
        var request = RequestReader.readJson(exchange, UpdateDirectoryRequest.class);
        try {
            directoryService.update(user.id, filter.dir(), map(request));
        } catch (DirectoryValidationException exception) {
            var errors = new MultiValueHashMap<String, ValidationError>();
            for (var error : exception.errors) {
                switch (error) {
                    case DirectoryValidationError.InvalidHierarchy ignored ->
                        errors.add("parentId", ValidationError.invalidValue());
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
        } catch (DirectoryNotFoundException exception) {
            throw new NotFoundException();
        } catch (DirectoryNotUpdatedException exception) {
            throw new ConflictException();
        }
        new ResponseWriter(exchange).status(204).write();
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
                    case DirectoryValidationError.InvalidHierarchy ignored ->
                        errors.add("dir", ValidationError.invalidValue());
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

    private static DirectoryUpdate map(UpdateDirectoryRequest request) {
        return new DirectoryUpdate(request.parentId(), request.unsetParentId(), request.name());
    }
}
