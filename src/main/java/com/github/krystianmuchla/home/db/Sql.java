package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.util.CollectionService;
import com.github.krystianmuchla.home.util.StreamService;
import com.github.krystianmuchla.home.util.TimestampFactory;

import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public class Sql {
    public final List<String> words = new ArrayList<>();
    public final List<Object> parameters = new ArrayList<>();

    public static Sql eq(final String field, final Object value) {
        return new Builder().eq(field, value).build();
    }

    public static Sql lt(final String field, final Object value) {
        return new Builder().lt(field, value).build();
    }

    public static Sql in(final String field, final Set<?> values) {
        return new Builder().in(field, values).build();
    }

    public static Sql and() {
        return new Builder().and().build();
    }

    public String template() {
        return CollectionService.join(" ", words);
    }

    public Object[] parameters() {
        return parameters.stream().map(this::parameter).toArray();
    }

    private Object parameter(final Object parameter) {
        if (parameter == null) {
            return null;
        }
        return switch (parameter) {
            case UUID uuid -> uuid.toString();
            case URI uri -> uri.toString();
            case Timestamp timestamp -> timestamp.toString();
            case Instant instant -> {
                final var timestamp = TimestampFactory.create(instant);
                yield parameter(timestamp);
            }
            default -> parameter;
        };
    }

    public static class Builder {
        private final Sql sql = new Sql();

        public Builder update(final String table) {
            sql.words.add("UPDATE");
            sql.words.add(table);
            return this;
        }

        public Builder delete() {
            sql.words.add("DELETE");
            return this;
        }

        public Builder select(final String... fields) {
            sql.words.add("SELECT");
            if (fields.length > 0) {
                sql.words.add(String.join(", ", fields));
            } else {
                sql.words.add("*");
            }
            return this;
        }

        public Builder insertInto(final String table, final String... fields) {
            sql.words.add("INSERT INTO");
            sql.words.add(table);
            for (var index = 0; index < fields.length; index++) {
                final var first = index == 0;
                final var last = index == fields.length - 1;
                final var builder = new StringBuilder(3);
                if (first) {
                    builder.append("(");
                }
                builder.append("?");
                if (last) {
                    builder.append(")");
                } else {
                    builder.append(",");
                }
                sql.words.add(builder.toString());
                sql.parameters.add(fields[index]);
            }
            return this;
        }

        public Builder from(final String table) {
            sql.words.add("FROM");
            sql.words.add(table);
            return this;
        }

        public Builder set(final Sql... sqls) {
            sql.words.add("SET");
            final var words = Arrays.stream(sqls).flatMap(sql -> sql.words.stream());
            sql.words.add(StreamService.join(", ", words));
            for (final var s : sqls) {
                sql.parameters.addAll(s.parameters);
            }
            return this;
        }

        public Builder values(final Object... values) {
            sql.words.add("VALUES");
            for (var index = 0; index < values.length; index++) {
                final var first = index == 0;
                final var last = index == values.length - 1;
                final var builder = new StringBuilder(3);
                if (first) {
                    builder.append("(");
                }
                builder.append("?");
                if (last) {
                    builder.append(")");
                } else {
                    builder.append(",");
                }
                sql.words.add(builder.toString());
                sql.parameters.add(values[index]);
            }
            return this;
        }

        public Builder where(final Sql... sqls) {
            sql.words.add("WHERE");
            for (final var s : sqls) {
                sql.words.addAll(s.words);
                sql.parameters.addAll(s.parameters);
            }
            return this;
        }

        public Builder and() {
            sql.words.add("AND");
            return this;
        }

        public Builder eq(final String field, final Object value) {
            sql.words.add(field + " = ?");
            if (value == null) {
                sql.parameters.add("NULL");
            } else {
                sql.parameters.add(value);
            }
            return this;
        }

        public Builder lt(final String field, final Object value) {
            sql.words.add(field + " < ?");
            sql.parameters.add(value);
            return this;
        }

        public Builder in(final String field, final Set<?> values) {
            final var parameters = values.toArray();
            sql.words.add(field);
            sql.words.add("IN");
            for (var index = 0; index < parameters.length; index++) {
                final var first = index == 0;
                final var last = index == parameters.length - 1;
                final var builder = new StringBuilder(3);
                if (first) {
                    builder.append("(");
                }
                builder.append("?");
                if (last) {
                    builder.append(")");
                } else {
                    builder.append(",");
                }
                sql.words.add(builder.toString());
                sql.parameters.add(parameters[index]);
            }
            return this;
        }

        public Builder isNull(final String field) {
            sql.words.add(field);
            sql.words.add("IS NULL");
            return this;
        }

        public Builder orderBy(final String field) {
            sql.words.add("ORDER BY");
            sql.words.add(field);
            return this;
        }

        public Builder desc() {
            sql.words.add("DESC");
            return this;
        }

        public Builder limit(final int limit) {
            sql.words.add("LIMIT ?");
            sql.parameters.add(limit);
            return this;
        }

        public Builder offset(final int offset) {
            sql.words.add("OFFSET ?");
            sql.parameters.add(offset);
            return this;
        }

        public Builder forUpdate() {
            sql.words.add("FOR UPDATE");
            return this;
        }

        public Sql build() {
            return sql;
        }
    }
}
