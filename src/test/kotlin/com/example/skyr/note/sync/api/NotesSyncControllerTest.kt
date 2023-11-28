package com.example.skyr.note.sync.api

import com.example.skyr.IntegrationTest
import com.example.skyr.note.Note
import com.example.skyr.note.sync.NotesSync
import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@AutoConfigureMockMvc
class NotesSyncControllerTest : IntegrationTest() {

    companion object {
        private lateinit var initialNotesSync: NotesSync

        @BeforeAll
        @JvmStatic
        fun beforeAllTests(@Autowired jdbcTemplate: JdbcTemplate) {
            initialNotesSync = jdbcTemplate.query(
                "SELECT sync_id, sync_time FROM notes_sync"
            ) { resultSet, _ ->
                NotesSync(
                    UUID.fromString(resultSet.getString("sync_id")),
                    resultSet.getTimestamp("sync_time").toLocalDateTime().toInstant(ZoneOffset.UTC)
                )
            }[0]
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @AfterEach
    fun afterEachTest() {
        deleteNotes()
        update(initialNotesSync)
    }

    @Test
    fun shouldSyncNotesMutually() {
        val externalSyncId = UUID.fromString("ac50944e-0677-445e-92a6-1b0bfd14f343")
        val externalSyncTime = Instant.parse("2010-10-10T10:10:10Z")
        val externalNotes = listOf(
            Note(
                UUID.fromString("4d8af443-bfa9-4d47-a886-b1ddc82a958d"),
                "Unique note",
                "External note 1",
                Instant.parse("2010-10-10T10:10:20Z"),
                Instant.parse("2010-10-10T10:10:20Z"),
            ),
            Note(
                UUID.fromString("cb8b51f8-63e5-4964-94e4-0b3b7944e7d4"),
                "Note with same id and different modification time",
                "External note 2",
                Instant.parse("2010-10-10T10:10:30Z"),
                Instant.parse("2010-10-10T10:10:30Z"),
            ),
            Note(
                UUID.fromString("2109af10-c870-4e8d-8f53-7220d693ca78"),
                "Note with same id and different modification time",
                "External note 3",
                Instant.parse("2010-10-10T10:10:40Z"),
                Instant.parse("2010-10-10T10:10:40Z"),
            ),
            Note(
                UUID.fromString("2f6772bc-2a58-4e25-9318-3a60d4fb52dc"),
                "Note with same id and same modification time",
                "External note 4",
                Instant.parse("2011-11-11T11:11:11Z"),
                Instant.parse("2011-11-11T11:11:11Z"),
            ),
        )
        val notesSync =
            NotesSync(UUID.fromString("e4af1370-c730-4842-a990-d52380bf6661"), Instant.parse("2011-11-11T11:11:41Z"))
        val notes = listOf(
            Note(
                UUID.fromString("2f6772bc-2a58-4e25-9318-3a60d4fb52dc"),
                "Note with same id and same modification time",
                "Note 1",
                Instant.parse("2011-11-11T11:11:11Z"),
                Instant.parse("2011-11-11T11:11:11Z"),
            ),
            Note(
                UUID.fromString("2109af10-c870-4e8d-8f53-7220d693ca78"),
                "Note with same id and different modification time",
                "Note 2",
                Instant.parse("2010-10-10T10:10:21Z"),
                Instant.parse("2010-10-10T10:10:21Z"),
            ),
            Note(
                UUID.fromString("cb8b51f8-63e5-4964-94e4-0b3b7944e7d4"),
                "Note with same id and different modification time",
                "Note 3",
                Instant.parse("2011-11-11T11:11:31Z"),
                Instant.parse("2011-11-11T11:11:31Z"),
            ),
            Note(
                UUID.fromString("b6f0ff55-7dcf-47b5-beae-75f192ecbb7f"),
                "Unique note",
                "Note 4",
                Instant.parse("2011-11-11T11:11:41Z"),
                Instant.parse("2011-11-11T11:11:41Z"),
            ),
        )
        update(notesSync)
        createNotes(notes)

        val resultActions = mockMvc.perform(
            put("/api/notes/sync").contentType("application/json")
                .content(
                    """
                        {
                          "syncId": "$externalSyncId",
                          "syncTime": "$externalSyncTime",
                          "modifiedNotes": [
                            {
                              "id": "${externalNotes[0].id}",
                              "title": "${externalNotes[0].title}",
                              "content": "${externalNotes[0].content}",
                              "creationTime": "${externalNotes[0].creationTime}",
                              "modificationTime": "${externalNotes[0].modificationTime}"
                            },
                            {
                              "id": "${externalNotes[1].id}",
                              "title": "${externalNotes[1].title}",
                              "content": "${externalNotes[1].content}",
                              "creationTime": "${externalNotes[1].creationTime}",
                              "modificationTime": "${externalNotes[1].modificationTime}"
                            },
                            {
                              "id": "${externalNotes[2].id}",
                              "title": "${externalNotes[2].title}",
                              "content": "${externalNotes[2].content}",
                              "creationTime": "${externalNotes[2].creationTime}",
                              "modificationTime": "${externalNotes[2].modificationTime}"
                            },
                            {
                              "id": "${externalNotes[3].id}",
                              "title": "${externalNotes[3].title}",
                              "content": "${externalNotes[3].content}",
                              "creationTime": "${externalNotes[3].creationTime}",
                              "modificationTime": "${externalNotes[3].modificationTime}"
                            }
                          ]
                        }
                    """.trimIndent()
                )
        )

        val responseContent = resultActions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.modifiedNotes", hasSize<Any>(3)))
            .andReturn()
            .response
            .contentAsString
        val syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "\$.syncId"))
        val syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "\$.syncTime"))
        assertThat(syncIdResponse).isNotEqualTo(notesSync.syncId).isNotEqualTo(externalSyncId)
        assertThat(syncTimeResponse).isEqualTo(notes[3].modificationTime)
        val notesDb = readNotes()
        assertThat(notesDb).hasSize(5)
        assertThat(notesDb[0].id).isEqualTo(externalNotes[0].id)
        assertThat(notesDb[0].title).isEqualTo(externalNotes[0].title)
        assertThat(notesDb[0].content).isEqualTo(externalNotes[0].content)
        assertThat(notesDb[0].creationTime).isEqualTo(externalNotes[0].creationTime)
        assertThat(notesDb[0].modificationTime).isEqualTo(externalNotes[0].modificationTime)
        assertThat(notesDb[1].id).isEqualTo(externalNotes[2].id)
        assertThat(notesDb[1].title).isEqualTo(externalNotes[2].title)
        assertThat(notesDb[1].content).isEqualTo(externalNotes[2].content)
        assertThat(notesDb[1].creationTime).isEqualTo(externalNotes[2].creationTime)
        assertThat(notesDb[1].modificationTime).isEqualTo(externalNotes[2].modificationTime)
        assertThat(notesDb[2].id).isEqualTo(notes[0].id)
        assertThat(notesDb[2].title).isEqualTo(notes[0].title)
        assertThat(notesDb[2].content).isEqualTo(notes[0].content)
        assertThat(notesDb[2].creationTime).isEqualTo(notes[0].creationTime)
        assertThat(notesDb[2].modificationTime).isEqualTo(notes[0].modificationTime)
        assertThat(notesDb[3].id).isEqualTo(notes[2].id)
        assertThat(notesDb[3].title).isEqualTo(notes[2].title)
        assertThat(notesDb[3].content).isEqualTo(notes[2].content)
        assertThat(notesDb[3].creationTime).isEqualTo(notes[2].creationTime)
        assertThat(notesDb[3].modificationTime).isEqualTo(notes[2].modificationTime)
        assertThat(notesDb[4].id).isEqualTo(notes[3].id)
        assertThat(notesDb[4].title).isEqualTo(notes[3].title)
        assertThat(notesDb[4].content).isEqualTo(notes[3].content)
        assertThat(notesDb[4].creationTime).isEqualTo(notes[3].creationTime)
        assertThat(notesDb[4].modificationTime).isEqualTo(notes[3].modificationTime)
    }

    @Test
    fun shouldSyncNotesWithOnlyNotesExisting() {
        val externalSyncId = UUID.fromString("3421f2a8-74a5-403f-927f-9fd5201484ca")
        val externalSyncTime = Instant.parse("2010-10-10T10:10:10Z")
        val notesSync =
            NotesSync(UUID.fromString("ed5dbd4c-09cd-4af5-90bd-e43fb30972c2"), Instant.parse("2011-11-11T11:11:11Z"))
        val note = Note(
            UUID.fromString("7476eca8-6228-4a17-8b4b-47c846b71ec3"),
            "Note title",
            "Note content",
            Instant.parse("2011-11-11T11:11:11Z"),
            Instant.parse("2011-11-11T11:11:11Z")
        )
        update(notesSync)
        createNote(note)

        val resultActions = mockMvc.perform(
            put("/api/notes/sync").contentType("application/json")
                .content(
                    """
                        {
                          "syncId": "$externalSyncId",
                          "syncTime": "$externalSyncTime",
                          "modifiedNotes": []
                        }
                    """.trimIndent()
                )
        )

        val responseContent = resultActions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.modifiedNotes", hasSize<Any>(1)))
            .andReturn()
            .response
            .contentAsString
        val syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "\$.syncId"))
        val syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "\$.syncTime"))
        assertThat(syncIdResponse).isEqualTo(notesSync.syncId)
        assertThat(syncTimeResponse).isEqualTo(notesSync.syncTime)
        val notesDb = readNotes()
        assertThat(notesDb).hasSize(1)
        assertThat(notesDb[0].id).isEqualTo(note.id)
        assertThat(notesDb[0].title).isEqualTo(note.title)
        assertThat(notesDb[0].content).isEqualTo(note.content)
        assertThat(notesDb[0].creationTime).isEqualTo(note.creationTime)
        assertThat(notesDb[0].modificationTime).isEqualTo(note.modificationTime)
    }

    @Test
    fun shouldSyncNotesWithOnlyExternalNotesExisting() {
        val externalSyncId = UUID.fromString("290a0bdb-2745-4be9-9e5b-f4fc1dfc14b5")
        val externalSyncTime = Instant.parse("2010-10-10T10:10:10Z")
        val externalNote = Note(
            UUID.fromString("e3e3bb7f-1b36-42af-9455-da68af94dc65"),
            "Note title",
            "Note content",
            Instant.parse("2012-12-12T12:12:12Z"),
            Instant.parse("2012-12-12T12:12:12Z")
        )
        val notesSync =
            NotesSync(UUID.fromString("e9ea5af9-1207-4372-bcda-5c1841984679"), Instant.parse("2011-11-11T11:11:11Z"))
        update(notesSync)

        val resultActions = mockMvc.perform(
            put("/api/notes/sync").contentType("application/json")
                .content(
                    """
                        {
                          "syncId": "$externalSyncId",
                          "syncTime": "$externalSyncTime",
                          "modifiedNotes": [
                            {
                              "id": "${externalNote.id}",
                              "title": "${externalNote.title}",
                              "content": "${externalNote.content}",
                              "creationTime": "${externalNote.creationTime}",
                              "modificationTime": "${externalNote.modificationTime}"
                            }
                          ]
                        }
                    """.trimIndent()
                )
        )

        val responseContent = resultActions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.modifiedNotes", hasSize<Any>(0)))
            .andReturn()
            .response
            .contentAsString
        val syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "\$.syncId"))
        val syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "\$.syncTime"))
        assertThat(syncIdResponse).isNotEqualTo(notesSync.syncId).isNotEqualTo(externalSyncId)
        assertThat(syncTimeResponse).isEqualTo(externalNote.modificationTime)
        val notesDb = readNotes()
        assertThat(notesDb).hasSize(1)
        assertThat(notesDb[0].id).isEqualTo(externalNote.id)
        assertThat(notesDb[0].title).isEqualTo(externalNote.title)
        assertThat(notesDb[0].content).isEqualTo(externalNote.content)
        assertThat(notesDb[0].creationTime).isEqualTo(externalNote.creationTime)
        assertThat(notesDb[0].modificationTime).isEqualTo(externalNote.modificationTime)
    }

    @Test
    fun shouldNotSyncNotesBeforeExternalSyncTime() {
        val externalSyncId = UUID.fromString("aab36f82-fb83-45e4-bc26-5e973c94dd5f")
        val externalSyncTime = Instant.parse("2010-10-10T10:10:10Z")
        val notesSync =
            NotesSync(UUID.fromString("85f3fe92-b40c-4d09-ad7b-e241ee78357a"), Instant.parse("2011-11-11T11:11:11Z"))
        val note = Note(
            UUID.fromString("50fe879c-3267-4ea0-97b7-263c703957c8"),
            "Note title",
            "Note content",
            Instant.parse("2009-09-09T09:09:09Z"),
            Instant.parse("2009-09-09T09:09:09Z")
        )
        update(notesSync)
        createNote(note)

        val resultActions = mockMvc.perform(
            put("/api/notes/sync").contentType("application/json")
                .content(
                    """
                        {
                          "syncId": "$externalSyncId",
                          "syncTime": "$externalSyncTime",
                          "modifiedNotes": []
                        }
                    """.trimIndent()
                )
        )

        val responseContent = resultActions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.modifiedNotes", hasSize<Any>(0)))
            .andReturn()
            .response
            .contentAsString
        val syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "\$.syncId"))
        val syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "\$.syncTime"))
        assertThat(syncIdResponse).isEqualTo(notesSync.syncId)
        assertThat(syncTimeResponse).isEqualTo(notesSync.syncTime)
        val notesDb = readNotes()
        assertThat(notesDb).hasSize(1)
        assertThat(notesDb[0].id).isEqualTo(note.id)
        assertThat(notesDb[0].title).isEqualTo(note.title)
        assertThat(notesDb[0].content).isEqualTo(note.content)
        assertThat(notesDb[0].creationTime).isEqualTo(note.creationTime)
        assertThat(notesDb[0].modificationTime).isEqualTo(note.modificationTime)
    }

    @Test
    fun shouldSyncNotesWithEmptyData() {
        val externalSyncId = UUID.fromString("ac50944e-0677-445e-92a6-1b0bfd14f343")
        val externalSyncTime = Instant.parse("2010-10-10T10:10:10Z")
        val notesSync =
            NotesSync(UUID.fromString("f2b7c3e6-cc85-4c3d-b789-f22685035fa9"), Instant.parse("2011-11-11T11:11:11Z"))
        update(notesSync)

        val resultActions = mockMvc.perform(
            put("/api/notes/sync").contentType("application/json")
                .content(
                    """
                        {
                          "syncId": "$externalSyncId",
                          "syncTime": "$externalSyncTime",
                          "modifiedNotes": []
                        }
                    """.trimIndent()
                )
        )

        val responseContent = resultActions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.modifiedNotes", hasSize<Any>(0)))
            .andReturn()
            .response
            .contentAsString
        val syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "\$.syncId"))
        val syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "\$.syncTime"))
        assertThat(syncIdResponse).isEqualTo(notesSync.syncId)
        assertThat(syncTimeResponse).isEqualTo(notesSync.syncTime)
        val notesDb = readNotes()
        assertThat(notesDb).hasSize(0)
    }

    @Test
    fun shouldSyncNotesForSameSyncIds() {
        val notesSync =
            NotesSync(UUID.fromString("5770e671-490b-4331-ac7f-fd22abe92841"), Instant.parse("2011-11-11T11:11:11Z"))
        update(notesSync)

        val resultActions = mockMvc.perform(
            put("/api/notes/sync").contentType("application/json")
                .content(
                    """
                        {
                          "syncId": "${notesSync.syncId}",
                          "syncTime": "${notesSync.syncTime}",
                          "modifiedNotes": []
                        }
                    """.trimIndent()
                )
        )

        val responseContent = resultActions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.modifiedNotes", hasSize<Any>(0)))
            .andReturn()
            .response
            .contentAsString
        val syncIdResponse = UUID.fromString(JsonPath.read(responseContent, "\$.syncId"))
        val syncTimeResponse = Instant.parse(JsonPath.read(responseContent, "\$.syncTime"))
        assertThat(syncIdResponse).isEqualTo(notesSync.syncId)
        assertThat(syncTimeResponse).isEqualTo(notesSync.syncTime)
        val notesDb = readNotes()
        assertThat(notesDb).hasSize(0)
    }

    private fun update(notesSync: NotesSync) {
        jdbcTemplate.update(
            "UPDATE notes_sync SET sync_id = ?, sync_time = ?",
            notesSync.syncId.toString(),
            Timestamp.valueOf(LocalDateTime.ofInstant(notesSync.syncTime, ZoneOffset.UTC))
        )
    }

    private fun createNote(note: Note) {
        jdbcTemplate.update(
            "INSERT INTO note VALUES (?, ?, ?, ?, ?)",
            note.id.toString(),
            note.title,
            note.content,
            Timestamp.valueOf(LocalDateTime.ofInstant(note.creationTime, ZoneOffset.UTC)).toString(),
            Timestamp.valueOf(LocalDateTime.ofInstant(note.modificationTime, ZoneOffset.UTC)).toString()
        )
    }

    private fun createNotes(notes: List<Note>) {
        notes.forEach { createNote(it) }
    }

    private fun readNotes(): List<Note> {
        return jdbcTemplate.query(
            "SELECT * FROM note order by modification_time"
        ) { resultSet, _ ->
            Note(
                UUID.fromString(resultSet.getString("id")),
                resultSet.getString("title"),
                resultSet.getString("content"),
                resultSet.getTimestamp("creation_time").toLocalDateTime().toInstant(ZoneOffset.UTC),
                resultSet.getTimestamp("modification_time").toLocalDateTime().toInstant(ZoneOffset.UTC)
            )
        }
    }

    private fun deleteNotes() {
        jdbcTemplate.update("DELETE FROM note")
    }
}
