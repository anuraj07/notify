package com.deep.notify.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NoteRepositoryImplTest {

    private lateinit var fakeNoteDao: FakeNoteDao
    private lateinit var repository: NoteRepositoryImpl

    @Before
    fun setUp() {
        fakeNoteDao = FakeNoteDao()
        repository = NoteRepositoryImpl(fakeNoteDao)
    }

    @Test
    fun getAllNotes_initiallyEmpty() = runBlocking {
        val notes = repository.getAllNotes().first()
        assertTrue(notes.isEmpty())
    }

    @Test
    fun insertNote_insertsSuccessfully() = runBlocking {
        val note = Note(
            id = 1,
            title = "Title 1",
            content = "Content 1",
            timestamp = 1000L,
            color = 0
        )
        
        repository.insertNote(note)
        
        val retrieved = repository.getNoteById(1)
        assertEquals(note, retrieved)
        
        val allNotes = repository.getAllNotes().first()
        assertEquals(1, allNotes.size)
        assertEquals(note, allNotes[0])
    }

    @Test
    fun deleteNote_removesSuccessfully() = runBlocking {
        val note = Note(
            id = 1,
            title = "Title 1",
            content = "Content 1",
            timestamp = 1000L,
            color = 0
        )
        
        repository.insertNote(note)
        repository.deleteNote(note)
        
        val retrieved = repository.getNoteById(1)
        assertNull(retrieved)
        
        val allNotes = repository.getAllNotes().first()
        assertTrue(allNotes.isEmpty())
    }

    @Test
    fun searchNotes_returnsFilteredList() = runBlocking {
        val note1 = Note(id = 1, title = "Apple", content = "Fruit", timestamp = 1000L, color = 0)
        val note2 = Note(id = 2, title = "Banana", content = "Yellow", timestamp = 2000L, color = 1)
        val note3 = Note(id = 3, title = "Pineapple", content = "Tropical", timestamp = 3000L, color = 2)

        repository.insertNote(note1)
        repository.insertNote(note2)
        repository.insertNote(note3)

        val results = repository.searchNotes("apple").first()
        assertEquals(2, results.size) // Apple and Pineapple both match "apple"
        assertTrue(results.any { it.id == 1 })
        assertTrue(results.any { it.id == 3 })
    }

    @Test
    fun deleteAllNotes_clearsDatabase() = runBlocking {
        val note1 = Note(id = 1, title = "Title 1", content = "Content 1", timestamp = 1000L, color = 0)
        val note2 = Note(id = 2, title = "Title 2", content = "Content 2", timestamp = 2000L, color = 1)
        
        repository.insertNote(note1)
        repository.insertNote(note2)
        
        repository.deleteAllNotes()
        
        val allNotes = repository.getAllNotes().first()
        assertTrue(allNotes.isEmpty())
    }
}

// Mock-free Fake DAO
class FakeNoteDao : NoteDao {
    private val notesList = mutableListOf<Note>()
    private val flow = MutableStateFlow<List<Note>>(emptyList())

    private fun updateFlow() {
        flow.value = notesList.toList()
    }

    override fun getAllNotes(): Flow<List<Note>> = flow

    override suspend fun getNoteById(id: Int): Note? = notesList.find { it.id == id }

    override suspend fun insertNote(note: Note) {
        notesList.removeAll { it.id == note.id }
        notesList.add(note)
        updateFlow()
    }

    override suspend fun deleteNote(note: Note) {
        notesList.removeAll { it.id == note.id }
        updateFlow()
    }

    override fun searchNotes(query: String): Flow<List<Note>> = flow.map { list ->
        list.filter { it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true) }
    }

    override suspend fun deleteAllNotes() {
        notesList.clear()
        updateFlow()
    }
}
