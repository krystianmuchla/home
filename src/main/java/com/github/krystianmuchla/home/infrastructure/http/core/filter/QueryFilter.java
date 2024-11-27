package com.github.krystianmuchla.home.infrastructure.http.core.filter;

import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.infrastructure.http.core.Attribute;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class QueryFilter extends Filter {
    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        var uri = exchange.getRequestURI();
        var query = parse(uri.getQuery());
        exchange.setAttribute(Attribute.QUERY, query);
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "";
    }

    private MultiValueMap<String, String> parse(String query) {
        var result = new MultiValueHashMap<String, String>();
        if (query == null) {
            return result;
        }
        var pairs = query.split("&");
        for (var pair : pairs) {
            var index = pair.indexOf("=");
            if (index < 0) {
                continue;
            }
            var key = pair.substring(0, index);
            var value = pair.substring(index + 1);
            result.add(key, value);
        }
        return result;
    }
}
