/**
 * WonderLight — Supabase Seed Script (Fixed)
 * Uses service role key to insert data via REST API
 */
const https = require('https');

const SUPABASE_URL    = 'https://elcahnyfaogvittfrxaa.supabase.co';
const SERVICE_KEY     = 'sb_secret_AFiaNX8om4SMXprlDVQpUA_OIehjX1v';

function apiRequest(method, path, body) {
  return new Promise((resolve, reject) => {
    const url = new URL(SUPABASE_URL + path);
    const data = body ? JSON.stringify(body) : null;
    const options = {
      hostname: url.hostname,
      path:     url.pathname + url.search,
      method,
      headers: {
        'Content-Type':  'application/json',
        'apikey':        SERVICE_KEY,
        'Authorization': 'Bearer ' + SERVICE_KEY,
        'Prefer':        'return=representation',
      }
    };
    if (data) options.headers['Content-Length'] = Buffer.byteLength(data);

    const req = https.request(options, (res) => {
      let raw = '';
      res.on('data', chunk => raw += chunk);
      res.on('end', () => {
        try { resolve({ status: res.statusCode, data: JSON.parse(raw) }); }
        catch(e) { resolve({ status: res.statusCode, data: raw }); }
      });
    });
    req.on('error', reject);
    if (data) req.write(data);
    req.end();
  });
}

async function insert(table, rows) {
  const r = await apiRequest('POST', `/rest/v1/${table}?on_conflict=id`, rows);
  if (r.status >= 400) {
    // Try without on_conflict
    const r2 = await apiRequest('POST', `/rest/v1/${table}`, rows);
    return r2;
  }
  return r;
}

async function count(table) {
  const r = await apiRequest('GET', `/rest/v1/${table}?select=id&limit=1`);
  if (r.status >= 400) return -1;
  return Array.isArray(r.data) ? r.data.length : 0;
}

