package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CricTokClip
import com.example.ui.viewmodel.CricketViewModel

@Composable
fun CricTokTab(viewModel: CricketViewModel) {
    val context = LocalContext.current
    val clips by viewModel.cricClips.collectAsState()
    val likedIds by viewModel.likedClipIds.collectAsState()
    val savedIds by viewModel.savedClipIds.collectAsState()

    var activeCategory by remember { mutableStateOf<String?>(null) }
    val displayedClips = remember(activeCategory, clips) {
        if (activeCategory == null) clips else clips.filter { it.clipType.equals(activeCategory, ignoreCase = true) }
    }

    var showCommentsForClip by remember { mutableStateOf<CricTokClip?>(null) }
    var mockCommentText by remember { mutableStateOf("") }
    val mockCommentsList = remember {
        mutableStateListOf(
            "Wow perfectly helpful footwork correction! 🏏",
            "I tried this shadow drill, my shoulder control was much better.",
            "Can you explain the wrist snap mechanics for Rajesh spinner spin?",
            "Vikas SD is a master bat coach!"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportColors.DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "CricTok Shorts",
                color = SportColors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Text(
                text = "Swipe to learn: Elite coaching tips and demos in under 60s.",
                color = SportColors.TextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 12.dp)
            )

            // Category Selector Rows on Top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(null, "Batting", "Bowling", "Fielding").forEach { category ->
                    val isSelected = activeCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) SportColors.ActiveBlue else SportColors.SoftCardBg
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else SportColors.CardBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { activeCategory = category }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category ?: "All clips",
                            color = if (isSelected) Color.White else SportColors.TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vertical scrollable reel feed
            if (displayedClips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No coaching clips in this category.", color = SportColors.TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 85.dp) // Clearance for bottom navigation bar
                ) {
                    itemsIndexed(displayedClips) { index, clip ->
                        val isLiked = likedIds.contains(clip.id)
                        val isSaved = savedIds.contains(clip.id)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                                .padding(horizontal = 18.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(clip.thumbnailGradientStart),
                                                Color(clip.thumbnailGradientEnd)
                                            )
                                        )
                                    )
                            ) {
                                // Background subtle lines simulating video graphics
                                Canvas(modifier = Modifier.matchParentSize()) {
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.1f),
                                        start = androidx.compose.ui.geometry.Offset(0f, size.height * 0.4f),
                                        end = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.45f),
                                        strokeWidth = 3f
                                    )
                                    drawCircle(
                                        color = SportColors.GoldYellow.copy(alpha = 0.06f),
                                        radius = size.width / 4,
                                        center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                                    )
                                }

                                // Play icon overlays in center
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Simulated Playback",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                // Right Sidebar Controls overlay
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(bottom = 40.dp, end = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Like indicator
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        IconButton(
                                            onClick = { viewModel.toggleLikeClip(clip.id) },
                                            modifier = Modifier
                                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                                .size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Like",
                                                tint = if (isLiked) Color.Red else Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text = formatCounts(clip.likes),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Comment triggers
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        IconButton(
                                            onClick = { showCommentsForClip = clip },
                                            modifier = Modifier
                                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                                .size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Comment,
                                                contentDescription = "Comments",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text = clip.comments.toString(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Share button
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Lesson shared! 🔗", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                            .size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Bookmark save button
                                    IconButton(
                                        onClick = { viewModel.toggleSaveClip(clip.id) },
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                            .size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (isSaved) SportColors.GoldYellow else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                // Bottom Details and descriptions
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth(0.75f)
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(SportColors.BrightOrange),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "@${clip.coachName}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Badge(containerColor = SportColors.ActiveBlue) {
                                            Text(clip.clipType, color = Color.White, fontSize = 8.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = clip.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = clip.desc,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 15.sp
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Progress seekbar simulator
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { 0.42f },
                                            modifier = Modifier.weight(1f).height(3.dp),
                                            color = SportColors.GlowBlueAccent,
                                            trackColor = Color.White.copy(alpha = 0.2f),
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = clip.durationText,
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Commentary Sheets/Drawer overlay
        if (showCommentsForClip != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showCommentsForClip = null }
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Video Discussion (${mockCommentsList.size})",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showCommentsForClip = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                        // Scrollable list
                        Column(
                            modifier = Modifier
                                .height(200.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            mockCommentsList.forEach { comment ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .padding(10.dp)
                                    ) {
                                        Text(comment, color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add comment input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = mockCommentText,
                                onValueChange = { mockCommentText = it },
                                placeholder = { Text("Write a response...", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SportColors.GlowBlueAccent
                                ),
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    if (mockCommentText.isNotBlank()) {
                                        mockCommentsList.add(mockCommentText)
                                        mockCommentText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SportColors.ActiveBlue)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatCounts(likes: Int): String {
    return if (likes >= 1000) {
        "${"%.1f".format(likes / 1000f)}k"
    } else {
        likes.toString()
    }
}
