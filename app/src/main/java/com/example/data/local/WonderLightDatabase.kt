package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.SavedDestination
import com.example.data.model.SearchHistoryItem
import com.example.data.model.TripBooking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM trip_bookings WHERE userId = :userId OR userId = '' OR email = :email ORDER BY id DESC")
    fun getBookingsForUser(userId: String, email: String): Flow<List<TripBooking>>

    @Query("SELECT * FROM trip_bookings WHERE userId = :userId OR userId = '' ORDER BY id DESC")
    fun getBookingsForUser(userId: String): Flow<List<TripBooking>>

    @Query("SELECT * FROM trip_bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<TripBooking>>

    @Query("SELECT * FROM trip_bookings WHERE bookingId = :bookingId LIMIT 1")
    suspend fun getBookingById(bookingId: String): TripBooking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: TripBooking): Long

    @Query("UPDATE trip_bookings SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, status: String)

    @Query("DELETE FROM trip_bookings WHERE userId = :userId")
    suspend fun deleteBookingsForUser(userId: String)

    @Query("DELETE FROM trip_bookings WHERE id NOT IN (SELECT MIN(id) FROM trip_bookings GROUP BY bookingId)")
    suspend fun deduplicateBookings()

    @Query("DELETE FROM trip_bookings")
    suspend fun clearAll()
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM saved_destinations WHERE userId = :userId ORDER BY savedAt DESC")
    fun getSavedForUser(userId: String): Flow<List<SavedDestination>>

    @Query("SELECT * FROM saved_destinations ORDER BY savedAt DESC")
    fun getAllSaved(): Flow<List<SavedDestination>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDestination(item: SavedDestination)

    @Query("DELETE FROM saved_destinations WHERE destinationId = :destinationId AND userId = :userId")
    suspend fun removeDestination(destinationId: String, userId: String)

    @Query("DELETE FROM saved_destinations WHERE destinationId = :destinationId")
    suspend fun removeDestination(destinationId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_destinations WHERE destinationId = :destinationId AND userId = :userId)")
    suspend fun isSaved(destinationId: String, userId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM saved_destinations WHERE destinationId = :destinationId)")
    suspend fun isSaved(destinationId: String): Boolean

    @Query("DELETE FROM saved_destinations WHERE userId = :userId")
    suspend fun clearForUser(userId: String)
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 8")
    fun getRecentSearches(): Flow<List<SearchHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(item: SearchHistoryItem)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearch(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}

@Database(
    entities = [TripBooking::class, SavedDestination::class, SearchHistoryItem::class],
    version = 2,
    exportSchema = false
)
abstract class WonderLightDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: WonderLightDatabase? = null

        fun getDatabase(context: Context): WonderLightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WonderLightDatabase::class.java,
                    "wonderlight_adventure.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
