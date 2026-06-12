package com.deep.notify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deep.notify.ui.screens.AddEditNoteScreen
import com.deep.notify.ui.screens.BackupRestoreScreen
import com.deep.notify.ui.screens.NotesListScreen
import com.deep.notify.ui.theme.NotifyTheme
import com.deep.notify.ui.viewmodel.NoteViewModel
import com.deep.notify.ui.viewmodel.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NoteViewModel = viewModel(factory = ViewModelProvider.Factory)

            NotifyTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "notes_list",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("notes_list") {
                            NotesListScreen(
                                viewModel = viewModel,
                                onNoteClick = { noteId ->
                                    navController.navigate("add_edit_note/$noteId")
                                },
                                onAddNoteClick = {
                                    navController.navigate("add_edit_note/-1")
                                },
                                onNavigateToBackup = {
                                    navController.navigate("backup_restore")
                                }
                            )
                        }

                        composable(
                            route = "add_edit_note/{noteId}",
                            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                            AddEditNoteScreen(
                                viewModel = viewModel,
                                noteId = noteId,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("backup_restore") {
                            BackupRestoreScreen(
                                viewModel = viewModel,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}