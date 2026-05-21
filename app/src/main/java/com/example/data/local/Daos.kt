package com.example.data.local

import androidx.room.*
import com.example.data.Booking
import com.example.data.Coach
import com.example.data.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)
}

@Dao
interface CoachDao {
    @Query("SELECT * FROM coaches")
    fun getAllCoaches(): Flow<List<Coach>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoaches(coaches: List<Coach>)

    @Query("SELECT * FROM coaches WHERE id = :id")
    suspend fun getCoachById(id: String): Coach?
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY date DESC, timeSlot DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBooking(booking: Booking)

    @Update
    suspend fun updateBooking(booking: Booking)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBooking(id: String)
}
