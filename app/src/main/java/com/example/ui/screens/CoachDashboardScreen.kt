package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Booking
import com.example.data.GroupSession
import com.example.data.CricketGround
import com.example.ui.viewmodel.CricketViewModel
import kotlinx.coroutines.launch

enum class CoachTab {
    DASHBOARD, BOOKINGS_MGR, AVAILABILITY, GROUNDS, PROFILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachDashboardScreen(
    viewModel: CricketViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentCoachTab by remember { mutableStateOf(CoachTab.DASHBOARD) }

    // Read current user profile which represents the logged in coach
    val profile by viewModel.userProfile.collectAsState()
    val coachName = profile.name

    // Fetch all bookings from room and filter for this coach
    val allBookings by viewModel.bookings.collectAsState(initial = emptyList())
    val coachBookings = remember(allBookings, coachName) {
        val naturalBookings = allBookings.filter { it.coachName.equals(coachName, ignoreCase = true) }
        val dummyBookings = listOf(
            Booking(
                id = "dummy_1",
                coachId = "coach_current",
                coachName = coachName,
                coachSkills = "Adv Batting swing & flow control",
                coachImageUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1,
                studentName = "Rohan Sharma",
                date = "2026-05-24",
                timeSlot = "07:00 AM - 09:00 AM",
                price = 1600.0,
                status = "Completed",
                sessionNotes = "Wanted to focus on cover drive weight transfer.",
                feedbackReport = "Excellent stability and grip adjustments. Needs slight work on forward leg reach alignment guide.",
                feedbackGrade = "A"
            ),
            Booking(
                id = "dummy_2",
                coachId = "coach_current",
                coachName = coachName,
                coachSkills = "Backfoot punches & high-speed pacing",
                coachImageUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1,
                studentName = "Kabir Mehta",
                date = "2026-05-25",
                timeSlot = "09:30 AM - 11:30 AM",
                price = 1800.0,
                status = "Completed",
                sessionNotes = "Needs to practice batting stance stability against fast pace bowler options.",
                feedbackReport = "Batting angle is much improved. Keep maintaining center of gravity low and eyes in vertical alignment.",
                feedbackGrade = "A-"
            ),
            Booking(
                id = "dummy_3",
                coachId = "coach_current",
                coachName = coachName,
                coachSkills = "Visual timing drills & spin sweeps",
                coachImageUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1,
                studentName = "Aman Verma",
                date = "2026-05-26",
                timeSlot = "04:30 PM - 06:30 PM",
                price = 1500.0,
                status = "Completed",
                sessionNotes = "Bat control guide for reverse sweeps.",
                feedbackReport = "Excellent grip rotation speed. Keep practicing the visual tracker on sweep contact point.",
                feedbackGrade = "B+"
            ),
            Booking(
                id = "dummy_4",
                coachId = "coach_current",
                coachName = coachName,
                coachSkills = "Turf fast training drills",
                coachImageUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1,
                studentName = "Neha Joshi",
                date = "2026-05-27",
                timeSlot = "02:00 PM - 04:00 PM",
                price = 1600.0,
                status = "Completed",
                sessionNotes = "General fitness and stamina drills.",
                feedbackReport = "Stamina was excellent. Continue working on sprint recoveries during dynamic intervals.",
                feedbackGrade = "A+"
            ),
            Booking(
                id = "dummy_5",
                coachId = "coach_current",
                coachName = coachName,
                coachSkills = "In-swing pacing adjustments",
                coachImageUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1,
                studentName = "Vikram Sen",
                date = "2026-05-29",
                timeSlot = "10:30 AM - 12:30 PM",
                price = 1800.0,
                status = "Upcoming",
                sessionNotes = "Needs custom video review of stance trigger speed."
            ),
            Booking(
                id = "dummy_6",
                coachId = "coach_current",
                coachName = coachName,
                coachSkills = "Adv Batting swing",
                coachImageUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1,
                studentName = "Siddharth Goel",
                date = "2026-05-30",
                timeSlot = "08:00 AM - 10:00 AM",
                price = 1600.0,
                status = "Pending",
                sessionNotes = "Requests guidance on handling short-pitched bouncers on flat green turf."
            ),
            Booking(
                id = "dummy_7",
                coachId = "coach_current",
                coachName = coachName,
                coachSkills = "Cricket core strength limits",
                coachImageUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1,
                studentName = "Aaditya Rai",
                date = "2026-05-22",
                timeSlot = "05:00 PM - 07:00 PM",
                price = 1500.0,
                status = "Cancelled",
                sessionNotes = "Suffered minor muscle strain in practice session."
            )
        )
        dummyBookings + naturalBookings
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SportColors.DeepBlueHeader, SportColors.DeepBlueHeaderEnd)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { 
                                currentCoachTab = CoachTab.PROFILE 
                                Toast.makeText(context, "Opening Coach Profile Settings", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SportColors.ActiveBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (coachName.isNotEmpty()) coachName.first().uppercaseChar().toString() else "C",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Coach $coachName",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Profile & Settings ✏️",
                                fontSize = 11.sp,
                                color = SportColors.SportGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onLogout()
                            Toast.makeText(context, "Logged out from Coach Portal", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Elegant Bottom Navigation Bar customized for Coaches
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(68.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Dashboard
                    IconButtonWithText(
                        icon = Icons.Default.Dashboard,
                        label = "Overview",
                        isSelected = currentCoachTab == CoachTab.DASHBOARD,
                        onClick = { currentCoachTab = CoachTab.DASHBOARD }
                    )

                    // Tab 2: Bookings Manager
                    IconButtonWithText(
                        icon = Icons.Default.CalendarMonth,
                        label = "Bookings",
                        isSelected = currentCoachTab == CoachTab.BOOKINGS_MGR,
                        onClick = { currentCoachTab = CoachTab.BOOKINGS_MGR }
                    )

                    // Tab 3: Availability & Rates
                    IconButtonWithText(
                        icon = Icons.Default.Timer,
                        label = "Timing & Rates",
                        isSelected = currentCoachTab == CoachTab.AVAILABILITY,
                        onClick = { currentCoachTab = CoachTab.AVAILABILITY }
                    )

                    // Tab 4: Grounds
                    IconButtonWithText(
                        icon = Icons.Default.AddLocation,
                        label = "Grounds",
                        isSelected = currentCoachTab == CoachTab.GROUNDS,
                        onClick = { currentCoachTab = CoachTab.GROUNDS }
                    )
                }
            }
        },
        containerColor = SportColors.DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentCoachTab) {
                CoachTab.DASHBOARD -> CoachOverviewTab(viewModel, coachBookings, coachName)
                CoachTab.BOOKINGS_MGR -> CoachBookingsManager(viewModel, coachBookings)
                CoachTab.AVAILABILITY -> CoachScheduleManager(viewModel)
                CoachTab.GROUNDS -> CoachGroundsManager(viewModel)
                CoachTab.PROFILE -> CoachProfileManager(viewModel)
            }
        }
    }
}

