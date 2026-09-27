package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DestinationCategory(val displayName: String) {
    ALL("All"),
    MOUNTAINS("Mountains"),
    BEACHES("Beaches"),
    HERITAGE("Heritage"),
    ADVENTURE("Adventure"),
    WILDLIFE("Wildlife")
}

data class ItineraryDay(
    val dayNumber: Int,
    val title: String,
    val location: String,
    val activities: List<String>,
    val overnightStay: String,
    val imageResName: String = "kashmir_hero"
)

data class Destination(
    val id: String,
    val name: String,
    val title: String,
    val tagline: String,
    val location: String,
    val duration: String,
    val price: Int,
    val rating: Double,
    val reviewCount: String,
    val isBestSeller: Boolean = false,
    val isTrending: Boolean = false,
    val category: DestinationCategory = DestinationCategory.ALL,
    val imageRes: Int,
    val highlights: List<String>,
    val itinerary: List<ItineraryDay>,
    val inclusions: List<String>,
    val description: String
)

data class Hotel(
    val id: String,
    val name: String,
    val location: String,
    val rating: Double,
    val pricePerNight: Int,
    val amenities: List<String>,
    val imageRes: Int
)

data class Flight(
    val id: String,
    val airline: String,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val stops: String,
    val price: Int
)

data class TravelOffer(
    val id: String,
    val code: String,
    val title: String,
    val discountPercent: Int,
    val category: String,
    val validUntil: String,
    val description: String
)

@Entity(tableName = "trip_bookings")
data class TripBooking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookingId: String,
    val userId: String = "",
    val destinationId: String,
    val destinationTitle: String,
    val duration: String,
    val travellerName: String,
    val email: String,
    val phone: String,
    val adults: Int,
    val children: Int,
    val paymentMethod: String,
    val totalAmount: Int,
    val bookingDate: String,
    val status: String = "Confirmed",
    val imageRes: Int
)

@Entity(tableName = "saved_destinations")
data class SavedDestination(
    @PrimaryKey
    val destinationId: String,
    val userId: String = "",
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class VillaItem(
    val id: String,
    val name: String,
    val location: String,
    val bedrooms: Int,
    val guests: Int,
    val pricePerNight: Int,
    val amenities: List<String>,
    val imageUrl: String? = null
)

data class TrainItem(
    val id: String,
    val trainName: String,
    val trainNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val classes: List<String>,
    val price: Int
)

data class BusItem(
    val id: String,
    val operator: String,
    val busType: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Int,
    val rating: Double
)

data class CabItem(
    val id: String,
    val carModel: String,
    val serviceType: String,
    val category: String,
    val baseFare: Int,
    val perKm: Int
)

data class CruiseItem(
    val id: String,
    val cruiseName: String,
    val sailingRoute: String,
    val duration: String,
    val price: Int,
    val rating: Double,
    val shipClass: String
)

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: String = "custom", // "promotion", "booking_update", "alert", "welcome", "custom"
    val target: String = "all",
    val sentBy: String? = null,
    val sentAt: String? = null,
    var isRead: Boolean = false
)

data class WalletTransaction(
    val id: String,
    val title: String,
    val amount: Int,
    val isCredit: Boolean,
    val date: String,
    val status: String = "Completed",
    val description: String = ""
)

data class UserReview(
    val id: String,
    val userName: String,
    val destinationTitle: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val verifiedBooking: Boolean = true
)
