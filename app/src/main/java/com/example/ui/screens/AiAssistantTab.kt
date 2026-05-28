package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.CricketViewModel
import kotlinx.coroutines.launch

@Composable
fun AiAssistantTab(viewModel: CricketViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val generating by viewModel.aiGenerating.collectAsState()
    var inputQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to bottom when generating or new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val actionSuggestions = listOf(
        "Suggest Drills" to "Recommend 15-minute cover drive drills.",
        "Diet Plan" to "Give me a cricket seamer energy breakfast diet.",
        "Stance Setup" to "How can I improve my high backlift stance?"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportColors.DarkBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Stylized Sports Header with AI Assistant Mode
            TabSportyHeader(
                title = "AI Cricket Coach",
                subtitle = "Smart athletic companion & match tactician.",
                viewModel = viewModel,
                showProfileRow = false
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { viewModel.clearChat() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Clear Chat",
                            tint = Color.White
                        )
                    }
                }
            }

            // Chat Messages Scroll Area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages) { msg ->
                    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
                    val bubbleColor = if (msg.isUser) SportColors.ActiveBlue else SportColors.SoftCardBg

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalAlignment = alignment
                    ) {
                        Row(
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            if (!msg.isUser) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(SportColors.BrightOrange),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                            bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                        )
                                    )
                                    .background(bubbleColor)
                                    .border(1.dp, SportColors.CardBorder, RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                        bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                    ))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (msg.isUser) Color.White else SportColors.TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Generating animation indicator
                if (generating) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(SportColors.BrightOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SportColors.CardBorder)
                            ) {
                                Text(
                                    "AI is analyzing your stance parameters...",
                                    color = SportColors.TextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Input panel block with suggestions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Predefined Suggestion action chips
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    actionSuggestions.forEach { (label, prompt) ->
                        Box(
                             modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SportColors.SoftCardBg)
                                .clickable {
                                    viewModel.sendChatMessage(prompt)
                                }
                                .border(1.dp, SportColors.CardBorder, RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(label, color = SportColors.TextPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Text outliners
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 90.dp), // Clear bottom tabs
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("Ask your AI coach helper...", fontSize = 12.sp, color = SportColors.TextSecondary) },
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(24.dp))
                            .testTag("username_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SportColors.TextPrimary,
                            unfocusedTextColor = SportColors.TextPrimary,
                            focusedBorderColor = SportColors.ActiveBlue,
                            unfocusedBorderColor = SportColors.CardBorder,
                            focusedContainerColor = SportColors.SoftCardBg,
                            unfocusedContainerColor = SportColors.SoftCardBg,
                        ),
                        singleLine = true
                    )

                    IconButton(
                        onClick = {
                            if (inputQuery.isNotBlank()) {
                                viewModel.sendChatMessage(inputQuery)
                                inputQuery = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SportColors.SportGreen)
                            .testTag("submit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
