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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

                        Spacer(modifier = Modifier.width(8.dp))

                        // Mode Selector Toggle Switch Widget (Learning / Practice)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .clickable {
                                    val newMode = if (appMode == AppMode.LEARNING) AppMode.PRACTICE else AppMode.LEARNING
                                    viewModel.setAppMode(newMode)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (appMode == AppMode.LEARNING) Icons.Default.School else Icons.Default.SportsCricket,
                                    contentDescription = null,
                                    tint = SportColors.DeepBlueHeader,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (appMode == AppMode.LEARNING) "Learning" else "Practice",
                                    color = SportColors.DeepBlueHeader,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
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
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(SportColors.BrightOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Whistle",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Centered decorative line "Discover coaches for your game" or "Explore players nearby"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Transparent, SportColors.GoldYellow)
                                    )
                                )
                        )
                        Text(
                            text = if (appMode == AppMode.LEARNING) "   Discover coaches for your game   " else "   Organize match events   ",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(SportColors.GoldYellow, Color.Transparent)
                                    )
                                )
                        )
                    }
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
                        // Horizontal Horizontal Row matching reference image
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filteredCoaches) { coach ->
                                Box(
                                    modifier = Modifier
                                        .width(155.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .clickable { onCoachClick(coach) }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Visual character box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(120.dp)
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
                                                modifier = Modifier.size(75.dp),
                                                coachName = coach.name,
                                                primaryColor = if (coach.id == "coach_1") SportColors.DeepBlueHeader else SportColors.ActiveBlue
                                            )

                                            // Verified Label on top of Avatar (Matches "Verified" green tag)
                                            if (coach.isVerified) {
                                                VerifiedBadge(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(8.dp)
                                                )
                                            }
                                        }

                                        // Text details
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = coach.name,
                                                color = Color(0xFF0F1E4C),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = coach.skills,
                                                color = Color.Black.copy(alpha = 0.5f),
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            StarRatingBar(rating = coach.rating)
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

                    // Green Group session card with Orange price circle badge replicating image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(SportColors.SportGreen, SportColors.SportGreenDark)
                                )
                            )
                            .clickable { onGroupSessionClick() }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1.3f)
                            ) {
                                Text(
                                    text = "Batting Techniques",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Shot selection",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Vikas SD",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = SportColors.GoldYellow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Anita Apartments, Bridge, CMS Marg, Near Solitaire Heights, Vakola, Santacruz East, Mumbai,",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 9.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Dynamic Orange ₹500 price circle & batsman graphics
                            Column(
                                modifier = Modifier.weight(0.7f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(SportColors.BrightOrange)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Only at", color = Color.White.copy(alpha = 0.8f), fontSize = 8.sp)
                                        Text("₹500", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .clickable { onGroupSessionClick() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        "Register Now!",
                                        color = SportColors.SportGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

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
                                    colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen),
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

                        Button(
                            onClick = {
                                viewModel.setRatingFilter(null)
                                viewModel.selectSkillChip(null)
                                viewModel.searchCoaches("")
                                showFilterSheet = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SportColors.BrightOrange)
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

