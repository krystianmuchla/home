package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.domain.id.SignUpToken;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataAlreadyExistsException;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationError;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationError;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationException;
import com.github.krystianmuchla.home.domain.id.session.SessionService;
import com.github.krystianmuchla.home.domain.id.user.User;
import com.github.krystianmuchla.home.domain.id.user.UserService;
import com.github.krystianmuchla.home.domain.id.user.error.UserNotFoundException;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationError;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationException;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.Cookie;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.error.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.UUID;

public class UserApiController extends Controller {
    public static final UserApiController INSTANCE = new UserApiController();

    public UserApiController() {
        super("/api/users");
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var signUpRequest = RequestReader.readJson(exchange, SignUpRequest.class);
        var tokenValid = SignUpToken.INSTANCE.test(signUpRequest.token());
        if (!tokenValid) {
            throw new UnauthorizedException();
        }
        UUID userId;
        try {
            userId = UserService.create(signUpRequest.name(), signUpRequest.login(), signUpRequest.password());
        } catch (UserValidationException exception) {
            var errors = new MultiValueHashMap<String, ValidationError>();
            for (var error : exception.errors) {
                switch (error) {
                    case UserValidationError.NullName ignored -> errors.add("name", ValidationError.nullValue());
                    case UserValidationError.NameBelowMinLength e ->
                        errors.add("name", ValidationError.belowMinLength(e.minLength));
                    case UserValidationError.NameAboveMaxLength e ->
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
        } catch (AccessDataAlreadyExistsException exception) {
            throw new ConflictException();
        } catch (PasswordValidationException exception) {
            var errors = new MultiValueHashMap<String, ValidationError>();
            for (var error : exception.errors) {
                switch (error) {
                    case PasswordValidationError.NullValue ignored ->
                        errors.add("password", ValidationError.nullValue());
                    case PasswordValidationError.ValueBelowMinLength e ->
                        errors.add("password", ValidationError.belowMinLength(e.minLength));
                    case PasswordValidationError.ValueAboveMaxLength e ->
                        errors.add("password", ValidationError.aboveMaxLength(e.maxLength));
                    case PasswordValidationError.ValueWrongFormat ignored ->
                        errors.add("password", ValidationError.wrongFormat());
                }
            }
            throw new BadRequestException(errors);
        } catch (AccessDataValidationException exception) {
            var errors = new MultiValueHashMap<String, ValidationError>();
            for (var error : exception.errors) {
                switch (error) {
                    case AccessDataValidationError.NullLogin ignored ->
                        errors.add("login", ValidationError.nullValue());
                    case AccessDataValidationError.LoginBelowMinLength e ->
                        errors.add("login", ValidationError.belowMinValue(e.minLength));
                    case AccessDataValidationError.LoginAboveMaxLength e ->
                        errors.add("login", ValidationError.aboveMaxLength(e.maxLength));
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
        User user;
        try {
            user = UserService.get(userId);
        } catch (UserNotFoundException exception) {
            throw new InternalServerErrorException(exception);
        }
        var token = SessionService.createSession(user);
        var cookie = Cookie.create("token", token);
        new ResponseWriter(exchange).status(201).cookies(cookie).write();
    }
}
