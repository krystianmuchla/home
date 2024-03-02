package com.github.krystianmuchla.home.mnemo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.krystianmuchla.home.AppContext;
import com.github.krystianmuchla.home.db.ConnectionManager;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.id.session.SessionManager;
import com.github.krystianmuchla.home.id.user.User;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveDao;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoteControllerTest {
    private static User user;
    private static Cookie[] cookies;
    private static String cookie;
    private static NoteDao noteDao;
    private static NoteGraveDao noteGraveDao;

    @BeforeAll
    static void beforeAllTests() {
        AppContext.init();
        noteDao = NoteDao.INSTANCE;
        noteGraveDao = NoteGraveDao.INSTANCE;
        user = new User(UUID.fromString("ca13a97e-daf6-4d54-801c-a279c6b3ef59"));
        cookies = SessionManager.createSession("test", user);
        cookie = "login=%s; token=%s".formatted(cookies[0].getValue(), cookies[1].getValue());
    }

    @AfterEach
    void afterEachTest() throws Exception {
        Transaction.run(() -> {
            noteDao.delete();
            noteGraveDao.delete();
        });
        ConnectionManager.getConnection().commit();
    }

    @AfterAll
    static void afterAllTests() {
        SessionManager.removeSession(cookies);
    }

    @Test
    void shouldPostNote() throws Exception {
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .POST(HttpRequest.BodyPublishers.ofString("""
                {
                  "title": "%s",
                  "content": "%s"
                }
                """.formatted(noteTitle, noteContent)))
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
        final var notes = noteDao.read();
        assertThat(notes).hasSize(1);
        final var note = notes.get(0);
        assertThat(note.id()).isEqualTo(noteId);
        assertThat(note.userId()).isEqualTo(user.id());
        assertThat(note.title()).isEqualTo(noteTitle);
        assertThat(note.content()).isEqualTo(noteContent);
        assertThat(note.creationTime()).isNotNull();
        assertThat(note.modificationTime()).isNotNull();
        assertThat(note.creationTime()).isEqualTo(note.modificationTime());
    }

    @Test
    void shouldNotPostNoteWithoutAuthorization() throws Exception {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{}"))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldGetNote() throws Exception {
        final var noteId = UUID.fromString("81f6d5f3-9226-437f-bf5d-3e9eba985eb7");
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> noteDao.create(
                new Note(
                    noteId,
                    user.id(),
                    noteTitle,
                    noteContent,
                    noteCreationTime,
                    noteModificationTime
                )
            )
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + noteId))
            .header("Cookie", cookie)
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
        assertThat(note.get("id")).isEqualTo(noteId.toString());
        assertThat(note.get("title")).isEqualTo(noteTitle);
        assertThat(note.get("content")).isEqualTo(noteContent);
        assertThat(note.get("creationTime")).isEqualTo(noteCreationTime.toString());
        assertThat(note.get("modificationTime")).isEqualTo(noteModificationTime.toString());
    }

    @Test
    void shouldNotGetNoteOfOtherUser() throws Exception {
        final var noteId = UUID.fromString("81f6d5f3-9226-437f-bf5d-3e9eba985eb7");
        final var userId = UUID.fromString("12ed4828-69a3-4094-a173-1e087326eee5");
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> noteDao.create(
                new Note(
                    noteId,
                    userId,
                    noteTitle,
                    noteContent,
                    noteCreationTime,
                    noteModificationTime
                )
            )
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + noteId))
            .header("Cookie", cookie)
            .GET()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isGreaterThan(299);
    }

    @Test
    void shouldNotGetNoteWithoutAuthorization() throws Exception {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + UUID.fromString("17e3721e-d7b0-43f2-9f84-b5b7299525bb")))
            .GET()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldGetNotes() throws Exception {
        final var noteId = UUID.fromString("7a07f782-d2c0-4dc5-9cf2-a984b9ad9690");
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> noteDao.create(
                new Note(
                    noteId,
                    user.id(),
                    noteTitle,
                    noteContent,
                    noteCreationTime,
                    noteModificationTime
                )
            )
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Cookie", cookie)
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
        assertThat(note.get("id")).isEqualTo(noteId.toString());
        assertThat(note.get("title")).isEqualTo(noteTitle);
        assertThat(note.get("content")).isEqualTo(noteContent);
        assertThat(note.get("creationTime")).isEqualTo(noteCreationTime.toString());
        assertThat(note.get("modificationTime")).isEqualTo(noteModificationTime.toString());
    }

    @Test
    void shouldNotGetNotesOfOtherUser() throws Exception {
        final var noteId = UUID.fromString("7a07f782-d2c0-4dc5-9cf2-a984b9ad9690");
        final var userId = UUID.fromString("0fdbbc6b-a71f-44d4-b8b1-2843cdb493b7");
        final var noteTitle = "Note title";
        final var noteContent = "Note content";
        final var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> noteDao.create(
                new Note(
                    noteId,
                    userId,
                    noteTitle,
                    noteContent,
                    noteCreationTime,
                    noteModificationTime
                )
            )
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Cookie", cookie)
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
        assertThat(data).hasSize(0);
    }

    @Test
    void shouldNotGetNotesWithoutAuthorization() throws Exception {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .GET()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldPutNote() throws Exception {
        final var noteId = UUID.fromString("d4630597-d447-4b81-ab7f-839f839a6931");
        final var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> noteDao.create(
                new Note(
                    noteId,
                    user.id(),
                    "Note title",
                    "Note content",
                    noteCreationTime,
                    noteModificationTime
                )
            )
        );
        final var noteTitle = "New note title";
        final var noteContent = "New note content";
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + noteId))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                  "title": "%s",
                  "content": "%s"
                }
                """.formatted(noteTitle, noteContent)))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.body()).isEmpty();
        final var notes = noteDao.read();
        assertThat(notes).hasSize(1);
        final var note = notes.getFirst();
        assertThat(note.id()).isEqualTo(noteId);
        assertThat(note.title()).isEqualTo(noteTitle);
        assertThat(note.content()).isEqualTo(noteContent);
        assertThat(note.creationTime()).isEqualTo(noteCreationTime);
        assertThat(note.modificationTime()).isAfter(noteModificationTime);
    }

    @Test
    void shouldNotPutNoteOfOtherUser() throws Exception {
        final var noteId = UUID.fromString("d4630597-d447-4b81-ab7f-839f839a6931");
        final var userId = UUID.fromString("20f42fc1-e523-4173-b51f-acc6806fffbd");
        final var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> noteDao.create(
                new Note(
                    noteId,
                    userId,
                    "Note title",
                    "Note content",
                    noteCreationTime,
                    noteModificationTime
                )
            )
        );
        final var noteTitle = "New note title";
        final var noteContent = "New note content";
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + noteId))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                  "title": "%s",
                  "content": "%s"
                }
                """.formatted(noteTitle, noteContent)))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isGreaterThan(299);
    }

    @Test
    void shouldNotPutNoteWithoutAuthorization() throws Exception {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + UUID.fromString("c914474a-6c4c-41c4-99cb-8e1b494878a0")))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString("{}"))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldDeleteNote() throws Exception {
        final var noteId = UUID.fromString("946a95dd-8cb5-4d59-ae7e-101ac3ea715b");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11Z");
        Transaction.run(
            () -> noteDao.create(
                new Note(
                    noteId,
                    user.id(),
                    "Note title",
                    "Note content",
                    Instant.parse("2011-11-11T11:11:11Z"),
                    noteModificationTime
                )
            )
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + noteId))
            .header("Cookie", cookie)
            .DELETE()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.body()).isEmpty();
        final var notes = noteDao.read();
        assertThat(notes).hasSize(0);
        final var noteGraves = noteGraveDao.read();
        assertThat(noteGraves).hasSize(1);
        final var noteGrave = noteGraves.get(0);
        assertThat(noteGrave.id()).isEqualTo(noteId);
        assertThat(noteGrave.userId()).isEqualTo(user.id());
        assertThat(noteGrave.creationTime()).isAfter(noteModificationTime);
    }

    @Test
    void shouldNotDeleteNotOfOtherUser() throws Exception {
        final var noteId = UUID.fromString("946a95dd-8cb5-4d59-ae7e-101ac3ea715b");
        final var userId = UUID.fromString("36cfee27-1b49-4540-965a-533044f7dbfc");
        final var noteModificationTime = Instant.parse("2011-11-11T11:11:11Z");
        Transaction.run(
            () -> noteDao.create(
                new Note(
                    noteId,
                    userId,
                    "Note title",
                    "Note content",
                    Instant.parse("2011-11-11T11:11:11Z"),
                    noteModificationTime
                )
            )
        );
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + noteId))
            .header("Cookie", cookie)
            .DELETE()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isGreaterThan(299);
    }

    @Test
    void shouldNotDeleteNoteWithoutAuthorization() throws Exception {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/" + UUID.fromString("80a13f20-27a7-4706-81b1-17c40bd0555f")))
            .DELETE()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }
}
