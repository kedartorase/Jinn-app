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
                sessionNotes = notes
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
}
