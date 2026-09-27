package com.example.data.mock

import com.example.R
import com.example.data.model.Destination
import com.example.data.model.DestinationCategory
import com.example.data.model.Flight
import com.example.data.model.Hotel
import com.example.data.model.ItineraryDay
import com.example.data.model.TravelOffer
import com.example.data.model.TripBooking

object TravelMockData {

    val kashmirItinerary = listOf(
        ItineraryDay(
            dayNumber = 1,
            title = "Srinagar Arrival & Dal Lake",
            location = "Srinagar",
            activities = listOf(
                "Arrive at Srinagar airport & private transfer",
                "Check-in to luxury houseboat on Dal Lake",
                "Sunset Shikara ride on tranquil waters",
                "Overnight stay in traditional wooden houseboat"
            ),
            overnightStay = "Houseboat in Srinagar"
        ),
        ItineraryDay(
            dayNumber = 2,
            title = "Srinagar to Gulmarg Meadow",
            location = "Gulmarg",
            activities = listOf(
                "Scenic morning drive to Gulmarg (Meadow of Flowers)",
                "Gulmarg Gondola cable car ride to Kongdoori peak",
                "Pine forest walk & photography",
                "Overnight stay at mountain luxury resort"
            ),
            overnightStay = "Resort in Gulmarg"
        ),
        ItineraryDay(
            dayNumber = 3,
            title = "Gulmarg Snow Adventures",
            location = "Gulmarg",
            activities = listOf(
                "Snow activities, skiing & sledging (seasonal)",
                "Visit historic St. Mary's Church & Golf Course",
                "Free afternoon for local Kashmiri Kahwa & crafts",
                "Overnight stay in Gulmarg"
            ),
            overnightStay = "Resort in Gulmarg"
        ),
        ItineraryDay(
            dayNumber = 4,
            title = "Gulmarg to Pahalgam Valley",
            location = "Pahalgam",
            activities = listOf(
                "Enroute scenic drive passing saffron fields of Pampore",
                "Visit enchanting Betaab Valley & Aru Valley",
                "Lidder River riverside walk",
                "Overnight stay in picturesque Pahalgam hotel"
            ),
            overnightStay = "River Lodge in Pahalgam"
        ),
        ItineraryDay(
            dayNumber = 5,
            title = "Departure from Srinagar",
            location = "Srinagar",
            activities = listOf(
                "Morning transfer back to Srinagar",
                "Shopping for Kashmiri Pashmina, saffron & dry fruits",
                "Drop-off at Srinagar International Airport with fond memories"
            ),
            overnightStay = "Departure"
        )
    )

    val goaItinerary = listOf(
        ItineraryDay(
            dayNumber = 1,
            title = "Goa Arrival & Sunset Beach",
            location = "North Goa",
            activities = listOf(
                "Arrive at Dabolim / Mopa airport & transfer to coastal resort",
                "Relax on Anjuna & Vagator beach",
                "Sunset cafe dinner with live acoustic music",
                "Overnight stay at beachside boutique resort"
            ),
            overnightStay = "Resort in North Goa"
        ),
        ItineraryDay(
            dayNumber = 2,
            title = "Water Sports & Heritage Forts",
            location = "Calangute & Aguada",
            activities = listOf(
                "Morning jet ski & parasailing at Calangute beach",
                "Explore historic 17th-century Fort Aguada & lighthouse",
                "Mandovi river sunset cruise with Goan folk dance",
                "Overnight stay in North Goa"
            ),
            overnightStay = "Resort in North Goa"
        ),
        ItineraryDay(
            dayNumber = 3,
            title = "Old Goa Heritage & South Coast",
            location = "Old Goa & Palolem",
            activities = listOf(
                "Visit UNESCO heritage Basilica of Bom Jesus",
                "Drive down to peaceful Palolem & butterfly beach in South Goa",
                "Beach shack seafood BBQ dinner under starlight",
                "Overnight stay in Palolem"
            ),
            overnightStay = "Eco Resort in South Goa"
        ),
        ItineraryDay(
            dayNumber = 4,
            title = "Souvenir Shopping & Departure",
            location = "Panaji",
            activities = listOf(
                "Walk through colorful Portuguese quarter Fontainhas",
                "Pick up Goan feni, cashew nuts & Mario Miranda souvenirs",
                "Transfer to airport for return journey"
            ),
            overnightStay = "Departure"
        )
    )

