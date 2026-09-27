# WonderLight Adventure - Android Mobile Application

**WonderLight Adventure** is a world-class travel, holiday booking, and exploration application built natively for Android using Kotlin and Jetpack Compose. Inspired by premium modern travel experiences, it features curated holiday expeditions across India (including Kashmir, Goa, Kerala, Rajasthan, Manali, Rishikesh, and Ladakh), seamless 3-step booking workflows, offline Room database persistence, and cutting-edge Gemini AI intelligence.

---

## 📱 Features & Highlights

1. **Brand Identity & Immersive Visuals**
   - Branded WonderLight emblem, mountain logo, and custom adaptive app launcher icon.
   - Cinematic photography hero banners and carousels for Kashmir, Goa, Kerala, and Rajasthan.
   - Material 3 dynamic color theming with Teal (`#08766E`) and Adventure Orange (`#F97316`).

2. **Full Travel Platform Capabilities**
   - **Home Screen**: Hero destination carousel, multi-service grid (Flights, Hotels, Holidays, Trains, Cabs, Villas, Buses, Visa, Forex, Insurance), Trending Destinations carousel, Weekend Escapes, and Explore by Interest.
   - **Search Screen**: Instant debounced search across destinations, popular chips, adventure categories, and recent queries.
   - **Explore Screen**: Category filtering (All, Mountains, Beaches, Heritage, Adventure, Wildlife), high-resolution cards with pricing, duration, ratings, and instant wishlist toggles.
   - **Destination Details**: Comprehensive 4-tab itinerary experience:
     - *Overview*: Highlights, why you'll love it, travel circuit.
     - *Itinerary*: Vertical timeline with day-by-day activities, overnight stays, and photo thumbnails.
     - *Inclusions*: Houseboats, private chauffeur vehicles, meal plans, Gondola cable car passes.
     - *Reviews*: Verified traveller reviews with star ratings.
   - **Complete 3-Step Booking Flow**:
     - *Step 1*: Traveller details (full name, email, phone with +91 country code, adults and children counter).
     - *Step 2*: Payment method selection (UPI with GPay/PhonePe, Credit/Debit cards, Net Banking, Wallets, EMI).
     - *Step 3*: Confirmation screen with animated checkmark, Booking ID (`WLAD######`), calendar integration, and receipt.
   - **My Trips**: Room database-backed booking manager with "Upcoming", "Completed", and "Cancelled" tabs, e-ticket downloads, and itinerary shortcuts.
   - **Special Offers & Deals**: Promo codes with instant one-tap clipboard copy (`WONDERHIMALAYA`, `COASTALESCAPE`, etc.).
   - **Hotels & Flights**: Dedicated reservation flows for luxury resorts and domestic flights.

3. **Gemini AI Travel Intelligence**
   - **`gemini-3.8-live` (Live API Voice Companion)**: Real-time voice interaction with animated audio pulse waves for natural conversational travel advice.
   - **`gemini-3.5-flash` with Google Maps Grounding**: Up-to-date accurate location recommendations, Dal Lake Shikara boarding ghats, Gulmarg cable car terminal coordinates, and hidden viewpoints.
   - **`gemini-3.1-pro-preview` with High Thinking (`thinkingLevel = ThinkingLevel.HIGH`)**: Deep analytical reasoning for custom multi-day itinerary route planning, altitude acclimation, and budget breakdowns.
   - **`gemini-3.1-flash-lite-preview`**: Ultra-fast packing tips, weather checks, and local Kashmiri Wazwan cuisine suggestions.

4. **Architecture & Persistence**
   - **Jetpack Compose & Material 3** with custom typography and design system.
   - **Room Database**:
     - `TripBooking`: Persists all confirmed bookings locally with reactive Kotlin `Flow`.
     - `SavedDestination`: Wishlist persistence with instant heart toggle.
     - `SearchHistoryItem`: Recent search query history.
   - **Secrets Gradle Plugin**: Secure injection of `GEMINI_API_KEY` from the AI Studio Secrets panel.

---

## 🏗️ Project Structure

```
WonderLightAdventure/
├── app/
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   └── WonderLightDatabase.kt
│   │   │   │   │   ├── mock/
│   │   │   │   │   │   └── TravelMockData.kt
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── TravelModels.kt
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   └── TravelAIEngine.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── TravelRepository.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── WLBottomNavigation.kt
│   │   │   │   │   │   ├── WLDestinationCard.kt
│   │   │   │   │   │   ├── WLHeroCarousel.kt
│   │   │   │   │   │   ├── WLServiceRow.kt
│   │   │   │   │   │   ├── WLTimelineView.kt
│   │   │   │   │   │   ├── WLTopBar.kt
│   │   │   │   │   │   └── WLVoiceDialog.kt
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── ai/AIAssistantScreen.kt
│   │   │   │   │   │   ├── booking/BookingScreens.kt
│   │   │   │   │   │   ├── destination/DestinationDetailScreen.kt
│   │   │   │   │   │   ├── explore/ExploreScreen.kt
│   │   │   │   │   │   ├── home/HomeScreen.kt
│   │   │   │   │   │   ├── offers/OffersScreen.kt
│   │   │   │   │   │   ├── profile/ProfileScreen.kt
│   │   │   │   │   │   ├── search/SearchScreen.kt
│   │   │   │   │   │   └── services/ServicesScreens.kt
│   │   │   │   │   │   └── trips/TripsScreen.kt
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Color.kt
│   │   │   │   │   │   ├── Theme.kt
│   │   │   │   │   │   └── Type.kt
│   │   │   │   │   └── viewmodel/
│   │   │   │   │       └── TravelViewModel.kt
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       ├── mipmap-*/
│   │   │       └── values/
├── build.gradle.kts
├── gradle/libs.versions.toml
├── metadata.json
└── README.md
```

---

## 🚀 Setup & Build Instructions

1. **Prerequisites**: Android SDK 36 (targetSdk 36, minSdk 24), JDK 11+, Gradle.
2. **Compile and Build**:
   ```bash
   gradle :app:assembleDebug
   ```
3. **Run Unit & Robolectric Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
4. **Gemini API Configuration**:
   - Add your Gemini API key in the Secrets panel in AI Studio or via `.env`:
     ```properties
     GEMINI_API_KEY=your_gemini_api_key_here
     ```
