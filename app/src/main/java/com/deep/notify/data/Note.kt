package com.deep.notify.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int, // Hex integer value of the background color
    val isPinned: Boolean = false,
    val category: String = "General"
)
