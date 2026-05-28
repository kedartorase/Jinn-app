package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class UserRole {
    LEARNER, COACH, PLAYER
}

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "current_user",
    val name: String = "Prashant",
    val age: Int = 24,
    val gender: String = "Male",
    val location: String = "South Mumbai",
    val skillLevel: String = "Beginner",
    val preferredSkills: String = "Bat swing, Footwork, Bat control",
    val experience: String = "6 Months",
    val availability: String = "Weekends & Evenings",
    val role: UserRole = UserRole.LEARNER
)

@Entity(tableName = "coaches")
data class Coach(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val skills: String,
    val rating: Float,
    val reviewsCount: Int,
    val isVerified: Boolean = true,
    val bio: String,
    val experienceYears: Int,
    val certifications: String,
    val sessionPrice: Double,
    val location: String,
    val availableDays: String,
    val demoVideoUrl: String = ""
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val coachId: String,
    val coachName: String,
    val coachSkills: String,
    val coachImageUrl: String,
    val studentName: String,
    val date: String,
    val timeSlot: String,
    val price: Double,
    val status: String = "Upcoming", // Upcoming, Completed, Cancelled
    val sessionNotes: String = "",
    val feedbackReport: String = "",
    val feedbackGrade: String = "" // e.g. A, B+
)

data class PlayerMatch(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val imageUrl: String,
    val skillLevel: String,
    val specialization: String,
    val location: String,
    val distanceKm: Double,
    val availability: String,
    val bio: String
)

data class HelperUmpire(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String, // Umpire, Helper, Main scorer, Net bowler
    val experienceMatches: Int,
    val chargePerHour: Double,
    val rating: Float,
    val verified: Boolean = true,
    val location: String,
    val distanceKm: Double
)

data class CricTokClip(
    val id: String,
    val title: String,
    val coachName: String,
    val desc: String,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val durationText: String,
    val thumbnailGradientStart: Long,
    val thumbnailGradientEnd: Long,
    val clipType: String // "Batting, Bowling, Fielding"
)

data class CricProduct(
    val id: String,
    val name: String,
    val category: String, // Bat, Ball, Pad, Helmet, Gloves
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewCount: Int,
    val desc: String,
    val features: String,
    val isHot: Boolean = false,
    val imageUrl: String = ""
)

data class CricketGround(
    val id: String,
    val name: String,
    val imageUrl: String,
    val distance: String,
    val ratePerHour: Double,
    val rating: Float
)
