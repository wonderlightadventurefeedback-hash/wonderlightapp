package com.example.data.remote

import android.util.Log
import com.example.R
import com.example.data.model.BusItem
import com.example.data.model.CabItem
import com.example.data.model.CruiseItem
import com.example.data.model.Destination
import com.example.data.model.DestinationCategory
import com.example.data.model.Flight
import com.example.data.model.Hotel
import com.example.data.model.TrainItem
import com.example.data.model.TripBooking
import com.example.data.model.VillaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object SupabaseSearchManager {
    private const val TAG = "SupabaseSearchManager"
    private const val SUPABASE_URL = SupabaseAuthManager.SUPABASE_URL
    private const val SUPABASE_KEY = SupabaseAuthManager.SUPABASE_KEY

    /**
     * Search Villas by location, guests, and price from Supabase
     */
    suspend fun searchVillas(
        location: String = "",
        minGuests: Int = 1,
        maxPrice: Int? = null
    ): List<VillaItem> = withContext(Dispatchers.IO) {
        val results = mutableListOf<VillaItem>()
        try {
            val endpoint = StringBuilder("$SUPABASE_URL/rest/v1/villas?select=*")
            if (location.isNotBlank()) {
                val cleanLoc = location.trim().split(" ", ",").firstOrNull { it.length > 2 } ?: location.trim()
                endpoint.append("&location=ilike.*").append(URLEncoder.encode(cleanLoc, "UTF-8")).append("*")
            }
            if (maxPrice != null && maxPrice > 0) {
                endpoint.append("&price_per_night=lte.$maxPrice")
            }

            val conn = (URL(endpoint.toString()).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val amenitiesList = mutableListOf<String>()
                    val amArray = obj.optJSONArray("amenities")
                    if (amArray != null) {
                        for (j in 0 until amArray.length()) {
                            amenitiesList.add(amArray.getString(j))
                        }
                    }

                    results.add(
                        VillaItem(
                            id = obj.optString("id", "vl_$i"),
                            name = obj.optString("name", "Luxury Villa"),
                            location = obj.optString("location", "Scenic Destination"),
                            bedrooms = obj.optInt("bedrooms", 3),
                            guests = obj.optInt("guests", 6),
                            pricePerNight = obj.optInt("price_per_night", 14000),
                            amenities = amenitiesList,
                            imageUrl = if (obj.has("image_url") && !obj.isNull("image_url")) obj.getString("image_url") else null
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Villa search query fallback: ${e.message}")
        }
        results
    }

    /**
     * Search Trains from Supabase by origin & destination
     */
    suspend fun searchTrains(
        origin: String = "",
        destination: String = ""
    ): List<TrainItem> = withContext(Dispatchers.IO) {
        val results = mutableListOf<TrainItem>()
        try {
            val endpoint = StringBuilder("$SUPABASE_URL/rest/v1/trains?select=*")
            val cleanOrigin = origin.trim().split(" ", "(").firstOrNull { it.length > 2 } ?: origin.trim()
            val cleanDest = destination.trim().split(" ", "(").firstOrNull { it.length > 2 } ?: destination.trim()

            if (cleanOrigin.isNotBlank()) {
                endpoint.append("&origin=ilike.*").append(URLEncoder.encode(cleanOrigin, "UTF-8")).append("*")
            }
            if (cleanDest.isNotBlank()) {
                endpoint.append("&destination=ilike.*").append(URLEncoder.encode(cleanDest, "UTF-8")).append("*")
            }

            val conn = (URL(endpoint.toString()).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val classesList = mutableListOf<String>()
                    val clArray = obj.optJSONArray("classes")
                    if (clArray != null) {
                        for (j in 0 until clArray.length()) {
                            classesList.add(clArray.getString(j))
                        }
                    }

                    results.add(
                        TrainItem(
                            id = obj.optString("id", "tr_$i"),
                            trainName = obj.optString("train_name", "Express"),
                            trainNumber = obj.optString("train_number", "12345"),
                            origin = obj.optString("origin", origin),
                            destination = obj.optString("destination", destination),
                            departureTime = obj.optString("departure_time", "06:00"),
                            arrivalTime = obj.optString("arrival_time", "14:00"),
                            duration = obj.optString("duration", "8h 00m"),
                            classes = if (classesList.isNotEmpty()) classesList else listOf("CC", "EC", "3A"),
                            price = obj.optInt("price", 1250)
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Train search query fallback: ${e.message}")
        }
        results
    }

    /**
     * Search Buses from Supabase by origin & destination
     */
    suspend fun searchBuses(
        origin: String = "",
        destination: String = ""
    ): List<BusItem> = withContext(Dispatchers.IO) {
        val results = mutableListOf<BusItem>()
        try {
            val endpoint = StringBuilder("$SUPABASE_URL/rest/v1/buses?select=*")
            val cleanOrigin = origin.trim().split(" ", "(").firstOrNull { it.length > 2 } ?: origin.trim()
            val cleanDest = destination.trim().split(" ", "(").firstOrNull { it.length > 2 } ?: destination.trim()

            if (cleanOrigin.isNotBlank()) {
                endpoint.append("&origin=ilike.*").append(URLEncoder.encode(cleanOrigin, "UTF-8")).append("*")
            }
            if (cleanDest.isNotBlank()) {
                endpoint.append("&destination=ilike.*").append(URLEncoder.encode(cleanDest, "UTF-8")).append("*")
            }

            val conn = (URL(endpoint.toString()).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    results.add(
                        BusItem(
                            id = obj.optString("id", "bus_$i"),
                            operator = obj.optString("operator", "Travel Operator"),
                            busType = obj.optString("bus_type", "AC Sleeper"),
                            origin = obj.optString("origin", origin),
                            destination = obj.optString("destination", destination),
                            departureTime = obj.optString("departure_time", "21:00"),
                            arrivalTime = obj.optString("arrival_time", "06:30"),
                            price = obj.optInt("price", 899),
                            rating = obj.optDouble("rating", 4.7)
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Bus search query fallback: ${e.message}")
        }
        results
    }

    /**
     * Search Cabs from Supabase by service type and model
     */
    suspend fun searchCabs(
        serviceType: String = ""
    ): List<CabItem> = withContext(Dispatchers.IO) {
        val results = mutableListOf<CabItem>()
        try {
            val endpoint = StringBuilder("$SUPABASE_URL/rest/v1/cabs?select=*")
            if (serviceType.isNotBlank() && !serviceType.equals("All", ignoreCase = true)) {
                val cleanType = serviceType.split(" ").firstOrNull() ?: serviceType
                endpoint.append("&service_type=ilike.*").append(URLEncoder.encode(cleanType, "UTF-8")).append("*")
            }

            val conn = (URL(endpoint.toString()).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    results.add(
                        CabItem(
                            id = obj.optString("id", "cab_$i"),
                            carModel = obj.optString("car_model", "Prime Sedan"),
                            serviceType = obj.optString("service_type", "Outstation"),
                            category = obj.optString("category", "4+1 AC Sedan"),
                            baseFare = obj.optInt("base_fare", 1200),
                            perKm = obj.optInt("per_km", 14)
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Cab search query fallback: ${e.message}")
        }
        results
    }

    /**
     * Search Cruises from Supabase
     */
    suspend fun searchCruises(
        port: String = "",
        destination: String = ""
    ): List<CruiseItem> = withContext(Dispatchers.IO) {
        val results = mutableListOf<CruiseItem>()
        try {
            val endpoint = StringBuilder("$SUPABASE_URL/rest/v1/cruise?select=*")
            if (port.isNotBlank()) {
                val cleanPort = port.trim().split(" ", ",").firstOrNull { it.length > 2 } ?: port.trim()
                endpoint.append("&sailing_route=ilike.*").append(URLEncoder.encode(cleanPort, "UTF-8")).append("*")
            }

            val conn = (URL(endpoint.toString()).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    results.add(
                        CruiseItem(
                            id = obj.optString("id", "cr_$i"),
                            cruiseName = obj.optString("cruise_name", "Luxury Cruise Liner"),
                            sailingRoute = obj.optString("sailing_route", "High Seas"),
                            duration = obj.optString("duration", "4 Nights"),
                            price = obj.optInt("price", 28000),
                            rating = obj.optDouble("rating", 4.8),
                            shipClass = obj.optString("ship_class", "5-Star Luxury Ocean Liner")
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Cruise search query fallback: ${e.message}")
        }
        results
    }

    /**
     * Fetch user-specific bookings from Supabase, strictly isolating data by userId
     */
    suspend fun fetchUserBookings(userId: String, token: String? = null, userEmail: String = ""): List<TripBooking> = withContext(Dispatchers.IO) {
        if (userId.isBlank() && userEmail.isBlank()) return@withContext emptyList()
        val bookings = mutableListOf<TripBooking>()
        try {
            val endpoint = if (userId.isNotBlank() && userEmail.isNotBlank()) {
                val encodedEmail = java.net.URLEncoder.encode(userEmail.trim(), "UTF-8")
                "$SUPABASE_URL/rest/v1/bookings?or=(user_id.eq.$userId,email.eq.$encodedEmail)&select=*&order=created_at.desc"
            } else if (userId.isNotBlank()) {
                "$SUPABASE_URL/rest/v1/bookings?user_id=eq.$userId&select=*&order=created_at.desc"
            } else {
                val encodedEmail = java.net.URLEncoder.encode(userEmail.trim(), "UTF-8")
                "$SUPABASE_URL/rest/v1/bookings?email=eq.$encodedEmail&select=*&order=created_at.desc"
            }
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                val authHeader = if (!token.isNullOrBlank()) "Bearer $token" else "Bearer $SUPABASE_KEY"
                setRequestProperty("Authorization", authHeader)
                connectTimeout = 4000
                readTimeout = 4000
            }

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val bookingId = obj.optString("booking_id", "WL${(10000..99999).random()}")
                    val title = obj.optString("title", "WonderLight Travel Booking")
                    val serviceType = obj.optString("service_type", "Travel")
                    val duration = obj.optString("duration", obj.optString("details", "Confirmed Booking"))
                    val totalAmount = obj.optInt("total_amount", 0)
                    val status = obj.optString("status", "Confirmed")
                    val bookingDate = obj.optString("booking_date", "Today")
                    val travellerName = obj.optString("traveller_name", "Traveller")
                    val email = obj.optString("email", "")
                    val phone = obj.optString("phone", "")

                    val imageRes = when {
                        serviceType.contains("Flight", ignoreCase = true) || title.contains("Flight", ignoreCase = true) -> R.drawable.hero_flight_service
                        serviceType.contains("Hotel", ignoreCase = true) || title.contains("Hotel", ignoreCase = true) -> R.drawable.hero_hotel_service
                        serviceType.contains("Train", ignoreCase = true) || title.contains("Train", ignoreCase = true) -> R.drawable.hero_train_service
                        title.contains("Goa", ignoreCase = true) -> R.drawable.goa_hero
                        title.contains("Kerala", ignoreCase = true) -> R.drawable.kerala_hero
                        title.contains("Rajasthan", ignoreCase = true) -> R.drawable.rajasthan_hero
                        else -> R.drawable.kashmir_hero
                    }

                    bookings.add(
                        TripBooking(
                            bookingId = bookingId,
                            userId = userId,
                            destinationId = "dest_${bookingId.lowercase()}",
                            destinationTitle = title,
                            duration = duration,
                            travellerName = travellerName,
                            email = email,
                            phone = phone,
                            adults = 2,
                            children = 0,
                            paymentMethod = "Online UPI",
                            totalAmount = totalAmount,
                            bookingDate = bookingDate,
                            status = status,
                            imageRes = imageRes
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Fetch user bookings error: ${e.message}")
        }
        bookings
    }

    /**
     * Save a user booking into Supabase bookings table
     */
    suspend fun saveBookingToSupabase(booking: TripBooking, userId: String, token: String? = null): Boolean = withContext(Dispatchers.IO) {
        if (userId.isBlank()) return@withContext false
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/bookings"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("apikey", SUPABASE_KEY)
                val authHeader = if (!token.isNullOrBlank()) "Bearer $token" else "Bearer $SUPABASE_KEY"
                setRequestProperty("Authorization", authHeader)
                setRequestProperty("Prefer", "return=representation")
                doOutput = true
                connectTimeout = 5000
                readTimeout = 5000
            }

            val body = JSONObject().apply {
                put("booking_id", booking.bookingId)
                put("user_id", userId)
                put("service_category", booking.destinationId)
                put("service_type", booking.destinationTitle.substringBefore(" •").substringBefore(" -"))
                put("title", booking.destinationTitle)
                put("details", "${booking.duration} • ${booking.adults} Adults")
                put("traveller_name", booking.travellerName)
                put("email", booking.email)
                put("phone", booking.phone)
                put("total_amount", booking.totalAmount)
                put("status", booking.status)
                put("booking_date", booking.bookingDate)
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(body.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            conn.disconnect()
            responseCode in 200..299
        } catch (e: Exception) {
            Log.e(TAG, "Error saving booking to Supabase: ${e.message}")
            false
        }
    }

    /**
     * Fetch all live flights from Supabase flights table
     */
    suspend fun fetchFlights(): List<Flight> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Flight>()
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/flights?select=*&order=created_at.desc"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }
            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Flight(
                            id = obj.optString("id", "fl_$i"),
                            airline = obj.optString("airline", "IndiGo"),
                            flightNumber = obj.optString("flight_number", obj.optString("flightNumber", "6E-101")),
                            origin = obj.optString("origin", "Delhi (DEL)"),
                            destination = obj.optString("destination", "Mumbai (BOM)"),
                            departureTime = obj.optString("departure_time", obj.optString("departureTime", "08:00")),
                            arrivalTime = obj.optString("arrival_time", obj.optString("arrivalTime", "10:15")),
                            duration = obj.optString("duration", "2h 15m"),
                            stops = obj.optString("stops", "Non-stop"),
                            price = obj.optInt("price", 4999)
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching live flights from Supabase: ${e.message}")
        }
        list
    }

    /**
     * Fetch all live hotels from Supabase hotels table
     */
    suspend fun fetchHotels(): List<Hotel> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Hotel>()
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/hotels?select=*&order=created_at.desc"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }
            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val amenities = mutableListOf<String>()
                    val amArray = obj.optJSONArray("amenities")
                    if (amArray != null) {
                        for (j in 0 until amArray.length()) amenities.add(amArray.getString(j))
                    } else {
                        amenities.addAll(listOf("Free WiFi", "Breakfast Included", "Pool"))
                    }
                    list.add(
                        Hotel(
                            id = obj.optString("id", "ht_$i"),
                            name = obj.optString("name", "Luxury Heritage Hotel"),
                            location = obj.optString("location", "Scenic Destination"),
                            rating = obj.optDouble("rating", 4.7),
                            pricePerNight = obj.optInt("price_per_night", obj.optInt("pricePerNight", 5499)),
                            amenities = amenities,
                            imageRes = R.drawable.hero_hotel_service
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching live hotels from Supabase: ${e.message}")
        }
        list
    }

    /**
     * Fetch all live holiday packages from Supabase holidays table
     */
    suspend fun fetchHolidays(): List<Destination> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Destination>()
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/holidays?select=*&order=created_at.desc"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }
            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val inclusions = mutableListOf<String>()
                    val incArray = obj.optJSONArray("inclusions")
                    if (incArray != null) {
                        for (j in 0 until incArray.length()) inclusions.add(incArray.getString(j))
                    } else {
                        inclusions.addAll(listOf("Sightseeing", "Transfers", "Breakfast"))
                    }
                    val loc = obj.optString("location", "India")
                    val img = when {
                        loc.contains("Goa", ignoreCase = true) -> R.drawable.goa_hero
                        loc.contains("Kerala", ignoreCase = true) -> R.drawable.kerala_hero
                        loc.contains("Rajasthan", ignoreCase = true) -> R.drawable.rajasthan_hero
                        loc.contains("Manali", ignoreCase = true) -> R.drawable.manali_snow
                        else -> R.drawable.kashmir_hero
                    }
                    list.add(
                        Destination(
                            id = obj.optString("id", "pkg_$i"),
                            name = obj.optString("title", "Holiday Package"),
                            title = obj.optString("title", "WonderLight Holiday Package"),
                            tagline = obj.optString("badge", "Popular Expedition"),
                            location = loc,
                            duration = obj.optString("duration", "5 Days / 4 Nights"),
                            price = obj.optInt("price", 19999),
                            rating = 4.8,
                            reviewCount = "240+ reviews",
                            isBestSeller = true,
                            isTrending = true,
                            category = DestinationCategory.ALL,
                            imageRes = img,
                            highlights = listOf("Curated Itinerary", "Verified Local Guides", "Luxury Transfers"),
                            itinerary = emptyList(),
                            inclusions = inclusions,
                            description = "Curated WonderLight holiday package with luxury accommodation and transfers."
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching live holidays from Supabase: ${e.message}")
        }
        list
    }

    /**
     * Fetch live notifications from Supabase notifications table
     */
    suspend fun fetchNotifications(userEmail: String?): List<com.example.data.model.AppNotification> = withContext(Dispatchers.IO) {
        val list = mutableListOf<com.example.data.model.AppNotification>()
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/notifications?order=created_at.desc"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }
            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val target = obj.optString("target", "all")
                    val matches = target.equals("all", ignoreCase = true) ||
                            (!userEmail.isNullOrBlank() && target.equals(userEmail.trim(), ignoreCase = true))
                    if (matches) {
                        list.add(
                            com.example.data.model.AppNotification(
                                id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                                title = obj.optString("title", "WonderLight Notification"),
                                body = obj.optString("body", ""),
                                type = obj.optString("type", "custom"),
                                target = target,
                                sentBy = obj.optString("sent_by", "WonderLight Admin"),
                                sentAt = obj.optString("sent_at", ""),
                                isRead = false
                            )
                        )
                    }
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching live notifications from Supabase: ${e.message}")
        }
        list
    }

    /**
     * Fetch the admin-controlled Signup Wallet Bonus from Supabase app_settings or Cloud Admin
     */
    suspend fun fetchSignupWalletBonus(): Int = withContext(Dispatchers.IO) {
        // 1. Try Supabase app_settings
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/app_settings?key=eq.signup_bonus&select=value"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 3000
                readTimeout = 3000
            }
            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = JSONArray(response)
                if (array.length() > 0) {
                    val valueStr = array.getJSONObject(0).optString("value", "2500")
                    val amt = valueStr.toIntOrNull()
                    if (amt != null && amt >= 0) {
                        Log.i(TAG, "Loaded signup wallet bonus from Supabase: ₹$amt")
                        conn.disconnect()
                        return@withContext amt
                    }
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching signup bonus from Supabase: ${e.message}")
        }

        // 2. Try Admin Server directly via AdminSyncManager
        try {
            val bonusFromAdmin = AdminSyncManager.fetchSignupBonusFromAdmin()
            if (bonusFromAdmin != null && bonusFromAdmin >= 0) {
                return@withContext bonusFromAdmin
            }
        } catch (_: Exception) {}

        // Default fallback if offline or not configured
        2500
    }
}

