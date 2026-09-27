-- ==============================================================================
-- WONDERLIGHT TRAVEL - SUPABASE ROW LEVEL SECURITY (RLS) & ISOLATION POLICIES
-- Run this script in the Supabase SQL Editor to enforce strict user-level isolation
-- and configure real-time updates.
-- ==============================================================================

-- 1. PROFILES TABLE (Isolated to authenticated user)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT,
    name TEXT,
    phone TEXT,
    profile_photo TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Drop existing policies if any
DROP POLICY IF EXISTS "Users can view own profile" ON public.profiles;
DROP POLICY IF EXISTS "Users can insert own profile" ON public.profiles;
DROP POLICY IF EXISTS "Users can update own profile" ON public.profiles;

CREATE POLICY "Users can view own profile"
    ON public.profiles FOR SELECT
    USING (auth.uid() = id);

CREATE POLICY "Users can insert own profile"
    ON public.profiles FOR INSERT
    WITH CHECK (auth.uid() = id);

CREATE POLICY "Users can update own profile"
    ON public.profiles FOR UPDATE
    USING (auth.uid() = id);

-- 2. BOOKINGS TABLE (Isolated to authenticated user)
CREATE TABLE IF NOT EXISTS public.bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id TEXT UNIQUE,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    service_id TEXT,
    service_category TEXT,
    service_type TEXT,
    title TEXT,
    details TEXT,
    traveller_name TEXT,
    email TEXT,
    phone TEXT,
    total_amount INTEGER DEFAULT 0,
    status TEXT DEFAULT 'Confirmed',
    booking_date TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

ALTER TABLE public.bookings ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can view own bookings" ON public.bookings;
DROP POLICY IF EXISTS "Users can insert own bookings" ON public.bookings;
DROP POLICY IF EXISTS "Users can update own bookings" ON public.bookings;
DROP POLICY IF EXISTS "Users can delete own bookings" ON public.bookings;

CREATE POLICY "Users can view own bookings"
    ON public.bookings FOR SELECT
    USING (auth.uid() = user_id OR auth.role() = 'service_role');

CREATE POLICY "Users can insert own bookings"
    ON public.bookings FOR INSERT
    WITH CHECK (auth.uid() = user_id OR auth.role() = 'service_role');

CREATE POLICY "Users can update own bookings"
    ON public.bookings FOR UPDATE
    USING (auth.uid() = user_id OR auth.role() = 'service_role');

CREATE POLICY "Users can delete own bookings"
    ON public.bookings FOR DELETE
    USING (auth.uid() = user_id OR auth.role() = 'service_role');

-- 3. FAVORITES / WISHLIST TABLE (Isolated to authenticated user)
CREATE TABLE IF NOT EXISTS public.favorites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    service_id TEXT NOT NULL,
    service_type TEXT DEFAULT 'destination',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, service_id)
);

ALTER TABLE public.favorites ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can view own favorites" ON public.favorites;
DROP POLICY IF EXISTS "Users can insert own favorites" ON public.favorites;
DROP POLICY IF EXISTS "Users can delete own favorites" ON public.favorites;

CREATE POLICY "Users can view own favorites"
    ON public.favorites FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own favorites"
    ON public.favorites FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own favorites"
    ON public.favorites FOR DELETE
    USING (auth.uid() = user_id);

-- 4. NOTIFICATIONS TABLE (Isolated to authenticated user)
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can view own notifications" ON public.notifications;
DROP POLICY IF EXISTS "Users can update own notifications" ON public.notifications;

CREATE POLICY "Users can view own notifications"
    ON public.notifications FOR SELECT
    USING (auth.uid() = user_id OR auth.role() = 'service_role');

CREATE POLICY "Users can update own notifications"
    ON public.notifications FOR UPDATE
    USING (auth.uid() = user_id OR auth.role() = 'service_role');

-- 5. CATALOG TABLES: PUBLIC READ ACCESS FOR GUEST BROWSING & AUTHENTICATED USERS
-- (Villas, Trains, Buses, Cabs, Cruise, Hotels, Flights, Holidays, Destinations)
DO $$
BEGIN
    -- Enable RLS and public read for catalog tables
    PERFORM 1;
END $$;

ALTER TABLE public.villas ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view villas" ON public.villas;
CREATE POLICY "Public can view villas" ON public.villas FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write villas" ON public.villas;
CREATE POLICY "Admin write villas" ON public.villas FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.trains ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view trains" ON public.trains;
CREATE POLICY "Public can view trains" ON public.trains FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write trains" ON public.trains;
CREATE POLICY "Admin write trains" ON public.trains FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.buses ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view buses" ON public.buses;
CREATE POLICY "Public can view buses" ON public.buses FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write buses" ON public.buses;
CREATE POLICY "Admin write buses" ON public.buses FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.cabs ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view cabs" ON public.cabs;
CREATE POLICY "Public can view cabs" ON public.cabs FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write cabs" ON public.cabs;
CREATE POLICY "Admin write cabs" ON public.cabs FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.cruise ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view cruise" ON public.cruise;
CREATE POLICY "Public can view cruise" ON public.cruise FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write cruise" ON public.cruise;
CREATE POLICY "Admin write cruise" ON public.cruise FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.hotels ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view hotels" ON public.hotels;
CREATE POLICY "Public can view hotels" ON public.hotels FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write hotels" ON public.hotels;
CREATE POLICY "Admin write hotels" ON public.hotels FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.hotel_rooms ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view hotel_rooms" ON public.hotel_rooms;
CREATE POLICY "Public can view hotel_rooms" ON public.hotel_rooms FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write hotel_rooms" ON public.hotel_rooms;
CREATE POLICY "Admin write hotel_rooms" ON public.hotel_rooms FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.flights ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view flights" ON public.flights;
CREATE POLICY "Public can view flights" ON public.flights FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write flights" ON public.flights;
CREATE POLICY "Admin write flights" ON public.flights FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.holidays ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view holidays" ON public.holidays;
CREATE POLICY "Public can view holidays" ON public.holidays FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write holidays" ON public.holidays;
CREATE POLICY "Admin write holidays" ON public.holidays FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

ALTER TABLE public.destinations ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Public can view destinations" ON public.destinations;
CREATE POLICY "Public can view destinations" ON public.destinations FOR SELECT USING (true);
DROP POLICY IF EXISTS "Admin write destinations" ON public.destinations;
CREATE POLICY "Admin write destinations" ON public.destinations FOR ALL USING (auth.role() = 'service_role' OR auth.role() = 'authenticated');

-- 6. ENABLE SUPABASE REALTIME REPLICATION FOR LIVE ADMIN UPDATES
-- Tables that sync live between Admin panel and Mobile app
ALTER PUBLICATION supabase_realtime ADD TABLE public.hotels;
ALTER PUBLICATION supabase_realtime ADD TABLE public.hotel_rooms;
ALTER PUBLICATION supabase_realtime ADD TABLE public.villas;
ALTER PUBLICATION supabase_realtime ADD TABLE public.trains;
ALTER PUBLICATION supabase_realtime ADD TABLE public.buses;
ALTER PUBLICATION supabase_realtime ADD TABLE public.cabs;
ALTER PUBLICATION supabase_realtime ADD TABLE public.cruise;
ALTER PUBLICATION supabase_realtime ADD TABLE public.flights;
ALTER PUBLICATION supabase_realtime ADD TABLE public.holidays;
ALTER PUBLICATION supabase_realtime ADD TABLE public.bookings;
