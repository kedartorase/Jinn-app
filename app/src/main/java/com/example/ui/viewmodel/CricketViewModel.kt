package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.api.GeminiApiClient
import com.example.data.repository.CricketRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AppMode {
    LEARNING, PRACTICE
}

class CricketViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CricketRepository(application)

    // Mode: Learning Mode vs Practice Mode
    private val _appMode = MutableStateFlow(AppMode.LEARNING)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    // Current logged in user profile (From Room)
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    // Coaches state (From Room)
    val originalCoaches: StateFlow<List<Coach>> = repository.allCoaches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All Bookings (From Room)
    val bookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Search & Filter state for Coaches
    private val _coachSearchQuery = MutableStateFlow("")
    val coachSearchQuery = _coachSearchQuery.asStateFlow()

    private val _selectedSkillChip = MutableStateFlow<String?>(null) // e.g. "Bat swing", "Footwork", "Bat control"
    val selectedSkillChip = _selectedSkillChip.asStateFlow()

    private val _coachRatingFilter = MutableStateFlow<Float?>(null)
    val coachRatingFilter = _coachRatingFilter.asStateFlow()

    // Dynamic filtered coaches
    val filteredCoaches: StateFlow<List<Coach>> = combine(
        originalCoaches,
        coachSearchQuery,
        selectedSkillChip,
        coachRatingFilter
    ) { coaches, query, chip, minRating ->
        coaches.filter { coach ->
            val matchQuery = query.isEmpty() || coach.name.contains(query, ignoreCase = true) || coach.skills.contains(query, ignoreCase = true)
            val matchChip = chip == null || coach.skills.contains(chip, ignoreCase = true) || coach.bio.contains(chip, ignoreCase = true)
            val matchRating = minRating == null || coach.rating >= minRating
            matchQuery && matchChip && matchRating
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Selected coach for details
    private val _selectedCoach = MutableStateFlow<Coach?>(null)
    val selectedCoach = _selectedCoach.asStateFlow()

    // AI Coach Chat History
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(text = "Namaste! I am your AI Cricket Assistant. Ask me to suggest drilling routines, fitness tips, or analyze your batting stance!", isUser = false)
        )
    )
    val chatMessages = _chatMessages.asStateFlow()

    private val _aiGenerating = MutableStateFlow(false)
    val aiGenerating = _aiGenerating.asStateFlow()

    // CricTok Clips List (interactive memory)
    private val _cricClips = MutableStateFlow(repository.cricTokClips)
    val cricClips = _cricClips.asStateFlow()

    private val _likedClipIds = MutableStateFlow<Set<String>>(emptySet())
    val likedClipIds = _likedClipIds.asStateFlow()

    private val _savedClipIds = MutableStateFlow<Set<String>>(emptySet())
    val savedClipIds = _savedClipIds.asStateFlow()

    // GinnMart Sports Equipment Store states
    private val _storeProducts = MutableStateFlow(repository.storeProducts)
    val storeProducts = _storeProducts.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null) // "Bat", "Ball", etc.
    val selectedCategory = _selectedCategory.asStateFlow()

    // Cart: Product ID -> Quantity
    private val _cartItems = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cartItems = _cartItems.asStateFlow()

    // Static nearby players and helpers
    val nearbyPlayers = repository.nearbyPlayers
    val helperUmpires = repository.helperUmpires

    // Actions
    fun setAppMode(mode: AppMode) {
        _appMode.value = mode
    }

    fun searchCoaches(query: String) {
        _coachSearchQuery.value = query
    }

    fun selectSkillChip(chip: String?) {
        if (_selectedSkillChip.value == chip) {
            _selectedSkillChip.value = null // Toggle off
        } else {
            _selectedSkillChip.value = chip
        }
    }

    fun setRatingFilter(rating: Float?) {
        _coachRatingFilter.value = rating
    }

    fun selectCoach(coach: Coach?) {
        _selectedCoach.value = coach
    }

    // Room Database Profile Setup
    fun updateProfile(name: String, age: Int, location: String, skillLevel: String, skills: String, experience: String) {
        viewModelScope.launch {
            val current = userProfile.value
            val updated = current.copy(
                name = name,
                age = age,
                location = location,
                skillLevel = skillLevel,
                preferredSkills = skills,
                experience = experience
            )
            repository.saveUserProfile(updated)
        }
    }

    fun updateUserRole(role: UserRole) {
        viewModelScope.launch {
            val current = userProfile.value
            val updated = current.copy(role = role)
            repository.saveUserProfile(updated)
        }
    }

    // Room Booking CRUD Actions
    fun bookSession(coach: Coach, date: String, timeSlot: String, customNotes: String = "") {
        viewModelScope.launch {
            val user = userProfile.value
            val notes = if (customNotes.isNotEmpty()) {
                customNotes
            } else {
                "Practice focus: ${user.preferredSkills}. Bring proper cricket safety guards and bat."
            }
            val newBooking = Booking(
                coachId = coach.id,
                coachName = coach.name,
                coachSkills = coach.skills,
                coachImageUrl = coach.imageUrl,
                studentName = user.name,
                date = date,
                timeSlot = timeSlot,
                price = coach.sessionPrice,
                status = "Upcoming",
                sessionNotes = notes,
                location = coach.location
            )
            repository.createBooking(newBooking)
        }
    }

    fun cancelSessionBooking(id: String) {
        viewModelScope.launch {
            repository.cancelBooking(id)
        }
    }

    // Complete session with notes block
    fun simulateCompletionFlow(booking: Booking, notes: String, scoreLabel: String) {
        viewModelScope.launch {
            val updated = booking.copy(
                status = "Completed",
                feedbackReport = notes,
                feedbackGrade = scoreLabel
            )
            repository.updateBooking(updated)
        }
    }

    // AI Messages Action
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(text = text, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        _aiGenerating.value = true
        viewModelScope.launch {
            val user = userProfile.value
            val userContext = "Name: ${user.name}, Skill: ${user.skillLevel}, Intereted: ${user.preferredSkills}"
            val responseText = GeminiApiClient.getCoachAiResponse(text, userContext)

            val systemMsg = ChatMessage(text = responseText, isUser = false)
            _chatMessages.value = _chatMessages.value + systemMsg
            _aiGenerating.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(text = "Namaste! I am your AI Cricket Assistant. Focus elements: Bat swing drills, footwork, spinner responses.", isUser = false)
        )
    }

    // CricTok reactions
    fun toggleLikeClip(id: String) {
        val currentLikes = _likedClipIds.value
        if (currentLikes.contains(id)) {
            _likedClipIds.value = currentLikes - id
            updateClipLikeCount(id, -1)
        } else {
            _likedClipIds.value = currentLikes + id
            updateClipLikeCount(id, 1)
        }
    }

    fun toggleSaveClip(id: String) {
        val currentSaves = _savedClipIds.value
        if (currentSaves.contains(id)) {
            _savedClipIds.value = currentSaves - id
        } else {
            _savedClipIds.value = currentSaves + id
        }
    }

    private fun updateClipLikeCount(id: String, diff: Int) {
        _cricClips.value = _cricClips.value.map { clip ->
            if (clip.id == id) {
                clip.copy(likes = clip.likes + diff)
            } else {
                clip
            }
        }
    }

    // GinnMart Cart Management
    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun addToCart(productId: String) {
        val current = _cartItems.value.toMutableMap()
        current[productId] = current.getOrDefault(productId, 0) + 1
        _cartItems.value = current
    }

    fun removeFromCart(productId: String) {
        val current = _cartItems.value.toMutableMap()
        val count = current.getOrDefault(productId, 0)
        if (count <= 1) {
            current.remove(productId)
        } else {
            current[productId] = count - 1
        }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    // --- Coach Module Dedicated States & Actions ---
    private val _verifiedGrounds = MutableStateFlow<List<CricketGround>>(
        listOf(
            CricketGround("g_1", "Shivaji Park Turf", "android.resource://com.example/" + com.example.R.drawable.img_cricket_nets, "1.2 km away", 400.0, 4.8f),
            CricketGround("g_2", "MCA Academy Ground", "android.resource://com.example/" + com.example.R.drawable.img_cricket_stadium, "2.8 km away", 750.0, 4.9f),
            CricketGround("g_3", "Wankhede Practice Turf", "android.resource://com.example/" + com.example.R.drawable.img_scoreboard, "3.5 km away", 650.0, 4.7f),
            CricketGround("g_4", "CCI Brabourne Nets", "android.resource://com.example/" + com.example.R.drawable.img_group_banner, "5.0 km away", 500.0, 4.6f)
        )
    )
    val verifiedGrounds = _verifiedGrounds.asStateFlow()

    private val _availableDates = MutableStateFlow<List<String>>(
        listOf("2026-05-26", "2026-05-27", "2026-05-28", "2026-05-29", "2026-05-30", "2026-05-31")
    )
    val availableDates = _availableDates.asStateFlow()

    private val _availableSlots = MutableStateFlow<List<String>>(
        listOf("07:00 AM - 09:00 AM", "09:30 AM - 11:30 AM", "01:00 PM - 03:00 PM", "04:30 PM - 06:30 PM")
    )
    val availableSlots = _availableSlots.asStateFlow()

    fun addGround(name: String, ratePerHour: Double, distance: String = "2.0 km away") {
        val currentList = _verifiedGrounds.value.toMutableList()
        val newId = "g_custom_" + System.currentTimeMillis()
        val defaultImg = "android.resource://com.example/" + com.example.R.drawable.img_cricket_nets
        val newGround = CricketGround(newId, name, defaultImg, distance, ratePerHour, 4.7f)
        currentList.add(newGround)
        _verifiedGrounds.value = currentList
    }

    fun removeGround(id: String) {
        val currentList = _verifiedGrounds.value.toMutableList()
        currentList.removeAll { it.id == id }
        _verifiedGrounds.value = currentList
    }

    fun addAvailableDate(date: String) {
        val current = _availableDates.value.toMutableList()
        if (!current.contains(date)) {
            current.add(date)
            // Sort dates if possible
            current.sort()
            _availableDates.value = current
        }
    }

    fun removeAvailableDate(date: String) {
        val current = _availableDates.value.toMutableList()
        current.remove(date)
        _availableDates.value = current
    }

    fun addAvailableSlot(slot: String) {
        val current = _availableSlots.value.toMutableList()
        if (!current.contains(slot)) {
            current.add(slot)
            _availableSlots.value = current
        }
    }

    fun removeAvailableSlot(slot: String) {
        val current = _availableSlots.value.toMutableList()
        current.remove(slot)
        _availableSlots.value = current
    }

    fun acceptBookingRequest(id: String) {
        viewModelScope.launch {
            val bookingsList = repository.allBookings.first()
            val match = bookingsList.find { it.id == id }
            if (match != null) {
                val updated = match.copy(status = "Upcoming")
                repository.updateBooking(updated)
            }
        }
    }

    fun rejectBookingRequest(id: String) {
        viewModelScope.launch {
            val bookingsList = repository.allBookings.first()
            val match = bookingsList.find { it.id == id }
            if (match != null) {
                // If rejected, mark as Cancelled which triggers refund initiation state
                val updated = match.copy(status = "Cancelled")
                repository.updateBooking(updated)
            }
        }
    }

    fun registerNewCoachProfile(name: String, skills: String, bio: String, rating: Float, reviewsCount: Int, sessionPrice: Double, location: String, availableDays: String) {
        viewModelScope.launch {
            val imageSeed = (1..7).random()
            val defaultImgRes = when (imageSeed) {
                1 -> com.example.R.drawable.img_coach_1
                2 -> com.example.R.drawable.img_coach_2
                3 -> com.example.R.drawable.img_coach_3
                4 -> com.example.R.drawable.img_coach_4
                5 -> com.example.R.drawable.img_coach_5
                6 -> com.example.R.drawable.img_coach_6
                else -> com.example.R.drawable.img_coach_7
            }
            val newCoach = Coach(
                id = "coach_" + System.currentTimeMillis(),
                name = name,
                imageUrl = "android.resource://com.example/" + defaultImgRes,
                skills = skills,
                rating = rating,
                reviewsCount = reviewsCount,
                isVerified = true,
                bio = bio,
                experienceYears = 8,
                certifications = "BCCI Certified Coach",
                sessionPrice = sessionPrice,
                location = location,
                availableDays = availableDays
            )
            repository.insertCoach(newCoach)
        }
    }

    // --- Dynamic Coaching Rates ---
    private val _coachHourlyRate = MutableStateFlow(800.0)
    val coachHourlyRate = _coachHourlyRate.asStateFlow()

    private val _coachWeeklyRate = MutableStateFlow(4500.0)
    val coachWeeklyRate = _coachWeeklyRate.asStateFlow()

    private val _coachMonthlyRate = MutableStateFlow(15000.0)
    val coachMonthlyRate = _coachMonthlyRate.asStateFlow()

    fun updateCoachRates(hourly: Double, weekly: Double, monthly: Double) {
        _coachHourlyRate.value = hourly
        _coachWeeklyRate.value = weekly
        _coachMonthlyRate.value = monthly
    }

    // --- Dynamic Group Sessions ---
    private val _groupSessions = MutableStateFlow<List<GroupSession>>(
        listOf(
            GroupSession(
                id = "gs_1",
                title = "Weekend Cover-Drive Masterclass",
                description = "Master your batting stance, elbow alignment, and foot spacing trigger. Includes 1-on-1 visual review.",
                hourlyRate = 500.0,
                location = "Shivaji Park Turf, Dadar",
                imageUrl = "android.resource://com.example/" + com.example.R.drawable.img_group_banner,
                coachName = "Vikas SD"
            ),
            GroupSession(
                id = "gs_2",
                title = "Fast Bowling Seam & Swing Clinic",
                description = "Analyze your wrist position and follow-through. Bring your cricket spikes for grass turf practice.",
                hourlyRate = 600.0,
                location = "MCA Academy Nets, Bandra",
                imageUrl = "android.resource://com.example/" + com.example.R.drawable.img_cricket_nets,
                coachName = "Prashant"
            )
        )
    )
    val groupSessions = _groupSessions.asStateFlow()

    fun addGroupSession(title: String, description: String, rate: Double, location: String, imageUrl: String, coachName: String) {
        val currentList = _groupSessions.value.toMutableList()
        val defaultImg = if (imageUrl.isBlank()) {
            "android.resource://com.example/" + com.example.R.drawable.img_group_banner
        } else {
            imageUrl
        }
        val newSession = GroupSession(
            id = "gs_" + System.currentTimeMillis(),
            title = title,
            description = description,
            hourlyRate = rate,
            location = location,
            imageUrl = defaultImg,
            coachName = coachName,
            maxParticipants = 15,
            registeredCount = 2
        )
        currentList.add(newSession)
        _groupSessions.value = currentList
    }

    fun removeGroupSession(id: String) {
        val currentList = _groupSessions.value.toMutableList()
        currentList.removeAll { it.id == id }
        _groupSessions.value = currentList
    }

    fun bookWalkInSession(coachName: String, studentName: String, date: String, timeSlot: String, price: Double, sessionNotes: String) {
        viewModelScope.launch {
            val newBooking = Booking(
                id = "b_walkin_" + System.currentTimeMillis(),
                coachId = "coach_current",
                coachName = coachName,
                coachSkills = "Personal Session",
                coachImageUrl = "android.resource://com.example/" + com.example.R.drawable.img_coach_1,
                studentName = studentName,
                date = date,
                timeSlot = timeSlot,
                price = price,
                status = "Upcoming",
                sessionNotes = sessionNotes,
                feedbackReport = "",
                feedbackGrade = ""
            )
            repository.createBooking(newBooking)
        }
    }

    // --- Coach Profile Management ---
    private val _coachBio = MutableStateFlow("Specialized cricket coach with 8+ years of coaching academy players. Focuses on advanced swing, stance weight transition, and backfoot drive techniques.")
    val coachBio = _coachBio.asStateFlow()

    private val _coachExperience = MutableStateFlow("8 Years")
    val coachExperience = _coachExperience.asStateFlow()

    private val _coachSkillsText = MutableStateFlow("Adv Batting, Backfoot Drive, Fast Bowling, Spin sweeps")
    val coachSkillsText = _coachSkillsText.asStateFlow()

    private val _coachCertificates = MutableStateFlow(listOf("ICC Level 2 Certified Coach", "BCCI Player Development Certificate", "National Academy High-Performance Trainer"))
    val coachCertificates = _coachCertificates.asStateFlow()

    private val _coachPhotos = MutableStateFlow(listOf("android.resource://com.example/" + com.example.R.drawable.img_coach_1, "android.resource://com.example/" + com.example.R.drawable.img_cricket_nets))
    val coachPhotos = _coachPhotos.asStateFlow()

    private val _coachVideos = MutableStateFlow(listOf("Cover Drive Alignment Drills - Video Link", "Bat Speed Acceleration Tips", "Backfoot Balance Stability"))
    val coachVideos = _coachVideos.asStateFlow()

    fun updateCoachBio(bio: String) {
        _coachBio.value = bio
    }

    fun updateCoachExperience(exp: String) {
        _coachExperience.value = exp
    }

    fun updateCoachSkillsText(skills: String) {
        _coachSkillsText.value = skills
    }

    fun updateCoachProfileName(newName: String) {
        viewModelScope.launch {
            val current = userProfile.value
            val updated = current.copy(name = newName)
            repository.saveUserProfile(updated)
        }
    }

    fun addCoachCertificate(cert: String) {
        if (cert.isNotBlank()) {
            _coachCertificates.value = _coachCertificates.value + cert
        }
    }

    fun removeCoachCertificate(cert: String) {
        _coachCertificates.value = _coachCertificates.value - cert
    }

    fun addCoachPhoto(url: String) {
        if (url.isNotBlank()) {
            _coachPhotos.value = _coachPhotos.value + url
        }
    }

    fun removeCoachPhoto(url: String) {
        _coachPhotos.value = _coachPhotos.value - url
    }

    fun addCoachVideo(title: String) {
        if (title.isNotBlank()) {
            _coachVideos.value = _coachVideos.value + title
        }
    }

    fun removeCoachVideo(title: String) {
        _coachVideos.value = _coachVideos.value - title
    }
}

