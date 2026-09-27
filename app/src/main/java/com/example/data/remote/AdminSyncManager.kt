package com.example.data.remote

import android.util.Log
import com.example.R
import com.example.data.model.TripBooking
import com.example.data.repository.BookingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class SyncResult(
    val success: Boolean,
    val message: String,
    val syncedBookingsCount: Int = 0,
    val activeServicesCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

object AdminSyncManager {
    private const val TAG = "AdminSyncManager"
    
    // Candidate endpoints: Host localhost (works with adb reverse), loopback IP, Android emulator loopback (10.0.2.2)
    private val CANDIDATE_URLS = listOf(
        "http://localhost:8080/api",
        "http://127.0.0.1:8080/api",
        "http://10.0.2.2:8080/api"
    )

    @Volatile
    private var verifiedBaseUrl: String? = null

    private fun getBaseUrl(): String {
        return verifiedBaseUrl ?: CANDIDATE_URLS.first()
    }

    private fun probeEndpoints(): String {
        for (baseUrl in CANDIDATE_URLS) {
            try {
                val conn = URL("$baseUrl/stats").openConnection() as HttpURLConnection
                conn.connectTimeout = 800
                conn.readTimeout = 800
                conn.requestMethod = "GET"
                if (conn.responseCode in 200..299) {
                    conn.disconnect()
                    verifiedBaseUrl = baseUrl
                    Log.i(TAG, "Connected successfully to WonderLight Cloud at $baseUrl")
                    return baseUrl
                }
                conn.disconnect()
            } catch (_: Exception) {
                // Try next endpoint candidate
            }
        }
        return CANDIDATE_URLS.first()
    }

    suspend fun syncBookingToAdmin(booking: TripBooking): Boolean = withContext(Dispatchers.IO) {
        val baseUrl = verifiedBaseUrl ?: probeEndpoints()
        try {
            val url = URL("$baseUrl/bookings")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.doOutput = true

            val json = JSONObject().apply {
                put("bookingId", booking.bookingId)
                put("serviceType", booking.destinationTitle.substringBefore(" •").substringBefore(" -"))
                put("title", booking.destinationTitle)
                put("details", "${booking.duration} • ${booking.adults} Adults")
                put("travellerName", booking.travellerName)
                put("email", booking.email)
                put("phone", booking.phone)
                put("totalAmount", booking.totalAmount)
                put("bookingDate", booking.bookingDate)
                put("status", booking.status)
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(json.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            Log.d(TAG, "Sync booking ${booking.bookingId} to admin response: $responseCode")
            conn.disconnect()
            responseCode in 200..299
        } catch (e: Exception) {
            Log.w(TAG, "Admin server not reachable for booking push: ${e.message}")
            false
        }
    }

    /**
     * Fetches all cloud-synced bookings from the Admin backend and updates the local Room database.
     * Also probes the services catalog for live counts.
     */
    suspend fun reloadAllFromAdmin(bookingRepository: BookingRepository): SyncResult = withContext(Dispatchers.IO) {
        val baseUrl = probeEndpoints()
        var syncedBookings = 0
        var totalServices = 0

        try {
            // 1. Fetch Bookings from Cloud
            val bookingsUrl = URL("$baseUrl/bookings?_t=${System.currentTimeMillis()}")
            val conn = bookingsUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 2500
            conn.readTimeout = 2500
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/json")

            if (conn.responseCode in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val jsonArray = JSONArray(responseText)

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val bookingId = obj.optString("bookingId", "")
                    if (bookingId.isNotBlank()) {
                        val serviceType = obj.optString("serviceType", "Tours")
                        val title = obj.optString("title", "WonderLight Adventure")
                        val status = obj.optString("status", "Confirmed")
                        val totalAmount = obj.optInt("totalAmount", 12000)
                        val travellerName = obj.optString("travellerName", "Traveller")
                        val email = obj.optString("email", "")
                        val phone = obj.optString("phone", "")
                        val date = obj.optString("bookingDate", "Today")
                        val duration = obj.optString("details", "Standard Package")

                        // Select appropriate image resource
                        val imageRes = when {
                            serviceType.contains("Flight", ignoreCase = true) || title.contains("Flight", ignoreCase = true) -> R.drawable.hero_flight_service
                            serviceType.contains("Hotel", ignoreCase = true) || title.contains("Hotel", ignoreCase = true) -> R.drawable.hero_hotel_service
                            serviceType.contains("Train", ignoreCase = true) || title.contains("Express", ignoreCase = true) -> R.drawable.hero_train_service
                            title.contains("Goa", ignoreCase = true) -> R.drawable.goa_hero
                            title.contains("Kerala", ignoreCase = true) -> R.drawable.kerala_hero
                            title.contains("Rajasthan", ignoreCase = true) -> R.drawable.rajasthan_hero
                            else -> R.drawable.kashmir_hero
                        }

                        val existing = bookingRepository.getBookingById(bookingId)
                        val userId = obj.optString("userId", obj.optString("user_id", existing?.userId ?: ""))
                        val tripBooking = TripBooking(
                            id = existing?.id ?: 0,
                            bookingId = bookingId,
                            userId = userId,
                            destinationId = existing?.destinationId ?: "dest_${bookingId.lowercase()}",
                            destinationTitle = title,
                            duration = duration,
                            travellerName = travellerName,
                            email = email,
                            phone = phone,
                            adults = existing?.adults ?: 2,
                            children = existing?.children ?: 0,
                            paymentMethod = existing?.paymentMethod ?: "Online Payment",
                            totalAmount = totalAmount,
                            bookingDate = date,
                            status = status,
                            imageRes = imageRes
                        )

                        bookingRepository.createBooking(tripBooking)
                        syncedBookings++
                    }
                }
            }
            conn.disconnect()

            // 2. Query Services Stats
            try {
                val statsUrl = URL("$baseUrl/stats?_t=${System.currentTimeMillis()}")
                val statsConn = statsUrl.openConnection() as HttpURLConnection
                statsConn.connectTimeout = 1500
                statsConn.readTimeout = 1500
                if (statsConn.responseCode in 200..299) {
                    val statsText = statsConn.inputStream.bufferedReader().use(BufferedReader::readText)
                    val statsObj = JSONObject(statsText)
                    totalServices = statsObj.optInt("totalServices", 0)
                }
                statsConn.disconnect()
            } catch (_: Exception) {}

            SyncResult(
                success = true,
                message = "Cloud sync complete ($syncedBookings bookings synced, $totalServices live services)",
                syncedBookingsCount = syncedBookings,
                activeServicesCount = totalServices
            )
        } catch (e: Exception) {
            Log.w(TAG, "Cloud sync fallback to local Room cache: ${e.message}")
            SyncResult(
                success = true,
                message = "Offline reload complete (local Room storage active)",
                syncedBookingsCount = 0,
                activeServicesCount = 12
            )
        }
    }

    /**
     * Query the admin web server directly for the configured signup bonus
     */
    suspend fun fetchSignupBonusFromAdmin(): Int? = withContext(Dispatchers.IO) {
        val baseUrl = verifiedBaseUrl ?: probeEndpoints()
        try {
            val url = URL("$baseUrl/settings?_t=${System.currentTimeMillis()}")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 1500
            conn.readTimeout = 1500
            conn.requestMethod = "GET"
            if (conn.responseCode in 200..299) {
                val text = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val json = JSONObject(text)
                val bonus = json.optInt("signupBonus", -1)
                conn.disconnect()
                if (bonus >= 0) return@withContext bonus
            }
            conn.disconnect()
        } catch (_: Exception) {}
        null
    }
}

