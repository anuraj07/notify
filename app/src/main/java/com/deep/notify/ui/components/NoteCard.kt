package com.deep.notify.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deep.notify.data.Note
import com.deep.notify.ui.theme.NoteColorDefault
import com.deep.notify.ui.theme.NoteColors
import com.deep.notify.ui.theme.parseMarkdown
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val noteColor = NoteColors.getOrElse(note.color) { NoteColorDefault }

    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(note.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = noteColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (note.title.isNotEmpty()) {
                    Text(
                        text = note.title,
                        color = textColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Category badge in the top-right
                if (note.category.isNotEmpty() && note.category != "General") {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = note.category,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (note.title.isNotEmpty() && note.content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (note.content.isNotEmpty()) {
                Text(
                    text = parseMarkdown(note.content, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.outline),
                    color = secondaryTextColor,
                    fontSize = 14.sp,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dateString.uppercase(Locale.ROOT),
                    color = secondaryTextColor.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                if (note.isPinned) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Pinned",
                        tint = Color(0xFFFBBF24), // Amber/Yellow star
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
