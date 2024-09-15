package com.github.krystianmuchla.home.note.sync;

import com.github.krystianmuchla.home.AppContext;
import com.github.krystianmuchla.home.api.GsonHolder;
import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.id.session.SessionId;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.User;
import com.github.krystianmuchla.home.id.user.UserPersistence;
import com.github.krystianmuchla.home.id.user.UserService;
import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.note.NotePersistence;
import com.github.krystianmuchla.home.note.removed.RemovedNote;
import com.github.krystianmuchla.home.note.removed.RemovedNotePersistence;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoteSyncApiControllerTest {
    private static Gson gson;
    private static User user;
    private static User otherUser;
    private static SessionId sessionId;
    private static String cookie;

    @BeforeAll
    static void beforeAllTests() {
        AppContext.init();
        gson = GsonHolder.INSTANCE;
        var login = "note_sync_controller_user";
        var userId = Transaction.run(() -> UserService.create("User name", login, "zaq1@WSX"));
        user = UserPersistence.read(userId);
        var otherUserId = Transaction.run(() -> UserService.create("Other user name", "other_user_login", "zaq1@WSX"));
        otherUser = UserPersistence.read(otherUserId);
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
    void shouldWriteExternalNotes() throws Exception {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                    "notes": [
                        {
                            "id": "4d8af443-bfa9-4d47-a886-b1ddc82a958d",
                            "title": "External note title",
                            "content": "External note content",
                            "contentsModificationTime": "2010-10-10T10:10:10Z"
                        },
                        {
                            "id": "cb8b51f8-63e5-4964-94e4-0b3b7944e7d4",
                            "title": "External note title",
                            "content": null,
                            "contentsModificationTime": "2010-10-10T10:10:10Z"
                        }
                    ]
                }
                """))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(1);
        var notesResponse = gson.fromJson(responseObject.get("notes"), NoteResponse[].class);
        assertThat(notesResponse).hasSize(0);
        var notesDb = Persistence.executeQuery("SELECT * FROM note", Note::fromResultSet);
        assertThat(notesDb).hasSize(1);
        var noteDb = notesDb.getFirst();
        assertThat(noteDb.id).isEqualTo(UUID.fromString("4d8af443-bfa9-4d47-a886-b1ddc82a958d"));
        assertThat(noteDb.userId).isEqualTo(user.id);
        assertThat(noteDb.title).isEqualTo("External note title");
        assertThat(noteDb.content).isEqualTo("External note content");
        assertThat(noteDb.contentsModificationTime).isEqualTo("2010-10-10T10:10:10Z");
        assertThat(noteDb.creationTime).isNotNull();
        assertThat(noteDb.modificationTime).isNotNull();
        assertThat(noteDb.version).isEqualTo(1);
        var removedNotesDb = Persistence.executeQuery("SELECT * FROM removed_note", RemovedNote::fromResultSet);
        assertThat(removedNotesDb).hasSize(1);
        var removedNoteDb = removedNotesDb.getFirst();
        assertThat(removedNoteDb.id).isEqualTo(UUID.fromString("cb8b51f8-63e5-4964-94e4-0b3b7944e7d4"));
        assertThat(removedNoteDb.userId).isEqualTo(user.id);
        assertThat(removedNoteDb.removalTime).isEqualTo("2010-10-10T10:10:10Z");
        assertThat(removedNoteDb.creationTime).isNotNull();
        assertThat(removedNoteDb.modificationTime).isNotNull();
        assertThat(removedNoteDb.version).isEqualTo(1);
    }

    @Test
    void shouldOverwriteInternalNotes() throws Exception {
        Transaction.run(() -> {
            NotePersistence.create(
                new Note(
                    UUID.fromString("6765b952-1db7-40ae-938c-51b49cac69ed"),
                    user.id,
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                    UUID.fromString("3b03610e-50c3-4602-aca4-280841a72496"),
                    user.id,
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
            RemovedNotePersistence.create(
                new RemovedNote(
                    UUID.fromString("292e3117-59f1-4374-afe8-d8b751e0b6e3"),
                    user.id,
                    Instant.parse("2010-10-10T10:10:10Z")
                ),
                new RemovedNote(
                    UUID.fromString("65b276f5-417d-458b-ad2c-0c6ffa7f5488"),
                    user.id,
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
        });
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                    "notes": [
                        {
                            "id": "6765b952-1db7-40ae-938c-51b49cac69ed",
                            "title": "External note title",
                            "content": "External note content",
                            "contentsModificationTime": "2011-11-11T11:11:11Z"
                        },
                        {
                            "id": "3b03610e-50c3-4602-aca4-280841a72496",
                            "title": "External note title",
                            "content": null,
                            "contentsModificationTime": "2011-11-11T11:11:11Z"
                        },
                        {
                            "id": "292e3117-59f1-4374-afe8-d8b751e0b6e3",
                            "title": "External note title",
                            "content": null,
                            "contentsModificationTime": "2011-11-11T11:11:11Z"
                        },
                        {
                            "id": "65b276f5-417d-458b-ad2c-0c6ffa7f5488",
                            "title": "External note title",
                            "content": "External note content",
                            "contentsModificationTime": "2011-11-11T11:11:11Z"
                        }
                    ]
                }
                """))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(1);
        var notesResponse = gson.fromJson(responseObject.get("notes"), NoteResponse[].class);
        assertThat(notesResponse).hasSize(0);
        var notesDb = Persistence.executeQuery("SELECT * FROM note", Note::fromResultSet);
        assertThat(notesDb).hasSize(2);
        assertThat(notesDb.get(0).id).isEqualTo(UUID.fromString("6765b952-1db7-40ae-938c-51b49cac69ed"));
        assertThat(notesDb.get(0).userId).isEqualTo(user.id);
        assertThat(notesDb.get(0).title).isEqualTo("External note title");
        assertThat(notesDb.get(0).content).isEqualTo("External note content");
        assertThat(notesDb.get(0).contentsModificationTime).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesDb.get(0).creationTime).isNotNull();
        assertThat(notesDb.get(0).modificationTime).isNotNull();
        assertThat(notesDb.get(0).version).isEqualTo(2);
        assertThat(notesDb.get(1).id).isEqualTo(UUID.fromString("65b276f5-417d-458b-ad2c-0c6ffa7f5488"));
        assertThat(notesDb.get(1).userId).isEqualTo(user.id);
        assertThat(notesDb.get(1).title).isEqualTo("External note title");
        assertThat(notesDb.get(1).content).isEqualTo("External note content");
        assertThat(notesDb.get(1).contentsModificationTime).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesDb.get(1).creationTime).isNotNull();
        assertThat(notesDb.get(1).modificationTime).isNotNull();
        assertThat(notesDb.get(1).version).isEqualTo(1);
        var removedNotesDb = Persistence.executeQuery("SELECT * FROM removed_note", RemovedNote::fromResultSet);
        assertThat(removedNotesDb).hasSize(2);
        assertThat(removedNotesDb.get(0).id).isEqualTo(UUID.fromString("292e3117-59f1-4374-afe8-d8b751e0b6e3"));
        assertThat(removedNotesDb.get(0).userId).isEqualTo(user.id);
        assertThat(removedNotesDb.get(0).removalTime).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(removedNotesDb.get(0).creationTime).isNotNull();
        assertThat(removedNotesDb.get(0).modificationTime).isNotNull();
        assertThat(removedNotesDb.get(0).version).isEqualTo(2);
        assertThat(removedNotesDb.get(1).id).isEqualTo(UUID.fromString("3b03610e-50c3-4602-aca4-280841a72496"));
        assertThat(removedNotesDb.get(1).userId).isEqualTo(user.id);
        assertThat(removedNotesDb.get(1).removalTime).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(removedNotesDb.get(1).creationTime).isNotNull();
        assertThat(removedNotesDb.get(1).modificationTime).isNotNull();
        assertThat(removedNotesDb.get(1).version).isEqualTo(1);
    }

    @Test
    void shouldOutputInternalNotes() throws Exception {
        Transaction.run(() -> {
            NotePersistence.create(
                new Note(
                    UUID.fromString("416b5888-4ee0-4460-8c4e-0531e62c029c"),
                    user.id,
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
            RemovedNotePersistence.create(
                new RemovedNote(
                    UUID.fromString("884f33f5-2b79-4f68-9118-73cabffc4f8a"),
                    user.id,
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
        });
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                    "notes": []
                }
                """))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(1);
        var notesResponse = gson.fromJson(responseObject.get("notes"), NoteResponse[].class);
        assertThat(notesResponse).hasSize(2);
        assertThat(notesResponse[0].id()).isEqualTo(UUID.fromString("416b5888-4ee0-4460-8c4e-0531e62c029c"));
        assertThat(notesResponse[0].title()).isEqualTo("Note title");
        assertThat(notesResponse[0].content()).isEqualTo("Note content");
        assertThat(notesResponse[0].contentsModificationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(notesResponse[1].id()).isEqualTo(UUID.fromString("884f33f5-2b79-4f68-9118-73cabffc4f8a"));
        assertThat(notesResponse[1].title()).isNull();
        assertThat(notesResponse[1].content()).isNull();
        assertThat(notesResponse[1].contentsModificationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        var notesDb = Persistence.executeQuery("SELECT * FROM note", Note::fromResultSet);
        assertThat(notesDb).hasSize(1);
        var noteDb = notesDb.getFirst();
        assertThat(noteDb.id).isEqualTo(UUID.fromString("416b5888-4ee0-4460-8c4e-0531e62c029c"));
        assertThat(noteDb.userId).isEqualTo(user.id);
        assertThat(noteDb.title).isEqualTo("Note title");
        assertThat(noteDb.content).isEqualTo("Note content");
        assertThat(noteDb.contentsModificationTime).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(noteDb.creationTime).isNotNull();
        assertThat(noteDb.modificationTime).isNotNull();
        assertThat(noteDb.version).isEqualTo(1);
        var removedNotesDb = Persistence.executeQuery("SELECT * FROM removed_note", RemovedNote::fromResultSet);
        assertThat(removedNotesDb).hasSize(1);
        var removedNoteDb = removedNotesDb.getFirst();
        assertThat(removedNoteDb.id).isEqualTo(UUID.fromString("884f33f5-2b79-4f68-9118-73cabffc4f8a"));
        assertThat(removedNoteDb.userId).isEqualTo(user.id);
        assertThat(removedNoteDb.removalTime).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(removedNoteDb.creationTime).isNotNull();
        assertThat(removedNoteDb.modificationTime).isNotNull();
        assertThat(removedNoteDb.version).isEqualTo(1);
    }

    @Test
    void shouldNotOutputInternalNotesOfOtherUser() throws Exception {
        Transaction.run(() -> {
            NotePersistence.create(
                new Note(
                    UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"),
                    otherUser.id,
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
            RemovedNotePersistence.create(
                new RemovedNote(
                    UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"),
                    otherUser.id,
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
        });
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                    "notes": []
                }
                """))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(1);
        var notesResponse = gson.fromJson(responseObject.get("notes"), NoteResponse[].class);
        assertThat(notesResponse).hasSize(0);
        var notesDb = Persistence.executeQuery("SELECT * FROM note", Note::fromResultSet);
        assertThat(notesDb).hasSize(1);
        var noteDb = notesDb.getFirst();
        assertThat(noteDb.id).isEqualTo(UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"));
        assertThat(noteDb.userId).isEqualTo(otherUser.id);
        assertThat(noteDb.title).isEqualTo("Note title");
        assertThat(noteDb.content).isEqualTo("Note content");
        assertThat(noteDb.contentsModificationTime).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(noteDb.creationTime).isNotNull();
        assertThat(noteDb.modificationTime).isNotNull();
        assertThat(noteDb.version).isEqualTo(1);
        var removedNotesDb = Persistence.executeQuery("SELECT * FROM removed_note", RemovedNote::fromResultSet);
        assertThat(removedNotesDb).hasSize(1);
        var removedNoteDb = removedNotesDb.getFirst();
        assertThat(removedNoteDb.id).isEqualTo(UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"));
        assertThat(removedNoteDb.userId).isEqualTo(otherUser.id);
        assertThat(removedNoteDb.removalTime).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(removedNoteDb.creationTime).isNotNull();
        assertThat(removedNoteDb.modificationTime).isNotNull();
        assertThat(removedNoteDb.version).isEqualTo(1);
    }

    @Test
    void shouldOverwriteExternalNotes() throws Exception {
        Transaction.run(() -> {
            NotePersistence.create(
                new Note(
                    UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"),
                    user.id,
                    "Note title",
                    "Note content",
                    Instant.parse("2011-11-11T11:11:11Z")
                ),
                new Note(
                    UUID.fromString("0c73c9f7-4af4-4fff-8b30-384636d12a00"),
                    user.id,
                    "Note title",
                    "Note content",
                    Instant.parse("2011-11-11T11:11:11Z")
                )
            );
            RemovedNotePersistence.create(
                new RemovedNote(
                    UUID.fromString("6d9e6e4d-be5e-4768-bd33-bc37f7b80284"),
                    user.id,
                    Instant.parse("2011-11-11T11:11:11Z")
                ),
                new RemovedNote(
                    UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"),
                    user.id,
                    Instant.parse("2011-11-11T11:11:11Z")
                )
            );
        });
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                    "notes": [
                        {
                            "id": "8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf",
                            "title": "External note title",
                            "content": "External note content",
                            "contentsModificationTime": "2010-10-10T10:10:10Z"
                        },
                        {
                            "id": "0c73c9f7-4af4-4fff-8b30-384636d12a00",
                            "title": "External note title",
                            "content": null,
                            "contentsModificationTime": "2010-10-10T10:10:10Z"
                        },
                        {
                            "id": "6d9e6e4d-be5e-4768-bd33-bc37f7b80284",
                            "title": "External note title",
                            "content": null,
                            "contentsModificationTime": "2010-10-10T10:10:10Z"
                        },
                        {
                            "id": "dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd",
                            "title": "External note title",
                            "content": "External note content",
                            "contentsModificationTime": "2010-10-10T10:10:10Z"
                        }
                    ]
                }
                """))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        var responseObject = gson.fromJson(response.body(), JsonObject.class);
        assertThat(responseObject.size()).isEqualTo(1);
        var notesResponse = gson.fromJson(responseObject.get("notes"), NoteResponse[].class);
        assertThat(notesResponse).hasSize(4);
        assertThat(notesResponse[0].id()).isEqualTo(UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"));
        assertThat(notesResponse[0].title()).isEqualTo("Note title");
        assertThat(notesResponse[0].content()).isEqualTo("Note content");
        assertThat(notesResponse[0].contentsModificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesResponse[1].id()).isEqualTo(UUID.fromString("0c73c9f7-4af4-4fff-8b30-384636d12a00"));
        assertThat(notesResponse[1].title()).isEqualTo("Note title");
        assertThat(notesResponse[1].content()).isEqualTo("Note content");
        assertThat(notesResponse[1].contentsModificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesResponse[2].id()).isEqualTo(UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"));
        assertThat(notesResponse[2].title()).isNull();
        assertThat(notesResponse[2].content()).isNull();
        assertThat(notesResponse[2].contentsModificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesResponse[3].id()).isEqualTo(UUID.fromString("6d9e6e4d-be5e-4768-bd33-bc37f7b80284"));
        assertThat(notesResponse[3].title()).isNull();
        assertThat(notesResponse[3].content()).isNull();
        assertThat(notesResponse[3].contentsModificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        var notesDb = Persistence.executeQuery("SELECT * FROM note", Note::fromResultSet);
        assertThat(notesDb).hasSize(2);
        assertThat(notesDb.get(0).id).isEqualTo(UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"));
        assertThat(notesDb.get(0).userId).isEqualTo(user.id);
        assertThat(notesDb.get(0).title).isEqualTo("Note title");
        assertThat(notesDb.get(0).content).isEqualTo("Note content");
        assertThat(notesDb.get(0).contentsModificationTime).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesDb.get(0).creationTime).isNotNull();
        assertThat(notesDb.get(0).modificationTime).isNotNull();
        assertThat(notesDb.get(0).version).isEqualTo(1);
        assertThat(notesDb.get(1).id).isEqualTo(UUID.fromString("0c73c9f7-4af4-4fff-8b30-384636d12a00"));
        assertThat(notesDb.get(1).userId).isEqualTo(user.id);
        assertThat(notesDb.get(1).title).isEqualTo("Note title");
        assertThat(notesDb.get(1).content).isEqualTo("Note content");
        assertThat(notesDb.get(1).contentsModificationTime).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesDb.get(1).creationTime).isNotNull();
        assertThat(notesDb.get(1).modificationTime).isNotNull();
        assertThat(notesDb.get(1).version).isEqualTo(1);
        var removedNotesDb = Persistence.executeQuery("SELECT * FROM removed_note", RemovedNote::fromResultSet);
        assertThat(removedNotesDb).hasSize(2);
        assertThat(removedNotesDb.get(0).id).isEqualTo(UUID.fromString("6d9e6e4d-be5e-4768-bd33-bc37f7b80284"));
        assertThat(removedNotesDb.get(0).userId).isEqualTo(user.id);
        assertThat(removedNotesDb.get(0).removalTime).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(removedNotesDb.get(0).creationTime).isNotNull();
        assertThat(removedNotesDb.get(0).modificationTime).isNotNull();
        assertThat(removedNotesDb.get(0).version).isEqualTo(1);
        assertThat(removedNotesDb.get(1).id).isEqualTo(UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"));
        assertThat(removedNotesDb.get(1).userId).isEqualTo(user.id);
        assertThat(removedNotesDb.get(1).removalTime).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(removedNotesDb.get(1).creationTime).isNotNull();
        assertThat(removedNotesDb.get(1).modificationTime).isNotNull();
        assertThat(removedNotesDb.get(1).version).isEqualTo(1);
    }

    @Test
    void shouldNotSyncWithoutAuthorization() throws Exception {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString("{}"))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(401);
    }
}
