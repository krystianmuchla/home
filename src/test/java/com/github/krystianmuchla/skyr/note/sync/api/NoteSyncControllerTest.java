package com.github.krystianmuchla.skyr.note.sync.api;

import com.github.krystianmuchla.skyr.IntegrationTest;
import com.github.krystianmuchla.skyr.note.Note;
import com.github.krystianmuchla.skyr.note.grave.NoteGrave;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class NoteSyncControllerTest extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEachTest() {
        deleteNotes();
        deleteNoteGraves();
    }

    @Test
    void shouldSyncNotes() throws Exception {
        final var externalNotes = List.of(
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
        );
        final var notes = List.of(
            new Note(
                UUID.fromString("0981a57b-9ccd-455e-956d-2daf39e45480"),
                "0981a57b-9ccd-455e-956d-2daf39e45480",
                "0981a57b-9ccd-455e-956d-2daf39e45480",
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes.get(1).id(),
                externalNotes.get(1).id().toString(),
                externalNotes.get(1).id().toString(),
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes.get(2).id(),
                externalNotes.get(2).id().toString(),
                externalNotes.get(2).id().toString(),
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes.get(3).id(),
                externalNotes.get(3).id().toString(),
                externalNotes.get(3).id().toString(),
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes.get(5).id(),
                externalNotes.get(5).id().toString(),
                externalNotes.get(5).id().toString(),
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes.get(6).id(),
                externalNotes.get(6).id().toString(),
                externalNotes.get(6).id().toString(),
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            ),
            new Note(
                externalNotes.get(7).id(),
                externalNotes.get(7).id().toString(),
                externalNotes.get(7).id().toString(),
                Instant.parse("2010-10-10T10:10:10Z"),
                Instant.parse("2010-10-10T10:10:10Z")
            )
        );
        final var noteGraves = List.of(
            new NoteGrave(externalNotes.get(8).id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes.get(9).id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes.get(10).id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes.get(11).id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes.get(12).id(), Instant.parse("2010-10-10T10:10:10Z")),
            new NoteGrave(externalNotes.get(13).id(), Instant.parse("2010-10-10T10:10:10Z"))
        );
        createNotes(notes);
        createNoteGraves(noteGraves);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
            .contentType("application/json")
            .content("""
                {
                  "notes": [
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
                    },
                    {
                      "id": "%s",
                      "title": "%s",
                      "creationTime": "%s",
                      "modificationTime": "%s"
                    },
                    {
                      "id": "%s",
                      "title": "%s",
                      "creationTime": "%s",
                      "modificationTime": "%s"
                    },
                    {
                      "id": "%s",
                      "title": "%s",
                      "creationTime": "%s",
                      "modificationTime": "%s"
                    },
                    {
                      "id": "%s",
                      "title": "%s",
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
                    },
                    {
                      "id": "%s",
                      "title": "%s",
                      "creationTime": "%s",
                      "modificationTime": "%s"
                    },
                    {
                      "id": "%s",
                      "title": "%s",
                      "creationTime": "%s",
                      "modificationTime": "%s"
                    },
                    {
                      "id": "%s",
                      "title": "%s",
                      "creationTime": "%s",
                      "modificationTime": "%s"
                    }
                  ]
                }
                """.formatted(
                externalNotes.get(0).id(),
                externalNotes.get(0).title(),
                externalNotes.get(0).content(),
                externalNotes.get(0).creationTime(),
                externalNotes.get(0).modificationTime(),
                externalNotes.get(1).id(),
                externalNotes.get(1).title(),
                externalNotes.get(1).content(),
                externalNotes.get(1).creationTime(),
                externalNotes.get(1).modificationTime(),
                externalNotes.get(2).id(),
                externalNotes.get(2).title(),
                externalNotes.get(2).content(),
                externalNotes.get(2).creationTime(),
                externalNotes.get(2).modificationTime(),
                externalNotes.get(3).id(),
                externalNotes.get(3).title(),
                externalNotes.get(3).content(),
                externalNotes.get(3).creationTime(),
                externalNotes.get(3).modificationTime(),
                externalNotes.get(4).id(),
                externalNotes.get(4).title(),
                externalNotes.get(4).creationTime(),
                externalNotes.get(4).modificationTime(),
                externalNotes.get(5).id(),
                externalNotes.get(5).title(),
                externalNotes.get(5).creationTime(),
                externalNotes.get(5).modificationTime(),
                externalNotes.get(6).id(),
                externalNotes.get(6).title(),
                externalNotes.get(6).creationTime(),
                externalNotes.get(6).modificationTime(),
                externalNotes.get(7).id(),
                externalNotes.get(7).title(),
                externalNotes.get(7).creationTime(),
                externalNotes.get(7).modificationTime(),
                externalNotes.get(8).id(),
                externalNotes.get(8).title(),
                externalNotes.get(8).content(),
                externalNotes.get(8).creationTime(),
                externalNotes.get(8).modificationTime(),
                externalNotes.get(9).id(),
                externalNotes.get(9).title(),
                externalNotes.get(9).content(),
                externalNotes.get(9).creationTime(),
                externalNotes.get(9).modificationTime(),
                externalNotes.get(10).id(),
                externalNotes.get(10).title(),
                externalNotes.get(10).content(),
                externalNotes.get(10).creationTime(),
                externalNotes.get(10).modificationTime(),
                externalNotes.get(11).id(),
                externalNotes.get(11).title(),
                externalNotes.get(11).creationTime(),
                externalNotes.get(11).modificationTime(),
                externalNotes.get(12).id(),
                externalNotes.get(12).title(),
                externalNotes.get(12).creationTime(),
                externalNotes.get(12).modificationTime(),
                externalNotes.get(13).id(),
                externalNotes.get(13).title(),
                externalNotes.get(13).creationTime(),
                externalNotes.get(13).modificationTime())
            )
        );

        final var responseContent = resultActions.andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "application/json"))
            .andReturn()
            .getResponse()
            .getContentAsString();
        final var notesResponse = getNotes(responseContent);
        assertThat(notesResponse).hasSize(9).contains(
            notes.get(0),
            notes.get(1),
            notes.get(2),
            notes.get(4),
            notes.get(5),
            noteGraves.get(0).toNote(),
            noteGraves.get(1).toNote(),
            noteGraves.get(3).toNote(),
            noteGraves.get(4).toNote()
        );
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(8).contains(
            externalNotes.get(0),
            externalNotes.get(3),
            externalNotes.get(10),
            notes.get(0),
            notes.get(1),
            notes.get(2),
            notes.get(4),
            notes.get(5)
        );
        final var noteGravesDb = readNoteGraves();
        assertThat(noteGravesDb).hasSize(6).contains(
            new NoteGrave(
                externalNotes.get(7).id(),
                externalNotes.get(7).modificationTime()
            ),
            noteGraves.get(0),
            noteGraves.get(1),
            noteGraves.get(3),
            noteGraves.get(4),
            noteGraves.get(5)
        );
    }

    @Test
    void shouldSyncNotesWithEmptyExternalNotes() throws Exception {
        final var note = new Note(
            UUID.fromString("2642208c-09bb-44fd-b036-fab080035974"),
            "2642208c-09bb-44fd-b036-fab080035974",
            "2642208c-09bb-44fd-b036-fab080035974",
            Instant.parse("2010-10-10T10:10:10Z"),
            Instant.parse("2010-10-10T10:10:10Z")
        );
        createNote(note);

        final var resultActions = mockMvc.perform(put("/api/notes/sync")
            .contentType("application/json")
            .content("{\"notes\":[]}")
        );

        final var responseContent = resultActions.andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "application/json"))
            .andReturn()
            .getResponse()
            .getContentAsString();
        final var notesResponse = getNotes(responseContent);
        assertThat(notesResponse).hasSize(1).contains(note);
        final var notesDb = readNotes();
        assertThat(notesDb).hasSize(1);
        assertThat(notesDb.getFirst()).isEqualTo(note);
        final var noteGravesDb = readNoteGraves();
        assertThat(noteGravesDb).hasSize(0);
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

    private void createNoteGrave(final NoteGrave noteGrave) {
        jdbcTemplate.update(
            "INSERT INTO note_grave VALUES (?, ?)",
            noteGrave.id().toString(),
            Timestamp.valueOf(LocalDateTime.ofInstant(noteGrave.creationTime(), ZoneOffset.UTC)).toString()
        );
    }

    private void createNoteGraves(final List<NoteGrave> noteGraves) {
        noteGraves.forEach(this::createNoteGrave);
    }

    private List<NoteGrave> readNoteGraves() {
        return jdbcTemplate.query(
            "SELECT * FROM note_grave",
            (resultSet, rowNum) -> new NoteGrave(
                UUID.fromString(resultSet.getString("id")),
                resultSet.getTimestamp("creation_time").toLocalDateTime().toInstant(ZoneOffset.UTC)
            )
        );
    }

    private void deleteNoteGraves() {
        jdbcTemplate.update("DELETE FROM note_grave");
    }

    private List<Note> getNotes(final String responseContent) throws IOException {
        final Map<String, Object> responseMap = new ObjectMapper().readValue(
            responseContent,
            new TypeReference<Map<String, Object>>() {
            }
        );
        final List<Map<String, Object>> notes = (List<Map<String, Object>>) responseMap.get("notes");
        return notes.stream().map(modifiedNote -> {
            final String creationTime = (String) modifiedNote.get("creationTime");
            return new Note(
                UUID.fromString((String) modifiedNote.get("id")),
                (String) modifiedNote.get("title"),
                (String) modifiedNote.get("content"),
                creationTime != null ? Instant.parse(creationTime) : null,
                Instant.parse((String) modifiedNote.get("modificationTime"))
            );
        }).toList();
    }
}
