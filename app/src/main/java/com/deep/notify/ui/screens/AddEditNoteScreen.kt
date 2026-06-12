package com.deep.notify.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deep.notify.data.Note
import com.deep.notify.ui.components.ColorPicker
import com.deep.notify.ui.theme.NoteColorDefault
import com.deep.notify.ui.theme.NoteColors
import com.deep.notify.ui.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: NoteViewModel,
    noteId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var contentValue by remember { mutableStateOf(TextFieldValue("")) }
    var selectedColorIndex by remember { mutableStateOf(0) }
    var isPinned by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("General") }
    var initialNote by remember { mutableStateOf<Note?>(null) }
    var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    val context = LocalContext.current

    // Helper function to apply markdown formatting tags at the current cursor / selection
    val applyFormatting = { prefix: String, suffix: String ->
        val text = contentValue.text
        val selection = contentValue.selection
        val start = selection.min
        val end = selection.max
        
        val selectedText = text.substring(start, end)
        val newText = text.substring(0, start) + prefix + selectedText + suffix + text.substring(end)
        
        val newCursorOffset = if (start == end) {
            start + prefix.length
        } else {
            start + prefix.length + selectedText.length + suffix.length
        }
        
        contentValue = TextFieldValue(
            text = newText,
            selection = TextRange(newCursorOffset)
        )
    }

    // Load Note if editing
    LaunchedEffect(noteId) {
        if (noteId != -1) {
            viewModel.getNoteById(noteId)?.let { note ->
                title = note.title
                contentValue = TextFieldValue(note.content)
                selectedColorIndex = note.color
                isPinned = note.isPinned
                category = note.category
                timestamp = note.timestamp
                initialNote = note
            }
        }
    }

    // Determine background color
    val noteColor = NoteColors.getOrElse(selectedColorIndex) { NoteColorDefault }

    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    val categories = listOf("Dev", "Work", "Personal", "Side-Gig")

    // Helper function to save the note
    fun saveNote() {
        val content = contentValue.text
        if (title.isBlank() && content.isBlank()) {
            initialNote?.let { viewModel.deleteNote(it) }
            return
        }

        val noteToSave = Note(
            id = initialNote?.id ?: 0,
            title = title.trim(),
            content = content.trim(),
            timestamp = System.currentTimeMillis(),
            color = selectedColorIndex,
            isPinned = isPinned,
            category = category
        )

        if (noteToSave != initialNote) {
            viewModel.insertNote(noteToSave)
        }
    }

    BackHandler {
        saveNote()
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor) },
                navigationIcon = {
                    IconButton(onClick = {
                        saveNote()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    // Pin Button
                    IconButton(onClick = { isPinned = !isPinned }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = if (isPinned) "Unpin Note" else "Pin Note",
                            tint = if (isPinned) Color(0xFFFBBF24) else textColor
                        )
                    }
                    
                    // Done Button
                    IconButton(onClick = {
                        saveNote()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done/Save",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = noteColor)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(noteColor)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding()
            ) {
                // Formatting toolbar shortcuts (Staggered contextual shortcuts row)
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(vertical = 4.dp)
                ) {
                    IconButton(onClick = { applyFormatting("**", "**") }) {
                        Icon(imageVector = Icons.Default.FormatBold, contentDescription = "Bold", tint = secondaryTextColor)
                    }
                    IconButton(onClick = { applyFormatting("*", "*") }) {
                        Icon(imageVector = Icons.Default.FormatItalic, contentDescription = "Italic", tint = secondaryTextColor)
                    }
                    IconButton(onClick = {
                        val start = contentValue.selection.min
                        val prefix = if (start == 0 || contentValue.text.getOrNull(start - 1) == '\n') "- " else "\n- "
                        applyFormatting(prefix, "")
                    }) {
                        Icon(imageVector = Icons.Default.FormatListBulleted, contentDescription = "Bulleted List", tint = secondaryTextColor)
                    }
                    IconButton(onClick = { applyFormatting("![", "](https://)") }) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Insert Image", tint = secondaryTextColor)
                    }
                    IconButton(onClick = {
                        val selection = contentValue.selection
                        val selectedText = contentValue.text.substring(selection.min, selection.max)
                        val prefix = if (selectedText.contains("\n")) "\n```\n" else "`"
                        val suffix = if (selectedText.contains("\n")) "\n```\n" else "`"
                        applyFormatting(prefix, suffix)
                    }) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = "Insert Code Block", tint = secondaryTextColor)
                    }
                }

                // Background Color Picker
                Text(
                    text = "Background Color",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ColorPicker(
                    selectedColorIndex = selectedColorIndex,
                    onColorSelected = { selectedColorIndex = it }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(noteColor)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            // Note Title Input
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Title", style = MaterialTheme.typography.headlineMedium, color = secondaryTextColor.copy(alpha = 0.4f)) },
                textStyle = MaterialTheme.typography.headlineMedium.copy(color = textColor, fontWeight = FontWeight.Bold),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            // Stitch Category Chip Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = secondaryTextColor,
                    modifier = Modifier.size(18.dp)
                )
                categories.forEach { cat ->
                    val isSelected = category == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { category = if (isSelected) "General" else cat },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            containerColor = Color.Transparent,
                            labelColor = secondaryTextColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = secondaryTextColor.copy(alpha = 0.3f),
                            selectedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // Note Content Input
            TextField(
                value = contentValue,
                onValueChange = { contentValue = it },
                placeholder = { Text("Start typing your note...", style = MaterialTheme.typography.bodyLarge, color = secondaryTextColor.copy(alpha = 0.4f)) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                val lastEditedStr = dateFormat.format(Date(timestamp))
                val content = contentValue.text
                val wordCount = if (content.isBlank()) 0 else content.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, tint = secondaryTextColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Edited $lastEditedStr", style = MaterialTheme.typography.labelMedium, color = secondaryTextColor, fontFamily = FontFamily.Monospace)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Article, contentDescription = null, tint = secondaryTextColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$wordCount words", style = MaterialTheme.typography.labelMedium, color = secondaryTextColor, fontFamily = FontFamily.Monospace)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Share Button
                    TextButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "$title\n\n$content")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp)
                    }

                    // Delete Button
                    if (noteId != -1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                initialNote?.let { viewModel.deleteNote(it) }
                                onBack()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
