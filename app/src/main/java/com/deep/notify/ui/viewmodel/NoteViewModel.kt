package com.deep.notify.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deep.notify.data.Note
import com.deep.notify.data.NoteRepository
import com.deep.notify.data.ThemePreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isStaggeredLayout = MutableStateFlow(themePreferences.isStaggeredLayout())
    val isStaggeredLayout: StateFlow<Boolean> = _isStaggeredLayout.asStateFlow()

    private val _lastBackupTime = MutableStateFlow(themePreferences.getLastBackupTime())
    val lastBackupTime: StateFlow<Long> = _lastBackupTime.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val notesList: StateFlow<List<Note>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                noteRepository.getAllNotes()
            } else {
                noteRepository.searchNotes(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleLayout() {
        val newLayout = !_isStaggeredLayout.value
        themePreferences.setStaggeredLayout(newLayout)
        _isStaggeredLayout.value = newLayout
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            noteRepository.insertNote(note.copy(isPinned = !note.isPinned))
        }
    }

    suspend fun getNoteById(id: Int): Note? {
        return noteRepository.getNoteById(id)
    }

    // Export Notes to JSON
    fun exportBackup(context: Context, uri: Uri, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val notes = noteRepository.getAllNotes().first()
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val gson = Gson()
                    val jsonString = gson.toJson(notes)
                    outputStream.write(jsonString.toByteArray())
                }
                updateLastBackupTime()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun updateLastBackupTime() {
        val now = System.currentTimeMillis()
        themePreferences.setLastBackupTime(now)
        _lastBackupTime.value = now
    }

    // Import Notes from JSON
    fun importBackup(context: Context, uri: Uri, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val jsonString = reader.readText()
                    val gson = Gson()
                    val listType = object : TypeToken<List<Note>>() {}.type
                    val notes: List<Note> = gson.fromJson(jsonString, listType)
                    
                    notes.forEach { note ->
                        noteRepository.insertNote(note)
                    }
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun deleteAllNotes(onSuccess: () -> Unit) {
        viewModelScope.launch {
            noteRepository.deleteAllNotes()
            onSuccess()
        }
    }
}
