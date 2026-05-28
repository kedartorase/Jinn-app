package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserRole
import com.example.ui.viewmodel.CricketViewModel

@Composable
fun LoginScreen(
    viewModel: CricketViewModel,
    onLoginSuccess: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()

    var name by remember { mutableStateOf(userProfile.name) }
    var ageString by remember { mutableStateOf(userProfile.age.toString()) }
    var location by remember { mutableStateOf(userProfile.location) }
    var experience by remember { mutableStateOf(userProfile.experience) }
    var selectedSkillLevel by remember { mutableStateOf(userProfile.skillLevel) }
    var selectedRole by remember { mutableStateOf(userProfile.role) }

    // List of skills to pick
    val availableSkillsList = listOf("Bat swing", "Footwork", "Bat control", "In-swing", "Spin Grip", "Wicket keeping")
    val selectedSkills = remember { mutableStateOf(userProfile.preferredSkills.split(", ").toMutableStateList()) }

    var otpStep by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var authMode by remember { mutableStateOf("PROFILES") } // PROFILES, SOCIAL, OTP

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportColors.DarkBackground)
    ) {
        // Aesthetic sports backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = SportColors.ActiveBlue.copy(alpha = 0.15f),
                radius = this.size.width / 1.5f,
                center = Offset(0f, 0f)
            )
            drawCircle(
                color = SportColors.SportGreen.copy(alpha = 0.12f),
                radius = this.size.width / 2f,
                center = Offset(this.size.width, this.size.height)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App branding header utilizing the beautiful Jinn app logo
            JinnAppLogo(
                modifier = Modifier.wrapContentSize(),
                showText = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Master Your Game with the Right Coach",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SportColors.ActiveBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (authMode == "PROFILES") {
                // Profile & Role Setup Cards (Professional / Sports feel)
                Card(
                     modifier = Modifier.fillMaxWidth(),
                     shape = RoundedCornerShape(16.dp),
                     colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                     elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Select Application Role",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Role Selector Rows
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UserRole.values().filter { it != UserRole.PLAYER }.forEach { role ->
                                val isSelected = selectedRole == role
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) SportColors.ActiveBlue else SportColors.DarkBackground
                                        )
                                        .clickable { selectedRole = role }
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isSelected) SportColors.GlowBlueAccent else SportColors.CardBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = role.name.capitalize(),
                                        color = if (isSelected) Color.White else SportColors.TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Bio & Setup Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Your Cricket Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("username_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportColors.ActiveBlue,
                                unfocusedBorderColor = SportColors.CardBorder,
                                focusedLabelColor = SportColors.ActiveBlue,
                                unfocusedLabelColor = SportColors.TextSecondary,
                                focusedTextColor = SportColors.TextPrimary,
                                unfocusedTextColor = SportColors.TextPrimary,
                                focusedContainerColor = SportColors.DarkBackground,
                                unfocusedContainerColor = SportColors.DarkBackground
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = ageString,
                                onValueChange = { ageString = it },
                                label = { Text("Age") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SportColors.ActiveBlue,
                                    unfocusedBorderColor = SportColors.CardBorder,
                                    focusedLabelColor = SportColors.ActiveBlue,
                                    unfocusedLabelColor = SportColors.TextSecondary,
                                    focusedTextColor = SportColors.TextPrimary,
                                    unfocusedTextColor = SportColors.TextPrimary,
                                    focusedContainerColor = SportColors.DarkBackground,
                                    unfocusedContainerColor = SportColors.DarkBackground
                                ),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                label = { Text("Academy Location") },
                                modifier = Modifier.weight(2f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SportColors.ActiveBlue,
                                    unfocusedBorderColor = SportColors.CardBorder,
                                    focusedLabelColor = SportColors.ActiveBlue,
                                    unfocusedLabelColor = SportColors.TextSecondary,
                                    focusedTextColor = SportColors.TextPrimary,
                                    unfocusedTextColor = SportColors.TextPrimary,
                                    focusedContainerColor = SportColors.DarkBackground,
                                    unfocusedContainerColor = SportColors.DarkBackground
                                ),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = experience,
                            onValueChange = { experience = it },
                            label = { Text("Playing Experience (e.g. 1 Year)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportColors.ActiveBlue,
                                unfocusedBorderColor = SportColors.CardBorder,
                                focusedLabelColor = SportColors.ActiveBlue,
                                unfocusedLabelColor = SportColors.TextSecondary,
                                focusedTextColor = SportColors.TextPrimary,
                                unfocusedTextColor = SportColors.TextPrimary,
                                focusedContainerColor = SportColors.DarkBackground,
                                unfocusedContainerColor = SportColors.DarkBackground
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Preferred Skills of Focus",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary
                        )

                        // Chips matrix setup
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableSkillsList.forEach { skill ->
                                val selected = selectedSkills.value.contains(skill)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (selected) SportColors.SportGreen else SportColors.DarkBackground
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (selected) Color.Transparent else SportColors.CardBorder,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            if (selected) {
                                                selectedSkills.value.remove(skill)
                                            } else {
                                                selectedSkills.value.add(skill)
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = skill,
                                            color = if (selected) Color.White else SportColors.TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (selected) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Submit profile and setup with explicitly high-contrast white text on primary blue button
                        GradientButton(
                            onClick = {
                                val skillsStr = selectedSkills.value.joinToString(", ")
                                val parsedAge = ageString.toIntOrNull() ?: 24
                                viewModel.updateProfile(
                                    name = name,
                                    age = parsedAge,
                                    location = location,
                                    skillLevel = selectedSkillLevel,
                                    skills = skillsStr,
                                    experience = experience
                                )
                                viewModel.updateUserRole(selectedRole)
                                authMode = "SOCIAL"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            testTag = "login_button",
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Confirm Profile Setup 🏏",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            } else if (authMode == "SOCIAL") {
                // Secondary login simulations (Google, Apple, Mobile OTP) as requested in requirements doc
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Authenticate Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "Logged in profile: ${userProfile.name} (${selectedRole.name})",
                            fontSize = 12.sp,
                            color = SportColors.TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        Button(
                            onClick = { onLoginSuccess() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)) // G-Color
                        ) {
                            Text("Continue with Google", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onLoginSuccess() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black) // Apple-Color
                        ) {
                            Text("Continue with Apple", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = SportColors.CardBorder)
                            Text(
                                text = " OR ",
                                color = SportColors.TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = SportColors.CardBorder)
                        }

                        GradientButton(
                            onClick = { authMode = "OTP" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Quick Mobile OTP Login 📱", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        TextButton(onClick = { authMode = "PROFILES" }) {
                            Text("← Edit profile setup details", color = SportColors.ActiveBlue)
                        }
                    }
                }
            } else {
                // Mobile OTP Field inputs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OTP Code Verification",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportColors.TextPrimary
                        )
                        Text(
                            text = "A code of 6-digits was simulated to your setup device.",
                            fontSize = 12.sp,
                            color = SportColors.TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                        )

                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { otpCode = it },
                            label = { Text("Verification OTP Code") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("123456") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SportColors.ActiveBlue,
                                unfocusedBorderColor = SportColors.CardBorder,
                                focusedLabelColor = SportColors.ActiveBlue,
                                unfocusedLabelColor = SportColors.TextSecondary,
                                focusedTextColor = SportColors.TextPrimary,
                                unfocusedTextColor = SportColors.TextPrimary,
                                focusedContainerColor = SportColors.DarkBackground,
                                unfocusedContainerColor = SportColors.DarkBackground
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        GradientButton(
                            onClick = { onLoginSuccess() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Verify Code & Start!", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        TextButton(onClick = { authMode = "SOCIAL" }) {
                            Text("← Back to social login options", color = SportColors.ActiveBlue)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
