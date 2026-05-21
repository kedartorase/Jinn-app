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

                    // Search input matching refernce: "What skill do you want to learn?"
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchCoaches(it) },
                        placeholder = {
                            Text(
                                text = if (appMode == AppMode.LEARNING) "What skill do you want to learn?" else "Search players or matches near by...",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = SportColors.GlowBlueAccent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        ),
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Trending Skills Chips: Bat swing, Footwork, Bat control
                    val chips = listOf(
                        "Bat swing" to "🏏",
                        "Footwork" to "👟",
                        "Bat control" to "🥢"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chips.forEach { (skill, emoji) ->
                            val isSelected = selectedSkillChip == skill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) SportColors.ActiveBlue else SportColors.SoftCardBg.copy(
                                            alpha = 0.6f
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) SportColors.GlowBlueAccent else Color.White.copy(
                                            alpha = 0.1f
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.selectSkillChip(skill) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = emoji, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = skill,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
