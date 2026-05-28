package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Booking
import com.example.ui.viewmodel.CricketViewModel

@Composable
fun BookingsTab(viewModel: CricketViewModel) {
    val bookings by viewModel.bookings.collectAsState()

    var showCompleteDialog by remember { mutableStateOf<Booking?>(null) }
    var simNotes by remember { mutableStateOf("") }
    var simGrade by remember { mutableStateOf("A") }

    val upcomingBookings = bookings.filter { it.status == "Upcoming" }
    val completedBookings = bookings.filter { it.status == "Completed" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportColors.DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TabSportyHeader(
                title = "My Booking",
                subtitle = "Track upcoming net practices, sessions, and training feedback logs.",
                viewModel = viewModel,
                showProfileRow = false
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Split 1: Upcoming Sessions
                Text(
                    text = "Upcoming Live Batches (${upcomingBookings.size})",
                    color = SportColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

            if (upcomingBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SportColors.SoftCardBg)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No active upcoming bookings.",
                            color = SportColors.TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            "Book dynamic sessions inside the Home tab!",
                            color = SportColors.GlowBlueAccent,
                            fontSize = 10.sp
                        )
                    }
                }
            } else {
                upcomingBookings.forEach { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (booking.coachImageUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = parseSportImageUrl(booking.coachImageUrl, androidx.compose.ui.platform.LocalContext.current),
                                            contentDescription = booking.coachName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        CoachAvatarIllustration(
                                            modifier = Modifier.size(34.dp),
                                            coachName = booking.coachName,
                                            primaryColor = SportColors.ActiveBlue
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            booking.coachName,
                                            color = SportColors.TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            booking.coachSkills,
                                            color = SportColors.TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Badge(containerColor = SportColors.ActiveBlue) {
                                    Text("Confirmed", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = SportColors.GlowBlueAccent, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(booking.date, color = SportColors.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = SportColors.GlowBlueAccent, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(booking.timeSlot, color = SportColors.TextPrimary, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Notes: ${booking.sessionNotes}",
                                color = SportColors.TextSecondary,
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Cancel Button
                                OutlinedButton(
                                    onClick = { viewModel.cancelSessionBooking(booking.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                    border = BorderStroke(1.dp, Color.Red),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Cancel Ticket", fontSize = 11.sp)
                                }

                                // Interactive Complete simulator button (to generate rich reports card)
                                GradientButton(
                                    onClick = { showCompleteDialog = booking },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Complete Session ✓", fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Split 2: Completed / Feedback Logs
            Text(
                text = "Past Feedback Reports (${completedBookings.size})",
                color = SportColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (completedBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SportColors.SoftCardBg)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No completed lessons match this history. Complete some pending slots above to see reports generated by coaches!",
                        color = SportColors.TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                completedBookings.forEach { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (booking.coachImageUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = parseSportImageUrl(booking.coachImageUrl, androidx.compose.ui.platform.LocalContext.current),
                                            contentDescription = booking.coachName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        CoachAvatarIllustration(
                                            modifier = Modifier.size(34.dp),
                                            coachName = booking.coachName,
                                            primaryColor = SportColors.SportGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            booking.coachName,
                                            color = SportColors.TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            booking.date,
                                            color = SportColors.TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                // Performance grade badge
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SportColors.BrightOrange),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = booking.feedbackGrade,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                "Coach Feedback Diagnosis Report:",
                                color = SportColors.GlowBlueAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                booking.feedbackReport,
                                color = SportColors.TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(110.dp))
            }
        }

        // Complete Session simulation notes dialog overlay
        if (showCompleteDialog != null) {
            val bookingToComplete = showCompleteDialog!!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { showCompleteDialog = null },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable(enabled = false) {},
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Simulate Coach Feedback Check",
                            color = SportColors.TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Write diagnostic notes as a Coach to verify AI recommendations workflows.",
                            color = SportColors.TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                        )

                        Text("Performance Rating Grade", color = SportColors.TextPrimary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("S", "A", "B+", "B", "C").forEach { grade ->
                                val isSelected = simGrade == grade
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SportColors.ActiveBlue else SportColors.DarkBackground)
                                        .clickable { simGrade = grade }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(grade, color = if (isSelected) Color.White else SportColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = simNotes,
                            onValueChange = { simNotes = it },
                            label = { Text("Coach Improvement Notes & Feedback", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Great front-foot swing, but your backlift coordination was slightly early.") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportColors.ActiveBlue,
                                focusedTextColor = SportColors.TextPrimary,
                                unfocusedTextColor = SportColors.TextPrimary,
                                focusedLabelColor = SportColors.ActiveBlue,
                                unfocusedLabelColor = SportColors.TextSecondary
                            ),
                            minLines = 3,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        GradientButton(
                            onClick = {
                                val finalNotes = if (simNotes.isBlank()) "Great balance throughout the strokes alignment." else simNotes
                                viewModel.simulateCompletionFlow(bookingToComplete, finalNotes, simGrade)
                                showCompleteDialog = null
                                simNotes = ""
                                simGrade = "A"
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Confirm & Log Diagnostics ✍️", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
