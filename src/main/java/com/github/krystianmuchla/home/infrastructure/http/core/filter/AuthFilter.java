package com.github.krystianmuchla.home.infrastructure.http.core.filter;

import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.domain.id.error.UnauthenticatedException;
import com.github.krystianmuchla.home.domain.id.session.SessionService;
import com.github.krystianmuchla.home.domain.id.user.User;
import com.github.krystianmuchla.home.infrastructure.http.core.*;
import com.github.krystianmuchla.home.infrastructure.http.core.error.UnauthorizedException;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class AuthFilter extends Filter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

    private final MultiValueMap<String, Method> optionalUserRoutes = ControllerConfig.OPTIONAL_USER_ROUTES;
    private final MultiValueMap<String, Method> noUserRoutes = ControllerConfig.NO_USER_ROUTES;

    private final SessionService sessionService = SessionService.INSTANCE;

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        if (matches(exchange, optionalUserRoutes)) {
            chain.doFilter(exchange);
        } else if (matches(exchange, noUserRoutes)) {
            try {
                user(exchange);
            } catch (UnauthorizedException exception) {
                chain.doFilter(exchange);
            } catch (Exception exception) {
                LOG.warn("{}", exception.getMessage(), exception);
                HttpErrorHandler.handle(exchange, exception);
            }
            new ResponseWriter(exchange).status(302).location(ControllerConfig.DEFAULT_PATH).write();
        } else {
            try {
                var user = user(exchange);
                exchange.setAttribute(Attribute.USER, user);
                chain.doFilter(exchange);
            } catch (UnauthorizedException exception) {
                HttpErrorHandler.handle(exchange, exception);
            } catch (Exception exception) {
                LOG.warn("{}", exception.getMessage(), exception);
                HttpErrorHandler.handle(exchange, exception);
            }
        }
    }

    @Override
    public String description() {
        return "";
    }

    private boolean matches(HttpExchange exchange, MultiValueMap<String, Method> routes) {
        var path = exchange.getRequestURI().getPath();
        var method = Method.of(exchange.getRequestMethod());
        return routes.maybeGet(path).map(methods -> methods.contains(method)).orElse(false);
    }

    private User user(HttpExchange exchange) {
        var cookies = RequestReader.readCookies(exchange);
        return Optional.ofNullable(cookies.get("token"))
            .map(token -> {
                try {
                    return sessionService.getSession(token);
                } catch (UnauthenticatedException exception) {
                    throw new UnauthorizedException();
                }
            })
            .map(session -> session.user)
            .orElseThrow(UnauthorizedException::new);
    }
}