    val keralaItinerary = listOf(
        ItineraryDay(
            dayNumber = 1,
            title = "Kochi to Munnar Hills",
            location = "Munnar",
            activities = listOf(
                "Pick up from Kochi airport & drive up through Cheeyappara waterfalls",
                "Check into misty hill resort overlooking tea gardens",
                "Evening stroll along Blossom International Park",
                "Overnight stay in Munnar"
            ),
            overnightStay = "Tea Valley Resort in Munnar"
        ),
        ItineraryDay(
            dayNumber = 2,
            title = "Munnar Tea Gardens & Eravikulam",
            location = "Munnar",
            activities = listOf(
                "Visit Eravikulam National Park (home to Nilgiri Tahr)",
                "Tour Tata Tea Museum and tea tasting session",
                "Photo stop at Mattupetty Dam & Echo Point",
                "Overnight stay in Munnar"
            ),
            overnightStay = "Tea Valley Resort in Munnar"
        ),
        ItineraryDay(
            dayNumber = 3,
            title = "Munnar to Thekkady Spice Sanctuary",
            location = "Thekkady",
            activities = listOf(
                "Scenic journey to spice hill town of Thekkady",
                "Guided spice plantation tour (cardamom, pepper, cinnamon)",
                "Evening Kathakali dance and Kalaripayattu martial arts show",
                "Overnight stay in Thekkady"
            ),
            overnightStay = "Wilderness Lodge in Thekkady"
        ),
        ItineraryDay(
            dayNumber = 4,
            title = "Thekkady to Alleppey Backwaters",
            location = "Alleppey",
            activities = listOf(
                "Board a private traditional Kerala Kettuvallam houseboat",
                "Cruise serene palm-fringed canals, paddy fields and lagoons",
                "Authentic freshly prepared Kerala lunch and dinner on board",
                "Overnight stay on the houseboat"
            ),
            overnightStay = "Deluxe Houseboat in Alleppey"
        ),
        ItineraryDay(
            dayNumber = 5,
            title = "Alleppey to Kochi & Departure",
            location = "Kochi",
            activities = listOf(
                "Disembark after morning cruise & sunrise breakfast",
                "Visit Chinese fishing nets & Fort Kochi colonial streets",
                "Transfer to Kochi airport for departure"
            ),
            overnightStay = "Departure"
        )
    )

    val rajasthanItinerary = listOf(
        ItineraryDay(
            dayNumber = 1,
            title = "Arrival in Pink City Jaipur",
            location = "Jaipur",
            activities = listOf(
                "Arrive at Jaipur airport & grand Rajasthani welcome",
                "Visit Hawa Mahal (Palace of Winds) & City Palace",
                "Evening street food tour and Johari bazaar shopping",
                "Overnight stay in heritage haveli hotel"
            ),
            overnightStay = "Heritage Haveli in Jaipur"
        ),
        ItineraryDay(
            dayNumber = 2,
            title = "Amber Fort & Royal Astronomy",
            location = "Jaipur",
            activities = listOf(
                "Jeep ride up to hilltop Amber Fort with panoramic views",
                "Marvel at Sheesh Mahal (Hall of Mirrors)",
                "Explore Jantar Mantar ancient astronomical observatory",
                "Overnight stay in Jaipur"
            ),
            overnightStay = "Heritage Haveli in Jaipur"
        ),
        ItineraryDay(
            dayNumber = 3,
            title = "Jaipur to Lake City Udaipur",
            location = "Udaipur",
            activities = listOf(
                "Drive through Aravalli hills to romantic Udaipur",
                "Sunset boat cruise on shimmering Lake Pichola",
                "View the illuminated Jag Mandir palace on the water",
                "Overnight stay in lake-view hotel"
            ),
            overnightStay = "Palace View Hotel in Udaipur"
        ),
        ItineraryDay(
            dayNumber = 4,
            title = "Udaipur Grand Palaces & Gardens",
            location = "Udaipur",
            activities = listOf(
                "Tour the sprawling Udaipur City Palace complex",
                "Visit Saheliyon-ki-Bari (Courtyard of Maidens)",
                "Evening cultural dance performance at Bagore Ki Haveli",
                "Overnight stay in Udaipur"
            ),
            overnightStay = "Palace View Hotel in Udaipur"
        ),
        ItineraryDay(
            dayNumber = 5,
            title = "Udaipur Departure",
            location = "Udaipur",
            activities = listOf(
                "Morning leisure & miniature painting workshop",
                "Transfer to Udaipur Maharana Pratap Airport"
            ),
            overnightStay = "Departure"
        )
    )

