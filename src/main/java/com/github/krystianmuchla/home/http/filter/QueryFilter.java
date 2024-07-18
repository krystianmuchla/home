package com.github.krystianmuchla.home.http.filter;

import com.github.krystianmuchla.home.http.Attribute;
import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.util.MultiValueMap;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class QueryFilter extends Filter {
    @Override
    public void doFilter(final HttpExchange exchange, final Chain chain) throws IOException {
        final var uri = exchange.getRequestURI();
        final var query = parse(uri.getQuery());
        exchange.setAttribute(Attribute.QUERY, query);
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Adds query parameters map to the exchange.";
    }

    private MultiValueMap<String, String> parse(final String query) {
        final var result = new MultiValueHashMap<String, String>();
        if (query == null) {
            return result;
        }
        final var pairs = query.split("&");
        for (final var pair : pairs) {
            final var index = pair.indexOf("=");
            if (index < 0) {
                continue;
            }
            final var key = pair.substring(0, index);
            final var value = pair.substring(index + 1);
            result.add(key, value);
        }
        return result;
    }
}
