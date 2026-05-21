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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Coach
import com.example.ui.viewmodel.CricketViewModel

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
                    CoachAvatarIllustration(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(8.dp, CircleShape),
                        coachName = coach.name,
                        primaryColor = SportColors.ActiveBlue
                    )
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
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = SportColors.GoldYellow, modifier = Modifier.size(16.dp))
                    Text(" ${coach.rating} (${coach.reviewsCount} reviews)", color = SportColors.TextPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Work, contentDescription = null, tint = SportColors.GlowBlueAccent, modifier = Modifier.size(16.dp))
                    Text(" ${coach.experienceYears} Years Exp", color = SportColors.TextPrimary, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bio
                Text(
                    text = "Specialist Bio",
                    color = SportColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = coach.bio,
                    color = SportColors.TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Certifications list block
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                    border = BorderStroke(1.dp, SportColors.CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = SportColors.GoldYellow,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Certifications & Credentials",
                                color = SportColors.TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                coach.certifications,
                                color = SportColors.TextSecondary,
                                fontSize = 11.sp
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dateOptions.forEach { date ->
                        val isSelected = selectedDate == date
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SportColors.ActiveBlue else SportColors.SoftCardBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else SportColors.CardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedDate = date }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = date.substring(8), // just Day
                                    color = if (isSelected) Color.White else SportColors.TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "May",
                                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else SportColors.TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Slots matrix list selector
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    slotOptions.chunked(2).forEach { rowSlots ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSlots.forEach { slot ->
                                val isSelected = selectedSlot == slot
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SportColors.ActiveBlue else SportColors.SoftCardBg)
                                        .clickable { selectedSlot = slot }
                                        .border(
                                            1.dp,
                                            if (isSelected) Color.Transparent else SportColors.CardBorder,
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
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
                    Button(
                        onClick = { checkoutStep = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen),
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
                                colors = ButtonDefaults.buttonColors(containerColor = SportColors.ActiveBlue)
                            ) {
                                Text("Apply", fontSize = 11.sp)
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
                            Text("₹${finalPrice + 35.0}", color = SportColors.GoldYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Complete Booking Action
                        Button(
                            onClick = {
                                viewModel.bookSession(coach, selectedDate, selectedSlot)
                                checkoutStep = false
                                onBookingSuccess()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SportColors.SportGreen)
                        ) {
                            Text("Pay and Reserve Instant 🛡️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
