package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Coach
import com.example.ui.viewmodel.CricketViewModel

enum class MainTab {
    HOME, BOOKINGS, AI_COACH, CRICTOK, GINNMART
}

@Composable
fun DashboardScreen(
    viewModel: CricketViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }
    val selectedCoach by viewModel.selectedCoach.collectAsState()

    var showGroupSessionToastAlert by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportColors.DarkBackground)
    ) {
        // Main view switcher
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    MainTab.HOME -> HomeTab(
                        viewModel = viewModel,
                        onCoachClick = { coach -> viewModel.selectCoach(coach) },
                        onGroupSessionClick = { showGroupSessionToastAlert = true }
                    )
                    MainTab.BOOKINGS -> BookingsTab(viewModel = viewModel)
                    MainTab.AI_COACH -> AiAssistantTab(viewModel = viewModel)
                    MainTab.CRICTOK -> CricTokTab(viewModel = viewModel)
                    MainTab.GINNMART -> GinnMartTab(viewModel = viewModel)
                }
            }
        }

        // Custom Bottom Navigation Bar matching the image parameters
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding() // Notch and Gesture safe area rule
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E2644).copy(alpha = 0.95f))
                .padding(vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = MainTab.HOME }
                        .testTag("submit_button")
                        .padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (selectedTab == MainTab.HOME) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Home",
                        color = if (selectedTab == MainTab.HOME) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tab 2: My Booking
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = MainTab.BOOKINGS }
                        .padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "My Booking",
                        tint = if (selectedTab == MainTab.BOOKINGS) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        "My Booking",
                        color = if (selectedTab == MainTab.BOOKINGS) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tab 3: Central floating AI Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(y = (-14).dp)
                        .clickable { selectedTab = MainTab.AI_COACH }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(SportColors.ActiveBlue, SportColors.GlowBlueAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Assistant",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        "AI Assistant",
                        color = if (selectedTab == MainTab.AI_COACH) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y = 4.dp)
                    )
                }

                // Tab 4: CricTok
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = MainTab.CRICTOK }
                        .padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OndemandVideo,
                        contentDescription = "CricTok",
                        tint = if (selectedTab == MainTab.CRICTOK) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "CricTok",
                        color = if (selectedTab == MainTab.CRICTOK) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tab 5: GinnMart Store (labeled Shopping Cart per screenshot image)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = MainTab.GINNMART }
                        .padding(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "GinnMart Store",
                        tint = if (selectedTab == MainTab.GINNMART) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "GinnMart",
                        color = if (selectedTab == MainTab.GINNMART) SportColors.GlowBlueAccent else Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Overlay 1: Coach Details Profile View Panel
        AnimatedVisibility(
            visible = selectedCoach != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            if (selectedCoach != null) {
                CoachDetailScreen(
                    coach = selectedCoach!!,
                    viewModel = viewModel,
                    onBackClick = { viewModel.selectCoach(null) },
                    onBookingSuccess = {
                        viewModel.selectCoach(null)
                        selectedTab = MainTab.BOOKINGS
                        Toast.makeText(context, "Session Reserved Successfully! 🏏", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        // Group session reservation alert dialog simulator
        if (showGroupSessionToastAlert) {
            AlertDialog(
                onDismissRequest = { showGroupSessionToastAlert = false },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen),
                        onClick = {
                            // Automatically insert group session booking to Room
                            val groupCoach = Coach(
                                id = "group_vikas",
                                name = "Vikas SD",
                                imageUrl = "",
                                skills = "Batting Techniques",
                                rating = 4.8f,
                                reviewsCount = 142,
                                isVerified = true,
                                bio = "Group setup",
                                experienceYears = 12,
                                certifications = "Level 2",
                                sessionPrice = 500.0,
                                location = "Vakola Maidan, Mumbai",
                                availableDays = "Sat"
                            )
                            viewModel.bookSession(groupCoach, "2026-05-24", "05:00 PM - 07:00 PM")
                            showGroupSessionToastAlert = false
                            selectedTab = MainTab.BOOKINGS
                            Toast.makeText(context, "Group Registration Successful! ₹500 reserved.", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        Text("Pay ₹500 & Register")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showGroupSessionToastAlert = false }) {
                        Text("Dismiss")
                    }
                },
                title = { Text("Group Session Booking") },
                text = {
                    Text(
                        "Would you like to register for the 'Batting Techniques: Shot selection' workshop conducted by Coach Vikas SD? \nLocation: Vakola Santacruz East.\n Price: ₹500."
                    )
                },
                containerColor = SportColors.SoftCardBg,
                titleContentColor = Color.White,
                textContentColor = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
