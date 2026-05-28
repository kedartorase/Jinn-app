package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Coach
import com.example.data.CricketGround
import com.example.ui.viewmodel.CricketViewModel
import com.example.R

@Composable
fun CoachDetailScreen(
    coach: Coach,
    viewModel: CricketViewModel,
    onBackClick: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    var selectedDate by remember { mutableStateOf("2026-05-24") }
    var selectedSlot by remember { mutableStateOf("07:00 AM - 09:00 AM") }

    val dateOptions = listOf("2026-05-24", "2026-05-25", "2026-05-26", "2026-05-27")
    val slotOptions = listOf("07:00 AM - 09:00 AM", "10:00 AM - 12:00 PM", "04:30 PM - 06:30 PM", "07:00 PM - 09:00 PM")

    var checkoutStep by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("UPI") } // UPI, CARD, WALLET
    var discountCoupon by remember { mutableStateOf("") }
    var couponApplied by remember { mutableStateOf(false) }
    var finalPrice by remember { mutableStateOf(coach.sessionPrice) }
    var selectedGround by remember { mutableStateOf<CricketGround?>(null) }

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
            // Header backdrop back button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SportColors.DeepBlueHeader, SportColors.DarkBackground)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Coach Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(36.dp))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(40.dp))
                    if (coach.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = parseSportImageUrl(coach.imageUrl, androidx.compose.ui.platform.LocalContext.current),
                            contentDescription = coach.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    } else {
                        CoachAvatarIllustration(
                            modifier = Modifier
                                .size(90.dp)
                                .shadow(8.dp, CircleShape),
                            coachName = coach.name,
                            primaryColor = SportColors.ActiveBlue
                        )
                    }
                }
            }

            // Body Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = coach.name,
                        color = SportColors.TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (coach.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Coach",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = coach.skills,
                    color = SportColors.GlowBlueAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(1.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                        border = BorderStroke(1.dp, SportColors.CardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SportColors.GoldYellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${coach.rating} Rating",
                                color = SportColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${coach.reviewsCount} Reviews",
                                color = SportColors.TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(1.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                        border = BorderStroke(1.dp, SportColors.CardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = SportColors.GlowBlueAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${coach.experienceYears} Years",
                                color = SportColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Experience",
                                color = SportColors.TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bio Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                    border = BorderStroke(1.dp, SportColors.CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(16.dp)
                                    .background(SportColors.GlowBlueAccent, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Specialist Bio",
                                color = SportColors.TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = coach.bio,
                            color = SportColors.TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Certifications list block
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                    border = BorderStroke(1.dp, SportColors.CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SportColors.GoldYellow.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = SportColors.GoldYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Certifications & Credentials",
                                color = SportColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                coach.certifications,
                                color = SportColors.TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Coaching Videos Section
                Text(
                    text = "Coaching & Demonstration Videos 🎥",
                    color = SportColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Watch training drills, mechanic lessons, and game plans uploaded by Coach ${coach.name.split(" ").first()}:",
                    color = SportColors.TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // List of realistic curated videos per coach
                val coachSpecificVideos = remember(coach.id) {
                    when (coach.id) {
                        "coach_gautam" -> listOf(
                            "T20 Opening Strike Rotation Tactics" to "04:12",
                            "Confronting Left-Arm Offspin Drills" to "06:30",
                            "Powerplay Field Infiltration Masterclass" to "08:15"
                        )
                        "coach_rahul" -> listOf(
                            "The Ultimate Defensive Bat Grip & Balance" to "12:05",
                            "Leaving the Outswinging Leather Ball" to "09:40",
                            "Patience & High-Concentration Building Drills" to "15:20"
                        )
                        "coach_ravi" -> listOf(
                            "How to Dominate Bowlers from Ball 1" to "05:10",
                            "The Art of Launching Over mid-on" to "03:45",
                            "Aggressive Team Huddles & Mental Tactics" to "07:50"
                        )
                        else -> listOf(
                            "Specialist Cricket Warm-Up Exercises" to "05:00",
                            "Optimal Bat Speed Swing Coordination" to "04:15",
                            "Footwork Coordination Cone Drills" to "03:30"
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(coachSpecificVideos) { (title, duration) ->
                        var isPlaying by remember { mutableStateOf(false) }
                        
                        Card(
                            modifier = Modifier
                                .width(180.dp)
                                .clickable { isPlaying = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                            border = BorderStroke(1.dp, SportColors.CardBorder)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(Color(0xFF0F172A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play Video",
                                            tint = SportColors.SportGreen,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Text(
                                            text = duration,
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = title,
                                        color = SportColors.TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        if (isPlaying) {
                            AlertDialog(
                                onDismissRequest = { isPlaying = false },
                                title = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Playing: ${coach.name.split(" ").first()}'s Masterclass",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SportColors.ActiveBlue
                                        )
                                        IconButton(
                                            onClick = { isPlaying = false },
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
                                            Text("Audio streaming at high definition 1080p", color = SportColors.TextSecondary, fontSize = 10.sp)
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { isPlaying = false }) {
                                        Text("Done Watching", fontWeight = FontWeight.Bold, color = SportColors.ActiveBlue)
                                    }
                                },
                                containerColor = Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Scheduling System
                Text(
                    text = "Book Practice Match Slot",
                    color = SportColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose date and batch period:",
                    color = SportColors.TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Dates horizontal chips list
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    dateOptions.forEach { date ->
                        val isSelected = selectedDate == date
                        val (dayLabel, dayNum) = when (date) {
                            "2026-05-24" -> "Sun" to "24"
                            "2026-05-25" -> "Mon" to "25"
                            "2026-05-26" -> "Tue" to "26"
                            "2026-05-27" -> "Wed" to "27"
                            else -> "May" to date.substring(8)
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SportColors.GlowBlueAccent else SportColors.SoftCardBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else SportColors.CardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedDate = date }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayLabel.uppercase(),
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else SportColors.TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dayNum,
                                    color = if (isSelected) Color.White else SportColors.TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Slots matrix list selector
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slotOptions.chunked(2).forEach { rowSlots ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowSlots.forEach { slot ->
                                val isSelected = selectedSlot == slot
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .shadow(if (isSelected) 3.dp else 0.5.dp, RoundedCornerShape(10.dp))
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) SportColors.GlowBlueAccent else SportColors.SoftCardBg)
                                        .clickable { selectedSlot = slot }
                                        .border(
                                            1.dp,
                                            if (isSelected) Color.Transparent else SportColors.CardBorder,
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else SportColors.GlowBlueAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = slot,
                                            color = if (isSelected) Color.White else SportColors.TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nearby Verified Cricket Grounds Selection Option
                Text(
                    text = "Select Practice Ground (Verified)",
                    color = SportColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pick a nearby verified turf/stadium for your session:",
                    color = SportColors.TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(verifiedCricketGrounds) { ground ->
                        val isSelected = selectedGround?.id == ground.id
                        Box(
                            modifier = Modifier
                                .width(190.dp)
                                .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(SportColors.SoftCardBg)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF10B981) else SportColors.CardBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    selectedGround = if (isSelected) null else ground
                                }
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                ) {
                                    AsyncImage(
                                        model = parseSportImageUrl(ground.imageUrl, androidx.compose.ui.platform.LocalContext.current),
                                        contentDescription = ground.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Verified Badge on top of Image
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .background(Color(0xFF10B981), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            "Verified",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Selection Overlay Indicator
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0xFF10B981).copy(alpha = 0.25f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .background(Color(0xFF10B981), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "SELECTED",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = ground.name,
                                        color = SportColors.TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = SportColors.ActiveBlue,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = ground.distance,
                                            color = SportColors.TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "₹${ground.ratePerHour.toInt()}/hr",
                                            color = SportColors.GoldYellow,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = SportColors.GoldYellow,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Text(
                                                text = " ${ground.rating}",
                                                color = SportColors.TextPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detail verification showing selected ground status
                AnimatedVisibility(
                    visible = selectedGround != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    selectedGround?.let { gnd ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF10B981).copy(alpha = 0.08f)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected ground icon",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Selected Venue Ground Added!",
                                        color = Color(0xFF10B981),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${gnd.name} • ${gnd.distance} • ₹${gnd.ratePerHour.toInt()}/hr",
                                        color = SportColors.TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Rate details and Checkout CTA
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Session Fee",
                            color = SportColors.TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "₹${coach.sessionPrice.toInt()}/hr",
                            color = SportColors.TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    GradientButton(
                        onClick = { checkoutStep = true },
                        modifier = Modifier
                            .height(48.dp)
                            .width(180.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Instant Book 🎫", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        // Razorpay / Stripe dynamic Payment Checkout System popup
        if (checkoutStep) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { checkoutStep = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Gateway Header details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3B82F6))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "CricketPay Checkout Gateway",
                                    color = SportColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { checkoutStep = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = SportColors.TextPrimary)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = SportColors.CardBorder)

                        // Coach and Slot summaries
                        Text(
                            "CRICKET COACHING APPOINTMENT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportColors.ActiveBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Specialist: ${coach.name}",
                            color = SportColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Slot: $selectedDate at $selectedSlot",
                            color = SportColors.TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Coupon input
                        Text("Have a discount coupon?", color = SportColors.TextPrimary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = discountCoupon,
                                onValueChange = { discountCoupon = it },
                                placeholder = { Text("MUMBAIY100", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SportColors.ActiveBlue,
                                    focusedTextColor = SportColors.TextPrimary,
                                    unfocusedTextColor = SportColors.TextPrimary,
                                    unfocusedBorderColor = SportColors.CardBorder,
                                    focusedLabelColor = SportColors.ActiveBlue,
                                    unfocusedLabelColor = SportColors.TextSecondary
                                ),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (discountCoupon.uppercase() == "MUMBAIY100") {
                                        finalPrice = maxOf(0.0, coach.sessionPrice - 100.0)
                                        couponApplied = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SportColors.ActiveBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Apply", color = Color.White, fontSize = 11.sp)
                            }
                        }
                        if (couponApplied) {
                            Text("Code Applied! ₹100 Flat discount credited.", color = SportColors.SportGreen, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Pricing calculation details
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Base session fare", color = SportColors.TextSecondary, fontSize = 12.sp)
                            Text("₹${coach.sessionPrice}", color = SportColors.TextPrimary, fontSize = 12.sp)
                        }
                        if (selectedGround != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Ground rental fee (${selectedGround!!.name})", color = SportColors.TextSecondary, fontSize = 12.sp)
                                Text("₹${selectedGround!!.ratePerHour}", color = SportColors.TextPrimary, fontSize = 12.sp)
                            }
                        }
                        if (couponApplied) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Discount Deduction", color = SportColors.SportGreen, fontSize = 12.sp)
                                Text("- ₹100.0", color = SportColors.SportGreen, fontSize = 12.sp)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gateway taxes & insurance", color = SportColors.TextSecondary, fontSize = 12.sp)
                            Text("₹35.0", color = SportColors.TextPrimary, fontSize = 12.sp)
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = SportColors.CardBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL AMOUNT DUE", color = SportColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            val groundPrice = selectedGround?.ratePerHour ?: 0.0
                            Text("₹${finalPrice + groundPrice + 35.0}", color = SportColors.GoldYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Complete Booking Action
                        GradientButton(
                            onClick = {
                                val notesText = if (selectedGround != null) {
                                    "Venue reserved: ${selectedGround?.name} (${selectedGround?.distance}). Price: ₹${selectedGround?.ratePerHour?.toInt()}/hr. " +
                                             "Practice focus: ${viewModel.userProfile.value.preferredSkills}."
                                } else {
                                    "Practice focus: ${viewModel.userProfile.value.preferredSkills}. Bring sports safety guards."
                                }
                                viewModel.bookSession(coach, selectedDate, selectedSlot, notesText)
                                checkoutStep = false
                                onBookingSuccess()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            testTag = "submit_button",
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Pay and Reserve Instant 🛡️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

val verifiedCricketGrounds = listOf(
    CricketGround(
        id = "ground_1",
        name = "Shivaji Park Turf",
        imageUrl = "android.resource://com.example/" + R.drawable.img_cricket_nets,
        distance = "1.2 km away",
        ratePerHour = 400.0,
        rating = 4.8f
    ),
    CricketGround(
        id = "ground_2",
        name = "MCA Academy Ground",
        imageUrl = "android.resource://com.example/" + R.drawable.img_cricket_stadium,
        distance = "2.8 km away",
        ratePerHour = 750.0,
        rating = 4.9f
    ),
    CricketGround(
        id = "ground_3",
        name = "Wankhede Practice Turf",
        imageUrl = "android.resource://com.example/" + R.drawable.img_scoreboard,
        distance = "3.5 km away",
        ratePerHour = 650.0,
        rating = 4.7f
    ),
    CricketGround(
        id = "ground_4",
        name = "CCI Brabourne Nets",
        imageUrl = "android.resource://com.example/" + R.drawable.img_group_banner,
        distance = "5.0 km away",
        ratePerHour = 500.0,
        rating = 4.6f
    )
)
