package com.github.krystianmuchla.home.mnemo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.krystianmuchla.home.AppTest;
import com.github.krystianmuchla.home.Dao;
import com.github.krystianmuchla.home.db.DbConnection;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class NoteControllerTest extends AppTest {

    private static Connection dbConnection;
    private static Dao dao;

    @BeforeAll
    protected static void beforeAllTests() throws Exception {
        AppTest.beforeAllTests();
        dbConnection = DbConnection.create();
        dao = new Dao(dbConnection);
    }

    @AfterEach
    void afterEachTest() throws SQLException {
        dao.executeUpdate("DELETE FROM note");
        dao.executeUpdate("DELETE FROM note_grave");
        dbConnection.commit();
    }

    @Test
    void shouldPostNote() throws URISyntaxException, IOException, InterruptedException {
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(APP_HOST + "/api/notes"))
            .headers("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("""
                {
                  "title": "%s",
                  "content": "%s"
                }
                """.formatted(noteTitle, noteContent)
            ))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var responseBody = new ObjectMapper().readValue(
            response.body(),
            new TypeReference<Map<String, Object>>() {
            }
        );
        assertThat(responseBody).hasSize(1);
        final var noteId = UUID.fromString((String) responseBody.get("id"));
        final var notes = readNotes();
        assertThat(notes).hasSize(1);
        final var note = notes.get(0);
        assertThat(note.id()).isEqualTo(noteId);
        assertThat(note.title()).isEqualTo(noteTitle);
        assertThat(note.content()).isEqualTo(noteContent);
        assertThat(note.creationTime()).isNotNull();
        assertThat(note.modificationTime()).isNotNull();
        assertThat(note.creationTime()).isEqualTo(note.modificationTime());
    }

    @Test
    void shouldGetNote() throws URISyntaxException, IOException, InterruptedException, SQLException {
        final var noteId = "81f6d5f3-9226-437f-bf5d-3e9eba985eb7";
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var noteCreationTime = "2010-10-10T10:10:10.100Z";
        final var noteModificationTime = "2011-11-11T11:11:11.111Z";
        createNote(
            UUID.fromString(noteId),
            noteTitle,
            noteContent,
            Instant.parse(noteCreationTime),
            Instant.parse(noteModificationTime)
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(APP_HOST + "/api/notes/" + noteId))
            .GET()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var note = new ObjectMapper().readValue(
            response.body(),
            new TypeReference<Map<String, Object>>() {
            }
        );
        assertThat(note.get("id")).isEqualTo(noteId);
        assertThat(note.get("title")).isEqualTo(noteTitle);
        assertThat(note.get("content")).isEqualTo(noteContent);
        assertThat(note.get("creationTime")).isEqualTo(noteCreationTime);
        assertThat(note.get("modificationTime")).isEqualTo(noteModificationTime);
    }

    @Test
    void shouldGetNotes() throws SQLException, URISyntaxException, IOException, InterruptedException {
        final var noteId = "7a07f782-d2c0-4dc5-9cf2-a984b9ad9690";
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var noteCreationTime = "2010-10-10T10:10:10.100Z";
        final var noteModificationTime = "2011-11-11T11:11:11.111Z";
        createNote(
            UUID.fromString(noteId),
            noteTitle,
            noteContent,
            Instant.parse(noteCreationTime),
            Instant.parse(noteModificationTime)
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(APP_HOST + "/api/notes"))
            .GET()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var responseBody = new ObjectMapper().readValue(
            response.body(),
            new TypeReference<Map<String, Object>>() {
            }
        );
        assertThat(responseBody).hasSize(2).containsKey("pagination");
        final var data = (List<Map<String, Object>>) responseBody.get("data");
        assertThat(data).hasSize(1);
        final var note = data.getFirst();
        assertThat(note.get("id")).isEqualTo(noteId);
        assertThat(note.get("title")).isEqualTo(noteTitle);
        assertThat(note.get("content")).isEqualTo(noteContent);
        assertThat(note.get("creationTime")).isEqualTo(noteCreationTime);
        assertThat(note.get("modificationTime")).isEqualTo(noteModificationTime);
    }

    @Test
    void shouldPutNote() throws SQLException, URISyntaxException, IOException, InterruptedException {
        final var noteId = UUID.fromString("d4630597-d447-4b81-ab7f-839f839a6931");
        final var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        createNote(
            noteId,
            "Note title",
            "Note content",
            noteCreationTime,
            noteModificationTime
        );
        final var noteTitle = "New note title";
        final var noteContent = "New note content";
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(APP_HOST + "/api/notes/" + noteId))
            .headers("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                  "title": "%s",
                  "content": "%s"
                }
                """.formatted(noteTitle, noteContent)
            ))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.body()).isEmpty();
        final var notes = readNotes();
        assertThat(notes).hasSize(1);
        final var note = notes.getFirst();
        assertThat(note.id()).isEqualTo(noteId);
        assertThat(note.title()).isEqualTo(noteTitle);
        assertThat(note.content()).isEqualTo(noteContent);
        assertThat(note.creationTime()).isEqualTo(noteCreationTime);
        assertThat(note.modificationTime()).isAfter(noteModificationTime);
    }

    @Test
    void shouldDeleteNote() throws SQLException, URISyntaxException, IOException, InterruptedException {
        final var noteId = UUID.fromString("946a95dd-8cb5-4d59-ae7e-101ac3ea715b");
        createNote(
            noteId,
            "Note title",
            "Note content",
            Instant.parse("2011-11-11T11:11:11Z"),
            Instant.parse("2011-11-11T11:11:11Z")
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(APP_HOST + "/api/notes/" + noteId))
            .DELETE()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.body()).isEmpty();
        final var notes = readNotes();
        assertThat(notes).hasSize(0);
    }

    private static void createNote(
        final UUID id,
        final String title,
        final String content,
        final Instant creationTime,
        final Instant modificationTime
    ) throws SQLException {
        dao.executeUpdate(
            "INSERT INTO note VALUES (?, ?, ?, ?, ?)",
            id.toString(),
            title,
            content,
            Timestamp.valueOf(LocalDateTime.ofInstant(creationTime, ZoneOffset.UTC)).toString(),
            Timestamp.valueOf(LocalDateTime.ofInstant(modificationTime, ZoneOffset.UTC)).toString()
        );
        dbConnection.commit();
    }

    private static List<Note> readNotes() {
        return dao.executeQuery("SELECT * FROM note", new Function<>() {
            @Override
            @SneakyThrows
            public Note apply(final ResultSet resultSet) {
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
}
