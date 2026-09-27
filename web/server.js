const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 8080;
const DATA_FILE = path.join(__dirname, 'data', 'store.json');
const PUBLIC_DIR = path.join(__dirname, 'public');
const UPLOADS_DIR = path.join(__dirname, 'public', 'uploads');
if (!fs.existsSync(UPLOADS_DIR)) {
  fs.mkdirSync(UPLOADS_DIR, { recursive: true });
}

const SUPABASE_URL = process.env.SUPABASE_URL || 'https://elcahnyfaogvittfrxaa.supabase.co';
const SUPABASE_KEY = process.env.SUPABASE_SECRET_KEY || 'sb_secret_AFiaNX8om4SMXprlDVQpUA_OIehjX1v';

// Snake case converter for Supabase Postgres tables
const FIELD_TO_SNAKE = {
  flightNumber: 'flight_number',
  departureTime: 'departure_time',
  arrivalTime: 'arrival_time',
  pricePerNight: 'price_per_night',
  trainName: 'train_name',
  trainNumber: 'train_number',
  busType: 'bus_type',
  carModel: 'car_model',
  serviceType: 'service_type',
  baseFare: 'base_fare',
  perKm: 'per_km',
  visaType: 'visa_type',
  processingTime: 'processing_time',
  successRate: 'success_rate',
  cruiseName: 'cruise_name',
  sailingRoute: 'sailing_route',
  shipClass: 'ship_class',
  cardFee: 'card_fee',
  deliveryTime: 'delivery_time',
  planName: 'plan_name',
  medicalCover: 'medical_cover',
  pricePerDay: 'price_per_day',
  imageUrl: 'image_url',
  bookingId: 'booking_id',
  travellerName: 'traveller_name',
  totalAmount: 'total_amount',
  bookingDate: 'booking_date'
};

function toSnakeCase(obj) {
  if (!obj || typeof obj !== 'object' || Array.isArray(obj)) return obj;
  const out = {};
  for (const [k, v] of Object.entries(obj)) {
    const key = FIELD_TO_SNAKE[k] || k.replace(/([A-Z])/g, '_$1').toLowerCase();
    out[key] = v;
  }
  return out;
}

// Supabase REST helper
function supabaseApi(method, pathStr, body) {
  return new Promise((resolve) => {
    try {
      const url = new URL(SUPABASE_URL + pathStr);
      const postData = body ? JSON.stringify(body) : null;
      const options = {
        hostname: url.hostname,
        path: url.pathname + url.search,
        method: method,
        headers: {
          'Content-Type': 'application/json',
          'apikey': SUPABASE_KEY,
          'Authorization': 'Bearer ' + SUPABASE_KEY,
          'Prefer': 'return=representation'
        }
      };
      if (postData) {
        options.headers['Content-Length'] = Buffer.byteLength(postData);
      }
      const req = https.request(options, (res) => {
        let raw = '';
        res.on('data', chunk => raw += chunk);
        res.on('end', () => {
          try {
            resolve({ status: res.statusCode, data: JSON.parse(raw) });
          } catch (e) {
            resolve({ status: res.statusCode, data: raw });
          }
        });
      });
      req.on('error', (err) => {
        console.warn('Supabase API sync error:', err.message);
        resolve({ status: 500, error: err.message });
      });
      if (postData) req.write(postData);
      req.end();
    } catch (e) {
      console.warn('Supabase request init error:', e.message);
      resolve({ status: 500, error: e.message });
    }
  });
}

function getDefaultData() {
  return { settings: { signupBonus: 2500, currency: 'INR' }, flights: [], hotels: [], villas: [], holidays: [], trains: [], buses: [], cabs: [], tours: [], visa: [], cruise: [], forex: [], insurance: [], bookings: [] };
}

// Helper to read database
function readData() {
  try {
    if (!fs.existsSync(DATA_FILE)) {
      const def = getDefaultData();
      writeData(def);
      return def;
    }
    const raw = fs.readFileSync(DATA_FILE, 'utf8');
    if (!raw.trim()) {
      const def = getDefaultData();
      writeData(def);
      return def;
    }
    return JSON.parse(raw);
  } catch (err) {
    console.error('Error reading store.json:', err);
    return getDefaultData();
  }
}