    val destinations = listOf(
        Destination(
            id = "kashmir",
            name = "Kashmir",
            title = "Kashmir Escape",
            tagline = "Mountains. Lakes. Magic.",
            location = "Srinagar • Gulmarg • Pahalgam",
            duration = "5 Days / 4 Nights",
            price = 24999,
            rating = 4.8,
            reviewCount = "2.4k reviews",
            isBestSeller = true,
            isTrending = true,
            category = DestinationCategory.MOUNTAINS,
            imageRes = R.drawable.kashmir_hero,
            highlights = listOf(
                "Stunning landscapes & scenic snow valleys",
                "Comfortable stays with breathtaking Himalayan views",
                "Local experiences & private guided tours",
                "Sunset Shikara ride on Dal Lake in Srinagar",
                "Gulmarg Gondola cable car ride included"
            ),
            itinerary = kashmirItinerary,
            inclusions = listOf(
                "4 nights stay (1N Houseboat, 2N Gulmarg, 1N Pahalgam)",
                "Daily gourmet breakfast & multi-course Kashmiri dinner",
                "Private chauffered AC sedan throughout the trip",
                "Shikara ride on Dal Lake (1 hour)",
                "Gulmarg Gondola phase 1 passes",
                "Sightseeing in Betaab & Aru valleys",
                "All toll taxes, parking fees, and driver allowances"
            ),
            description = "Known as paradise on earth, Kashmir offers snow-capped peaks, emerald meadows, glistening Dal Lake waters, and warm hospitality that creates unforgettable travel memories."
        ),
        Destination(
            id = "goa",
            name = "Goa",
            title = "Goa Sun & Coast",
            tagline = "Golden Sands. Turquoise Waters. Sunshine.",
            location = "North & South Goa Beaches",
            duration = "4 Days / 3 Nights",
            price = 18999,
            rating = 4.6,
            reviewCount = "1.8k reviews",
            isBestSeller = false,
            isTrending = true,
            category = DestinationCategory.BEACHES,
            imageRes = R.drawable.goa_hero,
            highlights = listOf(
                "Pristine beaches with golden sand and tropical vibes",
                "Exciting water sports & sunset Mandovi river cruise",
                "Charming Portuguese heritage quarters & beach shacks",
                "Vibrant nightlife, cafes, and seafood delicacies"
            ),
            itinerary = goaItinerary,
            inclusions = listOf(
                "3 nights stay at 4-star beachside resort with swimming pool",
                "Daily buffet breakfast",
                "Mandovi river sunset catamaran cruise with live music",
                "Full-day South Goa sightseeing in private AC car",
                "Airport pick-up and drop-off"
            ),
            description = "Unwind in the vibrant coastal haven of Goa. Bask under golden sunshine, feel the gentle waves, explore colonial Portuguese architecture, and celebrate life by the sea."
        ),
        Destination(
            id = "kerala",
            name = "Kerala",
            title = "Kerala Backwater Bliss",
            tagline = "God's Own Country. Serene canals & hills.",
            location = "Munnar • Thekkady • Alleppey",
            duration = "5 Days / 4 Nights",
            price = 22999,
            rating = 4.7,
            reviewCount = "3.1k reviews",
            isBestSeller = true,
            isTrending = true,
            category = DestinationCategory.WILDLIFE,
            imageRes = R.drawable.kerala_hero,
            highlights = listOf(
                "Exclusive overnight stay on a traditional Alleppey houseboat",
                "Misty rolling tea gardens of Munnar with valley viewpoints",
                "Aromatic spice plantation walks & Ayurvedic wellness",
                "Rich wildlife, waterfalls, and cultural performances"
            ),
            itinerary = keralaItinerary,
            inclusions = listOf(
                "2 nights in Munnar tea valley resort",
                "1 night in Thekkady spice villa",
                "1 night in private luxury Alleppey houseboat (all meals included)",
                "Daily breakfast at hotels",
                "Private vehicle for all transfers and sightseeing",
                "Spice plantation entry ticket & Kathakali show"
            ),
            description = "Immerse yourself in tranquil green waterways, cool mist-laden tea hills, and soulful Ayurvedic retreats in God's Own Country."
        ),
        Destination(
            id = "rajasthan",
            name = "Rajasthan",
            title = "Rajasthan Royal Heritage",
            tagline = "Forts. Palaces. Desert Royalty.",
            location = "Jaipur • Jodhpur • Udaipur",
            duration = "5 Days / 4 Nights",
            price = 26999,
            rating = 4.9,
            reviewCount = "4.2k reviews",
            isBestSeller = false,
            isTrending = true,
            category = DestinationCategory.HERITAGE,
            imageRes = R.drawable.rajasthan_hero,
            highlights = listOf(
                "Stately forts, royal courtyards, and mirror palaces",
                "Sunset boat cruise on Lake Pichola in Udaipur",
                "Rich Rajasthani culinary feast and folk music",
                "Vibrant bazaars with authentic handicrafts and jewelry"
            ),
            itinerary = rajasthanItinerary,
            inclusions = listOf(
                "4 nights stay in heritage haveli hotels",
                "Daily royal buffet breakfast",
                "Private chauffered sedan with English speaking driver",
                "Lake Pichola boat cruise in Udaipur",
                "Amber Fort jeep ride in Jaipur",
                "Monument entrance fees & local monument guides"
            ),
            description = "Step into a world of royal splendor, glorious maharajas, majestic hill forts, and shimmering palace lakes across regal Rajasthan."
        ),
        Destination(
            id = "manali",
            name = "Manali",
            title = "Manali & Solang Adventure",
            tagline = "Pine Forests. Snowy Peaks. Rohtang Pass.",
            location = "Manali • Solang • Rohtang",
            duration = "4 Days / 3 Nights",
            price = 17499,
            rating = 4.7,
            reviewCount = "1.9k reviews",
            isBestSeller = false,
            isTrending = false,
            category = DestinationCategory.ADVENTURE,
            imageRes = R.drawable.kashmir_hero,
            highlights = listOf(
                "Paragliding and ATV rides in Solang Valley",
                "Snow activities at Rohtang Pass / Atal Tunnel",
                "Historic Hadimba Temple in cedar forest",
                "Cozy cafes along Old Manali riverside"
            ),
            itinerary = kashmirItinerary,
            inclusions = listOf(
                "3 nights in alpine mountain view cottage",
                "Breakfast & dinner included",
                "Solang Valley adventure transfer",
                "Rohtang Pass excursion permit & cab"
            ),
            description = "High altitude thrills, cedar pine forest trails, and cool mountain breezes make Manali an eternal favorite for adventurers and nature lovers."
        ),
        Destination(
            id = "rishikesh",
            name = "Rishikesh",
            title = "Rishikesh River & Soul",
            tagline = "Ganges Rafting. Yoga. Campfire Starlight.",
            location = "Rishikesh • Shivpuri",
            duration = "3 Days / 2 Nights",
            price = 12999,
            rating = 4.8,
            reviewCount = "2.1k reviews",
            isBestSeller = false,
            isTrending = false,
            category = DestinationCategory.ADVENTURE,
            imageRes = R.drawable.kerala_hero,
            highlights = listOf(
                "Thrilling 16km white water rafting on the holy Ganges",
                "Riverside camping with campfire & barbecue",
                "Cliff jumping, body surfing, and bungee jumping",
                "Spiritual evening Ganga Aarti ceremony at Triveni Ghat"
            ),
            itinerary = goaItinerary,
            inclusions = listOf(
                "2 nights in luxury river-view camp with attached washroom",
                "All 3 meals (breakfast, lunch, dinner + evening snacks)",
                "16km rafting expedition with professional safety kayakers",
                "Evening bonfire music & games"
            ),
            description = "The adventure capital of India, where high-octane river rafting meets deep spiritual tranquility along the sacred Ganges river."
        ),
        Destination(
            id = "ladakh",
            name = "Ladakh",
            title = "Ladakh High Passes",
            tagline = "Roof of the World. Pangong Lake. Monasteries.",
            location = "Leh • Nubra Valley • Pangong Tso",
            duration = "6 Days / 5 Nights",
            price = 34999,
            rating = 4.9,
            reviewCount = "3.5k reviews",
            isBestSeller = true,
            isTrending = true,
            category = DestinationCategory.ADVENTURE,
            imageRes = R.drawable.kashmir_hero,
            highlights = listOf(
                "Drive over Khardung La, one of world's highest motorable passes",
                "Camp beside the ever-changing blues of Pangong Tso lake",
                "Double-humped camel safari amidst white sand dunes of Hunder",
                "Ancient cliffside Buddhist monasteries like Thiksey & Hemis"
            ),
            itinerary = kashmirItinerary,
            inclusions = listOf(
                "5 nights stay (3N Leh, 1N Nubra luxury tents, 1N Pangong lake camps)",
                "Oxygen cylinders & acclimatization first aid kit",
                "Inner Line permits & wildlife entry fees",
                "Buffet breakfast & warm Himalayan dinners"
            ),
            description = "A rugged, moon-like high desert realm crowned by turquoise lakes, ancient monasteries, and dramatic snow-clad mountain passes."
        )
    )

