package com.deep.notify.di

import android.content.Context
import com.deep.notify.data.NoteDatabase
import com.deep.notify.data.NoteRepository
import com.deep.notify.data.NoteRepositoryImpl
import com.deep.notify.data.ThemePreferences

interface AppContainer {
    val noteRepository: NoteRepository
    val themePreferences: ThemePreferences
}

class AppContainerImpl(private val context: Context) : AppContainer {
    override val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(NoteDatabase.getDatabase(context).noteDao())
    }
    override val themePreferences: ThemePreferences by lazy {
        ThemePreferences(context)
    }
}
