package com.github.krystianmuchla.home.http.filter;

import com.github.krystianmuchla.home.exception.http.UnauthorizedException;
import com.github.krystianmuchla.home.http.*;
import com.github.krystianmuchla.home.id.session.Session;
import com.github.krystianmuchla.home.id.session.SessionId;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.User;
import com.github.krystianmuchla.home.util.MultiValueMap;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuthFilter extends Filter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

    private final MultiValueMap<String, Method> optionalUserRoutes = HttpConfig.OPTIONAL_USER_ROUTES;
    private final MultiValueMap<String, Method> noUserRoutes = HttpConfig.NO_USER_ROUTES;

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
            ResponseWriter.writeLocation(exchange, 302, HttpConfig.DEFAULT_PATH);
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
        return routes.getAll(path).map(methods -> methods.contains(method)).orElse(false);
    }

    private User user(HttpExchange exchange) {
        var cookies = RequestReader.readCookies(exchange);
        return SessionId.fromCookies(cookies)
            .map(SessionService::getSession)
            .map(Session::user)
            .orElseThrow(UnauthorizedException::new);
    }
}