    val hotels = listOf(
        Hotel(
            id = "hotel_1",
            name = "The Waterfront Resort & Spa",
            location = "Boulevard Road, Dal Lake, Srinagar",
            rating = 4.8,
            pricePerNight = 8499,
            amenities = listOf("Lake View", "Free WiFi", "Breakfast Included", "Heated Pool", "Spa & Wellness"),
            imageRes = R.drawable.kashmir_hero
        ),
        Hotel(
            id = "hotel_2",
            name = "Taj Fort Aguada Resort",
            location = "Sinquerim Beach, Candolim, Goa",
            rating = 4.9,
            pricePerNight = 12999,
            amenities = listOf("Private Beach", "Infinity Pool", "Ocean View", "Fine Dining", "Fitness Center"),
            imageRes = R.drawable.goa_hero
        ),
        Hotel(
            id = "hotel_3",
            name = "Munnar Tea Hills Luxury Retreat",
            location = "Chithirapuram, Munnar, Kerala",
            rating = 4.7,
            pricePerNight = 6999,
            amenities = listOf("Tea Estate View", "Plantation Tour", "Bonfire", "Ayurvedic Spa", "Restaurant"),
            imageRes = R.drawable.kerala_hero
        ),
        Hotel(
            id = "hotel_4",
            name = "Umaid Bhawan Heritage Palace",
            location = "Circuit House Rd, Jodhpur, Rajasthan",
            rating = 4.9,
            pricePerNight = 15999,
            amenities = listOf("Royal Suites", "Historical Museum", "Palace Gardens", "Butler Service", "Pool"),
            imageRes = R.drawable.rajasthan_hero
        )
    )

