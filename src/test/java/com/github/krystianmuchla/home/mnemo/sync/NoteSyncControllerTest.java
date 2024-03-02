package com.github.krystianmuchla.home.mnemo.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.krystianmuchla.home.AppContext;
import com.github.krystianmuchla.home.api.ObjectMapperHolder;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.id.session.SessionManager;
import com.github.krystianmuchla.home.id.user.User;
import com.github.krystianmuchla.home.mnemo.Note;
import com.github.krystianmuchla.home.mnemo.NoteDao;
import com.github.krystianmuchla.home.mnemo.NoteResponse;
import com.github.krystianmuchla.home.mnemo.grave.NoteGrave;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoteSyncControllerTest {
    private static ObjectMapper objectMapper;
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
        objectMapper = ObjectMapperHolder.INSTANCE;
        user = new User(UUID.fromString("5b55e2c9-1fb4-4128-a5a4-9c1597fdfe19"));
        cookies = SessionManager.createSession("test", user);
        cookie = "login=%s; token=%s".formatted(cookies[0].getValue(), cookies[1].getValue());
    }

    @AfterEach
    void afterEachTest() {
        Transaction.run(() -> {
            noteDao.delete();
            noteGraveDao.delete();
        });
    }

    @AfterAll
    static void afterAllTests() {
        SessionManager.removeSession(cookies);
    }

    @Test
    void shouldWriteExternalNotes() throws Exception {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
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
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2010-10-10T10:10:10Z"
                        },
                        {
                            "id": "cb8b51f8-63e5-4964-94e4-0b3b7944e7d4",
                            "title": "External note title",
                            "content": null,
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2010-10-10T10:10:10Z"
                        }
                    ]
                }
                """))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var responseTree = objectMapper.readTree(response.body());
        assertThat(responseTree.size()).isEqualTo(1);
        final var notesResponse = objectMapper.readValue(
            responseTree.get("notes").toString(),
            new TypeReference<List<NoteResponse>>() {
            }
        );
        assertThat(notesResponse).hasSize(0);
        final var notesDb = noteDao.read();
        assertThat(notesDb).hasSize(1);
        final var noteDb = notesDb.get(0);
        assertThat(noteDb.id()).isEqualTo(UUID.fromString("4d8af443-bfa9-4d47-a886-b1ddc82a958d"));
        assertThat(noteDb.userId()).isEqualTo(user.id());
        assertThat(noteDb.title()).isEqualTo("External note title");
        assertThat(noteDb.content()).isEqualTo("External note content");
        assertThat(noteDb.creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(noteDb.modificationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        final var noteGravesDb = noteGraveDao.read();
        assertThat(noteGravesDb).hasSize(0);
    }

    @Test
    void shouldOverwriteInternalNotes() throws Exception {
        Transaction.run(() -> {
            noteDao.create(
                new Note(
                    UUID.fromString("6765b952-1db7-40ae-938c-51b49cac69ed"),
                    user.id(),
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z")
                ),
                new Note(
                    UUID.fromString("3b03610e-50c3-4602-aca4-280841a72496"),
                    user.id(),
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
            noteGraveDao.create(
                new NoteGrave(
                    UUID.fromString("292e3117-59f1-4374-afe8-d8b751e0b6e3"),
                    user.id(),
                    Instant.parse("2010-10-10T10:10:10Z")
                ),
                new NoteGrave(
                    UUID.fromString("65b276f5-417d-458b-ad2c-0c6ffa7f5488"),
                    user.id(),
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
        });
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
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
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2011-11-11T11:11:11Z"
                        },
                        {
                            "id": "3b03610e-50c3-4602-aca4-280841a72496",
                            "title": "External note title",
                            "content": null,
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2011-11-11T11:11:11Z"
                        },
                        {
                            "id": "292e3117-59f1-4374-afe8-d8b751e0b6e3",
                            "title": "External note title",
                            "content": null,
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2011-11-11T11:11:11Z"
                        },
                        {
                            "id": "65b276f5-417d-458b-ad2c-0c6ffa7f5488",
                            "title": "External note title",
                            "content": "External note content",
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2011-11-11T11:11:11Z"
                        }
                    ]
                }
                """))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var responseTree = objectMapper.readTree(response.body());
        assertThat(responseTree.size()).isEqualTo(1);
        final var notesResponse = objectMapper.readValue(
            responseTree.get("notes").toString(),
            new TypeReference<List<NoteResponse>>() {
            }
        );
        assertThat(notesResponse).hasSize(0);
        final var notesDb = noteDao.read();
        assertThat(notesDb).hasSize(2);
        assertThat(notesDb.get(0).id()).isEqualTo(UUID.fromString("65b276f5-417d-458b-ad2c-0c6ffa7f5488"));
        assertThat(notesDb.get(0).userId()).isEqualTo(user.id());
        assertThat(notesDb.get(0).title()).isEqualTo("External note title");
        assertThat(notesDb.get(0).content()).isEqualTo("External note content");
        assertThat(notesDb.get(0).creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(notesDb.get(0).modificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesDb.get(1).id()).isEqualTo(UUID.fromString("6765b952-1db7-40ae-938c-51b49cac69ed"));
        assertThat(notesDb.get(1).userId()).isEqualTo(user.id());
        assertThat(notesDb.get(1).title()).isEqualTo("External note title");
        assertThat(notesDb.get(1).content()).isEqualTo("External note content");
        assertThat(notesDb.get(1).creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(notesDb.get(1).modificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        final var noteGravesDb = noteGraveDao.read();
        assertThat(noteGravesDb).hasSize(2);
        assertThat(noteGravesDb.get(0).id()).isEqualTo(UUID.fromString("292e3117-59f1-4374-afe8-d8b751e0b6e3"));
        assertThat(noteGravesDb.get(0).userId()).isEqualTo(user.id());
        assertThat(noteGravesDb.get(0).creationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(noteGravesDb.get(1).id()).isEqualTo(UUID.fromString("3b03610e-50c3-4602-aca4-280841a72496"));
        assertThat(noteGravesDb.get(1).userId()).isEqualTo(user.id());
        assertThat(noteGravesDb.get(1).creationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
    }

    @Test
    void shouldOutputInternalNotes() throws Exception {
        Transaction.run(() -> {
            noteDao.create(
                new Note(
                    UUID.fromString("416b5888-4ee0-4460-8c4e-0531e62c029c"),
                    user.id(),
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
            noteGraveDao.create(
                new NoteGrave(
                    UUID.fromString("884f33f5-2b79-4f68-9118-73cabffc4f8a"),
                    user.id(),
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
        });
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                    "notes": []
                }
                """))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var responseTree = objectMapper.readTree(response.body());
        assertThat(responseTree.size()).isEqualTo(1);
        final var notesResponse = objectMapper.readValue(
            responseTree.get("notes").toString(),
            new TypeReference<List<NoteResponse>>() {
            }
        );
        assertThat(notesResponse).hasSize(1);
        final var noteResponse = notesResponse.get(0);
        assertThat(noteResponse.id()).isEqualTo(UUID.fromString("416b5888-4ee0-4460-8c4e-0531e62c029c"));
        assertThat(noteResponse.title()).isEqualTo("Note title");
        assertThat(noteResponse.content()).isEqualTo("Note content");
        assertThat(noteResponse.creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(noteResponse.modificationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        final var notesDb = noteDao.read();
        assertThat(notesDb).hasSize(1);
        final var noteDb = notesDb.get(0);
        assertThat(noteDb.id()).isEqualTo(UUID.fromString("416b5888-4ee0-4460-8c4e-0531e62c029c"));
        assertThat(noteDb.userId()).isEqualTo(user.id());
        assertThat(noteDb.title()).isEqualTo("Note title");
        assertThat(noteDb.content()).isEqualTo("Note content");
        assertThat(noteDb.creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(noteDb.modificationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        final var noteGravesDb = noteGraveDao.read();
        assertThat(noteGravesDb).hasSize(1);
        final var noteGraveDb = noteGravesDb.get(0);
        assertThat(noteGraveDb.id()).isEqualTo(UUID.fromString("884f33f5-2b79-4f68-9118-73cabffc4f8a"));
        assertThat(noteGraveDb.userId()).isEqualTo(user.id());
        assertThat(noteGraveDb.creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
    }

    @Test
    void shouldNotOutputInternalNotesOfOtherUser() throws Exception {
        final UUID userId = UUID.fromString("2ad341c6-e59e-42f3-94d8-8c089addc9a0");
        Transaction.run(() -> {
            noteDao.create(
                new Note(
                    UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"),
                    userId,
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
            noteGraveDao.create(
                new NoteGrave(
                    UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"),
                    userId,
                    Instant.parse("2010-10-10T10:10:10Z")
                )
            );
        });
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookie)
            .PUT(HttpRequest.BodyPublishers.ofString("""
                {
                    "notes": []
                }
                """))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var responseTree = objectMapper.readTree(response.body());
        assertThat(responseTree.size()).isEqualTo(1);
        final var notesResponse = objectMapper.readValue(
            responseTree.get("notes").toString(),
            new TypeReference<List<NoteResponse>>() {
            }
        );
        assertThat(notesResponse).hasSize(0);
        final var notesDb = noteDao.read();
        assertThat(notesDb).hasSize(1);
        final var noteDb = notesDb.get(0);
        assertThat(noteDb.id()).isEqualTo(UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"));
        assertThat(noteDb.userId()).isEqualTo(userId);
        assertThat(noteDb.title()).isEqualTo("Note title");
        assertThat(noteDb.content()).isEqualTo("Note content");
        assertThat(noteDb.creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(noteDb.modificationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        final var noteGravesDb = noteGraveDao.read();
        assertThat(noteGravesDb).hasSize(1);
        final var noteGraveDb = noteGravesDb.get(0);
        assertThat(noteGraveDb.id()).isEqualTo(UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"));
        assertThat(noteGraveDb.userId()).isEqualTo(userId);
        assertThat(noteGraveDb.creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
    }

    @Test
    void shouldOverwriteExternalNotes() throws Exception {
        Transaction.run(() -> {
            noteDao.create(
                new Note(
                    UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"),
                    user.id(),
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z"),
                    Instant.parse("2011-11-11T11:11:11Z")
                ),
                new Note(
                    UUID.fromString("0c73c9f7-4af4-4fff-8b30-384636d12a00"),
                    user.id(),
                    "Note title",
                    "Note content",
                    Instant.parse("2010-10-10T10:10:10Z"),
                    Instant.parse("2011-11-11T11:11:11Z")
                )
            );
            noteGraveDao.create(
                new NoteGrave(
                    UUID.fromString("6d9e6e4d-be5e-4768-bd33-bc37f7b80284"),
                    user.id(),
                    Instant.parse("2011-11-11T11:11:11Z")
                ),
                new NoteGrave(
                    UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"),
                    user.id(),
                    Instant.parse("2011-11-11T11:11:11Z")
                )
            );
        });
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
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
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2010-10-10T10:10:10Z"
                        },
                        {
                            "id": "0c73c9f7-4af4-4fff-8b30-384636d12a00",
                            "title": "External note title",
                            "content": null,
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2010-10-10T10:10:10Z"
                        },
                        {
                            "id": "6d9e6e4d-be5e-4768-bd33-bc37f7b80284",
                            "title": "External note title",
                            "content": null,
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2010-10-10T10:10:10Z"
                        },
                        {
                            "id": "dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd",
                            "title": "External note title",
                            "content": "External note content",
                            "creationTime": "2010-10-10T10:10:10Z",
                            "modificationTime": "2010-10-10T10:10:10Z"
                        }
                    ]
                }
                """))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        final var responseTree = objectMapper.readTree(response.body());
        assertThat(responseTree.size()).isEqualTo(1);
        final var notesResponse = objectMapper.readValue(
            responseTree.get("notes").toString(),
            new TypeReference<List<NoteResponse>>() {
            }
        );
        assertThat(notesResponse).hasSize(3);
        assertThat(notesResponse.get(0).id())
            .isEqualTo(UUID.fromString("0c73c9f7-4af4-4fff-8b30-384636d12a00"));
        assertThat(notesResponse.get(0).title()).isEqualTo("Note title");
        assertThat(notesResponse.get(0).content()).isEqualTo("Note content");
        assertThat(notesResponse.get(0).creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(notesResponse.get(0).modificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesResponse.get(1).id())
            .isEqualTo(UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"));
        assertThat(notesResponse.get(1).title()).isEqualTo("Note title");
        assertThat(notesResponse.get(1).content()).isEqualTo("Note content");
        assertThat(notesResponse.get(1).creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(notesResponse.get(1).modificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesResponse.get(2).id())
            .isEqualTo(UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"));
        assertThat(notesResponse.get(2).title()).isNull();
        assertThat(notesResponse.get(2).content()).isNull();
        assertThat(notesResponse.get(2).creationTime()).isNull();
        assertThat(notesResponse.get(2).modificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        final var notesDb = noteDao.read();
        assertThat(notesDb).hasSize(2);
        assertThat(notesDb.get(0).id()).isEqualTo(UUID.fromString("0c73c9f7-4af4-4fff-8b30-384636d12a00"));
        assertThat(notesDb.get(0).userId()).isEqualTo(user.id());
        assertThat(notesDb.get(0).title()).isEqualTo("Note title");
        assertThat(notesDb.get(0).content()).isEqualTo("Note content");
        assertThat(notesDb.get(0).creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(notesDb.get(0).modificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(notesDb.get(1).id()).isEqualTo(UUID.fromString("8b4ae3f2-02b9-47e2-b1d1-fbb761e2dccf"));
        assertThat(notesDb.get(1).userId()).isEqualTo(user.id());
        assertThat(notesDb.get(1).title()).isEqualTo("Note title");
        assertThat(notesDb.get(1).content()).isEqualTo("Note content");
        assertThat(notesDb.get(1).creationTime()).isEqualTo(Instant.parse("2010-10-10T10:10:10Z"));
        assertThat(notesDb.get(1).modificationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        final var noteGravesDb = noteGraveDao.read();
        assertThat(noteGravesDb).hasSize(2);
        assertThat(noteGravesDb.get(0).id()).isEqualTo(UUID.fromString("6d9e6e4d-be5e-4768-bd33-bc37f7b80284"));
        assertThat(noteGravesDb.get(0).userId()).isEqualTo(user.id());
        assertThat(noteGravesDb.get(0).creationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
        assertThat(noteGravesDb.get(1).id()).isEqualTo(UUID.fromString("dc5c2ee8-ba84-4019-b8f2-0a8d93e170cd"));
        assertThat(noteGravesDb.get(1).userId()).isEqualTo(user.id());
        assertThat(noteGravesDb.get(1).creationTime()).isEqualTo(Instant.parse("2011-11-11T11:11:11Z"));
    }

    @Test
    void shouldNotSyncWithoutAuthorization() throws Exception {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/notes/sync"))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString("{}"))
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isGreaterThan(299);
    }
}
