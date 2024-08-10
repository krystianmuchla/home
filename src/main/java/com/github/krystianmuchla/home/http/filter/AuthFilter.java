package com.github.krystianmuchla.home.http.filter;

import com.github.krystianmuchla.home.exception.http.UnauthorizedException;
import com.github.krystianmuchla.home.http.*;
import com.github.krystianmuchla.home.id.session.Session;
import com.github.krystianmuchla.home.id.session.SessionId;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.User;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class AuthFilter extends Filter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

    private final Set<String> optionalUserPaths = HttpConfig.OPTIONAL_USER_PATHS;
    private final Set<String> noUserPaths = HttpConfig.NO_USER_PATHS;

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        var path = exchange.getRequestURI().getPath();
        if (optionalUserPaths.contains(path)) {
            chain.doFilter(exchange);
        } else if (noUserPaths.contains(path)) {
            User user = null;
            try {
                user = user(exchange);
            } catch (UnauthorizedException exception) {
                chain.doFilter(exchange);
            } catch (Exception exception) {
                LOG.warn("{}", exception.getMessage(), exception);
                HttpErrorHandler.handle(exchange, exception);
            }
            if (user != null) {
                ResponseWriter.writeLocation(exchange, 302, HttpConfig.DEFAULT_PATH);
            }
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
        return "Handles authorization.";
    }

    private User user(HttpExchange exchange) {
        var cookies = RequestReader.readCookies(exchange);
        return SessionId.fromCookies(cookies)
            .map(SessionService::getSession)
            .map(Session::user)
            .orElseThrow(UnauthorizedException::new);
    }
}