    val flights = listOf(
        Flight(
            id = "fl_1",
            airline = "IndiGo",
            flightNumber = "6E-2419",
            origin = "DEL (New Delhi)",
            destination = "SXR (Srinagar)",
            departureTime = "06:15 AM",
            arrivalTime = "07:45 AM",
            duration = "1h 30m",
            stops = "Non-stop",
            price = 5499
        ),
        Flight(
            id = "fl_2",
            airline = "Air India",
            flightNumber = "AI-825",
            origin = "BOM (Mumbai)",
            destination = "GOI (Goa)",
            departureTime = "09:30 AM",
            arrivalTime = "10:45 AM",
            duration = "1h 15m",
            stops = "Non-stop",
            price = 4299
        ),
        Flight(
            id = "fl_3",
            airline = "Vistara",
            flightNumber = "UK-972",
            origin = "BLR (Bengaluru)",
            destination = "COK (Kochi)",
            departureTime = "11:20 AM",
            arrivalTime = "12:30 PM",
            duration = "1h 10m",
            stops = "Non-stop",
            price = 3899
        ),
        Flight(
            id = "fl_4",
            airline = "SpiceJet",
            flightNumber = "SG-184",
            origin = "DEL (New Delhi)",
            destination = "JAI (Jaipur)",
            departureTime = "02:15 PM",
            arrivalTime = "03:10 PM",
            duration = "55m",
            stops = "Non-stop",
            price = 2799
        )
    )

    val offers = listOf(
        TravelOffer(
            id = "off_1",
            code = "WONDERHIMALAYA",
            title = "Flat 20% Off on Kashmir & Ladakh",
            discountPercent = 20,
            category = "Holidays",
            validUntil = "Valid till Oct 31, 2026",
            description = "Get instant 20% discount on all mountain holiday packages above ₹20,000."
        ),
        TravelOffer(
            id = "off_2",
            code = "COASTALESCAPE",
            title = "Up to ₹3,500 Off on Beach Resorts",
            discountPercent = 15,
            category = "Hotels",
            validUntil = "Valid till Nov 15, 2026",
            description = "Enjoy exclusive savings on luxury beachfront properties in Goa & Kerala."
        ),
        TravelOffer(
            id = "off_3",
            code = "ADVENTUREFLY",
            title = "Zero Convenience Fee on Domestic Flights",
            discountPercent = 10,
            category = "Flights",
            validUntil = "Valid till Dec 31, 2026",
            description = "Book any round-trip flight across top Indian destinations with zero convenience charges."
        ),
        TravelOffer(
            id = "off_4",
            code = "EXPLORETRAIN",
            title = "15% Cashback on Vande Bharat & Express Trains",
            discountPercent = 15,
            category = "Trains",
            validUntil = "Valid till Oct 20, 2026",
            description = "Instant cashback on premium train bookings linking major tourism circuits."
        )
    )

    val defaultDemoBookings = emptyList<TripBooking>()
}
