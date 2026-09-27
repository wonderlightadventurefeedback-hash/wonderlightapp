package com.example.data.repository

import com.example.data.local.BookingDao
import com.example.data.local.SearchHistoryDao
import com.example.data.local.WishlistDao
import com.example.data.mock.TravelMockData
import com.example.data.model.Destination
import com.example.data.model.DestinationCategory
import com.example.data.model.Flight
import com.example.data.model.Hotel
import com.example.data.model.SavedDestination
import com.example.data.model.SearchHistoryItem
import com.example.data.model.TravelOffer
import com.example.data.model.TripBooking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TravelRepository(
    private val wishlistDao: WishlistDao,
    private val searchHistoryDao: SearchHistoryDao
) {
    private var liveDestinations: List<Destination> = TravelMockData.destinations

    fun updateLiveDestinations(destinations: List<Destination>) {
        if (destinations.isNotEmpty()) {
            liveDestinations = destinations
        }
    }

    fun getAllDestinations(): List<Destination> = liveDestinations

    fun getDestinationById(id: String): Destination? =
        liveDestinations.find { it.id.equals(id, ignoreCase = true) }
            ?: liveDestinations.firstOrNull()

    fun getTrendingDestinations(): List<Destination> =
        liveDestinations.filter { it.isTrending }

    fun getDestinationsByCategory(category: DestinationCategory): List<Destination> {
        return if (category == DestinationCategory.ALL) {
            liveDestinations
        } else {
            liveDestinations.filter { it.category == category }
        }
    }

    fun searchDestinations(query: String): List<Destination> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return liveDestinations
        return liveDestinations.filter {
            it.name.lowercase().contains(q) ||
            it.title.lowercase().contains(q) ||
            it.location.lowercase().contains(q) ||
            it.tagline.lowercase().contains(q) ||
            it.category.displayName.lowercase().contains(q)
        }
    }

    fun getAllHotels(): List<Hotel> = TravelMockData.hotels

    fun getAllFlights(): List<Flight> = TravelMockData.flights

    fun getAllOffers(): List<TravelOffer> = TravelMockData.offers

    fun getSavedDestinationIds(userId: String = ""): Flow<List<String>> {
        return if (userId.isNotBlank()) {
            wishlistDao.getSavedForUser(userId).map { list -> list.map { it.destinationId } }
        } else {
            wishlistDao.getAllSaved().map { list -> list.map { it.destinationId } }
        }
    }

    suspend fun toggleSaveDestination(destinationId: String, userId: String = "") {
        if (wishlistDao.isSaved(destinationId)) {
            if (userId.isNotBlank()) {
                wishlistDao.removeDestination(destinationId, userId)
            } else {
                wishlistDao.removeDestination(destinationId)
            }
        } else {
            wishlistDao.saveDestination(SavedDestination(destinationId = destinationId, userId = userId))
        }
    }

    suspend fun isSaved(destinationId: String, userId: String = ""): Boolean =
        if (userId.isNotBlank()) wishlistDao.isSaved(destinationId, userId) else wishlistDao.isSaved(destinationId)

    fun getRecentSearches(): Flow<List<SearchHistoryItem>> =
        searchHistoryDao.getRecentSearches()

    suspend fun saveSearch(query: String) {
        if (query.isNotBlank()) {
            searchHistoryDao.insertSearch(SearchHistoryItem(query = query.trim()))
        }
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.clearHistory()
    }
}

class BookingRepository(private val bookingDao: BookingDao) {
    val allBookings: Flow<List<TripBooking>> = bookingDao.getAllBookings()

    fun getBookingsForUser(userId: String, email: String = ""): Flow<List<TripBooking>> =
        if (email.isNotBlank()) {
            bookingDao.getBookingsForUser(userId, email)
        } else {
            bookingDao.getBookingsForUser(userId)
        }

    suspend fun createBooking(booking: TripBooking): Long {
        if (booking.bookingId.isBlank()) return -1
        val existing = bookingDao.getBookingById(booking.bookingId)
        val toSave = if (existing != null) {
            booking.copy(id = existing.id)
        } else {
            booking
        }
        return bookingDao.insertBooking(toSave)
    }

    suspend fun deduplicateBookings() {
        try {
            bookingDao.deduplicateBookings()
        } catch (_: Exception) {}
    }

    suspend fun clearBookingsForUser(userId: String) {
        bookingDao.deleteBookingsForUser(userId)
    }

    suspend fun deleteBookingsForUser(userId: String) {
        bookingDao.deleteBookingsForUser(userId)
    }

    suspend fun clearAllBookings() {
        bookingDao.clearAll()
    }

    suspend fun getBookingById(bookingId: String): TripBooking? {
        return bookingDao.getBookingById(bookingId)
    }
}
