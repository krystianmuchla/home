package com.github.krystianmuchla.skyr.note.sync.api;

import com.github.krystianmuchla.skyr.IntegrationTest;
import com.github.krystianmuchla.skyr.note.Note;
import com.github.krystianmuchla.skyr.note.sync.NotesSync;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class NotesSyncControllerTest extends IntegrationTest {
    private static NotesSync initialNotesSync;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void beforeAllTests(@Autowired final JdbcTemplate jdbcTemplate) {
        initialNotesSync = jdbcTemplate.query(
                "SELECT sync_id, sync_time FROM notes_sync",
                (resultSet, rowNum) -> new NotesSync(
                        UUID.fromString(resultSet.getString("sync_id")),
                        resultSet.getTimestamp("sync_time").toLocalDateTime().toInstant(ZoneOffset.UTC)
                )
        ).getFirst();
    }

    @AfterEach
    void afterEachTest() {
        deleteNotes();
        updateNotesSync(initialNotesSync);
    }

    @Test
    void shouldSyncNotesMutually() throws Exception {
        final var externalSyncId = UUID.fromString("ac50944e-0677-445e-92a6-1b0bfd14f343");
        final var externalSyncTime = Instant.parse("2010-10-10T10:10:10Z");
        final var externalNotes = List.of(
                new Note(
                        UUID.fromString("4d8af443-bfa9-4d47-a886-b1ddc82a958d"),
                        "Unique note",
                        "External note 1",
                        Instant.parse("2010-10-10T10:10:20Z"),
                        Instant.parse("2010-10-10T10:10:20Z")
                ),
                new Note(
                        UUID.fromString("cb8b51f8-63e5-4964-94e4-0b3b7944e7d4"),
                        "Note with same id and different modification time",
                        "External note 2",
                        Instant.parse("2010-10-10T10:10:30Z"),
                        Instant.parse("2010-10-10T10:10:30Z")
                ),
                new Note(
                        UUID.fromString("2109af10-c870-4e8d-8f53-7220d693ca78"),
                        "Note with same id and different modification time",
                        "External note 3",
                        Instant.parse("2010-10-10T10:10:40Z"),
                        Instant.parse("2010-10-10T10:10:40Z")
                ),
                new Note(
                        UUID.fromString("2f6772bc-2a58-4e25-9318-3a60d4fb52dc"),
                        "Note with same id and same modification time",
                        "External note 4",
                        Instant.parse("2011-11-11T11:11:11Z"),
                        Instant.parse("2011-11-11T11:11:11Z")
                )
        );
        final var notesSync = new NotesSync(
                UUID.fromString("e4af1370-c730-4842-a990-d52380bf6661"),
                Instant.parse("2011-11-11T11:11:41Z")
        );
        final var notes = List.of(
                new Note(
                        UUID.fromString("2f6772bc-2a58-4e25-9318-3a60d4fb52dc"),
                        "Note with same id and same modification time",
                        "Note 1",
                        Instant.parse("2011-11-11T11:11:11Z"),
                        Instant.parse("2011-11-11T11:11:11Z")
                ),
                new Note(
                        UUID.fromString("2109af10-c870-4e8d-8f53-7220d693ca78"),
                        "Note with same id and different modification time",
                        "Note 2",
                        Instant.parse("2010-10-10T10:10:21Z"),
                        Instant.parse("2010-10-10T10:10:21Z")
                ),
                new Note(
                        UUID.fromString("cb8b51f8-63e5-4964-94e4-0b3b7944e7d4"),
                        "Note with same id and different modification time",
                        "Note 3",
                        Instant.parse("2011-11-11T11:11:31Z"),
                        Instant.parse("2011-11-11T11:11:31Z")
                ),
                new Note(
                        UUID.fromString("b6f0ff55-7dcf-47b5-beae-75f192ecbb7f"),
                        "Unique note",
                        "Note 4",
                        Instant.parse("2011-11-11T11:11:41Z"),
                        Instant.parse("2011-11-11T11:11:41Z")
                )
        );
        updateNotesSync(notesSync);
        createNotes(notes);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "syncTime": "%s",
                          "modifiedNotes": [
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            },
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            }
                          ]
                        }
                        """.formatted(externalSyncId.toString(),
                        externalSyncTime.toString(),
                        externalNotes.get(0).id().toString(),
                        externalNotes.get(0).title(),
                        externalNotes.get(0).content(),
                        externalNotes.get(0).creationTime().toString(),
                        externalNotes.get(0).modificationTime().toString(),
                        externalNotes.get(1).id().toString(),
                        externalNotes.get(1).title(),
                        externalNotes.get(1).content(),
                        externalNotes.get(1).creationTime().toString(),
                        externalNotes.get(1).modificationTime().toString(),
                        externalNotes.get(2).id().toString(),
                        externalNotes.get(2).title(),
                        externalNotes.get(2).content(),
                        externalNotes.get(2).creationTime().toString(),
                        externalNotes.get(2).modificationTime().toString(),
                        externalNotes.get(3).id().toString(),
                        externalNotes.get(3).title(),
                        externalNotes.get(3).content(),
                        externalNotes.get(3).creationTime().toString(),
                        externalNotes.get(3).modificationTime().toString())
                )
        );

        final var responseContent = resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.modifiedNotes", hasSize(3)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "$.syncId"));
        final var syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "$.syncTime"));
        assertThat(syncIdResponse).isNotEqualTo(notesSync.syncId()).isNotEqualTo(externalSyncId);
        assertThat(syncTimeResponse).isEqualTo(notes.get(3).modificationTime());
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(5);
        assertThat(notesDb.get(0).id()).isEqualTo(externalNotes.get(0).id());
        assertThat(notesDb.get(0).title()).isEqualTo(externalNotes.get(0).title());
        assertThat(notesDb.get(0).content()).isEqualTo(externalNotes.get(0).content());
        assertThat(notesDb.get(0).creationTime()).isEqualTo(externalNotes.get(0).creationTime());
        assertThat(notesDb.get(0).modificationTime()).isEqualTo(externalNotes.get(0).modificationTime());
        assertThat(notesDb.get(1).id()).isEqualTo(externalNotes.get(2).id());
        assertThat(notesDb.get(1).title()).isEqualTo(externalNotes.get(2).title());
        assertThat(notesDb.get(1).content()).isEqualTo(externalNotes.get(2).content());
        assertThat(notesDb.get(1).creationTime()).isEqualTo(externalNotes.get(2).creationTime());
        assertThat(notesDb.get(1).modificationTime()).isEqualTo(externalNotes.get(2).modificationTime());
        assertThat(notesDb.get(2).id()).isEqualTo(notes.get(0).id());
        assertThat(notesDb.get(2).title()).isEqualTo(notes.get(0).title());
        assertThat(notesDb.get(2).content()).isEqualTo(notes.get(0).content());
        assertThat(notesDb.get(2).creationTime()).isEqualTo(notes.get(0).creationTime());
        assertThat(notesDb.get(2).modificationTime()).isEqualTo(notes.get(0).modificationTime());
        assertThat(notesDb.get(3).id()).isEqualTo(notes.get(2).id());
        assertThat(notesDb.get(3).title()).isEqualTo(notes.get(2).title());
        assertThat(notesDb.get(3).content()).isEqualTo(notes.get(2).content());
        assertThat(notesDb.get(3).creationTime()).isEqualTo(notes.get(2).creationTime());
        assertThat(notesDb.get(3).modificationTime()).isEqualTo(notes.get(2).modificationTime());
        assertThat(notesDb.get(4).id()).isEqualTo(notes.get(3).id());
        assertThat(notesDb.get(4).title()).isEqualTo(notes.get(3).title());
        assertThat(notesDb.get(4).content()).isEqualTo(notes.get(3).content());
        assertThat(notesDb.get(4).creationTime()).isEqualTo(notes.get(3).creationTime());
        assertThat(notesDb.get(4).modificationTime()).isEqualTo(notes.get(3).modificationTime());
    }

    @Test
    void shouldSyncNotesWithOnlyNotesExisting() throws Exception {
        final var externalSyncId = UUID.fromString("3421f2a8-74a5-403f-927f-9fd5201484ca");
        final var externalSyncTime = Instant.parse("2010-10-10T10:10:10Z");
        final var notesSync = new NotesSync(
                UUID.fromString("ed5dbd4c-09cd-4af5-90bd-e43fb30972c2"),
                Instant.parse("2011-11-11T11:11:11Z")
        );
        final var note = new Note(
                UUID.fromString("7476eca8-6228-4a17-8b4b-47c846b71ec3"),
                "Note title",
                "Note content",
                Instant.parse("2011-11-11T11:11:11Z"),
                Instant.parse("2011-11-11T11:11:11Z")
        );
        updateNotesSync(notesSync);
        createNote(note);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "syncTime": "%s",
                          "modifiedNotes": []
                        }
                        """.formatted(externalSyncId.toString(), externalSyncTime.toString())
                )
        );

        final var responseContent = resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.modifiedNotes", hasSize(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "$.syncId"));
        final var syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "$.syncTime"));
        assertThat(syncIdResponse).isEqualTo(notesSync.syncId());
        assertThat(syncTimeResponse).isEqualTo(notesSync.syncTime());
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(1);
        assertThat(notesDb.get(0).id()).isEqualTo(note.id());
        assertThat(notesDb.get(0).title()).isEqualTo(note.title());
        assertThat(notesDb.get(0).content()).isEqualTo(note.content());
        assertThat(notesDb.get(0).creationTime()).isEqualTo(note.creationTime());
        assertThat(notesDb.get(0).modificationTime()).isEqualTo(note.modificationTime());
    }

    @Test
    void shouldSyncNotesWithOnlyExternalNotesExisting() throws Exception {
        final var externalSyncId = UUID.fromString("290a0bdb-2745-4be9-9e5b-f4fc1dfc14b5");
        final var externalSyncTime = Instant.parse("2010-10-10T10:10:10Z");
        final var externalNote = new Note(
                UUID.fromString("e3e3bb7f-1b36-42af-9455-da68af94dc65"),
                "Note title",
                "Note content",
                Instant.parse("2012-12-12T12:12:12Z"),
                Instant.parse("2012-12-12T12:12:12Z")
        );
        final var notesSync = new NotesSync(
                UUID.fromString("e9ea5af9-1207-4372-bcda-5c1841984679"),
                Instant.parse("2011-11-11T11:11:11Z")
        );
        updateNotesSync(notesSync);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "syncTime": "%s",
                          "modifiedNotes": [
                            {
                              "id": "%s",
                              "title": "%s",
                              "content": "%s",
                              "creationTime": "%s",
                              "modificationTime": "%s"
                            }
                          ]
                        }
                        """.formatted(externalSyncId.toString(),
                        externalSyncTime.toString(),
                        externalNote.id().toString(),
                        externalNote.title(),
                        externalNote.content(),
                        externalNote.creationTime().toString(),
                        externalNote.modificationTime().toString()))
        );

        final var responseContent = resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.modifiedNotes", hasSize(0)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "$.syncId"));
        final var syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "$.syncTime"));
        assertThat(syncIdResponse).isNotEqualTo(notesSync.syncId()).isNotEqualTo(externalSyncId);
        assertThat(syncTimeResponse).isEqualTo(externalNote.modificationTime());
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(1);
        assertThat(notesDb.get(0).id()).isEqualTo(externalNote.id());
        assertThat(notesDb.get(0).title()).isEqualTo(externalNote.title());
        assertThat(notesDb.get(0).content()).isEqualTo(externalNote.content());
        assertThat(notesDb.get(0).creationTime()).isEqualTo(externalNote.creationTime());
        assertThat(notesDb.get(0).modificationTime()).isEqualTo(externalNote.modificationTime());
    }

    @Test
    void shouldNotSyncNotesBeforeExternalSyncTime() throws Exception {
        final var externalSyncId = UUID.fromString("aab36f82-fb83-45e4-bc26-5e973c94dd5f");
        final var externalSyncTime = Instant.parse("2010-10-10T10:10:10Z");
        final var notesSync = new NotesSync(
                UUID.fromString("85f3fe92-b40c-4d09-ad7b-e241ee78357a"),
                Instant.parse("2011-11-11T11:11:11Z")
        );
        final var note = new Note(
                UUID.fromString("50fe879c-3267-4ea0-97b7-263c703957c8"),
                "Note title",
                "Note content",
                Instant.parse("2009-09-09T09:09:09Z"),
                Instant.parse("2009-09-09T09:09:09Z")
        );
        updateNotesSync(notesSync);
        createNote(note);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "syncTime": "%s",
                          "modifiedNotes": []
                        }
                        """.formatted(externalSyncId.toString(), externalSyncTime.toString())
                )
        );

        final var responseContent = resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.modifiedNotes", hasSize(0)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "$.syncId"));
        final var syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "$.syncTime"));
        assertThat(syncIdResponse).isEqualTo(notesSync.syncId());
        assertThat(syncTimeResponse).isEqualTo(notesSync.syncTime());
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(1);
        assertThat(notesDb.get(0).id()).isEqualTo(note.id());
        assertThat(notesDb.get(0).title()).isEqualTo(note.title());
        assertThat(notesDb.get(0).content()).isEqualTo(note.content());
        assertThat(notesDb.get(0).creationTime()).isEqualTo(note.creationTime());
        assertThat(notesDb.get(0).modificationTime()).isEqualTo(note.modificationTime());
    }

    @Test
    void shouldSyncNotesWithEmptyData() throws Exception {
        final var externalSyncId = UUID.fromString("ac50944e-0677-445e-92a6-1b0bfd14f343");
        final var externalSyncTime = Instant.parse("2010-10-10T10:10:10Z");
        final var notesSync = new NotesSync(
                UUID.fromString("f2b7c3e6-cc85-4c3d-b789-f22685035fa9"),
                Instant.parse("2011-11-11T11:11:11Z")
        );
        updateNotesSync(notesSync);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "syncTime": "%s",
                          "modifiedNotes": []
                        }
                        """.formatted(externalSyncId.toString(), externalSyncTime.toString()))
        );

        final var responseContent = resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.modifiedNotes", hasSize(0)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "$.syncId"));
        final var syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "$.syncTime"));
        assertThat(syncIdResponse).isEqualTo(notesSync.syncId());
        assertThat(syncTimeResponse).isEqualTo(notesSync.syncTime());
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(0);
    }

    @Test
    void shouldSyncNotesForSameSyncIds() throws Exception {
        final var notesSync = new NotesSync(
                UUID.fromString("5770e671-490b-4331-ac7f-fd22abe92841"),
                Instant.parse("2011-11-11T11:11:11Z")
        );
        updateNotesSync(notesSync);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
                .contentType("application/json")
                .content("""
                        {
                          "syncId": "%s",
                          "syncTime": "%s",
                          "modifiedNotes": []
                        }
                        """.formatted(notesSync.syncId().toString(), notesSync.syncTime().toString()))
        );

        final var responseContent = resultActions.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.modifiedNotes", hasSize(0)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "$.syncId"));
        final var syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "$.syncTime"));
        assertThat(syncIdResponse).isEqualTo(notesSync.syncId());
        assertThat(syncTimeResponse).isEqualTo(notesSync.syncTime());
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(0);
    }

    private void updateNotesSync(final NotesSync notesSync) {
        jdbcTemplate.update(
                "UPDATE notes_sync SET sync_id = ?, sync_time = ?",
                notesSync.syncId().toString(),
                Timestamp.valueOf(LocalDateTime.ofInstant(notesSync.syncTime(), ZoneOffset.UTC))
        );
    }

    private void createNote(final Note note) {
        jdbcTemplate.update(
                "INSERT INTO note VALUES (?, ?, ?, ?, ?)",
                note.id().toString(),
                note.title(),
                note.content(),
                Timestamp.valueOf(LocalDateTime.ofInstant(note.creationTime(), ZoneOffset.UTC)).toString(),
                Timestamp.valueOf(LocalDateTime.ofInstant(note.modificationTime(), ZoneOffset.UTC)).toString()
        );
    }

    private void createNotes(final List<Note> notes) {
        notes.forEach(this::createNote);
    }

    private List<Note> readNotes() {
        return jdbcTemplate.query(
                "SELECT * FROM note order by modification_time",
                (resultSet, rowNum) -> new Note(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getTimestamp("creation_time").toLocalDateTime().toInstant(ZoneOffset.UTC),
                        resultSet.getTimestamp("modification_time").toLocalDateTime().toInstant(ZoneOffset.UTC)
                )
        );
    }

    private void deleteNotes() {
        jdbcTemplate.update("DELETE FROM note");
    }
}
