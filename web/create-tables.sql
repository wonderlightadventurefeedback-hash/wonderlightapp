-- ============================================================
-- WonderLight Adventure — Complete Database Schema + Seed Data
-- Run this entire script in Supabase SQL Editor
-- URL: https://elcahnyfaogvittfrxaa.supabase.co
-- ============================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- TABLE: profiles (app users)
-- ============================================================
CREATE TABLE IF NOT EXISTS profiles (
  id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id     UUID UNIQUE REFERENCES auth.users(id) ON DELETE CASCADE,
  full_name   TEXT,
  email       TEXT,
  phone       TEXT,
  avatar_url  TEXT,
  created_at  TIMESTAMPTZ DEFAULT NOW(),
  updated_at  TIMESTAMPTZ DEFAULT NOW()
);
-- ============================================================
-- TABLE: app_settings (admin controls, e.g. signup wallet bonus)
-- ============================================================
CREATE TABLE IF NOT EXISTS app_settings (
  key         TEXT PRIMARY KEY,
  value       TEXT NOT NULL,
  updated_at  TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE app_settings DISABLE ROW LEVEL SECURITY;
INSERT INTO app_settings (key, value) VALUES ('signup_bonus', '2500') ON CONFLICT (key) DO NOTHING;

-- ============================================================
-- TABLE: notifications
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
  id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  title      TEXT NOT NULL,
  body       TEXT NOT NULL,
  type       TEXT DEFAULT 'custom',
  target     TEXT DEFAULT 'all',
  sent_by    TEXT,
  status     TEXT DEFAULT 'sent',
  sent_at    TIMESTAMPTZ DEFAULT NOW(),
  created_at TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE notifications DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: bookings
-- ============================================================
CREATE TABLE IF NOT EXISTS bookings (
  id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  booking_id       TEXT UNIQUE,
  user_id          UUID,
  service_category TEXT,
  service_type     TEXT,
  title            TEXT,
  details          TEXT,
  traveller_name   TEXT,
  email            TEXT,
  phone            TEXT,
  travel_date      TEXT,
  duration         TEXT,
  total_amount     NUMERIC DEFAULT 0,
  status           TEXT DEFAULT 'Pending',
  booking_date     TEXT,
  created_at       TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE bookings DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: flights
-- ============================================================
CREATE TABLE IF NOT EXISTS flights (
  id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  airline        TEXT NOT NULL,
  flight_number  TEXT,
  origin         TEXT,
  destination    TEXT,
  departure_time TEXT,
  arrival_time   TEXT,
  duration       TEXT,
  price          NUMERIC DEFAULT 0,
  stops          TEXT DEFAULT 'Non-stop',
  created_at     TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE flights DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: hotels
-- ============================================================
CREATE TABLE IF NOT EXISTS hotels (
  id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name            TEXT NOT NULL,
  location        TEXT,
  rating          NUMERIC DEFAULT 4.5,
  price_per_night NUMERIC DEFAULT 0,
  amenities       TEXT[],
  image_url       TEXT,
  created_at      TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE hotels DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: villas
-- ============================================================
CREATE TABLE IF NOT EXISTS villas (
  id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name            TEXT NOT NULL,
  location        TEXT,
  bedrooms        INT DEFAULT 3,
  guests          INT DEFAULT 6,
  price_per_night NUMERIC DEFAULT 0,
  amenities       TEXT[],
  image_url       TEXT,
  created_at      TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE villas DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: holidays
-- ============================================================
CREATE TABLE IF NOT EXISTS holidays (
  id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  title      TEXT NOT NULL,
  location   TEXT,
  duration   TEXT,
  price      NUMERIC DEFAULT 0,
  badge      TEXT DEFAULT 'Popular',
  inclusions TEXT[],
  image_url  TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE holidays DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: trains
-- ============================================================
CREATE TABLE IF NOT EXISTS trains (
  id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  train_name     TEXT NOT NULL,
  train_number   TEXT,
  origin         TEXT,
  destination    TEXT,
  departure_time TEXT,
  arrival_time   TEXT,
  duration       TEXT,
  classes        TEXT[],
  price          NUMERIC DEFAULT 0,
  created_at     TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE trains DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: buses
-- ============================================================
CREATE TABLE IF NOT EXISTS buses (
  id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  operator       TEXT NOT NULL,
  bus_type       TEXT,
  origin         TEXT,
  destination    TEXT,
  departure_time TEXT,
  arrival_time   TEXT,
  price          NUMERIC DEFAULT 0,
  rating         NUMERIC DEFAULT 4.5,
  created_at     TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE buses DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: cabs
-- ============================================================
CREATE TABLE IF NOT EXISTS cabs (
  id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  car_model    TEXT NOT NULL,
  service_type TEXT,
  category     TEXT,
  base_fare    NUMERIC DEFAULT 0,
  per_km       NUMERIC DEFAULT 14,
  created_at   TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE cabs DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: tours
-- ============================================================
CREATE TABLE IF NOT EXISTS tours (
  id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  title      TEXT NOT NULL,
  location   TEXT,
  duration   TEXT,
  price      NUMERIC DEFAULT 0,
  rating     NUMERIC DEFAULT 4.8,
  image_url  TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE tours DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: visa
-- ============================================================
CREATE TABLE IF NOT EXISTS visa (
  id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  country         TEXT NOT NULL,
  visa_type       TEXT,
  processing_time TEXT,
  price           NUMERIC DEFAULT 0,
  success_rate    TEXT DEFAULT '99%',
  created_at      TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE visa DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: cruise
-- ============================================================
CREATE TABLE IF NOT EXISTS cruise (
  id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  cruise_name   TEXT NOT NULL,
  sailing_route TEXT,
  duration      TEXT,
  price         NUMERIC DEFAULT 0,
  rating        NUMERIC DEFAULT 4.8,
  ship_class    TEXT,
  created_at    TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE cruise DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: forex
-- ============================================================
CREATE TABLE IF NOT EXISTS forex (
  id                   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  title                TEXT NOT NULL,
  type                 TEXT,
  delivery_time        TEXT,
  card_fee             NUMERIC DEFAULT 0,
  currencies_supported TEXT[],
  created_at           TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE forex DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- TABLE: insurance
-- ============================================================
CREATE TABLE IF NOT EXISTS insurance (
  id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  plan_name     TEXT NOT NULL,
  provider      TEXT,
  medical_cover TEXT,
  price_per_day NUMERIC DEFAULT 0,
  benefits      TEXT[],
  created_at    TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE insurance DISABLE ROW LEVEL SECURITY;

-- ============================================================
-- SEED: FLIGHTS
-- ============================================================
INSERT INTO flights (airline, flight_number, origin, destination, departure_time, arrival_time, duration, price, stops) VALUES
('IndiGo',            '6E-2543', 'Mumbai (BOM)',    'Delhi (DEL)',    '06:15', '08:30', '2h 15m',  4599,  'Non-stop'),
('Air India',         'AI-101',  'Delhi (DEL)',     'London (LHR)',   '02:30', '08:45', '9h 15m',  38500, 'Non-stop'),
('Vistara',           'UK-965',  'Bangalore (BLR)', 'Goa (GOI)',      '07:50', '09:10', '1h 20m',  3299,  'Non-stop'),
('SpiceJet',          'SG-131',  'Chennai (MAA)',   'Kolkata (CCU)',  '14:30', '17:00', '2h 30m',  3850,  'Non-stop'),
('Emirates',          'EK-507',  'Mumbai (BOM)',    'Dubai (DXB)',    '10:40', '12:55', '3h 15m',  22000, 'Non-stop'),
('IndiGo',            '6E-5317', 'Hyderabad (HYD)','Goa (GOI)',      '16:00', '17:40', '1h 40m',  2899,  'Non-stop'),
('Singapore Airlines','SQ-424',  'Delhi (DEL)',     'Singapore (SIN)','23:15','07:30', '5h 45m',  34500, 'Non-stop'),
('Air Arabia',        'G9-472',  'Ahmedabad (AMD)', 'Sharjah (SHJ)', '09:00', '11:30', '3h 30m',  18500, 'Non-stop'),
('GoFirst',           'G8-301',  'Mumbai (BOM)',    'Srinagar (SXR)', '06:30', '09:00', '2h 30m',  5200,  'Non-stop')
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: HOTELS
-- ============================================================
INSERT INTO hotels (name, location, rating, price_per_night, amenities) VALUES
('The Taj Mahal Palace',   'Mumbai, Maharashtra',         5.0, 28000, ARRAY['Infinity Pool','Spa','Fine Dining','Butler Service','Sea View']),
('Umaid Bhawan Palace',    'Jodhpur, Rajasthan',          5.0, 65000, ARRAY['Private Pool','Spa','Heritage Tours','Banquet Hall','Museum']),
('The Leela Palace',       'Udaipur, Rajasthan',          5.0, 32000, ARRAY['Lake View','Pool','Spa','Multi-cuisine Restaurant','Yoga']),
('Wildflower Hall',        'Shimla, Himachal Pradesh',    4.9, 19500, ARRAY['Mountain View','Spa','Outdoor Pool','Trekking','Fine Dining']),
('W Goa',                  'Vagator, Goa',                4.8, 14000, ARRAY['Beach Access','Pool','DJ Nights','Spa','Water Sports']),
('Kumarakom Lake Resort',  'Kumarakom, Kerala',           4.9, 22000, ARRAY['Lake View','Ayurvedic Spa','Houseboat','Infinity Pool','Yoga']),
('Ananda in the Himalayas','Rishikesh, Uttarakhand',      5.0, 35000, ARRAY['Yoga & Meditation','Ayurveda','Himalaya View','Wellness','Ganga View']),
('Park Hyatt Goa',         'Cansaulim, South Goa',        4.8, 16000, ARRAY['Beach Front','4 Pools','Golf','Spa','Kids Club']),
('Amanbagh',               'Alwar, Rajasthan',            5.0, 78000, ARRAY['Desert Pool','Heritage Tents','Elephant Rides','Yoga','Camel Safari'])
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: VILLAS
-- ============================================================
INSERT INTO villas (name, location, bedrooms, guests, price_per_night, amenities) VALUES
('Azure Pool Villa Candolim',  'Candolim Beach, Goa',           4, 8,  18000, ARRAY['Private Pool','Ocean View','BBQ','Chef on Request','Driver']),
('Misty Mountain Retreat',     'Coorg, Karnataka',              3, 6,  12000, ARRAY['Coffee Plantation View','Jacuzzi','Forest Walk','Bonfire','Breakfast Included']),
('Backwater Luxury Villa',     'Alleppey, Kerala',              5, 10, 22000, ARRAY['Private Jetty','Houseboat Rides','Ayurvedic Massage','Infinity Pool','Chef']),
('Himalayan Chateau',          'Manali, Himachal Pradesh',      6, 12, 28000, ARRAY['Snow View','Indoor Fireplace','River Facing','Adventure Bookings','Butler']),
('Tea Estate Bungalow',        'Munnar, Kerala',                3, 6,  9500,  ARRAY['Tea Garden View','Heritage Decor','Trekking','Birdwatching','Homemade Meals']),
('Beachfront White Villa',     'Morjim Beach, Goa',             3, 6,  16000, ARRAY['Private Pool','Beach Access','Yoga Deck','Chef','Golf Cart'])
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: HOLIDAYS
-- ============================================================
INSERT INTO holidays (title, location, duration, price, badge, inclusions) VALUES
('Kerala God''s Own Country',    'Munnar - Alleppey - Kumarakom',    '7 Days / 6 Nights', 32999, 'Bestseller',   ARRAY['Houseboat Stay','AC Rooms','All Meals','Transfers','Spice Plantation Visit']),
('Rajasthan Royal Heritage Tour','Jaipur - Jodhpur - Udaipur',       '8 Days / 7 Nights', 45000, 'Premium',      ARRAY['5-Star Hotels','Camel Safari','Cultural Shows','Transfers','Breakfast & Dinner']),
('Goa Beach & Party Escape',     'North & South Goa',                '5 Days / 4 Nights', 19999, 'Hot Deal',     ARRAY['Beach Resort','Breakfast','Water Sports','Casino Night','Airport Transfers']),
('Manali & Spiti Valley Trek',   'Manali - Kaza - Chandrataal',      '10 Days / 9 Nights',38500, 'Adventure',    ARRAY['Camps & Hotels','All Meals','Trek Guide','Permits','4WD Vehicle']),
('Andaman Island Bliss',         'Port Blair - Havelock - Neil',     '6 Days / 5 Nights', 41000, 'Exotic',       ARRAY['Beach Resort','Scuba Diving','Boat Transfers','Breakfast','Snorkeling']),
('Leh Ladakh Expedition',        'Leh - Nubra - Pangong',            '9 Days / 8 Nights', 52000, 'Epic',         ARRAY['Hotel & Camps','All Meals','Inner Line Permits','SUV','Biker Escort']),
('Singapore & Bali Dual Delight','Singapore - Bali - Sentosa',       '9 Days / 8 Nights', 74999, 'International',ARRAY['4-Star Hotels','Universal Studios','Kecak Dance','Transfers','Breakfast']),
('Kashmir Paradise on Earth',    'Srinagar - Gulmarg - Pahalgam',    '6 Days / 5 Nights', 35000, 'Scenic',       ARRAY['Houseboat & Hotels','All Meals','Shikara Ride','Gondola Pass','Transfers'])
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: TRAINS
-- ============================================================
INSERT INTO trains (train_name, train_number, origin, destination, departure_time, arrival_time, duration, classes, price) VALUES
('Vande Bharat Express', '22439', 'New Delhi',       'Varanasi',           '06:00', '14:00', '8h 00m',  ARRAY['CC','EC'],       1545),
('Rajdhani Express',     '12301', 'Howrah',          'New Delhi',          '16:55', '10:00', '17h 05m', ARRAY['1A','2A','3A'],  2850),
('Shatabdi Express',     '12001', 'Bhopal',          'New Delhi',          '06:00', '13:25', '7h 25m',  ARRAY['CC','EC'],       1100),
('Duronto Express',      '12213', 'Mumbai Central',  'Delhi',              '23:00', '15:55', '16h 55m', ARRAY['1A','2A','3A'],  2450),
('Kerala Express',       '12625', 'New Delhi',       'Thiruvananthapuram', '11:45', '20:30', '32h 45m', ARRAY['2A','3A','SL'],  1850),
('Gatimaan Express',     '12050', 'Hazrat Nizamuddin','Agra Cantt',        '08:10', '09:50', '1h 40m',  ARRAY['CC','EC'],       755),
('Tejas Express',        '22119', 'Mumbai CST',      'Goa Madgaon',        '05:25', '15:50', '10h 25m', ARRAY['CC','EC'],       1685)
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: BUSES
-- ============================================================
INSERT INTO buses (operator, bus_type, origin, destination, departure_time, arrival_time, price, rating) VALUES
('Orange Travels',          'Volvo 9600 Multi-Axle AC Sleeper',    'Bangalore', 'Chennai',   '22:00', '06:00', 899,  4.8),
('RedBus Premium',          'Mercedes-Benz Multi-Axle AC Seater',  'Mumbai',    'Goa',       '21:30', '07:30', 1299, 4.7),
('SRS Travels',             'Volvo AC Sleeper Double Decker',       'Hyderabad', 'Bangalore', '20:00', '06:00', 999,  4.6),
('KSRTC Airavat Club Class','AC Seater Semi-Sleeper',               'Bangalore', 'Mysore',    '07:00', '09:30', 350,  4.9),
('Neeta Tours',             'Volvo 9400 AC Multi-Axle Sleeper',     'Mumbai',    'Ahmedabad', '23:00', '07:00', 1100, 4.7),
('VRL Travels',             'Volvo B11R AC Sleeper',                'Bangalore', 'Goa',       '19:30', '07:00', 1450, 4.8)
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: CABS
-- ============================================================
INSERT INTO cabs (car_model, service_type, category, base_fare, per_km) VALUES
('Toyota Fortuner 4WD',     'Outstation & Mountain',     '7+1 Premium SUV',      2500, 18),
('Honda City ZX',            'Airport & Outstation',      '4+1 Premium Sedan',    1200, 13),
('Innova Crysta 2.8 ZX',    'Outstation & Family Tours', '6+1 MUV',              1800, 16),
('Mercedes-Benz E-Class',   'Business & Airport VIP',    '4+1 Luxury Sedan',     4500, 35),
('Tata Nexon EV',            'City & Airport Transfer',   '4+1 EV SUV',           900,  10),
('Toyota Etios',             'Economy Outstation',        '4+1 Economy Sedan',    800,  11),
('BMW X5 xDrive40i',        'VIP & Corporate',           '5+1 Luxury SUV',       6500, 45)
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: TOURS
-- ============================================================
INSERT INTO tours (title, location, duration, price, rating) VALUES
('Taj Mahal Sunrise Tour by Tuk-Tuk',    'Agra, Uttar Pradesh',       '4 Hours',   1499, 5.0),
('Kerala Houseboat Day Cruise',           'Alleppey, Kerala',          '8 Hours',   3200, 4.9),
('Jaipur Elephant Safari at Amer Fort',  'Jaipur, Rajasthan',         '3 Hours',   2500, 4.7),
('Goa Scuba Diving PADI Open Water',     'Grand Island, Goa',         'Full Day',  4500, 4.8),
('Dal Lake Shikara Ride at Sunset',      'Srinagar, Kashmir',         '2 Hours',   1800, 4.9),
('Rishikesh White Water Rafting 16 KM',  'Rishikesh, Uttarakhand',    '3 Hours',   1200, 4.8),
('Varanasi Ganga Aarti Boat Experience', 'Varanasi, Uttar Pradesh',   '3 Hours',   999,  5.0),
('Mysore Palace Night Illumination Tour','Mysore, Karnataka',          '4 Hours',   1500, 4.9),
('Ranthambore Wildlife Safari',          'Sawai Madhopur, Rajasthan', 'Half Day',  3500, 4.8)
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: VISA
-- ============================================================
INSERT INTO visa (country, visa_type, processing_time, price, success_rate) VALUES
('United Arab Emirates',  'Tourist Visa 30 Days',      '24 Hours',         4500,  '99.8%'),
('Thailand',              'Tourist Visa On Arrival',   '48 Hours',         3200,  '99.5%'),
('United Kingdom',        'Standard Visitor 6 Months', '15 Working Days',  15000, '97%'),
('Schengen Europe',       'Short Stay Visa 90 Days',   '10 Working Days',  9500,  '96%'),
('Canada',                'Visitor Visa Single Entry',  '21 Working Days', 12000, '94%'),
('Sri Lanka',             'eVisa 30 Days',              '12 Hours',         1500,  '99.9%'),
('Singapore',             'Tourist Visa 30 Days',       '5 Working Days',   5000,  '98.5%'),
('Malaysia',              'eNTRI / eVisa 15 Days',      '24 Hours',         2000,  '99.7%')
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: CRUISE
-- ============================================================
INSERT INTO cruise (cruise_name, sailing_route, duration, price, rating, ship_class) VALUES
('Cordelia Empress',       'Mumbai - Goa - Lakshadweep - Kochi',         '5 Nights',  32000,  4.9, '5-Star Luxury Cruise Liner'),
('Costa Smeralda',         'Dubai - Abu Dhabi - Muscat - Mumbai',        '7 Nights',  68000,  4.8, '6-Star Ultra-Luxury Liner'),
('Antara Ganga Voyage',    'Varanasi - Patna - Kolkata',                 '14 Nights', 145000, 4.9, 'River Heritage Luxury Liner'),
('Royal Caribbean Spectrum','Singapore - Penang - Langkawi - Bangkok',   '8 Nights',  95000,  5.0, 'Mega Luxury Ocean Liner'),
('Jalesh Karnika',         'Goa - Mumbai - Oman',                        '4 Nights',  28000,  4.7, '4-Star Cruise Ship'),
('MSC Virtuosa',           'Mumbai - Male - Colombo - Kochi',            '10 Nights', 115000, 4.9, '5-Star European Liner')
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: FOREX
-- ============================================================
INSERT INTO forex (title, type, delivery_time, card_fee, currencies_supported) VALUES
('WonderLight Multi-Currency Card', 'Prepaid Zero Markup Forex Card',   '24 Hours Doorstep',          0,   ARRAY['USD','EUR','GBP','JPY','AED','SGD','AUD','CAD']),
('Euro Currency Cash Pack',         'Mint Notes Home Delivery',          'Same Day in Metro Cities',   150, ARRAY['EUR']),
('US Dollar Currency Bundle',       'Mint Banknotes Multiple Denominations','2 Hours Express Delivery',100, ARRAY['USD']),
('Thomas Cook Forex Card',          'Multi-Currency Prepaid Card',        '48 Hours',                  250, ARRAY['USD','EUR','GBP','AED','AUD','CAD']),
('UAE Dirham Cash Delivery',        'Crisp Banknotes Home Delivery',      'Same Day',                  100, ARRAY['AED'])
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: INSURANCE
-- ============================================================
INSERT INTO insurance (plan_name, provider, medical_cover, price_per_day, benefits) VALUES
('WonderLight Global Elite',   'WonderLight Assurance Partners', '$500,000 USD', 149, ARRAY['Emergency Evacuation','Trip Cancellation','Lost Baggage','Passport Loss','COVID Cover']),
('Asia Pacific Shield',         'HDFC ERGO Travel Guard',         '$250,000 USD', 99,  ARRAY['Medical Emergency','Flight Delay','Baggage Loss','Personal Accident','Adventure Cover']),
('Schengen Compliant Plan',    'Bajaj Allianz Travel Care',      '100,000 EUR',  129, ARRAY['Medical Cover','Repatriation','Trip Interruption','Baggage Delay','Dental Emergency']),
('Domestic Road Warrior',       'ICICI Lombard Tour Secure',      '10,00,000 INR',39,  ARRAY['Personal Accident','Hospital Cover','Trip Delay','Baggage Loss','Home Burglary']),
('Adventure Sports Cover',     'New India Assurance',            '$300,000 USD', 175, ARRAY['Adventure Sports','Heli Rescue','Extreme Sports','Mountain Rescue','Medical Evacuation'])
ON CONFLICT DO NOTHING;

-- ============================================================
-- SEED: BOOKINGS (Sample)
-- ============================================================
INSERT INTO bookings (booking_id, service_category, service_type, title, traveller_name, email, phone, travel_date, total_amount, status, booking_date) VALUES
('WL-2024-001', 'flights',   'Flight',   'Mumbai to Delhi (IndiGo 6E-2543)',          'Rahul Sharma',  'rahul.sharma@gmail.com',  '9876543210', '2024-12-15', 4599,  'Confirmed',   '2024-11-20'),
('WL-2024-002', 'hotels',    'Hotel',    'The Taj Mahal Palace - 3 Nights',           'Priya Patel',   'priya.patel@gmail.com',   '9876543211', '2024-12-20', 84000, 'Confirmed',   '2024-11-22'),
('WL-2024-003', 'holidays',  'Package',  'Kerala God''s Own Country - 7 Days',        'Amit Singh',    'amit.singh@gmail.com',    '9876543212', '2025-01-10', 65998, 'In Progress', '2024-11-25'),
('WL-2024-004', 'visa',      'Visa',     'UAE Tourist Visa 30 Days',                  'Sneha Menon',   'sneha.menon@gmail.com',   '9876543213', '2024-12-01', 4500,  'Completed',   '2024-11-15'),
('WL-2024-005', 'cruise',    'Cruise',   'Cordelia Empress - 5 Nights',               'Vikram Nair',   'vikram.nair@gmail.com',   '9876543214', '2025-02-14', 64000, 'Pending',     '2024-11-28'),
('WL-2024-006', 'cabs',      'Cab',      'Toyota Fortuner - Manali Outstation',       'Deepa Rao',     'deepa.rao@gmail.com',     '9876543215', '2024-12-26', 8500,  'Confirmed',   '2024-11-30'),
('WL-2024-007', 'trains',    'Train',    'Vande Bharat Express Delhi to Varanasi',    'Ravi Kumar',    'ravi.kumar@gmail.com',    '9876543216', '2024-12-18', 1545,  'Confirmed',   '2024-11-28'),
('WL-2024-008', 'tours',     'Tour',     'Taj Mahal Sunrise Tour by Tuk-Tuk',         'Anjali Verma',  'anjali.verma@gmail.com',  '9876543217', '2024-12-22', 1499,  'Confirmed',   '2024-12-01'),
('WL-2024-009', 'insurance', 'Insurance','WonderLight Global Elite - 14 Days',        'Suresh Reddy',  'suresh.reddy@gmail.com',  '9876543218', '2025-01-05', 2086,  'Completed',   '2024-11-18'),
('WL-2024-010', 'forex',     'Forex',    'Multi-Currency Travel Card Issuance',       'Kavitha Iyer',  'kavitha.iyer@gmail.com',  '9876543219', '2024-12-10', 0,     'Completed',   '2024-11-28'),
('WL-2024-011', 'hotels',    'Hotel',    'Wildflower Hall Shimla - 4 Nights',         'Mohan Das',     'mohan.das@gmail.com',     '9876543220', '2025-01-15', 78000, 'Pending',     '2024-12-02'),
('WL-2024-012', 'holidays',  'Package',  'Leh Ladakh Expedition - 9 Days',            'Nisha Kapoor',  'nisha.kapoor@gmail.com',  '9876543221', '2025-05-01', 104000,'Confirmed',   '2024-12-01')
ON CONFLICT (booking_id) DO NOTHING;

-- ============================================================
-- Grant public access for admin operations
-- ============================================================
GRANT ALL ON ALL TABLES IN SCHEMA public TO anon, authenticated;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO anon, authenticated;

-- ============================================================
-- Done!
-- ============================================================
SELECT 
  (SELECT COUNT(*) FROM flights)   AS flights,
  (SELECT COUNT(*) FROM hotels)    AS hotels,
  (SELECT COUNT(*) FROM villas)    AS villas,
  (SELECT COUNT(*) FROM holidays)  AS holidays,
  (SELECT COUNT(*) FROM trains)    AS trains,
  (SELECT COUNT(*) FROM buses)     AS buses,
  (SELECT COUNT(*) FROM cabs)      AS cabs,
  (SELECT COUNT(*) FROM tours)     AS tours,
  (SELECT COUNT(*) FROM visa)      AS visa,
  (SELECT COUNT(*) FROM cruise)    AS cruise,
  (SELECT COUNT(*) FROM forex)     AS forex,
  (SELECT COUNT(*) FROM insurance) AS insurance,
  (SELECT COUNT(*) FROM bookings)  AS bookings;