// Helper to write database
function writeData(data) {
  try {
    const tempFile = `${DATA_FILE}.tmp`;
    fs.writeFileSync(tempFile, JSON.stringify(data, null, 2), 'utf8');
    fs.renameSync(tempFile, DATA_FILE);
    return true;
  } catch (err) {
    console.error('Error writing store.json:', err);
    try {
      fs.writeFileSync(DATA_FILE, JSON.stringify(data, null, 2), 'utf8');
      return true;
    } catch (e) {
      return false;
    }
  }
}

// Helper to parse JSON body
function getJsonBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        resolve(body ? JSON.parse(body) : {});
      } catch (e) {
        reject(e);
      }
    });
    req.on('error', reject);
  });
}

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
};

// Helper to merge local bookings with Supabase bookings
async function getMergedBookings() {
  const data = readData();
  if (!data.bookings) data.bookings = [];

  try {
    const sbRes = await supabaseApi('GET', '/rest/v1/bookings?select=*&order=created_at.desc');
    if (sbRes && sbRes.status === 200 && Array.isArray(sbRes.data)) {
      const existingMap = new Map();
      data.bookings.forEach((b, idx) => {
        const idKey = String(b.bookingId || b.id || b.booking_id);
        existingMap.set(idKey, idx);
      });

      sbRes.data.forEach(sb => {
        const bId = sb.booking_id || sb.bookingId || sb.id;
        const key = String(bId);
        if (existingMap.has(key)) {
          const idx = existingMap.get(key);
          if (sb.status) data.bookings[idx].status = sb.status;
          if (sb.traveller_name && !data.bookings[idx].travellerName) data.bookings[idx].travellerName = sb.traveller_name;
          if (sb.title && !data.bookings[idx].title) data.bookings[idx].title = sb.title;
        } else {
          data.bookings.push({
            id: sb.id,
            bookingId: bId,
            userId: sb.user_id || '',
            serviceCategory: sb.service_category || 'holidays',
            serviceType: sb.service_type || 'Travel Package',
            title: sb.title || 'WonderLight Adventure',
            details: sb.details || sb.duration || 'Confirmed Booking',
            travellerName: sb.traveller_name || 'Guest User',
            email: sb.email || '',
            phone: sb.phone || '',
            totalAmount: Number(sb.total_amount) || 0,
            bookingDate: sb.booking_date || (sb.created_at ? sb.created_at.split('T')[0] : 'Recent'),
            status: sb.status || 'Confirmed',
            createdAt: sb.created_at
          });
          existingMap.set(key, data.bookings.length - 1);
        }
      });
      writeData(data);
    }
  } catch (err) {
    console.warn('Supabase booking merge warning:', err.message);
  }

  return data.bookings;
}

