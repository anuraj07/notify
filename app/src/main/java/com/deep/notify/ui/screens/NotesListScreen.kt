package com.deep.notify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deep.notify.data.Note
import com.deep.notify.ui.components.NoteCard
import com.deep.notify.ui.components.SearchBar
import com.deep.notify.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(
    viewModel: NoteViewModel,
    onNoteClick: (Int) -> Unit,
    onAddNoteClick: () -> Unit,
    onNavigateToBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notes by viewModel.notesList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isStaggered by viewModel.isStaggeredLayout.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var activeNoteForOptions by remember { mutableStateOf<Note?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Drawer Title / Brand header
                Text(
                    text = "Notify Pro",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Drawer Items
                NavigationDrawerItem(
                    label = { Text("All Notes", fontWeight = FontWeight.Bold) },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(imageVector = Icons.Default.Notes, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Folders") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        Toast.makeText(context, "Folders features coming soon!", Toast.LENGTH_SHORT).show()
                    },
                    icon = { Icon(imageVector = Icons.Default.Folder, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Backups") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToBackup()
                    },
                    icon = { Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Help") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        Toast.makeText(context, "Help documents under construction.", Toast.LENGTH_SHORT).show()
                    },
                    icon = { Icon(imageVector = Icons.Default.Help, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                // Large FAB Shape matching Stitch mockups (Rounded 24.dp soft-square pill shape)
                FloatingActionButton(
                    onClick = onAddNoteClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .size(64.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Note",
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Stitch Top Navigation & Search Bar (Custom Search Dock)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp)
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = "Search your notes",
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent)
                    )

                    IconButton(onClick = { viewModel.toggleLayout() }) {
                        Icon(
                            imageVector = if (isStaggered) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle Layout",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Scrollable Feed Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Welcome Hero Card Section
                    if (searchQuery.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Text(
                                    text = "Keep your thoughts organized",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Capture ideas, snippets, and plans in a minimal developer-centric workspace.",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Notes Content
                    if (notes.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "No matching notes found" else "Your thoughts are empty",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (searchQuery.isNotEmpty()) "Try searching for something else" else "Tap the '+' button at the bottom to write your first note.",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        if (isStaggered) {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(2),
                                verticalItemSpacing = 12.dp,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(notes, key = { it.id }) { note ->
                                    NoteCard(
                                        note = note,
                                        onClick = { onNoteClick(note.id) },
                                        onLongClick = { activeNoteForOptions = note }
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(notes, key = { it.id }) { note ->
                                    NoteCard(
                                        note = note,
                                        onClick = { onNoteClick(note.id) },
                                        onLongClick = { activeNoteForOptions = note }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Long Press Options Dialog
    activeNoteForOptions?.let { note ->
        AlertDialog(
            onDismissRequest = { activeNoteForOptions = null },
            title = { Text(text = "Note Options", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Choose an action for: \"${if (note.title.isNotEmpty()) note.title else "Untitled note"}\"") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.togglePin(note)
                        activeNoteForOptions = null
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (note.isPinned) "Unpin" else "Pin")
                    }
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            viewModel.deleteNote(note)
                            activeNoteForOptions = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                    TextButton(onClick = { activeNoteForOptions = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
