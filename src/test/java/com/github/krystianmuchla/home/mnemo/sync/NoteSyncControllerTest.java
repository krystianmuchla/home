package com.github.krystianmuchla.home.mnemo.sync;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.krystianmuchla.home.AppTest;
import com.github.krystianmuchla.home.Dao;
import com.github.krystianmuchla.home.db.DbConnection;
import com.github.krystianmuchla.home.mnemo.Note;
import com.github.krystianmuchla.home.mnemo.grave.NoteGrave;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class NoteSyncControllerTest extends AppTest {
    private static Connection dbConnection;
    private static Dao dao;
    private static ObjectMapper objectMapper;

    @BeforeAll
    protected static void beforeAllTests() throws Exception {
        AppTest.beforeAllTests();
        dbConnection = DbConnection.create();
        dao = new Dao(dbConnection);
        objectMapper = new ObjectMapper();
        final var module = new JavaTimeModule();
        module.addDeserializer(Instant.class, new StdDeserializer<>(Instant.class) {
            @Override
            public Instant deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
                final var time = parser.getValueAsString();
                return Instant.parse(time);
            }
        });
        module.addSerializer(new StdSerializer<>(Instant.class) {
            @Override
            public void serialize(final Instant time, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
                final var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
                generator.writeString(formatter.format(time));
            }
        });
        objectMapper.registerModule(module);
    }

    @AfterEach
    void afterEachTest() throws SQLException {
        dao.executeUpdate("DELETE FROM note");
        dao.executeUpdate("DELETE FROM note_grave");
        dbConnection.commit();
    }

    @Test
    void shouldSyncNotes() throws URISyntaxException, IOException, InterruptedException, SQLException {
        final var externalNotes = new Note[]{
            new Note(
                UUID.fromString("4d8af443-bfa9-4d47-a886-b1ddc82a958d"),
                "External note",
                "4d8af443-bfa9-4d47-a886-b1ddc82a958d",
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                UUID.fromString("cb8b51f8-63e5-4964-94e4-0b3b7944e7d4"),
                "External note with same id and earlier modification time",
                "cb8b51f8-63e5-4964-94e4-0b3b7944e7d4",
                Instant.parse("2000-01-01T00:00:00Z"),
                Instant.parse("2000-01-01T00:00:00Z")
            ),
            new Note(
                UUID.fromString("2109af10-c870-4e8d-8f53-7220d693ca78"),
                "External note with same id and same modification time",
                "2109af10-c870-4e8d-8f53-7220d693ca78",
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                UUID.fromString("2f6772bc-2a58-4e25-9318-3a60d4fb52dc"),
                "External note with same id and later modification time",
                "2f6772bc-2a58-4e25-9318-3a60d4fb52dc",
                Instant.parse("2020-12-31T23:59:59Z"),
                Instant.parse("2020-12-31T23:59:59Z")
            ),
            new Note(
                UUID.fromString("fb9e3586-9e71-49dd-a4f7-a6ed86a8999e"),
                "External note with no content",
                null,
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                UUID.fromString("7bf721b5-30de-4697-92aa-1a6b61a418a9"),
                "External note with same id and no content and earlier modification time",
                null,
                Instant.parse("2000-01-01T00:00:00Z"),
                Instant.parse("2000-01-01T00:00:00Z")
            ),
            new Note(
                UUID.fromString("c0dc5769-d1df-4e4f-8a49-87e3d8651c6e"),
                "External note with same id and no content and same modification time",
                null,
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                UUID.fromString("caa96b92-1469-43cd-b58e-59a4c271a270"),
                "External note with same id and no content and later modification time",
                null,
                Instant.parse("2020-12-31T23:59:59Z"),
                Instant.parse("2020-12-31T23:59:59Z")
            ),
            new Note(
                UUID.fromString("009bac89-770c-4bcc-ad93-e2d0986e105e"),
                "External note with same id as grave and earlier modification time",
                "009bac89-770c-4bcc-ad93-e2d0986e105e",
                Instant.parse("2000-01-01T00:00:00Z"),
                Instant.parse("2000-01-01T00:00:00Z")
            ),
            new Note(
                UUID.fromString("41f40e37-5b18-4637-8241-dc05dd8b52ba"),
                "External note with same id as grave and same modification time",
                "41f40e37-5b18-4637-8241-dc05dd8b52ba",
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                UUID.fromString("528c8c2f-ac76-4543-a333-b66135b0749a"),
                "External note with same id as grave and later modification time",
                "528c8c2f-ac76-4543-a333-b66135b0749a",
                Instant.parse("2020-12-31T23:59:59Z"),
                Instant.parse("2020-12-31T23:59:59Z")
            ),
            new Note(
                UUID.fromString("a884e780-476d-4fcb-b41b-4dd8e5f7754e"),
                "External note with same id as grave and no content and earlier modification time",
                null,
                Instant.parse("2000-01-01T00:00:00Z"),
                Instant.parse("2000-01-01T00:00:00Z")
            ),
            new Note(
                UUID.fromString("2a4cef21-ffa9-48a7-9659-7e03da2d7e01"),
                "External note with same id as grave and no content and same modification time",
                null,
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                UUID.fromString("dd2529d5-7380-496e-a52f-d5b4e7adf44d"),
                "External note with same id as grave and no content and later modification time",
                null,
                Instant.parse("2020-12-31T23:59:59Z"),
                Instant.parse("2020-12-31T23:59:59Z")
            )
        };
        final var notes = new Note[]{
            new Note(
                UUID.fromString("0981a57b-9ccd-455e-956d-2daf39e45480"),
                "0981a57b-9ccd-455e-956d-2daf39e45480",
                "0981a57b-9ccd-455e-956d-2daf39e45480",
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes[1].id(),
                externalNotes[1].id().toString(),
                externalNotes[1].id().toString(),
                externalNotes[1].creationTime(),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes[2].id(),
                externalNotes[2].id().toString(),
                externalNotes[2].id().toString(),
                externalNotes[2].creationTime(),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes[3].id(),
                externalNotes[3].id().toString(),
                externalNotes[3].id().toString(),
                externalNotes[3].creationTime(),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes[5].id(),
                externalNotes[5].id().toString(),
                externalNotes[5].id().toString(),
                externalNotes[5].creationTime(),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes[6].id(),
                externalNotes[6].id().toString(),
                externalNotes[6].id().toString(),
                externalNotes[6].creationTime(),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes[7].id(),
                externalNotes[7].id().toString(),
                externalNotes[7].id().toString(),
                externalNotes[7].creationTime(),
                Instant.parse("2010-10-10T10:10:10Z")
            )
        };
        final var noteGraves = new NoteGrave[]{
            new NoteGrave(externalNotes[8].id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes[9].id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes[10].id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes[11].id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes[12].id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes[13].id(), Instant.parse("2010-10-10T10:10:10Z"))
        };
        final var requestBody = createRequestBody(externalNotes);
        createNotes(notes);
        createNoteGraves(noteGraves);
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(APP_HOST + "/api/notes/sync"))
            .headers("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var responseTree = objectMapper.readTree(response.body());
        assertThat(responseTree.size()).isEqualTo(1);
        final var notesResponse = objectMapper.readValue(
            responseTree.get("notes").toString(),
            new TypeReference<List<Note>>() {
            }
        );
        assertThat(notesResponse).hasSize(9).contains(
            notes[0],
            notes[1],
            notes[2],
            notes[4],
            notes[5],
            noteGraves[0].toNote(),
            noteGraves[1].toNote(),
            noteGraves[3].toNote(),
            noteGraves[4].toNote()
        );
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(8).contains(
            externalNotes[0],
            externalNotes[3],
            externalNotes[10],
            notes[0],
            notes[1],
            notes[2],
            notes[4],
            notes[5]
        );
        final var noteGravesDb = readNoteGraves();
        assertThat(noteGravesDb).hasSize(6).contains(
            new NoteGrave(
                externalNotes[7].id(),
                externalNotes[7].modificationTime()
            ),
            noteGraves[0],
            noteGraves[1],
            noteGraves[3],
            noteGraves[4],
            noteGraves[5]
        );
    }

    private static void createNote(final Note note) throws SQLException {
        dao.executeUpdate(
            "INSERT INTO note VALUES (?, ?, ?, ?, ?)",
            note.id().toString(),
            note.title(),
            note.content(),
            Timestamp.valueOf(LocalDateTime.ofInstant(note.creationTime(), ZoneOffset.UTC)).toString(),
            Timestamp.valueOf(LocalDateTime.ofInstant(note.modificationTime(), ZoneOffset.UTC)).toString()
        );
        dbConnection.commit();
    }

    private static void createNotes(final Note... notes) throws SQLException {
        for (final var note : notes) {
            createNote(note);
        }
    }

    private static List<Note> readNotes() throws SQLException {
        return dao.executeQuery("SELECT * FROM note", new Function<>() {
            @Override
            @SneakyThrows
            public Note apply(ResultSet resultSet) {
                return new Note(
                    UUID.fromString(resultSet.getString("id")),
                    resultSet.getString("title"),
                    resultSet.getString("content"),
                    resultSet.getTimestamp("creation_time").toLocalDateTime().toInstant(ZoneOffset.UTC),
                    resultSet.getTimestamp("modification_time").toLocalDateTime().toInstant(ZoneOffset.UTC)
                );
            }
        });
    }

    private static void createNoteGrave(final NoteGrave noteGrave) throws SQLException {
        dao.executeUpdate(
            "INSERT INTO note_grave VALUES (?, ?)",
            noteGrave.id().toString(),
            Timestamp.valueOf(LocalDateTime.ofInstant(noteGrave.creationTime(), ZoneOffset.UTC)).toString()
        );
        dbConnection.commit();
    }

    private static void createNoteGraves(final NoteGrave... noteGraves) throws SQLException {
        for (final var noteGrave : noteGraves) {
            createNoteGrave(noteGrave);
        }
    }

    private static List<NoteGrave> readNoteGraves() {
        return dao.executeQuery("SELECT * FROM note_grave", new Function<>() {
            @Override
            @SneakyThrows
            public NoteGrave apply(ResultSet resultSet) {
                return new NoteGrave(
                    UUID.fromString(resultSet.getString("id")),
                    resultSet.getTimestamp("creation_time").toLocalDateTime().toInstant(ZoneOffset.UTC)
                );
            }
        });
    }

    private static String createRequestBody(final Note[] notes) {
        final var noteRequests = Arrays.stream(notes)
            .map(new Function<Note, String>() {
                @Override
                @SneakyThrows
                public String apply(Note note) {
                    return objectMapper.writeValueAsString(note);
                }
            })
            .collect(Collectors.joining(","));
        return "{\"notes\":[" + noteRequests + "]}";
    }
}