const server = http.createServer(async (req, res) => {
  // CORS Headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  const pathname = url.pathname;

  // API Routes
  if (pathname.startsWith('/api/')) {
    res.setHeader('Content-Type', 'application/json');

    // Image Upload API (Supports Base64 data URLs)
    if (pathname === '/api/upload' && req.method === 'POST') {
      try {
        const body = await getJsonBody(req);
        if (!body.image) {
          res.writeHead(400);
          return res.end(JSON.stringify({ error: 'Missing image data' }));
        }

        let base64Data = body.image;
        let ext = 'jpg';
        if (base64Data.startsWith('data:image/')) {
          const match = base64Data.match(/^data:image\/([a-zA-Z0-9+]+);base64,/);
          if (match) {
            ext = match[1] === 'jpeg' ? 'jpg' : match[1];
            base64Data = base64Data.replace(/^data:image\/[a-zA-Z0-9+]+;base64,/, '');
          }
        }

        const filename = `img_${Date.now()}_${Math.random().toString(36).substring(2, 6)}.${ext}`;
        const filePath = path.join(UPLOADS_DIR, filename);
        fs.writeFileSync(filePath, Buffer.from(base64Data, 'base64'));

        const imageUrl = `/uploads/${filename}`;
        res.writeHead(201);
        return res.end(JSON.stringify({ success: true, url: imageUrl, filename }));
      } catch (err) {
        res.writeHead(500);
        return res.end(JSON.stringify({ error: err.message }));
      }
    }

    if ((pathname === '/api/status' || pathname === '/api/stats') && req.method === 'GET') {
      await getMergedBookings();
      const data = readData();
      let totalServices = 0;
      Object.keys(data).forEach(k => {
        if (Array.isArray(data[k]) && k !== 'bookings') {
          totalServices += data[k].length;
        }
      });
      res.writeHead(200);
      return res.end(JSON.stringify({
        status: 'online',
        app: 'WonderLight Adventure Cloud Admin & Sync Engine',
        version: '2.0.0',
        port: PORT,
        totalServices,
        totalBookings: data.bookings ? data.bookings.length : 0,
        timestamp: new Date().toISOString()
      }));
    }

    // GET /api/services -> full store
    if (pathname === '/api/services' && req.method === 'GET') {
      await getMergedBookings();
      const data = readData();
      res.writeHead(200);
      return res.end(JSON.stringify(data));
    }

    // Real Registered Users from Supabase Auth
    if (pathname === '/api/users' && req.method === 'GET') {
      try {
        const authRes = await supabaseApi('GET', '/auth/v1/admin/users');
        if (authRes && authRes.data && Array.isArray(authRes.data.users)) {
          const users = authRes.data.users.map(u => ({
            id: u.id,
            email: u.email || '',
            full_name: (u.user_metadata && u.user_metadata.full_name) || u.email?.split('@')[0] || 'Registered User',
            phone: (u.user_metadata && u.user_metadata.phone) || u.phone || '',
            created_at: u.created_at,
            last_sign_in: u.last_sign_in_at || u.updated_at
          }));
          res.writeHead(200);
          return res.end(JSON.stringify(users));
        }
      } catch (err) {
        console.warn('Error fetching Supabase auth users:', err.message);
      }
      res.writeHead(200);
      return res.end(JSON.stringify([]));
    }

    // Bookings API
    if (pathname === '/api/bookings') {
      if (req.method === 'GET') {
        const bookings = await getMergedBookings();
        res.writeHead(200);
        return res.end(JSON.stringify(bookings));
      }

      if (req.method === 'POST') {
        try {
          const data = readData();
          if (!data.bookings) data.bookings = [];
          const newBooking = await getJsonBody(req);
          if (!newBooking.id) newBooking.id = Date.now();
          if (!newBooking.bookingId) newBooking.bookingId = `WL-${Math.random().toString(36).substring(2, 8).toUpperCase()}`;
          if (!newBooking.bookingDate) newBooking.bookingDate = new Date().toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' });
          if (!newBooking.status) newBooking.status = 'Confirmed';

          data.bookings.unshift(newBooking);
          writeData(data);

          // Asynchronously mirror booking to Supabase
          const snakeBooking = toSnakeCase(newBooking);
          delete snakeBooking.id;
          supabaseApi('POST', '/rest/v1/bookings', snakeBooking).catch(() => {});

          res.writeHead(201);
          return res.end(JSON.stringify({ success: true, booking: newBooking }));
        } catch (e) {
          res.writeHead(400);
          return res.end(JSON.stringify({ error: 'Invalid JSON body' }));
        }
      }
    }

    // /api/bookings/:id
    const bookingMatch = pathname.match(/^\/api\/bookings\/([^\/]+)$/);
    if (bookingMatch) {
      const id = bookingMatch[1];
      const data = readData();
      if (!data.bookings) data.bookings = [];
      const index = data.bookings.findIndex(b => String(b.id) === id || String(b.bookingId) === id);

      if (index === -1) {
        res.writeHead(404);
        return res.end(JSON.stringify({ error: 'Booking not found' }));
      }

      if (req.method === 'PUT') {
        const body = await getJsonBody(req);
        data.bookings[index] = { ...data.bookings[index], ...body };
        writeData(data);

        // Update in Supabase
        const snakeBody = toSnakeCase(body);
        supabaseApi('PATCH', `/rest/v1/bookings?booking_id=eq.${encodeURIComponent(id)}`, snakeBody).catch(() => {});

        res.writeHead(200);
        return res.end(JSON.stringify({ success: true, booking: data.bookings[index] }));
      }

      if (req.method === 'DELETE') {
        const deleted = data.bookings.splice(index, 1);
        writeData(data);

        // Delete from Supabase
        supabaseApi('DELETE', `/rest/v1/bookings?booking_id=eq.${encodeURIComponent(id)}`).catch(() => {});

        res.writeHead(200);
        return res.end(JSON.stringify({ success: true, deleted: deleted[0] }));
      }
    }

    // Notifications API
    if (pathname === '/api/notifications') {
      if (req.method === 'GET') {
        try {
          const notifRes = await supabaseApi('GET', '/rest/v1/notifications?order=created_at.desc');
          if (notifRes && Array.isArray(notifRes.data)) {
            res.writeHead(200);
            return res.end(JSON.stringify(notifRes.data));
          }
        } catch (err) {
          console.warn('Error fetching notifications:', err.message);
        }
        res.writeHead(200);
        return res.end(JSON.stringify([]));
      }

      if (req.method === 'POST') {
        try {
          const payload = await getJsonBody(req);
          const notifRes = await supabaseApi('POST', '/rest/v1/notifications', payload);
          res.writeHead(201);
          return res.end(JSON.stringify({ success: true, notification: notifRes.data?.[0] || payload }));
        } catch (err) {
          res.writeHead(500);
          return res.end(JSON.stringify({ error: err.message }));
        }
      }
    }

    // App Settings & Signup Wallet Bonus API
    if (pathname === '/api/settings' || pathname === '/api/wallet-settings') {
      const data = readData();
      if (!data.settings) {
        data.settings = { signupBonus: 2500, currency: 'INR' };
      }

      if (req.method === 'GET') {
        // Try reading latest signup_bonus from Supabase app_settings
        try {
          const supaRes = await supabaseApi('GET', '/rest/v1/app_settings?key=eq.signup_bonus');
          if (supaRes.status === 200 && Array.isArray(supaRes.data) && supaRes.data.length > 0) {
            const remoteVal = parseInt(supaRes.data[0].value, 10);
            if (!isNaN(remoteVal) && remoteVal > 0) {
              data.settings.signupBonus = remoteVal;
            }
          }
        } catch (_) {}

        res.writeHead(200);
        return res.end(JSON.stringify(data.settings));
      }

      if (req.method === 'POST') {
        try {
          const body = await getJsonBody(req);
          if (body.signupBonus !== undefined) {
            const amt = parseInt(body.signupBonus, 10);
            if (!isNaN(amt) && amt >= 0) {
              data.settings.signupBonus = amt;
            }
          }
          if (body.currency) data.settings.currency = body.currency;
          data.settings.updatedAt = new Date().toISOString();
          writeData(data);

          // Asynchronously upsert to Supabase app_settings table
          try {
            supabaseApi('POST', '/rest/v1/app_settings', {
              key: 'signup_bonus',
              value: String(data.settings.signupBonus),
              updated_at: data.settings.updatedAt
            }).catch(() => {});
          } catch (_) {}

          res.writeHead(200);
          return res.end(JSON.stringify({
            success: true,
            message: `Signup bonus updated to ₹${data.settings.signupBonus}`,
            settings: data.settings
          }));
        } catch (e) {
          res.writeHead(400);
          return res.end(JSON.stringify({ error: 'Failed to update settings' }));
        }
      }
    }

    // /api/services/:category
    const categoryMatch = pathname.match(/^\/api\/services\/([a-zA-Z0-9_-]+)$/);
    if (categoryMatch) {
      const category = categoryMatch[1];
      const data = readData();
      if (!data[category]) data[category] = [];

      if (req.method === 'GET') {
        res.writeHead(200);
        return res.end(JSON.stringify(data[category]));
      }

      if (req.method === 'POST') {
        try {
          const item = await getJsonBody(req);
          if (!item.id) {
            item.id = `${category.slice(0, 3)}_${Date.now()}`;
          }
          data[category].unshift(item);
          writeData(data);

          // Mirror addition to Supabase
          const snakeItem = toSnakeCase(item);
          if (String(snakeItem.id).includes('_')) delete snakeItem.id; // allow Supabase UUID auto-generation if not valid UUID
          supabaseApi('POST', `/rest/v1/${category}`, snakeItem).then(res => {
            if (res.status in [200, 201] && Array.isArray(res.data) && res.data[0]) {
              // Update local item with Supabase generated ID
              item.supabaseId = res.data[0].id;
              writeData(data);
            }
          }).catch(() => {});

          res.writeHead(201);
          return res.end(JSON.stringify({ success: true, item }));
        } catch (e) {
          res.writeHead(400);
          return res.end(JSON.stringify({ error: 'Invalid request' }));
        }
      }
    }

    // /api/services/:category/:id
    const itemMatch = pathname.match(/^\/api\/services\/([a-zA-Z0-9_-]+)\/([a-zA-Z0-9_-]+)$/);
    if (itemMatch) {
      const category = itemMatch[1];
      const itemId = itemMatch[2];
      const data = readData();
      if (!data[category]) data[category] = [];

      const itemIdx = data[category].findIndex(it => String(it.id) === itemId || String(it.supabaseId) === itemId);

      if (itemIdx === -1) {
        res.writeHead(404);
        return res.end(JSON.stringify({ error: `Item not found in ${category}` }));
      }

      if (req.method === 'PUT') {
        try {
          const updates = await getJsonBody(req);
          data[category][itemIdx] = { ...data[category][itemIdx], ...updates, id: itemId };
          writeData(data);

          // Mirror update to Supabase
          const snakeUpdates = toSnakeCase(updates);
          delete snakeUpdates.id;
          const filterCol = itemId.length > 20 && !itemId.includes('_') ? `id=eq.${itemId}` : `id=eq.${itemId}`;
          supabaseApi('PATCH', `/rest/v1/${category}?${filterCol}`, snakeUpdates).catch(() => {});

          res.writeHead(200);
          return res.end(JSON.stringify({ success: true, item: data[category][itemIdx] }));
        } catch (e) {
          res.writeHead(400);
          return res.end(JSON.stringify({ error: 'Failed to update item' }));
        }
      }

      if (req.method === 'DELETE') {
        const removed = data[category].splice(itemIdx, 1);
        writeData(data);

        // Mirror delete to Supabase
        const targetId = removed[0].supabaseId || itemId;
        supabaseApi('DELETE', `/rest/v1/${category}?id=eq.${encodeURIComponent(targetId)}`).catch(() => {});

        res.writeHead(200);
        return res.end(JSON.stringify({ success: true, removed: removed[0] }));
      }
    }

    res.writeHead(404);
    return res.end(JSON.stringify({ error: 'Endpoint not found' }));
  }

  // Static File Serving
  let filePath = path.join(PUBLIC_DIR, pathname === '/' ? 'index.html' : pathname);
  const ext = path.extname(filePath);

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      filePath = path.join(PUBLIC_DIR, 'index.html');
    }

    const fileExt = path.extname(filePath);
    const contentType = MIME_TYPES[fileExt] || 'text/plain';

    fs.readFile(filePath, (readErr, content) => {
      if (readErr) {
        res.writeHead(500);
        res.end('500 Server Error');
      } else {
        res.writeHead(200, { 'Content-Type': contentType });
        res.end(content);
      }
    });
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`=======================================================`);
  console.log(`✨ WonderLight Adventure Admin Panel Server Online! ✨`);
  console.log(`🚀 Web URL: http://localhost:${PORT}`);
  console.log(`📡 API URL: http://localhost:${PORT}/api/services`);
  console.log(`=======================================================`);
});