async function run() {
  console.log('\n🚀 WonderLight — Supabase Data Seeder\n');

  const SEED = {
    app_settings: [
      { key: 'signup_bonus', value: '2500' }
    ],
    flights: [
      { airline:'IndiGo', flight_number:'6E-2543', origin:'Mumbai (BOM)', destination:'Delhi (DEL)', departure_time:'06:15', arrival_time:'08:30', duration:'2h 15m', price:4599, stops:'Non-stop' },
      { airline:'Air India', flight_number:'AI-101', origin:'Delhi (DEL)', destination:'London (LHR)', departure_time:'02:30', arrival_time:'08:45', duration:'9h 15m', price:38500, stops:'Non-stop' },
      { airline:'Vistara', flight_number:'UK-965', origin:'Bangalore (BLR)', destination:'Goa (GOI)', departure_time:'07:50', arrival_time:'09:10', duration:'1h 20m', price:3299, stops:'Non-stop' },
      { airline:'SpiceJet', flight_number:'SG-131', origin:'Chennai (MAA)', destination:'Kolkata (CCU)', departure_time:'14:30', arrival_time:'17:00', duration:'2h 30m', price:3850, stops:'Non-stop' },
      { airline:'Emirates', flight_number:'EK-507', origin:'Mumbai (BOM)', destination:'Dubai (DXB)', departure_time:'10:40', arrival_time:'12:55', duration:'3h 15m', price:22000, stops:'Non-stop' },
      { airline:'IndiGo', flight_number:'6E-5317', origin:'Hyderabad (HYD)', destination:'Goa (GOI)', departure_time:'16:00', arrival_time:'17:40', duration:'1h 40m', price:2899, stops:'Non-stop' },
      { airline:'Singapore Airlines', flight_number:'SQ-424', origin:'Delhi (DEL)', destination:'Singapore (SIN)', departure_time:'23:15', arrival_time:'07:30', duration:'5h 45m', price:34500, stops:'Non-stop' },
      { airline:'GoFirst', flight_number:'G8-301', origin:'Mumbai (BOM)', destination:'Srinagar (SXR)', departure_time:'06:30', arrival_time:'09:00', duration:'2h 30m', price:5200, stops:'Non-stop' },
    ],
    hotels: [
      { name:'The Taj Mahal Palace', location:'Mumbai, Maharashtra', rating:5.0, price_per_night:28000, amenities:['Infinity Pool','Spa','Fine Dining','Butler Service','Sea View'] },
      { name:'Umaid Bhawan Palace', location:'Jodhpur, Rajasthan', rating:5.0, price_per_night:65000, amenities:['Private Pool','Spa','Heritage Tours','Banquet Hall','Museum'] },
      { name:'The Leela Palace', location:'Udaipur, Rajasthan', rating:5.0, price_per_night:32000, amenities:['Lake View','Pool','Spa','Multi-cuisine Restaurant','Yoga'] },
      { name:'Wildflower Hall', location:'Shimla, Himachal Pradesh', rating:4.9, price_per_night:19500, amenities:['Mountain View','Spa','Outdoor Pool','Trekking','Fine Dining'] },
      { name:'W Goa', location:'Vagator, Goa', rating:4.8, price_per_night:14000, amenities:['Beach Access','Pool','DJ Nights','Spa','Water Sports'] },
      { name:'Kumarakom Lake Resort', location:'Kumarakom, Kerala', rating:4.9, price_per_night:22000, amenities:['Lake View','Ayurvedic Spa','Houseboat','Infinity Pool','Yoga'] },
      { name:'Ananda in the Himalayas', location:'Rishikesh, Uttarakhand', rating:5.0, price_per_night:35000, amenities:['Yoga & Meditation','Ayurveda','Himalaya View','Wellness','Ganga View'] },
      { name:'Park Hyatt Goa', location:'Cansaulim, South Goa', rating:4.8, price_per_night:16000, amenities:['Beach Front','4 Pools','Golf','Spa','Kids Club'] },
    ],
    villas: [
      { name:'Azure Pool Villa Candolim', location:'Candolim Beach, Goa', bedrooms:4, guests:8, price_per_night:18000, amenities:['Private Pool','Ocean View','BBQ','Chef on Request','Driver'] },
      { name:'Misty Mountain Retreat', location:'Coorg, Karnataka', bedrooms:3, guests:6, price_per_night:12000, amenities:['Coffee Plantation View','Jacuzzi','Forest Walk','Bonfire','Breakfast Included'] },
      { name:'Backwater Luxury Villa', location:'Alleppey, Kerala', bedrooms:5, guests:10, price_per_night:22000, amenities:['Private Jetty','Houseboat Rides','Ayurvedic Massage','Infinity Pool','Chef'] },
      { name:'Himalayan Chateau', location:'Manali, Himachal Pradesh', bedrooms:6, guests:12, price_per_night:28000, amenities:['Snow View','Indoor Fireplace','River Facing','Adventure Bookings','Butler'] },
      { name:'Tea Estate Bungalow', location:'Munnar, Kerala', bedrooms:3, guests:6, price_per_night:9500, amenities:['Tea Garden View','Heritage Decor','Trekking','Birdwatching','Homemade Meals'] },
    ],
    holidays: [
      { title:"Kerala God's Own Country", location:'Munnar - Alleppey - Kumarakom', duration:'7 Days / 6 Nights', price:32999, badge:'Bestseller', inclusions:['Houseboat Stay','AC Rooms','All Meals','Transfers','Spice Plantation Visit'] },
      { title:'Rajasthan Royal Heritage Tour', location:'Jaipur - Jodhpur - Udaipur', duration:'8 Days / 7 Nights', price:45000, badge:'Premium', inclusions:['5-Star Hotels','Camel Safari','Cultural Shows','Transfers','Breakfast & Dinner'] },
      { title:'Goa Beach & Party Escape', location:'North & South Goa', duration:'5 Days / 4 Nights', price:19999, badge:'Hot Deal', inclusions:['Beach Resort','Breakfast','Water Sports','Casino Night','Airport Transfers'] },
      { title:'Manali & Spiti Valley Trek', location:'Manali - Kaza - Chandrataal', duration:'10 Days / 9 Nights', price:38500, badge:'Adventure', inclusions:['Camps & Hotels','All Meals','Trek Guide','Permits','4WD Vehicle'] },
      { title:'Andaman Island Bliss', location:'Port Blair - Havelock - Neil Island', duration:'6 Days / 5 Nights', price:41000, badge:'Exotic', inclusions:['Beach Resort','Scuba Diving','Boat Transfers','Breakfast','Snorkeling'] },
      { title:'Leh Ladakh Expedition', location:'Leh - Nubra - Pangong', duration:'9 Days / 8 Nights', price:52000, badge:'Epic', inclusions:['Hotel & Camps','All Meals','Inner Line Permits','SUV','Biker Escort'] },
      { title:'Singapore & Bali Dual Delight', location:'Singapore - Bali - Sentosa', duration:'9 Days / 8 Nights', price:74999, badge:'International', inclusions:['4-Star Hotels','Universal Studios','Kecak Dance','Transfers','Breakfast'] },
    ],
    trains: [
      { train_name:'Vande Bharat Express', train_number:'22439', origin:'New Delhi', destination:'Varanasi', departure_time:'06:00', arrival_time:'14:00', duration:'8h 00m', classes:['CC','EC'], price:1545 },
      { train_name:'Rajdhani Express', train_number:'12301', origin:'Howrah', destination:'New Delhi', departure_time:'16:55', arrival_time:'10:00', duration:'17h 05m', classes:['1A','2A','3A'], price:2850 },
      { train_name:'Shatabdi Express', train_number:'12001', origin:'Bhopal', destination:'New Delhi', departure_time:'06:00', arrival_time:'13:25', duration:'7h 25m', classes:['CC','EC'], price:1100 },
      { train_name:'Duronto Express', train_number:'12213', origin:'Mumbai Central', destination:'Delhi', departure_time:'23:00', arrival_time:'15:55', duration:'16h 55m', classes:['1A','2A','3A'], price:2450 },
      { train_name:'Kerala Express', train_number:'12625', origin:'New Delhi', destination:'Thiruvananthapuram', departure_time:'11:45', arrival_time:'20:30', duration:'32h 45m', classes:['2A','3A','SL'], price:1850 },
      { train_name:'Gatimaan Express', train_number:'12050', origin:'Hazrat Nizamuddin', destination:'Agra Cantt', departure_time:'08:10', arrival_time:'09:50', duration:'1h 40m', classes:['CC','EC'], price:755 },
    ],
    buses: [
      { operator:'Orange Travels', bus_type:'Volvo 9600 Multi-Axle AC Sleeper', origin:'Bangalore', destination:'Chennai', departure_time:'22:00', arrival_time:'06:00', price:899, rating:4.8 },
      { operator:'RedBus Premium', bus_type:'Mercedes-Benz Multi-Axle AC Seater', origin:'Mumbai', destination:'Goa', departure_time:'21:30', arrival_time:'07:30', price:1299, rating:4.7 },
      { operator:'SRS Travels', bus_type:'Volvo AC Sleeper Double Decker', origin:'Hyderabad', destination:'Bangalore', departure_time:'20:00', arrival_time:'06:00', price:999, rating:4.6 },
      { operator:'KSRTC Airavat Club Class', bus_type:'AC Seater Semi-Sleeper', origin:'Bangalore', destination:'Mysore', departure_time:'07:00', arrival_time:'09:30', price:350, rating:4.9 },
      { operator:'VRL Travels', bus_type:'Volvo B11R AC Sleeper', origin:'Bangalore', destination:'Goa', departure_time:'19:30', arrival_time:'07:00', price:1450, rating:4.8 },
    ],
    cabs: [
      { car_model:'Toyota Fortuner 4WD', service_type:'Outstation & Mountain', category:'7+1 Premium SUV', base_fare:2500, per_km:18 },
      { car_model:'Honda City ZX', service_type:'Airport & Outstation', category:'4+1 Premium Sedan', base_fare:1200, per_km:13 },
      { car_model:'Innova Crysta 2.8 ZX', service_type:'Outstation & Family Tours', category:'6+1 MUV', base_fare:1800, per_km:16 },
      { car_model:'Mercedes-Benz E-Class', service_type:'Business & Airport VIP', category:'4+1 Luxury Sedan', base_fare:4500, per_km:35 },
      { car_model:'Tata Nexon EV', service_type:'City & Airport Transfer', category:'4+1 EV SUV', base_fare:900, per_km:10 },
      { car_model:'Toyota Etios', service_type:'Economy Outstation', category:'4+1 Economy Sedan', base_fare:800, per_km:11 },
    ],
    tours: [
      { title:'Taj Mahal Sunrise Tour by Tuk-Tuk', location:'Agra, Uttar Pradesh', duration:'4 Hours', price:1499, rating:5.0 },
      { title:'Kerala Houseboat Day Cruise', location:'Alleppey, Kerala', duration:'8 Hours', price:3200, rating:4.9 },
      { title:'Jaipur Elephant Safari at Amer Fort', location:'Jaipur, Rajasthan', duration:'3 Hours', price:2500, rating:4.7 },
      { title:'Goa Scuba Diving PADI Open Water', location:'Grand Island, Goa', duration:'Full Day', price:4500, rating:4.8 },
      { title:'Dal Lake Shikara Ride at Sunset', location:'Srinagar, Kashmir', duration:'2 Hours', price:1800, rating:4.9 },
      { title:'Rishikesh White Water Rafting 16 KM', location:'Rishikesh, Uttarakhand', duration:'3 Hours', price:1200, rating:4.8 },
      { title:'Varanasi Ganga Aarti Boat Experience', location:'Varanasi, Uttar Pradesh', duration:'3 Hours', price:999, rating:5.0 },
    ],
    visa: [
      { country:'United Arab Emirates', visa_type:'Tourist Visa 30 Days', processing_time:'24 Hours', price:4500, success_rate:'99.8%' },
      { country:'Thailand', visa_type:'Tourist Visa On Arrival', processing_time:'48 Hours', price:3200, success_rate:'99.5%' },
      { country:'United Kingdom', visa_type:'Standard Visitor Visa 6 Months', processing_time:'15 Working Days', price:15000, success_rate:'97%' },
      { country:'Schengen Europe', visa_type:'Short Stay Visa 90 Days', processing_time:'10 Working Days', price:9500, success_rate:'96%' },
      { country:'Canada', visa_type:'Visitor Visa Single Entry', processing_time:'21 Working Days', price:12000, success_rate:'94%' },
      { country:'Sri Lanka', visa_type:'eVisa 30 Days', processing_time:'12 Hours', price:1500, success_rate:'99.9%' },
      { country:'Singapore', visa_type:'Tourist Visa 30 Days', processing_time:'5 Working Days', price:5000, success_rate:'98.5%' },
    ],
    cruise: [
      { cruise_name:'Cordelia Empress', sailing_route:'Mumbai - Goa - Lakshadweep - Kochi', duration:'5 Nights', price:32000, rating:4.9, ship_class:'5-Star Luxury Cruise Liner' },
      { cruise_name:'Costa Smeralda', sailing_route:'Dubai - Abu Dhabi - Muscat - Mumbai', duration:'7 Nights', price:68000, rating:4.8, ship_class:'6-Star Ultra-Luxury Liner' },
      { cruise_name:'Antara Ganga Voyage', sailing_route:'Varanasi - Patna - Kolkata', duration:'14 Nights', price:145000, rating:4.9, ship_class:'River Heritage Luxury Liner' },
      { cruise_name:'Royal Caribbean Spectrum', sailing_route:'Singapore - Penang - Bangkok', duration:'8 Nights', price:95000, rating:5.0, ship_class:'Mega Luxury Ocean Liner' },
      { cruise_name:'Jalesh Karnika', sailing_route:'Goa - Mumbai - Oman', duration:'4 Nights', price:28000, rating:4.7, ship_class:'4-Star Cruise Ship' },
    ],
    forex: [
      { title:'WonderLight Multi-Currency Travel Card', type:'Prepaid Zero Markup Forex Card', delivery_time:'24 Hours Doorstep', card_fee:0, currencies_supported:['USD','EUR','GBP','JPY','AED','SGD','AUD','CAD'] },
      { title:'Euro Currency Cash Pack', type:'Mint Notes Home Delivery', delivery_time:'Same Day in Metro Cities', card_fee:150, currencies_supported:['EUR'] },
      { title:'US Dollar Currency Bundle', type:'Mint Banknotes Multiple Denominations', delivery_time:'2 Hours Express Delivery', card_fee:100, currencies_supported:['USD'] },
      { title:'Thomas Cook Forex Card', type:'Multi-Currency Prepaid Card', delivery_time:'48 Hours', card_fee:250, currencies_supported:['USD','EUR','GBP','AED','AUD','CAD'] },
      { title:'UAE Dirham Cash Delivery', type:'Crisp Banknotes Home Delivery', delivery_time:'Same Day', card_fee:100, currencies_supported:['AED'] },
    ],
    insurance: [
      { plan_name:'WonderLight Global Elite', provider:'WonderLight Assurance Partners', medical_cover:'$500,000 USD', price_per_day:149, benefits:['Emergency Evacuation','Trip Cancellation','Lost Baggage','Passport Loss','COVID Cover'] },
      { plan_name:'Asia Pacific Shield', provider:'HDFC ERGO Travel Guard', medical_cover:'$250,000 USD', price_per_day:99, benefits:['Medical Emergency','Flight Delay','Baggage Loss','Personal Accident','Adventure Cover'] },
      { plan_name:'Schengen Compliant Plan', provider:'Bajaj Allianz Travel Care', medical_cover:'100,000 EUR', price_per_day:129, benefits:['Medical Cover','Repatriation','Trip Interruption','Baggage Delay','Dental Emergency'] },
      { plan_name:'Domestic Road Warrior', provider:'ICICI Lombard Tour Secure', medical_cover:'10,00,000 INR', price_per_day:39, benefits:['Personal Accident','Hospital Cover','Trip Delay','Baggage Loss','Home Burglary'] },
      { plan_name:'Adventure Sports Cover', provider:'New India Assurance', medical_cover:'$300,000 USD', price_per_day:175, benefits:['Adventure Sports','Heli Rescue','Extreme Sports','Mountain Rescue','Medical Evacuation'] },
    ],
    bookings: [
      { booking_id:'WL-2024-001', service_category:'flights',   service_type:'Flight',    title:'Mumbai to Delhi IndiGo 6E-2543',       traveller_name:'Rahul Sharma',  email:'rahul.sharma@gmail.com', phone:'9876543210', travel_date:'2024-12-15', total_amount:4599,  status:'Confirmed',   booking_date:'2024-11-20' },
      { booking_id:'WL-2024-002', service_category:'hotels',    service_type:'Hotel',     title:'The Taj Mahal Palace 3 Nights',         traveller_name:'Priya Patel',   email:'priya.patel@gmail.com',  phone:'9876543211', travel_date:'2024-12-20', total_amount:84000, status:'Confirmed',   booking_date:'2024-11-22' },
      { booking_id:'WL-2024-003', service_category:'holidays',  service_type:'Package',   title:"Kerala God's Own Country 7 Days",       traveller_name:'Amit Singh',    email:'amit.singh@gmail.com',   phone:'9876543212', travel_date:'2025-01-10', total_amount:65998, status:'In Progress', booking_date:'2024-11-25' },
      { booking_id:'WL-2024-004', service_category:'visa',      service_type:'Visa',      title:'UAE Tourist Visa 30 Days',              traveller_name:'Sneha Menon',   email:'sneha.menon@gmail.com',  phone:'9876543213', travel_date:'2024-12-01', total_amount:4500,  status:'Completed',   booking_date:'2024-11-15' },
      { booking_id:'WL-2024-005', service_category:'cruise',    service_type:'Cruise',    title:'Cordelia Empress 5 Nights',             traveller_name:'Vikram Nair',   email:'vikram.nair@gmail.com',  phone:'9876543214', travel_date:'2025-02-14', total_amount:64000, status:'Pending',     booking_date:'2024-11-28' },
      { booking_id:'WL-2024-006', service_category:'cabs',      service_type:'Cab',       title:'Toyota Fortuner Manali Outstation',     traveller_name:'Deepa Rao',     email:'deepa.rao@gmail.com',    phone:'9876543215', travel_date:'2024-12-26', total_amount:8500,  status:'Confirmed',   booking_date:'2024-11-30' },
      { booking_id:'WL-2024-007', service_category:'trains',    service_type:'Train',     title:'Vande Bharat Express Delhi to Varanasi', traveller_name:'Ravi Kumar',    email:'ravi.kumar@gmail.com',   phone:'9876543216', travel_date:'2024-12-18', total_amount:1545,  status:'Confirmed',   booking_date:'2024-11-28' },
      { booking_id:'WL-2024-008', service_category:'tours',     service_type:'Tour',      title:'Taj Mahal Sunrise Tour by Tuk-Tuk',     traveller_name:'Anjali Verma',  email:'anjali.verma@gmail.com', phone:'9876543217', travel_date:'2024-12-22', total_amount:1499,  status:'Confirmed',   booking_date:'2024-12-01' },
      { booking_id:'WL-2024-009', service_category:'insurance', service_type:'Insurance', title:'WonderLight Global Elite 14 Days',      traveller_name:'Suresh Reddy',  email:'suresh.reddy@gmail.com', phone:'9876543218', travel_date:'2025-01-05', total_amount:2086,  status:'Completed',   booking_date:'2024-11-18' },
      { booking_id:'WL-2024-010', service_category:'forex',     service_type:'Forex',     title:'Multi-Currency Travel Card Issuance',   traveller_name:'Kavitha Iyer',  email:'kavitha.iyer@gmail.com', phone:'9876543219', travel_date:'2024-12-10', total_amount:0,     status:'Completed',   booking_date:'2024-11-28' },
      { booking_id:'WL-2024-011', service_category:'hotels',    service_type:'Hotel',     title:'Wildflower Hall Shimla 4 Nights',       traveller_name:'Mohan Das',     email:'mohan.das@gmail.com',    phone:'9876543220', travel_date:'2025-01-15', total_amount:78000, status:'Pending',     booking_date:'2024-12-02' },
      { booking_id:'WL-2024-012', service_category:'holidays',  service_type:'Package',   title:'Leh Ladakh Expedition 9 Days',          traveller_name:'Nisha Kapoor',  email:'nisha.kapoor@gmail.com', phone:'9876543221', travel_date:'2025-05-01', total_amount:104000,status:'Confirmed',   booking_date:'2024-12-01' },
    ],
  };

  for (const [table, rows] of Object.entries(SEED)) {
    process.stdout.write(`   Seeding ${table} (${rows.length} rows)... `);
    try {
      const r = await insert(table, rows);
      if (r.status >= 400) {
        console.log('SKIPPED/ERROR ' + r.status + ' - ' + JSON.stringify(r.data).slice(0,100));
      } else {
        const inserted = Array.isArray(r.data) ? r.data.length : '?';
        console.log('OK (' + inserted + ' records)');
      }
    } catch(err) {
      console.log('FAILED: ' + err.message);
    }
  }

  console.log('\n✅ Seeding complete! Refreshing admin panel at http://localhost:8080\n');
}

run().catch(console.error);