@Composable
fun IconButtonWithText(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF0F1E4C)
    val inactiveColor = Color(0xFF8A93A6)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

// ----------------------------------------------------
// SCREEN 1: SUMMARY OVERVIEW
// ----------------------------------------------------
@Composable
fun CoachOverviewTab(
    viewModel: CricketViewModel,
    bookings: List<Booking>,
    coachName: String
) {
    val stats = remember(bookings) {
        val total = bookings.size
        val pending = bookings.count { it.status == "Pending" }
        val cancelled = bookings.count { it.status == "Cancelled" }
        val upcoming = bookings.count { it.status == "Upcoming" }
        val completed = bookings.count { it.status == "Completed" }
        val totalEarnings = bookings.filter { it.status == "Completed" || it.status == "Upcoming" }.sumOf { it.price }
        
        CoachStats(total, pending, cancelled, upcoming, completed, totalEarnings)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Performance Dashboard",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = SportColors.TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Stats grid helper
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Bookings",
                    value = stats.totalBookings.toString(),
                    icon = Icons.Default.Book,
                    accentColor = SportColors.ActiveBlue,
                    modifier = Modifier.weight(1.5f)
                )

                StatCard(
                    title = "Earnings (INR)",
                    value = "₹${stats.earnings.toInt()}",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = SportColors.GoldYellow,
                    modifier = Modifier.weight(2f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Pending",
                    value = stats.pendingBookings.toString(),
                    icon = Icons.Default.HourglassEmpty,
                    accentColor = SportColors.BrightOrange,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Completed",
                    value = stats.completedBookings.toString(),
                    icon = Icons.Default.DoneAll,
                    accentColor = SportColors.SportGreen,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Cancelled",
                    value = stats.cancelledBookings.toString(),
                    icon = Icons.Default.Cancel,
                    accentColor = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Additional information metrics card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SportColors.CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Insights",
                        fontWeight = FontWeight.Bold,
                        color = SportColors.TextPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active upcoming slots:", color = SportColors.TextSecondary, fontSize = 12.sp)
                        Badge(containerColor = SportColors.ActiveBlue) {
                            Text("${stats.upcomingBookings} slots", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Approval rate:", color = SportColors.TextSecondary, fontSize = 12.sp)
                        val totalNonCancelled = stats.totalBookings - stats.cancelledBookings
                        val rate = if (stats.totalBookings > 0) ((totalNonCancelled.toFloat() / stats.totalBookings) * 100).toInt() else 100
                        Text("$rate%", color = SportColors.SportGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Analytics Graphic / Performance Trend Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SportColors.CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly Revenue Trend",
                        fontWeight = FontWeight.Bold,
                        color = SportColors.TextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Progressive earnings generated over time periods",
                        color = SportColors.TextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // A beautiful custom Canvas chart representing data
                    val graphData = listOf(3200f, 4800f, 6500f, 8100f) // Weekly progressive sums
                    val weeks = listOf("Week 1", "Week 2", "Week 3", "Week 4")
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height
                            val maxVal = 10000f
                            val minVal = 0f
                            
                            val points = graphData.mapIndexed { idx, valRaw ->
                                val x = (idx.toFloat() / (graphData.size - 1)) * width
                                val y = height - ((valRaw - minVal) / (maxVal - minVal)) * height
                                androidx.compose.ui.geometry.Offset(x, y)
                            }
                            
                            // Draw Grid Lines
                            for (i in 1..3) {
                                val gridY = (i.toFloat() / 4) * height
                                drawLine(
                                    color = SportColors.CardBorder.copy(alpha = 0.5f),
                                    start = androidx.compose.ui.geometry.Offset(0f, gridY),
                                    end = androidx.compose.ui.geometry.Offset(width, gridY),
                                    strokeWidth = 1f
                                )
                            }
                            
                            // Draw path gradient
                            val fillPath = androidx.compose.ui.graphics.Path().apply {
                                moveTo(0f, height)
                                points.forEach { lineTo(it.x, it.y) }
                                lineTo(width, height)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        SportColors.ActiveBlue.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                            
                            // Draw continuous trend line
                            for (i in 0 until points.size - 1) {
                                drawLine(
                                    color = SportColors.ActiveBlue,
                                    start = points[i],
                                    end = points[i + 1],
                                    strokeWidth = 3.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            }
                            
                            // Draw nodes/points
                            points.forEach { pt ->
                                drawCircle(
                                    color = SportColors.SportGreen,
                                    radius = 4.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Weeks Label indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weeks.forEach { week ->
                            Text(week, color = SportColors.TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Group Sessions Manager Card
        item {
            var showAddGroupSessionDialog by remember { mutableStateOf(false) }
            val groupSessions by viewModel.groupSessions.collectAsState()

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Host Group Workshops",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SportColors.TextPrimary
                    )

                    Button(
                        onClick = { showAddGroupSessionDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Group Session", color = Color.White, fontSize = 11.sp)
                    }
                }

                if (groupSessions.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                        border = BorderStroke(1.dp, SportColors.CardBorder)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No group workshops configured yet.", color = SportColors.TextSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        groupSessions.forEach { session ->
                            GroupSessionCoachCard(session = session, onDelete = {
                                viewModel.removeGroupSession(session.id)
                            })
                        }
                    }
                }
            }

            if (showAddGroupSessionDialog) {
                var title by remember { mutableStateOf("") }
                var desc by remember { mutableStateOf("") }
                var rateInput by remember { mutableStateOf("") }
                var locInput by remember { mutableStateOf("") }
                var selectedImgUrl by remember { mutableStateOf("") }

                val context = LocalContext.current

                AlertDialog(
                    onDismissRequest = { showAddGroupSessionDialog = false },
                    title = {
                        Text("Host New Group Workshop Session", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SportColors.TextPrimary)
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Workshop Title") },
                                placeholder = { Text("e.g. Spin Bowling Masterclass") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = desc,
                                onValueChange = { desc = it },
                                label = { Text("Description") },
                                placeholder = { Text("Focus on flight, spin grip & turn...") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = rateInput,
                                onValueChange = { rateInput = it },
                                label = { Text("Rate per Student (₹)") },
                                placeholder = { Text("e.g. 500") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = locInput,
                                onValueChange = { locInput = it },
                                label = { Text("Net Venue/Location") },
                                placeholder = { Text("e.g. Shivaji Park Maidan, Dadar") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            
                            // Image select layout
                            Text("Banner Theme Image Style:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SportColors.TextPrimary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { selectedImgUrl = "android.resource://com.example/" + com.example.R.drawable.img_group_banner },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedImgUrl.contains("img_group_banner")) SportColors.ActiveBlue else SportColors.DarkBackground
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Text("Group Training", color = if (selectedImgUrl.contains("img_group_banner")) Color.White else SportColors.TextPrimary, fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { selectedImgUrl = "android.resource://com.example/" + com.example.R.drawable.img_cricket_nets },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedImgUrl.contains("img_cricket_nets")) SportColors.ActiveBlue else SportColors.DarkBackground
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Text("Turf Nets", color = if (selectedImgUrl.contains("img_cricket_nets")) Color.White else SportColors.TextPrimary, fontSize = 10.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (title.isBlank() || locInput.isBlank()) {
                                    Toast.makeText(context, "Title and Location are required!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val r = rateInput.toDoubleOrNull() ?: 500.0
                                viewModel.addGroupSession(title, desc, r, locInput, selectedImgUrl, coachName)
                                showAddGroupSessionDialog = false
                                Toast.makeText(context, "Group session added successfully!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen)
                        ) {
                            Text("Create Session ✅", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddGroupSessionDialog = false }) {
                            Text("Cancel", color = SportColors.TextSecondary)
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}

@Composable
fun RateDisplayBadge(
    label: String,
    rate: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SportColors.DarkBackground),
        border = BorderStroke(1.dp, SportColors.CardBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = SportColors.TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(rate, color = SportColors.ActiveBlue, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun GroupSessionCoachCard(
    session: GroupSession,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SportColors.CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SportColors.DarkBackground)
            ) {
                AsyncImage(
                    model = parseSportImageUrl(session.imageUrl, context),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(session.title, color = SportColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(session.location, color = SportColors.TextSecondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("₹${session.hourlyRate.toInt()}/student", color = SportColors.SportGreenDark, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(30.dp)
                    .background(Color(0xFFEF4444).copy(alpha = 0.12f), CircleShape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
            }
        }
    }
}

data class CoachStats(
    val totalBookings: Int,
    val pendingBookings: Int,
    val cancelledBookings: Int,
    val upcomingBookings: Int,
    val completedBookings: Int,
    val earnings: Double
)

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(105.dp),
        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SportColors.CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Text(
                text = value,
                fontSize = 20.sp,
                color = SportColors.TextPrimary,
                fontWeight = FontWeight.Black
            )
        }
    }
}

// ----------------------------------------------------
// SCREEN 2: BOOKINGS & REQUESTS MANAGER
// ----------------------------------------------------
enum class BookingCategoryFilter {
    ALL, TODAY, PENDING_REQS, HISTORY, NEEDS_FEEDBACK
}

@Composable
fun CoachBookingsManager(
    viewModel: CricketViewModel,
    bookings: List<Booking>
) {
    val context = LocalContext.current
    var subTab by remember { mutableStateOf(BookingCategoryFilter.ALL) }
    var selectedBookingForDetail by remember { mutableStateOf<Booking?>(null) }
    var bookingToSubmitFeedback by remember { mutableStateOf<Booking?>(null) }
    var rejectBookingWithRefundAlert by remember { mutableStateOf<Booking?>(null) }

    // Dynamic Filter arrays
    val filteredList = remember(bookings, subTab) {
        when (subTab) {
            BookingCategoryFilter.ALL -> bookings
            BookingCategoryFilter.TODAY -> bookings.filter { it.status == "Upcoming" } // Treated as upcoming schedule
            BookingCategoryFilter.PENDING_REQS -> bookings.filter { it.status == "Pending" }
            BookingCategoryFilter.HISTORY -> bookings.filter { it.status == "Completed" || it.status == "Cancelled" }
            BookingCategoryFilter.NEEDS_FEEDBACK -> bookings.filter { it.status == "Completed" && it.feedbackReport.isEmpty() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bookings Schedules",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SportColors.TextPrimary
            )
            
            var showAddWalkInDialog by remember { mutableStateOf(false) }
            
            Button(
                onClick = { showAddWalkInDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SportColors.ActiveBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Walk-In", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            
            if (showAddWalkInDialog) {
                var studentName by remember { mutableStateOf("") }
                var selectedDate by remember { mutableStateOf("") }
                var selectedTimeSlot by remember { mutableStateOf("") }
                var priceInput by remember { mutableStateOf("1000") }
                
                AlertDialog(
                    onDismissRequest = { showAddWalkInDialog = false },
                    title = {
                        Text("Log Offline Walk-In Booking", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SportColors.TextPrimary)
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = studentName,
                                onValueChange = { studentName = it },
                                label = { Text("Student Name") },
                                placeholder = { Text("e.g. Rohan Sharma") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = selectedDate,
                                onValueChange = { selectedDate = it },
                                label = { Text("Date (YYYY-MM-DD)") },
                                placeholder = { Text("e.g. 2026-05-30") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = selectedTimeSlot,
                                onValueChange = { selectedTimeSlot = it },
                                label = { Text("Time Slot") },
                                placeholder = { Text("e.g. 04:00 PM - 05:00 PM") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = priceInput,
                                onValueChange = { priceInput = it },
                                label = { Text("Charges/Rate (₹)") },
                                placeholder = { Text("1000") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (studentName.isBlank() || selectedDate.isBlank() || selectedTimeSlot.isBlank()) {
                                    Toast.makeText(context, "Please configure all fields!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val pr = priceInput.toDoubleOrNull() ?: 1000.0
                                viewModel.bookWalkInSession(
                                    coachName = viewModel.userProfile.value.name,
                                    studentName = studentName,
                                    date = selectedDate,
                                    timeSlot = selectedTimeSlot,
                                    price = pr,
                                    sessionNotes = "Manual offline booking logged by Coach."
                                )
                                showAddWalkInDialog = false
                                Toast.makeText(context, "Walk-in booked and logged!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen)
                        ) {
                            Text("Confirm Booking ✅", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddWalkInDialog = false }) {
                            Text("Cancel", color = SportColors.TextSecondary)
                        }
                    },
                    containerColor = Color.White
                )
            }
        }

        // Sub horizontal filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BookingCategoryFilter.values().forEach { filter ->
                val isSelected = subTab == filter
                val label = when (filter) {
                    BookingCategoryFilter.ALL -> "All"
                    BookingCategoryFilter.TODAY -> "Today / Active"
                    BookingCategoryFilter.PENDING_REQS -> "Requests Pending"
                    BookingCategoryFilter.HISTORY -> "History Log"
                    BookingCategoryFilter.NEEDS_FEEDBACK -> "Pending Feedback"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) SportColors.ActiveBlue else SportColors.SoftCardBg
                        )
                        .clickable { subTab = filter }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else SportColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = SportColors.TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No booking items in this filter.",
                        color = SportColors.TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { booking ->
                    BookingItemCard(
                        booking = booking,
                        onViewDetails = { selectedBookingForDetail = booking },
                        onAccept = {
                            viewModel.acceptBookingRequest(booking.id)
                            Toast.makeText(context, "Booking successfully accepted!", Toast.LENGTH_SHORT).show()
                        },
                        onRejectPrompt = { rejectBookingWithRefundAlert = booking },
                        onAddFeedback = { bookingToSubmitFeedback = booking }
                    )
                }
            }
        }
    }

    // Modal Dialog 1: Booking Detailed view
    selectedBookingForDetail?.let { b ->
        AlertDialog(
            onDismissRequest = { selectedBookingForDetail = null },
            title = {
                Text("Booking Detail: #${b.id.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Student Athlete:", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text(b.studentName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Scheduled Date:", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text(b.date, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Time Duration:", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text(b.timeSlot, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Session Price:", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("₹${b.price}", fontWeight = FontWeight.Bold, color = SportColors.GoldYellow, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Booking Status:", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text(b.status, fontWeight = FontWeight.Bold, color = if (b.status == "Upcoming") SportColors.SportGreen else SportColors.BrightOrange, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Strap Session Notes:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                    Text(
                        text = b.sessionNotes.ifEmpty { "None provided by athlete." },
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    if (b.feedbackReport.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Submitted Feedback Advice:", fontWeight = FontWeight.Bold, color = SportColors.SportGreen, fontSize = 11.sp)
                        Text(
                            text = b.feedbackReport,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                        Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Evaluation Grade: ", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                            Badge(containerColor = SportColors.ActiveBlue) {
                                Text(b.feedbackGrade, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedBookingForDetail = null }) {
                    Text("Close", color = Color(0xFF38BDF8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // Modal Dialog 2: Reject with Refund disclaimer agreement
    rejectBookingWithRefundAlert?.let { b ->
        var agreementChecked by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { rejectBookingWithRefundAlert = null },
            title = {
                Text("Cancel Session & Initiate Refund?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "You are rejecting the slot requested by ${b.studentName} on ${b.date} (${b.timeSlot}).",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Refund Policy Agreement:\nRejecting this active request will trigger an immediate automatic gateway credit refund of ₹${b.price} back to the user's wallet.",
                        color = SportColors.BrightOrange,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { agreementChecked = !agreementChecked }
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreementChecked,
                            onCheckedChange = { agreementChecked = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I understand and agree to initiate automated refund.",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectBookingRequest(b.id)
                        rejectBookingWithRefundAlert = null
                        Toast.makeText(context, "Rejected. Refund initiated successfully!", Toast.LENGTH_LONG).show()
                    },
                    enabled = agreementChecked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Agree & Cancel Slot", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectBookingWithRefundAlert = null }) {
                    Text("Go Back", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // Modal Dialog 3: Feedback submission
    bookingToSubmitFeedback?.let { b ->
        var feedbackComment by remember { mutableStateOf("") }
        var selectedGrade by remember { mutableStateOf("A") }
        val gradesList = listOf("A+", "A", "B+", "B", "C")

        AlertDialog(
            onDismissRequest = { bookingToSubmitFeedback = null },
            title = {
                Text("Submit Session Evaluation & Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Student Name: ${b.studentName}\nCompleted Date: ${b.date}",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = feedbackComment,
                        onValueChange = { feedbackComment = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        label = { Text("Performance Advice / Feedback", color = Color(0xFFCBD5E1)) },
                        placeholder = { Text("Excellent stance balance. Focus next on high bat-lift control and footwork trigger...", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Text("Performance Grade Evaluation:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        gradesList.forEach { grade ->
                            val isSel = selectedGrade == grade
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF38BDF8) else Color(0xFF334155))
                                    .border(1.dp, if (isSel) Color.White else Color(0xFF475569), RoundedCornerShape(8.dp))
                                    .clickable { selectedGrade = grade }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = grade,
                                    color = if (isSel) Color.White else Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (feedbackComment.isBlank()) {
                            Toast.makeText(context, "Feedback advice cannot be empty!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.simulateCompletionFlow(b, feedbackComment, selectedGrade)
                        bookingToSubmitFeedback = null
                        Toast.makeText(context, "Feedback report submitted advice saved!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen)
                ) {
                    Text("Submit Report Cards 📝", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToSubmitFeedback = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    onViewDetails: () -> Unit,
    onAccept: () -> Unit,
    onRejectPrompt: () -> Unit,
    onAddFeedback: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetails),
        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SportColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(SportColors.ActiveBlue.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SportColors.ActiveBlue, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = booking.studentName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = booking.date,
                            color = SportColors.TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Unified Status Badge
                val badgeColor = when (booking.status) {
                    "Pending" -> SportColors.BrightOrange
                    "Upcoming" -> SportColors.SportGreen
                    "Completed" -> SportColors.ActiveBlue
                    else -> Color(0xFFEF4444)
                }
                Badge(containerColor = badgeColor) {
                    Text(
                        text = booking.status.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 8.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⏰ ${booking.timeSlot}",
                    color = SportColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${booking.price.toInt()}",
                    color = SportColors.GoldYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Quick actions if pending requested booking
            if (booking.status == "Pending") {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SportColors.CardBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRejectPrompt,
                        modifier = Modifier.weight(1f).height(34.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Reject Slot", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1.2f).height(34.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Accept Request", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Quick actions if completed but feedback needed
            if (booking.status == "Completed" && booking.feedbackReport.isEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAddFeedback,
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SportColors.ActiveBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit Evaluation Report Card", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SCREEN 3: AVAILABILITY / SCHEDULE SLOTS
// ----------------------------------------------------
@Composable
fun CoachScheduleManager(viewModel: CricketViewModel) {
    val context = LocalContext.current
    val dates by viewModel.availableDates.collectAsState()
    val slots by viewModel.availableSlots.collectAsState()

    var showAddDateLayout by remember { mutableStateOf(false) }
    var inputNewDate by remember { mutableStateOf("") }

    var showAddSlotLayout by remember { mutableStateOf(false) }
    var inputNewSlot by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Availability Scheduling",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SportColors.TextPrimary
            )
            Text(
                text = "Add and remove date and times slots available for student bookings.",
                color = SportColors.TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Coaching Rates Setup Card
        item {
            var isEditingRates by remember { mutableStateOf(false) }
            val hourlyRate by viewModel.coachHourlyRate.collectAsState()
            val weeklyRate by viewModel.coachWeeklyRate.collectAsState()
            val monthlyRate by viewModel.coachMonthlyRate.collectAsState()

            var tempHourly by remember(hourlyRate) { mutableStateOf(hourlyRate.toInt().toString()) }
            var tempWeekly by remember(weeklyRate) { mutableStateOf(weeklyRate.toInt().toString()) }
            var tempMonthly by remember(monthlyRate) { mutableStateOf(monthlyRate.toInt().toString()) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SportColors.CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = SportColors.ActiveBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Coaching Rates Setup",
                                fontWeight = FontWeight.Bold,
                                color = SportColors.TextPrimary,
                                fontSize = 14.sp
                            )
                        }

                        TextButton(onClick = {
                            if (isEditingRates) {
                                val hr = tempHourly.toDoubleOrNull() ?: hourlyRate
                                val wk = tempWeekly.toDoubleOrNull() ?: weeklyRate
                                val mn = tempMonthly.toDoubleOrNull() ?: monthlyRate
                                viewModel.updateCoachRates(hr, wk, mn)
                            }
                            isEditingRates = !isEditingRates
                        }) {
                            Text(if (isEditingRates) "Save Rates" else "Configure Rates", color = SportColors.ActiveBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isEditingRates) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RateDisplayBadge("Per Hour", "₹${hourlyRate.toInt()}", modifier = Modifier.weight(1f))
                            RateDisplayBadge("Per Week", "₹${weeklyRate.toInt()}", modifier = Modifier.weight(1f))
                            RateDisplayBadge("Per Month", "₹${monthlyRate.toInt()}", modifier = Modifier.weight(1f))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = tempHourly,
                                onValueChange = { tempHourly = it },
                                label = { Text("Hourly Rate (₹)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportColors.TextPrimary,
                                    unfocusedTextColor = SportColors.TextPrimary
                                )
                            )
                            OutlinedTextField(
                                value = tempWeekly,
                                onValueChange = { tempWeekly = it },
                                label = { Text("Weekly Premium Rate (₹)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportColors.TextPrimary,
                                    unfocusedTextColor = SportColors.TextPrimary
                                )
                            )
                            OutlinedTextField(
                                value = tempMonthly,
                                onValueChange = { tempMonthly = it },
                                label = { Text("Monthly Pass Rate (₹)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportColors.TextPrimary,
                                    unfocusedTextColor = SportColors.TextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }



        // Section A: Dates
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SportColors.CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Training Dates",
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = { showAddDateLayout = !showAddDateLayout },
                            modifier = Modifier
                                .size(28.dp)
                                .background(SportColors.ActiveBlue, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (showAddDateLayout) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Add Date",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (showAddDateLayout) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = inputNewDate,
                                onValueChange = { inputNewDate = it },
                                placeholder = { Text("YYYY-MM-DD", color = SportColors.TextSecondary) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SportColors.ActiveBlue,
                                    unfocusedBorderColor = SportColors.CardBorder,
                                    focusedTextColor = SportColors.TextPrimary,
                                    unfocusedTextColor = SportColors.TextPrimary
                                ),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (inputNewDate.isBlank() || !inputNewDate.contains("-")) {
                                        Toast.makeText(context, "Provide valid YYYY-MM-DD format!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    viewModel.addAvailableDate(inputNewDate)
                                    inputNewDate = ""
                                    showAddDateLayout = false
                                    Toast.makeText(context, "Added available date!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Chips layout Flow Style
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dates.forEach { date ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SportColors.DarkBackground)
                                    .border(1.dp, SportColors.CardBorder, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(date, color = SportColors.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable {
                                                viewModel.removeAvailableDate(date)
                                                Toast.makeText(context, "Removed date: $date", Toast.LENGTH_SHORT).show()
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section B: Time Slots
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SportColors.CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Training Hour Slots",
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = { showAddSlotLayout = !showAddSlotLayout },
                            modifier = Modifier
                                .size(28.dp)
                                .background(SportColors.ActiveBlue, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (showAddSlotLayout) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Add slot",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (showAddSlotLayout) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = inputNewSlot,
                                onValueChange = { inputNewSlot = it },
                                placeholder = { Text("e.g. 02:00 PM - 04:00 PM", color = SportColors.TextSecondary) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SportColors.ActiveBlue,
                                    unfocusedBorderColor = SportColors.CardBorder,
                                    focusedTextColor = SportColors.TextPrimary,
                                    unfocusedTextColor = SportColors.TextPrimary
                                ),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (inputNewSlot.isBlank()) {
                                        Toast.makeText(context, "Slot hours cannot be blank!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    viewModel.addAvailableSlot(inputNewSlot)
                                    inputNewSlot = ""
                                    showAddSlotLayout = false
                                    Toast.makeText(context, "Added new session slot!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        slots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SportColors.DarkBackground)
                                    .border(1.dp, SportColors.CardBorder, RoundedCornerShape(8.dp))
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = SportColors.ActiveBlue, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(slot, color = SportColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape)
                                        .clickable {
                                            viewModel.removeAvailableSlot(slot)
                                            Toast.makeText(context, "Removed session slot!", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SCREEN 4: GROUNDS & LOCATIONS MANAGER
// ----------------------------------------------------
@Composable
fun CoachGroundsManager(viewModel: CricketViewModel) {
    val context = LocalContext.current
    val grounds by viewModel.verifiedGrounds.collectAsState()

    var showAddGroundLayout by remember { mutableStateOf(false) }
    var groundNameInput by remember { mutableStateOf("") }
    var groundPriceInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Practice Grounds Nets",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportColors.TextPrimary
                    )
                    Text(
                        text = "Linked training turfs where you host sessions.",
                        color = SportColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showAddGroundLayout = !showAddGroundLayout },
                    colors = ButtonDefaults.buttonColors(containerColor = if (showAddGroundLayout) Color(0xFFEF4444) else SportColors.ActiveBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = if (showAddGroundLayout) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add Ground",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showAddGroundLayout) "Close" else "Add New", fontSize = 11.sp, color = Color.White)
                }
            }
        }

        if (showAddGroundLayout) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SportColors.CardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Register Verified Practice Ground", color = SportColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        OutlinedTextField(
                            value = groundNameInput,
                            onValueChange = { groundNameInput = it },
                            label = { Text("Ground/Turf Name") },
                            placeholder = { Text("e.g. Bandra East Nets") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportColors.ActiveBlue,
                                unfocusedBorderColor = SportColors.CardBorder,
                                focusedTextColor = SportColors.TextPrimary,
                                unfocusedTextColor = SportColors.TextPrimary
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = groundPriceInput,
                            onValueChange = { groundPriceInput = it },
                            label = { Text("Hourly Rent Rate (INR)") },
                            placeholder = { Text("e.g. 500") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportColors.ActiveBlue,
                                unfocusedBorderColor = SportColors.CardBorder,
                                focusedTextColor = SportColors.TextPrimary,
                                unfocusedTextColor = SportColors.TextPrimary
                            ),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val rate = groundPriceInput.toDoubleOrNull()
                                if (groundNameInput.isBlank()) {
                                    Toast.makeText(context, "Ground name is required!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (rate == null || rate <= 0.0) {
                                    Toast.makeText(context, "Provide positive numerical rent rate!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                viewModel.addGround(groundNameInput, rate)
                                groundNameInput = ""
                                groundPriceInput = ""
                                showAddGroundLayout = false
                                Toast.makeText(context, "Practice ground linked successfully!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Confirm Link Ground ✅", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(grounds) { ground ->
            GroundItemCard(
                ground = ground,
                onDelete = {
                    viewModel.removeGround(ground.id)
                    Toast.makeText(context, "Practice ground unlinked!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun GroundItemCard(
    ground: CricketGround,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SportColors.CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SportColors.DarkBackground)
            ) {
                val ctx = LocalContext.current
                AsyncImage(
                    model = parseSportImageUrl(ground.imageUrl, ctx),
                    contentDescription = ground.name,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ground.name,
                    color = SportColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = SportColors.ActiveBlue, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = ground.distance, color = SportColors.TextSecondary, fontSize = 11.sp)
                }
                Text(
                    text = "₹${ground.ratePerHour.toInt()}/hr Rent",
                    color = SportColors.SportGreenDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Delete link
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Ground",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoachProfileManager(viewModel: CricketViewModel) {
    val context = LocalContext.current
    
    val profile by viewModel.userProfile.collectAsState()
    val bio by viewModel.coachBio.collectAsState()
    val experience by viewModel.coachExperience.collectAsState()
    val skillsText by viewModel.coachSkillsText.collectAsState()
    
    val photos by viewModel.coachPhotos.collectAsState()
    val videos by viewModel.coachVideos.collectAsState()
    val certificates by viewModel.coachCertificates.collectAsState()

    var isEditingMainInfo by remember { mutableStateOf(false) }
    var tempName by remember(profile.name) { mutableStateOf(profile.name) }
    var tempBio by remember(bio) { mutableStateOf(bio) }
    var tempExperience by remember(experience) { mutableStateOf(experience) }
    var tempSkills by remember(skillsText) { mutableStateOf(skillsText) }

    var showAddVideoDialog by remember { mutableStateOf(false) }
    var inputVideoTitle by remember { mutableStateOf("") }
    var activePlayingVideoTitle by remember { mutableStateOf<String?>(null) }

    var showAddCertDialog by remember { mutableStateOf(false) }
    var inputCertName by remember { mutableStateOf("") }

    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var inputPhotoUrl by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Title section
        item {
            Text(
                text = "Coach Profile Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SportColors.TextPrimary
            )
            Text(
                text = "Manage your public profile bio, certificates, videos, photos, and general experience.",
                color = SportColors.TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Section A: Main Profile Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SportColors.CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "General Information & Bio",
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = {
                                if (isEditingMainInfo) {
                                    // Save
                                    viewModel.updateCoachProfileName(tempName)
                                    viewModel.updateCoachBio(tempBio)
                                    viewModel.updateCoachExperience(tempExperience)
                                    viewModel.updateCoachSkillsText(tempSkills)
                                    Toast.makeText(context, "General information updated!", Toast.LENGTH_SHORT).show()
                                }
                                isEditingMainInfo = !isEditingMainInfo
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditingMainInfo) Icons.Default.CheckCircle else Icons.Default.Edit,
                                contentDescription = if (isEditingMainInfo) "Save" else "Edit Information",
                                tint = if (isEditingMainInfo) SportColors.SportGreen else SportColors.ActiveBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isEditingMainInfo) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Name
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = SportColors.ActiveBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Coach Display Name: ",
                                    fontSize = 12.sp,
                                    color = SportColors.TextSecondary
                                )
                                Text(
                                    text = profile.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportColors.TextPrimary
                                )
                            }

                            // Experience
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = SportColors.GoldYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Experience: ",
                                    fontSize = 12.sp,
                                    color = SportColors.TextSecondary
                                )
                                Text(
                                    text = experience,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportColors.TextPrimary
                                )
                            }

                            // Skills
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.SportsCricket,
                                    contentDescription = null,
                                    tint = SportColors.ActiveBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Coaching Specializations:",
                                        fontSize = 12.sp,
                                        color = SportColors.TextSecondary
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        skillsText.split(",").forEach { s ->
                                            if (s.trim().isNotEmpty()) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(SportColors.ActiveBlue.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = s.trim(),
                                                        color = SportColors.ActiveBlue,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Bio
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, SportColors.CardBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Biography",
                                    fontSize = 11.sp,
                                    color = SportColors.TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = bio,
                                    fontSize = 12.sp,
                                    color = SportColors.TextPrimary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = tempName,
                                onValueChange = { tempName = it },
                                label = { Text("Display Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = tempExperience,
                                onValueChange = { tempExperience = it },
                                label = { Text("Coaching Experience Years (e.g. 8 Years)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = tempSkills,
                                onValueChange = { tempSkills = it },
                                label = { Text("Coaching Specialization (comma separated)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = tempBio,
                                onValueChange = { tempBio = it },
                                label = { Text("Coach Pitch / Biography Statement") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )
                        }
                    }
                }
            }
        }

        // Section B: Certifications List Manager
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = SportColors.GoldYellow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Certificates & Training Documents (${certificates.size})",
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            fontSize = 14.sp
                        )
                    }

                    TextButton(onClick = { showAddCertDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = SportColors.ActiveBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Document", fontSize = 11.sp, color = SportColors.ActiveBlue, fontWeight = FontWeight.Bold)
                    }
                }

                if (certificates.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SportColors.SoftCardBg, RoundedCornerShape(10.dp))
                            .border(1.dp, SportColors.CardBorder, RoundedCornerShape(10.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No verified certificates linked. Add your credentials above.", color = SportColors.TextSecondary, fontSize = 11.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        certificates.forEach { cert ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, SportColors.CardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stars,
                                            contentDescription = null,
                                            tint = SportColors.SportGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = cert,
                                            color = SportColors.TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.removeCoachCertificate(cert)
                                            Toast.makeText(context, "Certificate removed", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section C: Demo Videos List Manager
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, tint = SportColors.ActiveBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Training & Analysis Videos (${videos.size})",
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            fontSize = 14.sp
                        )
                    }

                    TextButton(onClick = { showAddVideoDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = SportColors.ActiveBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Video", fontSize = 11.sp, color = SportColors.ActiveBlue, fontWeight = FontWeight.Bold)
                    }
                }

                if (videos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SportColors.SoftCardBg, RoundedCornerShape(10.dp))
                            .border(1.dp, SportColors.CardBorder, RoundedCornerShape(10.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No play demonstration videos linked yet.", color = SportColors.TextSecondary, fontSize = 11.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        videos.forEach { video ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { activePlayingVideoTitle = video },
                                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, SportColors.CardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = SportColors.ActiveBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = video,
                                                color = SportColors.TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Video Demonstration Tutorial",
                                                color = SportColors.TextSecondary,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.removeCoachVideo(video)
                                            Toast.makeText(context, "Video removed", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section D: Public Portfolio Photos Gallery
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = SportColors.ActiveBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Portfolio Photo Gallery (${photos.size})",
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            fontSize = 14.sp
                        )
                    }

                    TextButton(onClick = { showAddPhotoDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = SportColors.ActiveBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Photo", fontSize = 11.sp, color = SportColors.ActiveBlue, fontWeight = FontWeight.Bold)
                    }
                }

                if (photos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SportColors.SoftCardBg, RoundedCornerShape(10.dp))
                            .border(1.dp, SportColors.CardBorder, RoundedCornerShape(10.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No training ground photos linked yet.", color = SportColors.TextSecondary, fontSize = 11.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        photos.forEach { uriString ->
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SportColors.SoftCardBg)
                                    .border(1.dp, SportColors.CardBorder, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = parseSportImageUrl(uriString, context),
                                    contentDescription = "Portfolio Photo",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(20.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        .clickable {
                                            viewModel.removeCoachPhoto(uriString)
                                            Toast.makeText(context, "Photo removed", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Dialogs ---
    
    if (showAddVideoDialog) {
        AlertDialog(
            onDismissRequest = { showAddVideoDialog = false },
            title = { Text("Link Training Tutorial Video", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                OutlinedTextField(
                    value = inputVideoTitle,
                    onValueChange = { inputVideoTitle = it },
                    label = { Text("Video Title or Topic") },
                    placeholder = { Text("e.g. Masterclass on Spin sweep contact point") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputVideoTitle.isBlank()) {
                            Toast.makeText(context, "Please enter a valid title!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addCoachVideo(inputVideoTitle)
                        inputVideoTitle = ""
                        showAddVideoDialog = false
                        Toast.makeText(context, "Workshop dynamic video linked successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SportColors.ActiveBlue)
                ) {
                    Text("Add Video Link ✅", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddVideoDialog = false }) {
                    Text("Cancel", color = SportColors.TextSecondary)
                }
            },
            containerColor = Color.White
        )
    }

    if (showAddCertDialog) {
        AlertDialog(
            onDismissRequest = { showAddCertDialog = false },
            title = { Text("Link Coach Certificate", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                OutlinedTextField(
                    value = inputCertName,
                    onValueChange = { inputCertName = it },
                    label = { Text("Certificate Name / Title") },
                    placeholder = { Text("e.g. ICC level 2 coaching master certificate") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputCertName.isBlank()) {
                            Toast.makeText(context, "Please enter a valid certificate name!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addCoachCertificate(inputCertName)
                        inputCertName = ""
                        showAddCertDialog = false
                        Toast.makeText(context, "Academy credential certificate logged!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SportColors.ActiveBlue)
                ) {
                    Text("Link Certification ✅", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCertDialog = false }) {
                    Text("Cancel", color = SportColors.TextSecondary)
                }
            },
            containerColor = Color.White
        )
    }

    if (showAddPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showAddPhotoDialog = false },
            title = { Text("Add Academy Profile Photo", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Provide a URL or use one of our beautiful default academy presets below:", fontSize = 11.sp, color = SportColors.TextSecondary)
                    OutlinedTextField(
                        value = inputPhotoUrl,
                        onValueChange = { inputPhotoUrl = it },
                        label = { Text("Photo URL") },
                        placeholder = { Text("e.g. https://...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                inputPhotoUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SportColors.SoftCardBg),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Preset 1 (Coach)", color = SportColors.ActiveBlue, fontSize = 9.sp)
                        }
                        Button(
                            onClick = {
                                inputPhotoUrl = "android.resource://com.example/" + com.example.R.drawable.img_cricket_nets
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SportColors.SoftCardBg),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Preset 2 (Nets)", color = SportColors.ActiveBlue, fontSize = 9.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalUrl = if (inputPhotoUrl.trim().isEmpty()) {
                            "android.resource://com.example/" + com.example.R.drawable.img_coach_1
                        } else {
                            inputPhotoUrl
                        }
                        viewModel.addCoachPhoto(finalUrl)
                        inputPhotoUrl = ""
                        showAddPhotoDialog = false
                        Toast.makeText(context, "Academy portfolio photo loaded!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SportColors.ActiveBlue)
                ) {
                    Text("Link Photo ✅", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPhotoDialog = false }) {
                    Text("Cancel", color = SportColors.TextSecondary)
                }
            },
            containerColor = Color.White
        )
    }

    activePlayingVideoTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { activePlayingVideoTitle = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Playing: Your Coaching Masterclass",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportColors.ActiveBlue
                    )
                    IconButton(
                        onClick = { activePlayingVideoTitle = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Player", modifier = Modifier.size(16.dp))
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = SportColors.SportGreen,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Streaming: $title...",
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 40.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "", tint = SportColors.TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("HD 1080p previewing active", color = SportColors.TextSecondary, fontSize = 10.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { activePlayingVideoTitle = null }) {
                    Text("Done", fontWeight = FontWeight.Bold, color = SportColors.ActiveBlue)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
