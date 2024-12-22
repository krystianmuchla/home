package com.github.krystianmuchla.home.infrastructure.persistence.core;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.util.CollectionService;
import com.github.krystianmuchla.home.application.util.StreamService;
import com.github.krystianmuchla.home.application.util.TimestampFactory;

import java.net.URI;
import java.sql.Timestamp;
import java.util.*;

public class Sql {
    public final List<String> words = new ArrayList<>();
    public final List<Object> parameters = new ArrayList<>();

    public static Sql eq(String field, Object value) {
        return new Builder().eq(field, value).build();
    }

    public static Sql lt(String field, Object value) {
        return new Builder().lt(field, value).build();
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

    private Object parameter(Object parameter) {
        if (parameter == null) {
            return null;
        }
        return switch (parameter) {
            case Enum<?> enumeration -> enumeration.toString();
            case UUID uuid -> uuid.toString();
            case URI uri -> uri.toString();
            case Timestamp timestamp -> timestamp.toString();
            case Time time -> {
                var timestamp = TimestampFactory.create(time);
                yield parameter(timestamp);
            }
            default -> parameter;
        };
    }

    public static class Builder {
        private final Sql sql = new Sql();

        public Builder update(String table) {
            sql.words.add("UPDATE");
            sql.words.add(table);
            return this;
        }

        public Builder delete() {
            sql.words.add("DELETE");
            return this;
        }

        public Builder select(String... fields) {
            sql.words.add("SELECT");
            if (fields.length > 0) {
                sql.words.add(String.join(", ", fields));
            } else {
                sql.words.add("*");
            }
            return this;
        }

        public Builder insertInto(String table, String... fields) {
            sql.words.add("INSERT INTO");
            sql.words.add(table);
            for (var index = 0; index < fields.length; index++) {
                var first = index == 0;
                var last = index == fields.length - 1;
                var builder = new StringBuilder(3);
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

        public Builder from(String table) {
            sql.words.add("FROM");
            sql.words.add(table);
            return this;
        }

        public Builder set(Sql... sqls) {
            sql.words.add("SET");
            var words = Arrays.stream(sqls).flatMap(sql -> sql.words.stream());
            sql.words.add(StreamService.join(", ", words));
            for (var s : sqls) {
                sql.parameters.addAll(s.parameters);
            }
            return this;
        }

        public Builder values(Object... values) {
            sql.words.add("VALUES");
            for (var index = 0; index < values.length; index++) {
                var first = index == 0;
                var last = index == values.length - 1;
                var builder = new StringBuilder(3);
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

        public Builder where(Sql... sqls) {
            sql.words.add("WHERE");
            for (var s : sqls) {
                sql.words.addAll(s.words);
                sql.parameters.addAll(s.parameters);
            }
            return this;
        }

        public Builder and() {
            sql.words.add("AND");
            return this;
        }

        public Builder eq(String field, Object value) {
            sql.words.add(field + " = ?");
            if (value == null) {
                sql.parameters.add("NULL");
            } else {
                sql.parameters.add(value);
            }
            return this;
        }

        public Builder lt(String field, Object value) {
            sql.words.add(field + " < ?");
            sql.parameters.add(value);
            return this;
        }

        public Builder in(String field, Set<?> values) {
            var parameters = values.toArray();
            sql.words.add(field);
            sql.words.add("IN");
            for (var index = 0; index < parameters.length; index++) {
                var first = index == 0;
                var last = index == parameters.length - 1;
                var builder = new StringBuilder(3);
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

        public Builder isNull(String field) {
            sql.words.add(field);
            sql.words.add("IS NULL");
            return this;
        }

        public Builder orderBy(String field) {
            sql.words.add("ORDER BY");
            sql.words.add(field);
            return this;
        }

        public Builder desc() {
            sql.words.add("DESC");
            return this;
        }

        public Builder limit(int limit) {
            sql.words.add("LIMIT ?");
            sql.parameters.add(limit);
            return this;
        }

        public Sql build() {
            return sql;
        }
    }
}
