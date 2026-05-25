package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.Coach
import com.example.ui.viewmodel.AppMode
import com.example.ui.viewmodel.CricketViewModel

@Composable
fun HomeTab(
    viewModel: CricketViewModel,
    onCoachClick: (Coach) -> Unit,
    onGroupSessionClick: () -> Unit
) {
    val appMode by viewModel.appMode.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val filteredCoaches by viewModel.filteredCoaches.collectAsState()
    val searchQuery by viewModel.coachSearchQuery.collectAsState()
    val selectedSkillChip by viewModel.selectedSkillChip.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedNearbyService by remember { mutableStateOf<NearbyMoreService?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportColors.DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp)
        ) {
            // Dark blue gradient sporty header block matching the reference screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SportColors.DeepBlueHeader, Color(0xFF132252), SportColors.DarkBackground)
                        )
                    )
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .statusBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Top row: Avatar, Greeting, notification widget, & Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SportColors.SoftCardBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "User Initials", tint = SportColors.GlowBlueAccent)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Greeting and Location
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hello, ${userProfile.name}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { /* Simulate location select */ }
                            ) {
                                Text(
                                    text = userProfile.location,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Change Location",
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        // Notification Icon (Matches Bell on reference image)
                        IconButton(
                            onClick = { /* Actions */ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            BadgedBox(
                                badge = { Badge { Text("1") } }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Title: Pick skill whistle/coach matching image
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Pick skill ",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_whistle),
                            contentDescription = "Whistle",
                            modifier = Modifier.size(45.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Find coach",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Search input matching mockup: Pill shape, gradient border, custom search icon with AI sparkle star
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(Color(0xFF0F1E4C).copy(alpha = 0.25f))
                            .border(
                                width = 1.6.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFE2E8F0).copy(alpha = 0.9f),
                                        SportColors.GlowBlueAccent
                                    )
                                ),
                                shape = CircleShape
                            )
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchCoaches(it) },
                            placeholder = {
                                Text(
                                    text = if (appMode == AppMode.LEARNING) "What skill do you want to learn?" else "Search players or matches near by...",
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_field_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                errorBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                            singleLine = true,
                            trailingIcon = {
                                CustomSearchIcon(
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .offset(x = (-4).dp)
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 13 Important Batting & Bowling Skill Chips
                    val chips = listOf(
                        "Bat swing",
                        "Footwork",
                        "Bat control",
                        "Cover drive",
                        "Pull shot",
                        "Straight drive",
                        "Out-swing",
                        "In-swing",
                        "Off-spin",
                        "Leg-spin",
                        "Yorker",
                        "Bouncer",
                        "Power hitting"
                    )

                    val chipsScrollState = rememberScrollState()

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(chipsScrollState)
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            chips.forEach { skill ->
                                val isSelected = selectedSkillChip == skill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) {
                                                Color(0xFF2D46CD).copy(alpha = 0.85f)
                                            } else {
                                                Color(0xFF0F1E4C).copy(alpha = 0.45f)
                                            }
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) {
                                                Color(0xFF818CF8)
                                            } else {
                                                Color(0xFFE2E8F0).copy(alpha = 0.15f)
                                            },
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { viewModel.selectSkillChip(skill) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        SkillChipIcon(
                                            skill = skill,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = skill,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom hardware-accelerated horizontal scrollbar tracker
                        val density = androidx.compose.ui.platform.LocalDensity.current
                        val maxScroll = chipsScrollState.maxValue
                        val currentScroll = chipsScrollState.value
                        val handleWidth = 24.dp
                        val trackWidth = 72.dp
                        val availableSpacePx = with(density) { (trackWidth - handleWidth).toPx() }

                        if (maxScroll > 0) {
                            Box(
                                modifier = Modifier
                                    .width(trackWidth)
                                    .height(3.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                            ) {
                                val progress = currentScroll.toFloat() / maxScroll
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(handleWidth)
                                        .graphicsLayer {
                                            translationX = progress * availableSpacePx
                                        }
                                        .clip(CircleShape)
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    SportColors.GlowBlueAccent,
                                                    Color(0xFF818CF8)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Auto-scrolling decorative cricket quotes ticker
                    HomeCricketQuotesTicker(primaryColor = SportColors.ActiveBlue)
                }
            }

            // CORE VARIABLE RENDER ACCORDING TO ACTIVE APP-MODE
            if (appMode == AppMode.LEARNING) {
                // LEANING MODE ELEMENTS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Doorstep Coaching Finder Radar component on the Landing Page
                    DoorstepCoachingSection(
                        coaches = filteredCoaches,
                        onCoachClick = onCoachClick
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Top coaches near by you",
                            color = SportColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showFilterSheet = !showFilterSheet },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter Coaches",
                                tint = SportColors.TextPrimary
                            )
                        }
                    }

                    if (filteredCoaches.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SportColors.SoftCardBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No coaches found matching current filters. Click filter list to reset.",
                                color = SportColors.TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        // Horizontal Horizontal Row matching reference image with compact width to fit at least three cards at once
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filteredCoaches) { coach ->
                                Box(
                                    modifier = Modifier
                                        .width(112.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { onCoachClick(coach) }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Visual character box or actual image
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(90.dp)
                                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                        ) {
                                            if (coach.imageUrl.isNotEmpty()) {
                                                AsyncImage(
                                                    model = coach.imageUrl,
                                                    contentDescription = coach.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Brush.verticalGradient(
                                                                colors = listOf(
                                                                    Color(0xFFE2E6EF),
                                                                    Color(0xFFCBD5E1)
                                                                )
                                                            )
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CoachAvatarIllustration(
                                                        modifier = Modifier.size(45.dp),
                                                        coachName = coach.name,
                                                        primaryColor = if (coach.id == "coach_1") SportColors.DeepBlueHeader else SportColors.ActiveBlue
                                                    )
                                                }
                                            }

                                            // Rating Badge overlay on the bottom-left of the image (smaller size)
                                            Row(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(6.dp)
                                                    .background(
                                                        color = Color(0xFFF59E0B),
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = "%.1f".format(coach.rating),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }

                                        // Text details styled cleanly inside a smaller container
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White)
                                                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 8.dp)
                                        ) {
                                            Text(
                                                text = coach.name,
                                                color = Color(0xFF0F1E4C),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Text(
                                                text = coach.skills,
                                                color = Color.Black.copy(alpha = 0.5f),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Normal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Rs.${coach.sessionPrice.toInt()}/hr",
                                                    color = Color(0xFF9E6422),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                
                                                if (coach.isVerified) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(5.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(0xFF10B981))
                                                        )
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Text(
                                                            text = "OK",
                                                            color = Color(0xFF10B981),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // "Find more near by" Section (Net Bowlers, Commentators, Umpires, Fitness Staff etc.)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                tint = SportColors.ActiveBlue,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Find more near by",
                                color = SportColors.TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(nearbyMoreServices) { service ->
                            val isSelected = selectedNearbyService?.id == service.id
                            Card(
                                modifier = Modifier
                                    .width(132.dp)
                                    .height(115.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedNearbyService = if (isSelected) null else service
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) SportColors.ActiveBlue.copy(alpha = 0.08f) else SportColors.SoftCardBg
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) SportColors.ActiveBlue.copy(alpha = 0.7f) else SportColors.CardBorder.copy(alpha = 0.5f)
                                )
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
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        // The main service icon using the shared colorful png images
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(service.iconColor.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(id = service.iconResId),
                                                contentDescription = service.title,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }

                                        // Selection tick overlay
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF10B981), CircleShape)
                                                    .size(18.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = service.title,
                                            color = SportColors.TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF10B981))
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${service.countNearby} nearby",
                                                color = SportColors.TextSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Selected Service Listing Section (Interactive details below the scroller)
                    AnimatedVisibility(
                        visible = selectedNearbyService != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        selectedNearbyService?.let { service ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SportColors.ActiveBlue.copy(alpha = 0.04f))
                                    .border(1.dp, SportColors.ActiveBlue.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(service.iconColor.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(id = service.iconResId),
                                                contentDescription = service.title,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Available ${service.title} (${service.countNearby})",
                                            color = SportColors.TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "Close",
                                        color = SportColors.TextSecondary,
                                        fontSize = 11.sp,
                                        modifier = Modifier
                                            .clickable { selectedNearbyService = null }
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                service.professionals.forEach { prof ->
                                    val rateVal = try {
                                        prof.rate.replace("₹", "").split("/").firstOrNull()?.toDoubleOrNull() ?: 150.0
                                    } catch (e: Exception) {
                                        150.0
                                    }
                                    val fakeCoach = Coach(
                                        id = "nearby_" + service.id + "_" + prof.name.replace(" ", "_").lowercase(),
                                        name = prof.name + " (" + service.title.removeSuffix("y").removeSuffix("s") + ")",
                                        imageUrl = service.imageUrl,
                                        skills = service.title + " • " + prof.specialty,
                                        rating = prof.rating,
                                        reviewsCount = (15..45).random(),
                                        isVerified = prof.isVerified,
                                        bio = "Certified professional with ${prof.experience} of dedicated field experience. Specializes in ${prof.specialty} with exceptional performance standard.",
                                        experienceYears = prof.experience.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 8,
                                        certifications = if (prof.isVerified) "Verified ${service.title.removeSuffix("s")} License" else "Standard Certification",
                                        sessionPrice = rateVal,
                                        location = prof.distance,
                                        availableDays = "Mon, Tue, Wed, Thu, Fri, Sat, Sun"
                                    )
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                onCoachClick(fakeCoach)
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        ),
                                        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Avatar
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(service.iconColor.copy(alpha = 0.1f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = service.iconColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = prof.name,
                                                        color = SportColors.TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    if (prof.isVerified) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(
                                                            imageVector = Icons.Default.Verified,
                                                            contentDescription = "Verified",
                                                            tint = Color(0xFF10B981),
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "${prof.specialty} • ${prof.experience}",
                                                    color = SportColors.TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                                Text(
                                                    text = "📍 ${prof.distance}",
                                                    color = SportColors.GlowBlueAccent,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = prof.rate,
                                                    color = SportColors.GoldYellow,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = SportColors.GoldYellow,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = " ${prof.rating}",
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
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Group session Near By section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                tint = SportColors.TextPrimary,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Group session near by you",
                                color = SportColors.TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "See More",
                            color = SportColors.GlowBlueAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onGroupSessionClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Group session single clickable image banner directly as requested
                    Image(
                        painter = painterResource(id = R.drawable.img_group_banner),
                        contentDescription = "Group Batting Session Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onGroupSessionClick() },
                        contentScale = ContentScale.FillWidth
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                }
            } else {
                // PRACTICE MODE ELEMENTS (Games joining, net hires, helper finders)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Cricket Matches & Partners Nearby",
                        color = SportColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Connect with local players looking to practice right now.",
                        color = SportColors.TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // List of nearby players
                    viewModel.nearbyPlayers.forEach { player ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(SportColors.ActiveBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsCricket,
                                        contentDescription = null,
                                        tint = SportColors.GlowBlueAccent
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(
                                        text = player.name,
                                        color = SportColors.TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${player.specialization} (${player.skillLevel})",
                                        color = SportColors.GlowBlueAccent,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "📍 ${player.location} (${player.distanceKm} km)",
                                        color = SportColors.TextSecondary,
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Button(
                                    onClick = { /* Simulated request match */ },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportColors.ActiveBlue),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                ) {
                                    Text("Invite", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Scorers & Helpers Hires block
                    Text(
                        text = "Match Helpers & Certified Umpires",
                        color = SportColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(viewModel.helperUmpires) { helper ->
                            Card(
                                modifier = Modifier.width(180.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Badge(containerColor = SportColors.GoldYellow) {
                                            Text(
                                                "★ ${helper.rating}",
                                                color = Color.Black,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Verified",
                                            color = SportColors.SportGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = helper.name,
                                        color = SportColors.TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = helper.role,
                                        color = SportColors.TextSecondary,
                                        fontSize = 10.sp,
                                        minLines = 2,
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₹${helper.chargePerHour.toInt()}/hr",
                                            color = SportColors.GlowBlueAccent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Button(
                                            onClick = { /* Hire simulation */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = SportColors.ActiveBlue),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                        ) {
                                            Text("Hire", fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Active Practice Nets Hires
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text(
                                    "Locked and Loaded Nets Booking",
                                    color = SportColors.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Rent out top concrete, turf, or indoor cricket tunnels with auto bowling machines in Mumbai.",
                                    color = SportColors.TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { /* Net booking */ },
                                colors = ButtonDefaults.buttonColors(containerColor = SportColors.BrightOrange)
                            ) {
                                Text("Book Net", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Expanded Bottom Sheet Filters simulator
        if (showFilterSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showFilterSheet = false }
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
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Filter Coaches Nearby",
                            color = SportColors.TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Minimum Star Rating", color = SportColors.TextSecondary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(3.5f, 4.0f, 4.5f, 4.8f).forEach { stars ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SportColors.DarkBackground)
                                        .clickable {
                                            viewModel.setRatingFilter(stars)
                                            showFilterSheet = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text("★ $stars+", color = SportColors.TextPrimary, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        GradientButton(
                            onClick = {
                                viewModel.setRatingFilter(null)
                                viewModel.selectSkillChip(null)
                                viewModel.searchCoaches("")
                                showFilterSheet = false
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reset All Active Filters", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSearchIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        // Scaled coordinates based on standard 24x24 design
        val scaleX = w / 24f
        val scaleY = h / 24f

        val cx = 10f * scaleX
        val cy = 10f * scaleY
        val r = 5.5f * scaleX

        // 1. Draw Ring (Lens)
        drawCircle(
            color = tint,
            radius = r,
            center = androidx.compose.ui.geometry.Offset(cx, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth
            )
        )

        // 2. Draw Handle
        val handleStart = androidx.compose.ui.geometry.Offset(
            cx + r * 0.707f,
            cy + r * 0.707f
        )
        val handleEnd = androidx.compose.ui.geometry.Offset(
            20f * scaleX,
            20f * scaleY
        )
        drawLine(
            color = tint,
            start = handleStart,
            end = handleEnd,
            strokeWidth = strokeWidth * 1.1f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // 3. Draw Overlapping 4-point Sparkle Star
        val starX = 17.5f * scaleX
        val starY = 6.5f * scaleY
        val starHalf = 4.5f * scaleX

        val starPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(starX, starY - starHalf)
            quadraticTo(starX, starY, starX + starHalf, starY)
            quadraticTo(starX, starY, starX, starY + starHalf)
            quadraticTo(starX, starY, starX - starHalf, starY)
            quadraticTo(starX, starY, starX, starY - starHalf)
            close()
        }
        drawPath(starPath, color = tint)
    }
}

@Composable
fun SkillChipIcon(skill: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        when (skill) {
            "Bat swing" -> {
                rotate(-45f, pivot = androidx.compose.ui.geometry.Offset(w/2f, h/2f)) {
                    // Wooden bat body centered
                    drawRoundRect(
                        color = Color(0xFFCBB093), // Tan wood
                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.4f),
                        size = androidx.compose.ui.geometry.Size(w * 0.24f, h * 0.55f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    // Red handle grip
                    drawRoundRect(
                        color = Color(0xFFEF4444), // Red grip
                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.44f, h * 0.05f),
                        size = androidx.compose.ui.geometry.Size(w * 0.12f, h * 0.38f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )
                    // Separator ring
                    drawRect(
                        color = Color(0xFF1E293B),
                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.4f),
                        size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.04f)
                    )
                }
            }
            "Footwork" -> {
                val path = androidx.compose.ui.graphics.Path().apply {
                     moveTo(w * 0.82f, h * 0.32f)
                     quadraticTo(w * 0.88f, h * 0.55f, w * 0.82f, h * 0.75f)
                     lineTo(w * 0.22f, h * 0.75f)
                     quadraticTo(w * 0.12f, h * 0.72f, w * 0.15f, h * 0.58f)
                     lineTo(w * 0.35f, h * 0.48f)
                     lineTo(w * 0.52f, h * 0.35f)
                     lineTo(w * 0.72f, h * 0.30f)
                     close()
                }
                drawPath(path, color = Color(0xFF3B82F6)) // Royal Blue main
                
                val solePath = androidx.compose.ui.graphics.Path().apply {
                     moveTo(w * 0.15f, h * 0.73f)
                     lineTo(w * 0.85f, h * 0.73f)
                     lineTo(w * 0.82f, h * 0.82f)
                     lineTo(w * 0.18f, h * 0.82f)
                     close()
                }
                drawPath(solePath, color = Color.White)
                
                val stripeWidth = 1.2.dp.toPx()
                drawLine(
                    color = Color(0xFF93C5FD),
                    start = androidx.compose.ui.geometry.Offset(w * 0.55f, h * 0.45f),
                    end = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.65f),
                    strokeWidth = stripeWidth
                )
                drawLine(
                    color = Color(0xFF93C5FD),
                    start = androidx.compose.ui.geometry.Offset(w * 0.61f, h * 0.43f),
                    end = androidx.compose.ui.geometry.Offset(w * 0.51f, h * 0.63f),
                    strokeWidth = stripeWidth
                )
                drawLine(
                    color = Color(0xFF93C5FD),
                    start = androidx.compose.ui.geometry.Offset(w * 0.67f, h * 0.41f),
                    end = androidx.compose.ui.geometry.Offset(w * 0.57f, h * 0.61f),
                    strokeWidth = stripeWidth
                )
            }
            "Bat control" -> {
                rotate(45f, pivot = androidx.compose.ui.geometry.Offset(w/2f, h/2f)) {
                    drawRoundRect(
                        color = Color(0xFFFBBF24), // Vibrant Yellow
                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.4f),
                        size = androidx.compose.ui.geometry.Size(w * 0.24f, h * 0.52f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFFEC4899), // Pink grip
                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.44f, h * 0.08f),
                        size = androidx.compose.ui.geometry.Size(w * 0.12f, h * 0.35f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )
                }
                drawCircle(
                    color = Color(0xFFDC2626), // Cricket ball
                    radius = 2.4.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.72f)
                )
            }
            "Cover drive" -> {
                rotate(-30f) {
                    drawRoundRect(
                        color = Color(0xFFCBB093),
                        topLeft = androidx.compose.ui.geometry.Offset(w*0.42f, h*0.35f),
                        size = androidx.compose.ui.geometry.Size(w*0.16f, h*0.55f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFFEF4444),
                        topLeft = androidx.compose.ui.geometry.Offset(w*0.46f, h*0.05f),
                        size = androidx.compose.ui.geometry.Size(w*0.08f, h*0.3f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(0.8.dp.toPx(), 0.8.dp.toPx())
                    )
                }
                val starX = w * 0.78f
                val starY = h * 0.65f
                val starHalf = 3.5.dp.toPx()
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(starX, starY - starHalf)
                    quadraticTo(starX, starY, starX + starHalf, starY)
                    quadraticTo(starX, starY, starX, starY + starHalf)
                    quadraticTo(starX, starY, starX - starHalf, starY)
                    quadraticTo(starX, starY, starX, starY - starHalf)
                    close()
                }
                drawPath(path, color = Color(0xFFFBBF24))
            }
            "Pull shot" -> {
                rotate(80f, pivot = androidx.compose.ui.geometry.Offset(w/2f, h/2f)) {
                    drawRoundRect(
                        color = Color(0xFFCBB093),
                        topLeft = androidx.compose.ui.geometry.Offset(w*0.42f, h*0.35f),
                        size = androidx.compose.ui.geometry.Size(w*0.16f, h*0.55f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFF10B981),
                        topLeft = androidx.compose.ui.geometry.Offset(w*0.46f, h*0.05f),
                        size = androidx.compose.ui.geometry.Size(w*0.08f, h*0.3f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(0.8.dp.toPx(), 0.8.dp.toPx())
                    )
                }
                drawArc(
                    color = Color.White.copy(alpha = 0.8f),
                    startAngle = 140f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.1f, h * 0.1f),
                    size = androidx.compose.ui.geometry.Size(w * 0.8f, h * 0.8f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
            "Straight drive" -> {
                drawRoundRect(
                    color = Color(0xFFCBB093),
                    topLeft = androidx.compose.ui.geometry.Offset(w*0.42f, h*0.4f),
                    size = androidx.compose.ui.geometry.Size(w*0.16f, h*0.52f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFFF59E0B),
                    topLeft = androidx.compose.ui.geometry.Offset(w*0.46f, h*0.08f),
                    size = androidx.compose.ui.geometry.Size(w*0.08f, h*0.32f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(0.8.dp.toPx(), 0.8.dp.toPx())
                )
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = androidx.compose.ui.geometry.Offset(w*0.25f, h*0.8f),
                    end = androidx.compose.ui.geometry.Offset(w*0.25f, h*0.3f),
                    strokeWidth = 1.2.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = androidx.compose.ui.geometry.Offset(w*0.75f, h*0.8f),
                    end = androidx.compose.ui.geometry.Offset(w*0.75f, h*0.3f),
                    strokeWidth = 1.2.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
            "Out-swing" -> {
                drawCircle(color = Color(0xFFDC2626), radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w*0.4f, h*0.5f))
                rotate(-20f, pivot = androidx.compose.ui.geometry.Offset(w*0.4f, h*0.5f)) {
                    drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(w*0.4f, h*0.5f - 5.dp.toPx()), end = androidx.compose.ui.geometry.Offset(w*0.4f, h*0.5f + 5.dp.toPx()), strokeWidth = 1.2.dp.toPx())
                }
                val swingPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f, h * 0.75f)
                    quadraticTo(w * 0.7f, h * 0.65f, w * 0.85f, h * 0.35f)
                }
                drawPath(swingPath, color = Color(0xFF60A5FA), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
            "In-swing" -> {
                drawCircle(color = Color(0xFFDC2626), radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w*0.6f, h*0.5f))
                rotate(20f, pivot = androidx.compose.ui.geometry.Offset(w*0.6f, h*0.5f)) {
                    drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(w*0.6f, h*0.5f - 5.dp.toPx()), end = androidx.compose.ui.geometry.Offset(w*0.6f, h*0.5f + 5.dp.toPx()), strokeWidth = 1.2.dp.toPx())
                }
                val swingPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f, h * 0.75f)
                    quadraticTo(w * 0.3f, h * 0.65f, w * 0.15f, h * 0.35f)
                }
                drawPath(swingPath, color = Color(0xFFF87171), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
            "Off-spin" -> {
                drawArc(
                    color = Color(0xFFA78BFA),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(w*0.15f, h*0.15f),
                    size = androidx.compose.ui.geometry.Size(w*0.7f, h*0.7f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(color = Color(0xFFDC2626), radius = 3.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w*0.5f, h*0.5f))
            }
            "Leg-spin" -> {
                drawArc(
                    color = Color(0xFF22D3EE),
                    startAngle = 90f,
                    sweepAngle = 280f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(w*0.1f, h*0.2f),
                    size = androidx.compose.ui.geometry.Size(w*0.8f, h*0.6f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(color = Color(0xFFDC2626), radius = 3.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w*0.5f, h*0.5f))
            }
            "Yorker" -> {
                drawLine(color = Color.White.copy(alpha = 0.5f), start = androidx.compose.ui.geometry.Offset(w*0.5f, h*0.15f), end = androidx.compose.ui.geometry.Offset(w*0.5f, h*0.85f), strokeWidth = 2.dp.toPx())
                drawCircle(color = Color(0xFFDC2626), radius = 3.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w*0.5f, h*0.8f))
                drawCircle(color = Color(0xFFFBBF24), radius = 1.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w*0.5f, h*0.8f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.0.dp.toPx()))
            }
            "Bouncer" -> {
                drawLine(color = Color.White.copy(alpha = 0.3f), start = androidx.compose.ui.geometry.Offset(w*0.1f, h*0.85f), end = androidx.compose.ui.geometry.Offset(w*0.9f, h*0.85f), strokeWidth = 1.dp.toPx())
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.15f, h * 0.5f)
                    lineTo(w * 0.45f, h * 0.85f)
                    lineTo(w * 0.8f, h * 0.25f)
                }
                drawPath(path, color = Color(0xFFF472B6), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                drawCircle(color = Color(0xFFDC2626), radius = 2.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.25f))
            }
            "Power hitting" -> {
                val fireTail = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w*0.12f, h*0.5f)
                    quadraticTo(w*0.35f, h*0.38f, w*0.62f, h*0.45f)
                    lineTo(w*0.55f, h*0.55f)
                    quadraticTo(w*0.3f, h*0.62f, w*0.12f, h*0.5f)
                }
                drawPath(fireTail, color = Color(0xFFF59E0B))
                drawCircle(color = Color(0xFFDC2626), radius = 3.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w*0.65f, h*0.5f))
                drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w*0.65f, h*0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.8.dp.toPx()))
            }
        }
    }
}

@Composable
fun HomeCricketQuotesTicker(
    modifier: Modifier = Modifier,
    primaryColor: Color = SportColors.ActiveBlue
) {
    val quotes = listOf(
        "\"Enjoy the game & chase your dreams.\" — Sachin Tendulkar",
        "\"Cricket is a pressure game, when you do well, you enjoy.\" — Kapil Dev",
        "\"My job is to perform and get selected.\" — Virat Kohli",
        "\"Every defeat is a victory in itself.\" — MS Dhoni",
        "\"You don't play for the crowd, you play for country.\" — MS Dhoni",
        "\"No cricket team in the world depends on one or two players.\" — Virat Kohli",
        "\"First of all, love cricket, then enjoy it.\" — Sachin Tendulkar"
    )

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(4500)
            currentIndex = (currentIndex + 1) % quotes.size
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SportColors.ActiveBlue.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, SportColors.ActiveBlue.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SportsCricket,
                contentDescription = "Cricket Icon",
                tint = SportColors.ActiveBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                Crossfade(
                    targetState = quotes[currentIndex],
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 600),
                    label = "QuoteAnimation"
                ) { quoteText ->
                    Text(
                        text = quoteText,
                        color = SportColors.TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

data class NearbyMoreService(
    val id: String,
    val title: String,
    val countNearby: Int,
    val imageUrl: String,
    val iconResId: Int,
    val iconColor: Color,
    val professionals: List<NearbyProfessional>
)

data class NearbyProfessional(
    val name: String,
    val experience: String,
    val rating: Float,
    val rate: String,
    val distance: String,
    val specialty: String,
    val isVerified: Boolean = true
)

val nearbyMoreServices = listOf(
    NearbyMoreService(
        id = "net_bowlers",
        title = "Net Bowlers",
        countNearby = 15,
        imageUrl = "https://images.unsplash.com/photo-1540747737956-378724044492?auto=format&fit=crop&q=80&w=300",
        iconResId = R.drawable.img_cricket_nets,
        iconColor = Color(0xFF10B981),
        professionals = listOf(
            NearbyProfessional("Arjun Singh", "12 years exp", 4.9f, "₹150/session", "1.2 km away", "Right-Arm Medium Fast"),
            NearbyProfessional("Vijay Kumar", "8 years exp", 4.7f, "₹120/session", "2.8 km away", "Left-Arm Orthodox Spin")
        )
    ),
    NearbyMoreService(
        id = "commentators",
        title = "Commentators",
        countNearby = 8,
        imageUrl = "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?auto=format&fit=crop&q=80&w=300",
        iconResId = R.drawable.img_commentator,
        iconColor = Color(0xFFF97316),
        professionals = listOf(
            NearbyProfessional("Sanjay Mehta", "15 years exp", 4.9f, "₹1500/match", "0.5 km away", "English & Hindi Commentary"),
            NearbyProfessional("Rohan Joshi", "6 years exp", 4.5f, "₹800/match", "4.2 km away", "English & Marathi Expert")
        )
    ),
    NearbyMoreService(
        id = "anchors",
        title = "Anchors",
        countNearby = 4,
        imageUrl = "https://images.unsplash.com/photo-1478737270239-2f02b77fc618?auto=format&fit=crop&q=80&w=300",
        iconResId = R.drawable.img_microphone,
        iconColor = Color(0xFF3B82F6),
        professionals = listOf(
            NearbyProfessional("Tina Sen", "9 years exp", 4.8f, "₹2000/event", "1.5 km away", "Live Presentation & Interview"),
            NearbyProfessional("Dev Arya", "5 years exp", 4.6f, "₹1200/event", "3.0 km away", "Corporate & Match Emcee")
        )
    ),
    NearbyMoreService(
        id = "umpires",
        title = "Umpires",
        countNearby = 12,
        imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&q=80&w=300",
        iconResId = R.drawable.img_umpire,
        iconColor = Color(0xFF1D4ED8),
        professionals = listOf(
            NearbyProfessional("K. Raghavan", "19 years exp", 4.9f, "₹1000/day", "2.0 km away", "BCCI Certified Level 2"),
            NearbyProfessional("Pradeep Patil", "10 years exp", 4.7f, "₹600/day", "3.8 km away", "State Association Licensed")
        )
    ),
    NearbyMoreService(
        id = "groundsmen",
        title = "Groundsmen",
        countNearby = 6,
        imageUrl = "https://images.unsplash.com/photo-1589923188900-85dae523342b?auto=format&fit=crop&q=80&w=300",
        iconResId = R.drawable.img_cricket_stadium,
        iconColor = Color(0xFF047857),
        professionals = listOf(
            NearbyProfessional("Madan Lal", "20 years exp", 4.9f, "₹2500/day", "1.1 km away", "Pitch Curator & Soil Expert"),
            NearbyProfessional("Somu Gowda", "11 years exp", 4.6f, "₹1200/day", "5.4 km away", "Outfield Maintenance Specialist")
        )
    ),
    NearbyMoreService(
        id = "scorers",
        title = "Scorers",
        countNearby = 10,
        imageUrl = "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?auto=format&fit=crop&q=80&w=300",
        iconResId = R.drawable.img_scoreboard,
        iconColor = Color(0xFF475569),
        professionals = listOf(
            NearbyProfessional("Nitin Desai", "7 years exp", 4.7f, "₹500/match", "2.5 km away", "Digital CricHero Specialist"),
            NearbyProfessional("Ananya Rao", "4 years exp", 4.8f, "₹400/match", "1.9 km away", "Manual & Digital Stats Pro")
        )
    ),
    NearbyMoreService(
        id = "fitness_staff",
        title = "Fitness Staff",
        countNearby = 14,
        imageUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&q=80&w=300",
        iconResId = R.drawable.img_fitness_trainer,
        iconColor = Color(0xFFEF4444),
        professionals = listOf(
            NearbyProfessional("Dr. Amit Roy", "13 years exp", 4.9f, "₹1000/hr", "2.1 km away", "Sports Physio & Rehab"),
            NearbyProfessional("Coach Sunil", "9 years exp", 4.8f, "₹600/hr", "4.0 km away", "Strength & Conditioning Trainer")
        )
    )
)

enum class DoorstepSearchState {
    INITIAL,
    LOADING,
    COMPLETED
}

@Composable
fun DoorstepCoachingSection(
    coaches: List<Coach>,
    onCoachClick: (Coach) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchState by remember { mutableStateOf(DoorstepSearchState.INITIAL) }
    var progressText by remember { mutableStateOf("Scanning local cricket nets...") }

    LaunchedEffect(searchState) {
        if (searchState == DoorstepSearchState.LOADING) {
            progressText = "Scanning nearby nets and cricket pitch fields..."
            kotlinx.coroutines.delay(800)
            progressText = "Analyzing distance for doorstep-certified coaches..."
            kotlinx.coroutines.delay(800)
            progressText = "Finding slots with available travel buffers..."
            kotlinx.coroutines.delay(600)
            searchState = DoorstepSearchState.COMPLETED
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
    ) {
        // --- GLASSMORPHISM BACKDROP GLOW ORBS ---
        // A vibrant green glow in the upper right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .size(160.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF53F38D).copy(alpha = 0.45f),
                            Color(0xFF53F38D).copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // A warm orange glow near the map area or bottom-left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
                .size(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFB923C).copy(alpha = 0.4f),
                            Color(0xFFFB923C).copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Additional subtle indigo flare center-left to add richness
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = (-30).dp)
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // --- GLASS CARD FRONT CONTAINER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A).copy(alpha = 0.55f), // Glass translucent slate 900
                            Color(0xFF1E293B).copy(alpha = 0.45f)  // Glass translucent slate 800
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.22f), // Strong highlighted top edge
                            Color.White.copy(alpha = 0.06f)  // Fading bottom edge
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (searchState == DoorstepSearchState.INITIAL) {
                    Text(
                        text = "Coach at Your Doorstep?",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "We offer mobile coaching sessions at your local nets. Check availability in your area.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { searchState = DoorstepSearchState.LOADING },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF53F38D),
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Check My Area",
                                color = Color(0xFF0F172A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(145.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFB923C),
                                            Color(0xFFEA580C)
                                        )
                                    ),
                                    CircleShape
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = 65.dp, y = (-50).dp)
                                .size(32.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF53F38D),
                                            Color(0xFF10B981)
                                        )
                                    ),
                                    CircleShape
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = (-70).dp, y = 60.dp)
                                .size(24.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF93C5FD),
                                            Color(0xFF3B82F6)
                                        )
                                    ),
                                    CircleShape
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        )
                    }

                } else if (searchState == DoorstepSearchState.LOADING) {
                    Text(
                        text = "Coach at Your Doorstep?",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF53F38D).copy(alpha = 0.2f),
                                            Color(0xFF53F38D).copy(alpha = 0.02f)
                                        )
                                    ),
                                    CircleShape
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF53F38D),
                                modifier = Modifier.size(54.dp),
                                strokeWidth = 3.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = progressText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Checking professional radius...",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Doorstep Coaches Near You",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Reset",
                            color = Color(0xFF53F38D),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { searchState = DoorstepSearchState.INITIAL }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "These active professional mentors are verified to travel directly to your local nets in Mumbai:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val doorstepVerified = coaches.filter { 
                        it.id == "coach_helen" || it.id == "coach_mark" || it.id == "coach_gautam"
                    }.ifEmpty { coaches.take(2) }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        doorstepVerified.forEach { coach ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCoachClick(coach) }
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.15f),
                                                Color.White.copy(alpha = 0.03f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1E293B).copy(alpha = 0.5f) // Glass list item
                                ),
                                shape = RoundedCornerShape(16.dp)
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
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        if (coach.imageUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = coach.imageUrl,
                                                contentDescription = coach.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color(0xFF334155)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = coach.name.take(1),
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .background(
                                                    Color(0xFF53F38D),
                                                    RoundedCornerShape(topStart = 8.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            val distance = when (coach.id) {
                                                "coach_helen" -> "0.9 km"
                                                "coach_mark" -> "1.4 km"
                                                "coach_gautam" -> "1.8 km"
                                                else -> "1.2 km"
                                            }
                                            Text(
                                                text = distance,
                                                color = Color(0xFF0F172A),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = coach.name,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                tint = Color(0xFF53F38D),
                                                contentDescription = "Doorstep Certified",
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = coach.skills,
                                            color = Color.White.copy(alpha = 0.65f),
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                tint = Color(0xFFFBBF24),
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${coach.rating} (${coach.reviewsCount})",
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 10.sp
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = "₹${coach.sessionPrice.toInt()}/hr",
                                                color = Color(0xFFFDBA74),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
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
    }
}



