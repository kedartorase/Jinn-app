package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Booking
import com.example.data.CricketGround
import com.example.ui.viewmodel.CricketViewModel
import kotlinx.coroutines.launch

enum class CoachTab {
    DASHBOARD, BOOKINGS_MGR, AVAILABILITY, GROUNDS
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
        allBookings.filter { it.coachName.equals(coachName, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Professional Coach Portal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Log In: $coachName",
                            fontSize = 12.sp,
                            color = SportColors.SportGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onLogout()
                            Toast.makeText(context, "Logged out from Coach Portal", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SportColors.DarkBackground,
                    titleContentColor = Color.White
                )
            )
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

                    // Tab 3: Availability
                    IconButtonWithText(
                        icon = Icons.Default.Timer,
                        label = "Schedule",
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
                CoachTab.DASHBOARD -> CoachOverviewTab(coachBookings, coachName)
                CoachTab.BOOKINGS_MGR -> CoachBookingsManager(viewModel, coachBookings)
                CoachTab.AVAILABILITY -> CoachScheduleManager(viewModel)
                CoachTab.GROUNDS -> CoachGroundsManager(viewModel)
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
    bookings: List<Booking>,
    coachName: String
) {
    val stats = remember(bookings) {
        val total = bookings.size
        val pending = bookings.count { it.status == "Pending" }
        val cancelled = bookings.count { it.status == "Cancelled" }
        val upcoming = bookings.count { it.status == "Upcoming" }
        val completed = bookings.count { it.status == "Completed" }
        // Earnings: completed past bookings + upcoming active sessions count times price
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
            // Coach Profile Quick Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SportColors.CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SportColors.ActiveBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (coachName.isNotEmpty()) coachName.first().toString() else "C",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Hello, Coach $coachName! 👋",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary
                        )
                        Text(
                            text = "Manage your academy matches & student schedule lists.",
                            fontSize = 12.sp,
                            color = SportColors.TextSecondary
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Performance Dashboard",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
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
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Insights",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
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
    modifier = Modifier = Modifier
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
                color = Color.White,
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
        Text(
            text = "Bookings Schedules",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 10.dp)
        )

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
                Text("Booking Detail: #${b.id.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Student Athlete:", color = SportColors.TextSecondary, fontSize = 12.sp)
                        Text(b.studentName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Scheduled Date:", color = SportColors.TextSecondary, fontSize = 12.sp)
                        Text(b.date, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Time Duration:", color = SportColors.TextSecondary, fontSize = 12.sp)
                        Text(b.timeSlot, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Session Price:", color = SportColors.TextSecondary, fontSize = 12.sp)
                        Text("₹${b.price}", fontWeight = FontWeight.Bold, color = SportColors.GoldYellow, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Booking Status:", color = SportColors.TextSecondary, fontSize = 12.sp)
                        Text(b.status, fontWeight = FontWeight.Bold, color = if (b.status == "Upcoming") SportColors.SportGreen else SportColors.BrightOrange, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Strap Session Notes:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                    Text(
                        text = b.sessionNotes.ifEmpty { "None provided by athlete." },
                        color = SportColors.TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    if (b.feedbackReport.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Submitted Feedback Advice:", fontWeight = FontWeight.Bold, color = SportColors.SportGreen, fontSize = 11.sp)
                        Text(
                            text = b.feedbackReport,
                            color = SportColors.TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                        Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Evaluation Grade: ", color = SportColors.TextSecondary, fontSize = 11.sp)
                            Badge(containerColor = SportColors.ActiveBlue) {
                                Text(b.feedbackGrade, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedBookingForDetail = null }) {
                    Text("Close", color = SportColors.ActiveBlue)
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
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "You are rejecting the slot requested by ${b.studentName} on ${b.date} (${b.timeSlot}).",
                        color = SportColors.TextSecondary,
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Student Name: ${b.studentName}\nCompleted Date: ${b.date}",
                        color = SportColors.TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = feedbackComment,
                        onValueChange = { feedbackComment = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        label = { Text("Performance Advice / Feedback") },
                        placeholder = { Text("Excellent stance balance. Focus next on high bat-lift control and footwork trigger...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SportColors.ActiveBlue,
                            unfocusedBorderColor = SportColors.CardBorder,
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
                                    .background(if (isSel) SportColors.ActiveBlue else SportColors.DarkBackground)
                                    .border(1.dp, if (isSel) Color.White else SportColors.CardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedGrade = grade }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(grade, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                color = Color.White
            )
            Text(
                text = "Add and remove date and times slots available for student bookings.",
                color = SportColors.TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
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
                            color = Color.White,
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
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
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
                                    Text(date, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFF87171),
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
                            color = Color.White,
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
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
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
                                    Text(slot, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                        color = Color.White
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
                        Text("Register Verified Practice Ground", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        OutlinedTextField(
                            value = groundNameInput,
                            onValueChange = { groundNameInput = it },
                            label = { Text("Ground/Turf Name") },
                            placeholder = { Text("e.g. Bandra East Nets") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportColors.ActiveBlue,
                                unfocusedBorderColor = SportColors.CardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
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
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
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
                // If a decorative turf illustration, load standard icon or load from Uri
                val ctx = LocalContext.current
                val finalImgUrl = if (ground.imageUrl.startsWith("android.resource://")) {
                    ground.imageUrl
                } else {
                    "android.resource://com.example/" + com.example.R.drawable.img_cricket_nets
                }

                AsyncImage(
                    model = finalImgUrl,
                    contentDescription = ground.name,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ground.name,
                    color = Color.White,
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
                    color = SportColors.GoldYellow,
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
