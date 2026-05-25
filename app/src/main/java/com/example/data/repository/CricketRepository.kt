package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.*
import com.example.data.local.AppDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CricketRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userProfileDao = db.userProfileDao()
    private val coachDao = db.coachDao()
    private val bookingDao = db.bookingDao()

    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val allCoaches: Flow<List<Coach>> = coachDao.getAllCoaches()
    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings()

    // Initialize mock data in background coroutine on startup
    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Check and insert default coaches
            val dummyCoaches = listOf(
                Coach(
                    id = "coach_gautam",
                    name = "Gautam Gambhir",
                    imageUrl = "android.resource://com.example/" + R.drawable.img_coach_1,
                    skills = "Opening Batsman | T20 Strategy",
                    rating = 4.9f,
                    reviewsCount = 380,
                    isVerified = true,
                    bio = "Current Head Coach of the Indian National Cricket Team (2024-Present) and double World Cup winner. High intensity white-ball tactics, wristy flick shots, and match dominance.",
                    experienceYears = 10,
                    certifications = "BCCI Elite Level Master Coach",
                    sessionPrice = 1600.00,
                    location = "Cricket Club of India (CCI), Mumbai",
                    availableDays = "Mon, Wed, Fri, Sat"
                ),
                Coach(
                    id = "coach_rahul",
                    name = "Rahul Dravid",
                    imageUrl = "android.resource://com.example/" + R.drawable.img_coach_5,
                    skills = "Defensive Mastery | Technique",
                    rating = 4.9f,
                    reviewsCount = 720,
                    isVerified = true,
                    bio = "Known as 'The Wall'. Renowned Head Coach of India (2021-2024). Specializes in classic batting technique, defensive build-up, and extreme high-pressure endurance.",
                    experienceYears = 18,
                    certifications = "NCA Director of Coaching | Elite Masterclass Cert",
                    sessionPrice = 1800.00,
                    location = "Bandra Kurla Complex (BKC), Mumbai",
                    availableDays = "Mon, Tue, Thu, Sat"
                ),
                Coach(
                    id = "coach_ravi",
                    name = "Ravi Shastri",
                    imageUrl = "android.resource://com.example/" + R.drawable.img_coach_7,
                    skills = "All-Rounder | Captaincy Tips",
                    rating = 4.8f,
                    reviewsCount = 540,
                    isVerified = true,
                    bio = "Legendary Head Coach of India (2017-2021) and World Cup hero. Outstanding strategical management, aggressive batsman mindset, and champion team building.",
                    experienceYears = 15,
                    certifications = "BCCI Elite Coach | Former National Director",
                    sessionPrice = 1500.00,
                    location = "Wankhede Stadium Area, Mumbai",
                    availableDays = "Tue, Thu, Sat, Sun"
                ),
                Coach(
                    id = "coach_anil",
                    name = "Anil Kumble",
                    imageUrl = "android.resource://com.example/" + R.drawable.img_coach_3,
                    skills = "Leg Spin | Match Planning",
                    rating = 4.8f,
                    reviewsCount = 410,
                    isVerified = true,
                    bio = "Former legendary Head Coach of India (2016-2017) and highest national wicket-taker. Specialized in leg spin grip release, flight/bounce control, and intense tactical setups.",
                    experienceYears = 12,
                    certifications = "BCCI Level 3 Spin Bowling Master",
                    sessionPrice = 1400.00,
                    location = "Vakola, Santacruz East, Mumbai",
                    availableDays = "Mon, Tue, Wed, Thu, Fri"
                ),
                Coach(
                    id = "coach_gary",
                    name = "Gary Kirsten",
                    imageUrl = "android.resource://com.example/" + R.drawable.img_coach_4,
                    skills = "Batting Strokeplay | Strategy",
                    rating = 4.9f,
                    reviewsCount = 890,
                    isVerified = true,
                    bio = "2011 World Cup-winning Head Coach of India (2008-2011). Specialized in modern aggressive batting, run-chase pacing, and building psychological dominance.",
                    experienceYears = 20,
                    certifications = "ICC High Performance Level 4 Coach",
                    sessionPrice = 2000.00,
                    location = "Dharavi Sports Complex, Mumbai",
                    availableDays = "Sat, Sun, Tue, Thu"
                ),
                Coach(
                    id = "coach_helen",
                    name = "Helen Campbell",
                    imageUrl = "android.resource://com.example/" + R.drawable.img_coach_2,
                    skills = "Fielding Drill | High-Energy Focus",
                    rating = 4.9f,
                    reviewsCount = 210,
                    isVerified = true,
                    bio = "Veteran Australian fielding mentor and women's elite league specialist coach. Highly effective agility sessions, direct-hit throwing mechanics, and active reflexes drill.",
                    experienceYears = 11,
                    certifications = "Cricket Australia High Performance L3 Coach",
                    sessionPrice = 1350.00,
                    location = "Dnyaneshwar Maidan, Mumbai",
                    availableDays = "Wed, Thu, Sat, Sun"
                ),
                Coach(
                    id = "coach_mark",
                    name = "Mark Boucher",
                    imageUrl = "android.resource://com.example/" + R.drawable.img_coach_6,
                    skills = "Wicketkeeping | Fast Bowling Line",
                    rating = 4.8f,
                    reviewsCount = 195,
                    isVerified = true,
                    bio = "Former elite South African wicketkeeper and bowling strategist. Masterclass coaching in glovework positioning, swing/seam bowling release, and high stamina setups.",
                    experienceYears = 9,
                    certifications = "CSA National Coaching Masterclass Certificate",
                    sessionPrice = 1450.00,
                    location = "Shivaji Park Ground, Mumbai",
                    availableDays = "Mon, Tue, Thu, Fri"
                )
            )

            // Insert initial default profile if database is blank
            userProfileDao.getUserProfile().first() ?: run {
                val defaultProfile = UserProfile()
                userProfileDao.saveUserProfile(defaultProfile)
            }

            // Populate coaches
            coachDao.insertCoaches(dummyCoaches)

            // Add sample bookings if there are none to make the UI populated immediately
            bookingDao.getAllBookings().first().let { current ->
                if (current.isEmpty()) {
                    val booking1 = Booking(
                        id = "book_sample_1",
                        coachId = "coach_gautam",
                        coachName = "Gautam Gambhir",
                        coachSkills = "Opening Batsman | T20 Strategy",
                        coachImageUrl = "android.resource://com.example/" + R.drawable.img_coach_1,
                        studentName = "Prashant",
                        date = "2026-05-24", // Dynamic future date
                        timeSlot = "07:00 AM - 09:00 AM",
                        price = 1600.0,
                        status = "Upcoming",
                        sessionNotes = "Remember to bring your custom bats. We will focus primarily on front-foot drive balance and power extension.",
                        feedbackReport = ""
                    )
                    val booking2 = Booking(
                        id = "book_sample_2",
                        coachId = "coach_rahul",
                        coachName = "Rahul Dravid",
                        coachSkills = "Defensive Mastery | Technique",
                        coachImageUrl = "android.resource://com.example/" + R.drawable.img_coach_5,
                        studentName = "Prashant",
                        date = "2026-05-18", // Past date
                        timeSlot = "04:30 PM - 06:30 PM",
                        price = 1800.0,
                        status = "Completed",
                        sessionNotes = "Completed basics of stance setup.",
                        feedbackReport = "Excellent intent shown. Stance width was corrected. Work on bending your knees slightly to find a lower center of gravity while preparing for the swing. High backlift is good but needs better control.",
                        feedbackGrade = "B+"
                    )
                    bookingDao.addBooking(booking1)
                    bookingDao.addBooking(booking2)
                }
            }
        }
    }

    // Save profile updates
    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.saveUserProfile(profile)
    }

    // Add dynamic booking
    suspend fun createBooking(booking: Booking) {
        bookingDao.addBooking(booking)
    }

    // Cancel dynamic booking
    suspend fun cancelBooking(id: String) {
        bookingDao.deleteBooking(id)
    }

    // Complete / Update feedback of dynamic booking
    suspend fun updateBooking(booking: Booking) {
        bookingDao.updateBooking(booking)
    }

    // Static Nearby Players List
    val nearbyPlayers = listOf(
        PlayerMatch(
            name = "Rohit Sharma (Jr.)",
            imageUrl = "",
            skillLevel = "Beginner",
            specialization = "Opening Batsman",
            location = "BKC Sports Complex, Mumbai",
            distanceKm = 1.2,
            availability = "Everyday 6 AM - 8 AM",
            bio = "Love batting, looking for fast out-swing bowlers to practice with. Always up for net sessions!"
        ),
        PlayerMatch(
            name = "Saurabh Mishra / Spinner",
            imageUrl = "",
            skillLevel = "Advanced",
            specialization = "Left-arm Orthodox",
            location = "Shivaji Park Nets, Dadar",
            distanceKm = 2.8,
            availability = "Weekends All Day",
            bio = "Wicket-taking SLA bowler. I can bowl consistent lengths in the nets for batting drills."
        ),
        PlayerMatch(
            name = "Kunal Kulkarni",
            imageUrl = "",
            skillLevel = "Intermediate",
            specialization = "Medium Fast Bowler / Seamer",
            location = "South Mumbai Oval Maidan",
            distanceKm = 0.5,
            availability = "Weekdays after 6 PM",
            bio = "Decent pace, focuses on hitting the hard length. Let's form a practice team for local T20 friendly matches."
        ),
        PlayerMatch(
            name = "Akash Gaikwad",
            imageUrl = "",
            skillLevel = "Intermediate",
            specialization = "Hard Hitting Finisher",
            location = "Vakola Tilak Maidan, Santacruz",
            distanceKm = 1.6,
            availability = "Saturday Morning",
            bio = "T20 aggressive middle-order batsman. Need practice facing leather balls."
        )
    )

    // Static Assistant Helpers & Umpires
    val helperUmpires = listOf(
        HelperUmpire(
            name = "Umpire Devendra G.",
            role = "State Certified Head Umpire",
            experienceMatches = 150,
            chargePerHour = 300.0,
            rating = 4.9f,
            location = "Santacruz East Turf Grounds",
            distanceKm = 1.1
        ),
        HelperUmpire(
            name = "Nilesh P. (Net Helper)",
            role = "Arm Ball Tosser & Pitch Prep",
            experienceMatches = 210,
            chargePerHour = 150.0,
            rating = 4.4f,
            location = "Shivaji Park Ground 4",
            distanceKm = 2.4
        ),
        HelperUmpire(
            name = "Shrikant M. (Scorer)",
            role = "Certified Match Scorer & Video Analyst",
            experienceMatches = 85,
            chargePerHour = 200.0,
            rating = 4.6f,
            location = "South Mumbai Gymkhana",
            distanceKm = 3.2
        )
    )

    // CricTok Clips List
    val cricTokClips = listOf(
        CricTokClip(
            id = "clip_1",
            title = "Mastering the Stance & Footwork Guidance",
            coachName = "Vikas SD",
            desc = "Positioning your back foot correctly allows you to adapt to short balls immediately. 🏏 Essential tips for the classic cover drive swing!",
            likes = 12450,
            comments = 112,
            shares = 2400,
            durationText = "0:45",
            thumbnailGradientStart = 0xFF1E3A8A,
            thumbnailGradientEnd = 0xFF10B981,
            clipType = "Batting"
        ),
        CricTokClip(
            id = "clip_2",
            title = "Rip it like Shane: Leg Spin Wrist Release",
            coachName = "Rajesh Kumar",
            desc = "Unlocking leg-spin turn starts in the third claw finger. See the release seam angle! 🌀 #bowling #spinsecrets",
            likes = 8520,
            comments = 84,
            shares = 1125,
            durationText = "1:12",
            thumbnailGradientStart = 0xFF701A75,
            thumbnailGradientEnd = 0xFFF43F5E,
            clipType = "Bowling"
        ),
        CricTokClip(
            id = "clip_3",
            title = "Glove Speed Drill for Keepers ⚡",
            coachName = "Ananya Sharma",
            desc = "Boost your wicketkeeping reflex and pocket stay-low techniques. Perfect for keepers looking to grab low edges!",
            likes = 15150,
            comments = 189,
            shares = 3200,
            durationText = "0:30",
            thumbnailGradientStart = 0xFF065F46,
            thumbnailGradientEnd = 0xFFD97706,
            clipType = "Fielding"
        ),
        CricTokClip(
            id = "clip_4",
            title = "The Wristy Flick: Bat Control Mechanics",
            coachName = "Prasad P",
            desc = "Flicking off the hips gracefully is about timing, not brute force. Master your wrist snap with this daily batting drill.",
            likes = 5410,
            comments = 45,
            shares = 672,
            durationText = "0:58",
            thumbnailGradientStart = 0xFF1E3A8A,
            thumbnailGradientEnd = 0xFF4F46E5,
            clipType = "Batting"
        )
    )

    // Store products list inside GinnMart
    val storeProducts = listOf(
        CricProduct(
            id = "prod_1",
            name = "Saurashtra Master Pro English Willow Bat",
            category = "Bat",
            price = 4500.0,
            originalPrice = 9000.0,
            rating = 4.8f,
            reviewCount = 148,
            desc = "Grade 1 English Willow cricket bat with customized short handle, sweet spot configured mid-to-low for subcontinental decks. Hand-picked for perfect balance.",
            features = "Weight: 1180 grams | Edge: 40mm | Sweetspot: Low-mid | Grains: 8-10 straight grains",
            isHot = true
        ),
        CricProduct(
            id = "prod_2",
            name = "Nylon Seamer Hard Leather Cricket Ball (Box of 2)",
            category = "Ball",
            price = 399.0,
            originalPrice = 750.0,
            rating = 4.4f,
            reviewCount = 380,
            desc = "Alum tanned premium quality four-piece leather balls. Highly pronounced seam perfect for out-swing training, net practice, and official club matches.",
            features = "Weight: 156 grams | Grade A Leather | Waterproof outer lacquer | Wool wounded cork core"
        ),
        CricProduct(
            id = "prod_3",
            name = "Pro-Elite High Density Foam Batting Pads",
            category = "Pad",
            price = 1499.0,
            originalPrice = 2800.0,
            rating = 4.6f,
            reviewCount = 94,
            desc = "Lightweight high-density plastazote foam facing with cane reinforcement. Molded knee bolster with mesh lining ensuring dry sweat absorption during high-energy innings.",
            features = "Weight: 820g/pair | Gel insert knee zone | 3-band durable strap system",
            isHot = true
        ),
        CricProduct(
            id = "prod_4",
            name = "Impact Combat Face-Wire ABS Helmet",
            category = "Helmet",
            price = 1999.0,
            originalPrice = 3500.0,
            rating = 4.9f,
            reviewCount = 67,
            desc = "Premium high impact resistant ABS outer shell with a sweat absorption cushion liner. Adjusting size dialed head strap. Sleek powder-coated steel face grid.",
            features = "Size: Adjustable Medium (54-58cm) | Air vents for cooling | Steel face protector"
        ),
        CricProduct(
            id = "prod_5",
            name = "Armor Grip Calfskin Batting Gloves",
            category = "Gloves",
            price = 699.0,
            originalPrice = 1200.0,
            rating = 4.5f,
            reviewCount = 121,
            desc = "Calf leather palm for exceptional comfort and touch grip. Dual-density finger rolls with fiber protection shields. Ergonomic side bar protection.",
            features = "Right-Handed | Double sided sweat band | Calf skin premium durability"
        )
    )
}
