package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.AppContext;
import com.github.krystianmuchla.home.api.GsonHolder;
import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.id.session.SessionId;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.User;
import com.github.krystianmuchla.home.id.user.UserService;
import com.github.krystianmuchla.home.note.removed.RemovedNote;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoteApiControllerTest {
    private static Gson gson;
    private static User user;
    private static User otherUser;
    private static SessionId sessionId;
    private static String cookie;

    @BeforeAll
    static void beforeAllTests() {
        AppContext.init();
        gson = GsonHolder.INSTANCE;
        var login = "note_controller_user";
        user = Transaction.run(() -> UserService.createUser("User name", login, "zaq1@WSX"));
        otherUser = Transaction.run(() -> UserService.createUser("Other user name", "other_user_login", "zaq1@WSX"));
        sessionId = SessionService.createSession(login, user);
        cookie = "login=%s; token=%s".formatted(sessionId.login(), sessionId.token());
    }

    @AfterEach
    void afterEachTest() {
        Transaction.run(() -> {
            Persistence.executeUpdate("DELETE FROM note");
            Persistence.executeUpdate("DELETE FROM removed_note");
        });
    }

    @AfterAll
    static void afterAllTests() {
        Transaction.run(() -> {
            Persistence.executeUpdate("DELETE FROM access_data");
            Persistence.executeUpdate("DELETE FROM user");
        });
        SessionService.removeSession(sessionId);
    }

    @Test
    void shouldPostNote() throws Exception {
        var noteTitle = "Note title";
        var noteContent = "Note content";
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
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

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(1);
        var noteId = gson.fromJson(responseObject.get("id"), UUID.class);
        var notes = Persistence.executeQuery("SELECT * FROM note", Note::fromResultSet);
        assertThat(notes).hasSize(1);
        var note = notes.getFirst();
        assertThat(note.id).isEqualTo(noteId);
        assertThat(note.userId).isEqualTo(user.id());
        assertThat(note.title).isEqualTo(noteTitle);
        assertThat(note.content).isEqualTo(noteContent);
        assertThat(note.creationTime).isNotNull();
        assertThat(note.modificationTime).isNotNull();
        assertThat(note.creationTime).isEqualTo(note.modificationTime);
    }

    @Test
    void shouldNotPostNoteWithoutAuthorization() throws Exception {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{}"))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldGetNote() throws Exception {
        var noteId = UUID.fromString("81f6d5f3-9226-437f-bf5d-3e9eba985eb7");
        var noteTitle = "Note title";
        var noteContent = "Note content";
        var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(() -> {
            NotePersistence.create(
                new Note(
                    noteId,
                    user.id(),
                    noteTitle,
                    noteContent,
                    noteCreationTime,
                    noteModificationTime
                )
            );
            NotePersistence.create(
                new Note(
                    UUID.fromString("32ad70b0-4e30-495a-b5c6-35c45b0243a7"),
                    user.id(),
                    noteTitle,
                    noteContent,
                    noteCreationTime,
                    noteModificationTime
                )
            );
        });
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes?id=" + noteId))
            .header("Cookie", cookie)
            .GET()
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(2);
        var data = responseObject.getAsJsonArray("data");
        assertThat(data.size()).isEqualTo(1);
        var note = gson.fromJson(data.get(0), Map.class);
        assertThat(note.get("id")).isEqualTo(noteId.toString());
        assertThat(note.get("title")).isEqualTo(noteTitle);
        assertThat(note.get("content")).isEqualTo(noteContent);
        assertThat(note.get("creationTime")).isEqualTo(noteCreationTime.toString());
        assertThat(note.get("modificationTime")).isEqualTo(noteModificationTime.toString());
        assertThat(responseObject.get("pagination")).isNotNull();
    }

    @Test
    void shouldGetNotes() throws Exception {
        var noteId = UUID.fromString("7a07f782-d2c0-4dc5-9cf2-a984b9ad9690");
        var noteTitle = "Note title";
        var noteContent = "Note content";
        var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> NotePersistence.create(
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
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Cookie", cookie)
            .GET()
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(2);
        var data = responseObject.getAsJsonArray("data");
        assertThat(data.size()).isEqualTo(1);
        var note = gson.fromJson(data.get(0), Map.class);
        assertThat(note.get("id")).isEqualTo(noteId.toString());
        assertThat(note.get("title")).isEqualTo(noteTitle);
        assertThat(note.get("content")).isEqualTo(noteContent);
        assertThat(note.get("creationTime")).isEqualTo(noteCreationTime.toString());
        assertThat(note.get("modificationTime")).isEqualTo(noteModificationTime.toString());
        assertThat(responseObject.get("pagination")).isNotNull();
    }

    @Test
    void shouldNotGetNotesOfOtherUser() throws Exception {
        var noteId = UUID.fromString("7a07f782-d2c0-4dc5-9cf2-a984b9ad9690");
        var noteTitle = "Note title";
        var noteContent = "Note content";
        var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> NotePersistence.create(
                new Note(
                    noteId,
                    otherUser.id(),
                    noteTitle,
                    noteContent,
                    noteCreationTime,
                    noteModificationTime
                )
            )
        );
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Cookie", cookie)
            .GET()
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(2);
        var data = responseObject.getAsJsonArray("data");
        assertThat(data.size()).isEqualTo(0);
        assertThat(responseObject.get("pagination")).isNotNull();
    }

    @Test
    void shouldNotGetNotesWithoutAuthorization() throws Exception {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .GET()
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldPutNote() throws Exception {
        var noteId = UUID.fromString("d4630597-d447-4b81-ab7f-839f839a6931");
        var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> NotePersistence.create(
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
        var noteTitle = "New note title";
        var noteContent = "New note content";
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                  "id": "%s",
                  "title": "%s",
                  "content": "%s"
                }
                """.formatted(noteId, noteTitle, noteContent)))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.body()).isEmpty();
        var notes = Persistence.executeQuery("SELECT * FROM note", Note::fromResultSet);
        assertThat(notes).hasSize(1);
        var note = notes.getFirst();
        assertThat(note.id).isEqualTo(noteId);
        assertThat(note.title).isEqualTo(noteTitle);
        assertThat(note.content).isEqualTo(noteContent);
        assertThat(note.creationTime).isEqualTo(noteCreationTime);
        assertThat(note.modificationTime).isAfter(noteModificationTime);
    }

    @Test
    void shouldNotPutNoteOfOtherUser() throws Exception {
        var noteId = UUID.fromString("d4630597-d447-4b81-ab7f-839f839a6931");
        var noteCreationTime = Instant.parse("2010-10-10T10:10:10.100Z");
        var noteModificationTime = Instant.parse("2011-11-11T11:11:11.111Z");
        Transaction.run(
            () -> NotePersistence.create(
                new Note(
                    noteId,
                    otherUser.id(),
                    "Note title",
                    "Note content",
                    noteCreationTime,
                    noteModificationTime
                )
            )
        );
        var noteTitle = "New note title";
        var noteContent = "New note content";
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                  "id": "%s",
                  "title": "%s",
                  "content": "%s"
                }
                """.formatted(noteId, noteTitle, noteContent)))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldNotPutNoteWithoutAuthorization() throws Exception {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes"))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString("{}"))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldDeleteNote() throws Exception {
        var noteId = UUID.fromString("946a95dd-8cb5-4d59-ae7e-101ac3ea715b");
        var noteModificationTime = Instant.parse("2011-11-11T11:11:11Z");
        Transaction.run(
            () -> NotePersistence.create(
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
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes?id=" + noteId))
            .header("Cookie", cookie)
            .DELETE()
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.body()).isEmpty();
        var notes = Persistence.executeQuery("SELECT * FROM note", Note::fromResultSet);
        assertThat(notes).hasSize(0);
        var removedNotes = Persistence.executeQuery("SELECT * FROM removed_note", RemovedNote::fromResultSet);
        assertThat(removedNotes).hasSize(1);
        var removedNote = removedNotes.getFirst();
        assertThat(removedNote.id()).isEqualTo(noteId);
        assertThat(removedNote.userId()).isEqualTo(user.id());
        assertThat(removedNote.creationTime()).isAfter(noteModificationTime);
        assertThat(removedNote.modificationTime()).isAfter(noteModificationTime);
    }

    @Test
    void shouldNotDeleteNotOfOtherUser() throws Exception {
        var noteId = UUID.fromString("946a95dd-8cb5-4d59-ae7e-101ac3ea715b");
        var noteModificationTime = Instant.parse("2011-11-11T11:11:11Z");
        Transaction.run(
            () -> NotePersistence.create(
                new Note(
                    noteId,
                    otherUser.id(),
                    "Note title",
                    "Note content",
                    Instant.parse("2011-11-11T11:11:11Z"),
                    noteModificationTime
                )
            )
        );
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes?id=" + noteId))
            .header("Cookie", cookie)
            .DELETE()
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void shouldNotDeleteNoteWithoutAuthorization() throws Exception {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes?id=" + UUID.fromString("80a13f20-27a7-4706-81b1-17c40bd0555f")))
            .DELETE()
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).isEmpty();
    }
}
