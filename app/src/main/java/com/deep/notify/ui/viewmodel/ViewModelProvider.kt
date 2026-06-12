package com.deep.notify.ui.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.deep.notify.NotifyApplication

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application = (this[APPLICATION_KEY] as NotifyApplication)
            NoteViewModel(
                noteRepository = application.container.noteRepository,
                themePreferences = application.container.themePreferences
            )
        }
    }
}
