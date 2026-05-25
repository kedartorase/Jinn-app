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
import androidx.compose.foundation.border
import com.example.data.Coach
import com.example.R
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

        // Custom Bottom Navigation Bar exactly matching the mockup screenshot
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // 1. Pristine white bottom bar background container
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(Color.White)
                    .border(width = 1.dp, color = Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Home
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = MainTab.HOME }
                            .testTag("submit_button")
                            .padding(vertical = 4.dp)
                    ) {
                        HomeIcon(
                            tint = if (selectedTab == MainTab.HOME) Color(0xFF0F1E4C) else Color(0xFF8A93A6),
                            filled = selectedTab == MainTab.HOME,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Home",
                            color = if (selectedTab == MainTab.HOME) Color(0xFF0F1E4C) else Color(0xFF8A93A6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tab 2: My Booking
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = MainTab.BOOKINGS }
                            .padding(vertical = 4.dp)
                    ) {
                        CalendarIcon(
                            tint = if (selectedTab == MainTab.BOOKINGS) Color(0xFF0F1E4C) else Color(0xFF8A93A6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "My Booking",
                            color = if (selectedTab == MainTab.BOOKINGS) Color(0xFF0F1E4C) else Color(0xFF8A93A6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Central tab empty placeholder spacer
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .size(68.dp)
                    )

                    // Tab 4: CricTok
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = MainTab.CRICTOK }
                            .padding(vertical = 4.dp)
                    ) {
                        CricTokIcon(
                            tint = if (selectedTab == MainTab.CRICTOK) Color(0xFF0F1E4C) else Color(0xFF8A93A6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "CricTok",
                            color = if (selectedTab == MainTab.CRICTOK) Color(0xFF0F1E4C) else Color(0xFF8A93A6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tab 5: JinnMart
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = MainTab.GINNMART }
                            .padding(vertical = 4.dp)
                    ) {
                        ShoppingCartIcon(
                            tint = if (selectedTab == MainTab.GINNMART) Color(0xFF0F1E4C) else Color(0xFF8A93A6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "JinnMart",
                            color = if (selectedTab == MainTab.GINNMART) Color(0xFF0F1E4C) else Color(0xFF8A93A6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. The central elevated/protruding AI button overlapping on top with white border container
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-18).dp)
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SportColors.PrimaryGradient)
                        .clickable { selectedTab = MainTab.AI_COACH },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(36.dp, 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // The custom rounded Ai bubble box
                        Box(
                            modifier = Modifier
                                .size(28.dp, 24.dp)
                                .border(2.dp, Color.White, RoundedCornerShape(6.dp))
                                .align(Alignment.BottomStart),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ai",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.offset(y = (-1).dp)
                            )
                        }
                        
                        // Overlapping custom 4-point sparkle star
                        CustomSparkleStar(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                        )
                    }
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
                    GradientButton(
                        onClick = {
                            // Automatically insert group session booking to Room
                            val groupCoach = Coach(
                                id = "group_vikas",
                                name = "Vikas SD",
                                imageUrl = "android.resource://com.example/" + R.drawable.img_coach_3,
                                skills = "Batting Techniques",
                                rating = 4.9f,
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

@Composable
fun CustomSparkleStar(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(10.dp)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width / 2, 0f)
            quadraticTo(size.width / 2, size.height / 2, size.width, size.height / 2)
            quadraticTo(size.width / 2, size.height / 2, size.width / 2, size.height)
            quadraticTo(size.width / 2, size.height / 2, 0f, size.height / 2)
            quadraticTo(size.width / 2, size.height / 2, size.width / 2, 0f)
            close()
        }
        drawPath(path, color = Color.White)
    }
}

@Composable
fun HomeIcon(tint: Color, filled: Boolean, modifier: Modifier = Modifier) {
    if (filled) {
        androidx.compose.foundation.Canvas(modifier = modifier.size(24.dp)) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(12.dp.toPx(), 3.dp.toPx())
                lineTo(3.dp.toPx(), 11.dp.toPx())
                lineTo(3.dp.toPx(), 21.dp.toPx())
                lineTo(10.dp.toPx(), 21.dp.toPx())
                lineTo(10.dp.toPx(), 15.dp.toPx())
                lineTo(14.dp.toPx(), 15.dp.toPx())
                lineTo(14.dp.toPx(), 21.dp.toPx())
                lineTo(21.dp.toPx(), 21.dp.toPx())
                lineTo(21.dp.toPx(), 11.dp.toPx())
                close()
            }
            drawPath(path, color = tint)
        }
    } else {
        androidx.compose.foundation.Canvas(modifier = modifier.size(24.dp)) {
            val strokeWidth = 1.6.dp.toPx()
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(12.dp.toPx(), 4.dp.toPx())
                lineTo(4.dp.toPx(), 11.dp.toPx())
                lineTo(4.dp.toPx(), 20.dp.toPx())
                lineTo(20.dp.toPx(), 20.dp.toPx())
                lineTo(20.dp.toPx(), 11.dp.toPx())
                close()
            }
            drawPath(
                path = path,
                color = tint,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(12.dp.toPx(), 15.dp.toPx()),
                end = androidx.compose.ui.geometry.Offset(12.dp.toPx(), 20.dp.toPx()),
                strokeWidth = strokeWidth
            )
        }
    }
}

@Composable
fun CalendarIcon(tint: Color, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 1.6.dp.toPx()
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(0f, 4.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(size.width, size.height - 4.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(6.dp.toPx(), 0f),
            end = androidx.compose.ui.geometry.Offset(6.dp.toPx(), 5.dp.toPx()),
            strokeWidth = strokeWidth * 1.5f
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(size.width - 6.dp.toPx(), 0f),
            end = androidx.compose.ui.geometry.Offset(size.width - 6.dp.toPx(), 5.dp.toPx()),
            strokeWidth = strokeWidth * 1.5f
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(0f, 9.dp.toPx()),
            end = androidx.compose.ui.geometry.Offset(size.width, 9.dp.toPx()),
            strokeWidth = strokeWidth / 2
        )
        val dotRadius = 1.dp.toPx()
        val startX = 6.dp.toPx()
        val startY = 13.dp.toPx()
        val gapX = 6.dp.toPx()
        val gapY = 4.dp.toPx()
        for (row in 0..1) {
            for (col in 0..2) {
                drawCircle(
                    color = tint,
                    radius = dotRadius,
                    center = androidx.compose.ui.geometry.Offset(startX + col * gapX, startY + row * gapY)
                )
            }
        }
    }
}

@Composable
fun CricTokIcon(tint: Color, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 1.6.dp.toPx()
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(0f, 4.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(size.width, size.height - 4.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(0f, 9.dp.toPx()),
            end = androidx.compose.ui.geometry.Offset(size.width, 9.dp.toPx()),
            strokeWidth = strokeWidth
        )
        val slashes = listOf(5.dp, 10.dp, 15.dp, 20.dp)
        slashes.forEach { offset ->
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(offset.toPx(), 4.dp.toPx()),
                end = androidx.compose.ui.geometry.Offset((offset - 4.dp).toPx(), 9.dp.toPx()),
                strokeWidth = strokeWidth
            )
        }
        val path = androidx.compose.ui.graphics.Path().apply {
            val cx = size.width / 2f
            val cy = size.height / 2f + 2.dp.toPx()
            moveTo(cx - 3.dp.toPx(), cy - 4.dp.toPx())
            lineTo(cx + 4.dp.toPx(), cy)
            lineTo(cx - 3.dp.toPx(), cy + 4.dp.toPx())
            close()
        }
        drawPath(path, color = tint)
    }
}

@Composable
fun ShoppingCartIcon(tint: Color, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 1.6.dp.toPx()
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(2.dp.toPx(), 4.dp.toPx())
            lineTo(5.dp.toPx(), 4.dp.toPx())
            lineTo(8.dp.toPx(), 13.dp.toPx())
            lineTo(18.dp.toPx(), 13.dp.toPx())
            lineTo(20.5.dp.toPx(), 6.dp.toPx())
            lineTo(7.dp.toPx(), 6.dp.toPx())
        }
        drawPath(
            path = path,
            color = tint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                join = androidx.compose.ui.graphics.StrokeJoin.Round,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
        drawCircle(
            color = tint,
            radius = 1.8.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(9.dp.toPx(), 17.dp.toPx())
        )
        drawCircle(
            color = tint,
            radius = 1.8.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(17.dp.toPx(), 17.dp.toPx())
        )
    }
}
