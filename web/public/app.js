/* WonderLight Cloud Admin Panel - Complete Supabase Integration */
'use strict';

// ----------------------------------------------------------------
// CONFIG
// ----------------------------------------------------------------
var SUPABASE_URL = 'https://elcahnyfaogvittfrxaa.supabase.co';
var SUPABASE_KEY = 'sb_publishable_Yj7EQj1ZKy3ro5cEGt0r7A_EhUttYan';

// Initialize Supabase (CDN sets window.supabase, we use db as our handle)
var db = window.supabase.createClient(SUPABASE_URL, SUPABASE_KEY);

// Snake-case mapping for Supabase Postgres schema
var FIELD_TO_SNAKE = {
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
  var out = {};
  for (var k in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, k)) {
      var snake = FIELD_TO_SNAKE[k] || k.replace(/([A-Z])/g, '_$1').toLowerCase();
      out[snake] = obj[k];
    }
  }
  return out;
}

// ----------------------------------------------------------------
// STATE
// ----------------------------------------------------------------
var appData = {
  bookings:[], flights:[], hotels:[], villas:[], holidays:[],
  trains:[], buses:[], cabs:[], tours:[], visa:[], cruise:[], forex:[], insurance:[]
};
var usersData = [];
var notificationsLog = [];
var walletSettings = { signupBonus: 2500 };
var currentCategory = 'bookings';
var currentSearch = '';
var editingItemId = null;
var deletingItemId = null;
var currentAdminUser = null;
var lastSyncTimestamp = Date.now();
var autoReloadSeconds = 8;
var remainingCountdown = 8;
var isAutoReloadPaused = false;
var countdownTimer = null;
var isReloading = false;

// ----------------------------------------------------------------
// CATEGORY CONFIG
// ----------------------------------------------------------------
var CATEGORY_CONFIG = {
  bookings:      { title:'Customer Bookings',         desc:'Live customer bookings from mobile app. Update status or cancel.', showAdd:false, columns:['Booking ID','Service','Details','Customer','Amount','Date','Status','Actions'] },
  users:         { title:'Registered Users',          desc:'All verified users registered through Supabase in the WonderLight mobile app.', showAdd:false, columns:[] },
  notifications: { title:'Send Notifications',        desc:'Compose and push real-time notifications to users.', showAdd:false, columns:[] },
  wallet:        { title:'Wallet & Signup Bonus',     desc:'Configure how much WonderCash wallet bonus is credited to new users upon registration.', showAdd:false, columns:[] },
  flights:       { title:'Flights',                   desc:'Manage flight routes, airlines, and fares.', showAdd:true, columns:['Flight / Airline','Route','Departure / Arrival','Duration','Price','Stops','Actions'] },
  hotels:        { title:'Hotels & Resorts',          desc:'Curated 5-star properties and boutique hotels.', showAdd:true, columns:['Hotel Name','Location','Rating','Price / Night','Amenities','Actions'] },
  villas:        { title:'Villas & Homestays',        desc:'Luxury private villas and waterfront chalets.', showAdd:true, columns:['Villa Name','Location','Capacity','Price / Night','Amenities','Actions'] },
  holidays:      { title:'Holiday Packages',          desc:'Multi-day all-inclusive tours with transfers.', showAdd:true, columns:['Package Title','Destinations','Duration','Price / Person','Badge','Inclusions','Actions'] },
  trains:        { title:'Trains',                    desc:'Train routes, numbers, seat classes, and fares.', showAdd:true, columns:['Train Name & No.','Origin to Destination','Timings','Classes','Price','Actions'] },
  buses:         { title:'Buses',                     desc:'Volvo Multi-Axle, AC Sleepers, intercity routes.', showAdd:true, columns:['Operator & Bus Type','Route','Departure / Arrival','Price','Rating','Actions'] },
  cabs:          { title:'Cabs & Transfers',          desc:'Sedans, SUVs, and EV fleet for transfers.', showAdd:true, columns:['Vehicle Model','Service Type','Category','Base Fare','Per KM Rate','Actions'] },
  tours:         { title:'Tours & Activities',        desc:'Guided experiences and monument passes.', showAdd:true, columns:['Experience Title','Location','Duration','Price / Person','Rating','Actions'] },
  visa:          { title:'Visa Assistance',           desc:'Tourist eVisas and documentation support.', showAdd:true, columns:['Country','Visa Type','Processing Speed','Service Fee','Success Rate','Actions'] },
  cruise:        { title:'Luxury Cruises',            desc:'Cordelia Cruises and river liners.', showAdd:true, columns:['Cruise Liner','Sailing Route','Duration','Price / Cabin','Rating','Actions'] },
  forex:         { title:'Forex Card & Cash',         desc:'Zero-markup travel smart cards.', showAdd:true, columns:['Product Name','Type','Delivery Speed','Issuance Fee','Currencies','Actions'] },
  insurance:     { title:'Travel Insurance',          desc:'Medical cover, flight delay, baggage protection.', showAdd:true, columns:['Plan Name','Underwriter','Medical Cover','Premium / Day','Key Coverage','Actions'] }
};

// ================================================================
// AUTHENTICATION (Only Real Supabase Sign-In)
// ================================================================
async function initAuth() {
  try {
    var result = await db.auth.getSession();
    if (result.data && result.data.session && result.data.session.user) {
      showAdminPanel(result.data.session.user);
      return;
    }
  } catch(e) { /* ignore */ }

  showLoginScreen();

  db.auth.onAuthStateChange(function(event, session) {
    if (event === 'SIGNED_IN' && session) {
      showAdminPanel(session.user);
    } else if (event === 'SIGNED_OUT') {
      showLoginScreen();
    }
  });
}

function showLoginScreen() {
  document.getElementById('login-screen').style.display = 'flex';
  document.getElementById('admin-panel').style.display = 'none';
  currentAdminUser = null;
}

function showAdminPanel(user) {
  currentAdminUser = user;
  document.getElementById('login-screen').style.display = 'none';
  document.getElementById('admin-panel').style.display = 'flex';

  var email = user.email || 'Admin';
  var rawName = (user.user_metadata && user.user_metadata.full_name)
    ? user.user_metadata.full_name
    : email.split('@')[0].replace(/[._]/g, ' ').replace(/\b\w/g, function(c){ return c.toUpperCase(); });
  var initials = rawName.split(' ').map(function(w){ return w[0]; }).join('').toUpperCase().slice(0,2);

  document.getElementById('admin-display-name').textContent = rawName;
  document.getElementById('admin-display-email').textContent = email;
  document.getElementById('admin-avatar-initials').textContent = initials;

  reloadData({ isManual: false });
  startAutoReloadTimer();
  setInterval(updateLastSyncedDisplay, 2000);

  window.addEventListener('keydown', function(e) {
    var tag = e.target && e.target.tagName ? e.target.tagName.toLowerCase() : '';
    if (tag !== 'input' && tag !== 'textarea' && tag !== 'select') {
      if ((e.key === 'r' || e.key === 'R') && !e.ctrlKey && !e.metaKey) {
        e.preventDefault();
        triggerManualReload();
      }
    }
  });
}

async function handleAdminLogin(event) {
  event.preventDefault();
  var email    = document.getElementById('login-email').value.trim();
  var password = document.getElementById('login-password').value;
  var errEl    = document.getElementById('login-error');
  var btnText  = document.getElementById('login-btn-text');
  var spinner  = document.getElementById('login-spinner');

  errEl.style.display = 'none';
  btnText.style.display = 'none';
  spinner.style.display = 'inline-block';

  // Authenticate exclusively with Supabase Auth
  try {
    var result = await db.auth.signInWithPassword({ email: email, password: password });
    if (result.error) {
      throw result.error;
    }
    if (result.data && result.data.session) {
      // Success: onAuthStateChange handles transition
      return;
    }
    throw new Error('Sign-in failed. Please verify your Supabase credentials.');
  } catch(err) {
    errEl.textContent = err.message || 'Invalid Supabase email or password. Please try again.';
    errEl.style.display = 'block';
    btnText.style.display = 'inline';
    spinner.style.display = 'none';
  }
}

async function handleAdminSignOut() {
  try { await db.auth.signOut(); } catch(e) { /* ignore */ }
  showLoginScreen();
  showToast('Signed out successfully.');
}

function togglePasswordVisibility() {
  var input = document.getElementById('login-password');
  var btn   = document.getElementById('toggle-pw-btn');
  if (input.type === 'password') {
    input.type = 'text';
    btn.textContent = String.fromCodePoint(0x1F648);
  } else {
    input.type = 'password';
    btn.textContent = String.fromCodePoint(0x1F441);
  }
}

// ================================================================
// DATA LOADING
// ================================================================
async function reloadData(opts) {
  var isManual = opts && opts.isManual;
  if (isReloading) return;
  isReloading = true;
  var startTime = performance.now();

  var reloadBtn    = document.getElementById('btn-reload-app');
  var reloadBtnTxt = document.getElementById('reload-btn-text');
  var catBtn       = document.getElementById('btn-reload-current-cat');
  var bar          = document.getElementById('reload-progress-bar');
  var tbl          = document.getElementById('table-container');

  if (reloadBtn)    reloadBtn.classList.add('spinning');
  if (reloadBtnTxt && isManual) reloadBtnTxt.textContent = 'Reloading...';
  if (catBtn)       catBtn.classList.add('spinning');
  if (bar)          bar.classList.add('active');
  if (tbl)          tbl.classList.add('table-reloading');

  try {
    var res = null;
    try { res = await fetch('/api/services?_t=' + Date.now(), { cache: 'no-store' }); } catch(e) {}

    if (res && res.ok) {
      appData = await res.json();
    } else {
      await loadFromSupabase();
    }

    // Always fetch latest merged bookings explicitly
    try {
      var bkRes = await fetch('/api/bookings?_t=' + Date.now(), { cache: 'no-store' });
      if (bkRes && bkRes.ok) {
        var bks = await bkRes.json();
        if (Array.isArray(bks) && bks.length > 0) {
          appData.bookings = bks;
        }
      }
    } catch(eBk) {}

    if (!appData.bookings || appData.bookings.length === 0) {
      try {
        var rBk = await db.from('bookings').select('*').order('created_at', { ascending: false });
        if (rBk.data && rBk.data.length > 0) appData.bookings = rBk.data;
      } catch(eSb) {}
    }

    Object.keys(CATEGORY_CONFIG).forEach(function(cat) {
      if (!appData[cat]) appData[cat] = [];
    });

    await loadUsersFromSupabase();
    await loadWalletSettings();
    updateCounters();
    renderContent();
    setServerStatus(true);
    lastSyncTimestamp = Date.now();
    updateLastSyncedDisplay();

    if (isManual) {
      var elapsed = Math.round(performance.now() - startTime);
      var inv = 0;
      Object.keys(CATEGORY_CONFIG).forEach(function(k) {
        if (k !== 'bookings' && k !== 'users' && k !== 'notifications' && k !== 'wallet') inv += (appData[k] || []).length;
      });
      showToast('Synced in ' + elapsed + 'ms - ' + (appData.bookings || []).length + ' Bookings & ' + inv + ' Services');
    }
  } catch(err) {
    setServerStatus(false);
    if (isManual) showToast('Sync failed - check connection');
  } finally {
    isReloading = false;
    remainingCountdown = autoReloadSeconds;
    if (reloadBtn)    reloadBtn.classList.remove('spinning');
    if (reloadBtnTxt) reloadBtnTxt.textContent = 'Reload App';
    if (catBtn)       catBtn.classList.remove('spinning');
    if (bar)          bar.classList.remove('active');
    if (tbl)          tbl.classList.remove('table-reloading');
  }
}

async function loadFromSupabase() {
  try {
    var r = await db.from('bookings').select('*').order('created_at', { ascending: false });
    appData.bookings = r.data || [];
  } catch(e) { appData.bookings = []; }

  var cats = ['flights','hotels','villas','holidays','trains','buses','cabs','tours','visa','cruise','forex','insurance'];
  for (var i = 0; i < cats.length; i++) {
    try {
      var cr = await db.from(cats[i]).select('*').order('created_at', { ascending: false });
      appData[cats[i]] = cr.data || [];
    } catch(e) { appData[cats[i]] = []; }
  }
}

async function loadUsersFromSupabase() {
  try {
    var res = await fetch('/api/users?_t=' + Date.now());
    if (res.ok) {
      var realUsers = await res.json();
      if (Array.isArray(realUsers)) {
        usersData = realUsers;
      }
    } else {
      var r = await db.from('profiles').select('*').order('created_at', { ascending: false });
      if (r.data && Array.isArray(r.data)) {
        usersData = r.data;
      }
    }
  } catch(e) {
    usersData = [];
  }

  var el = document.getElementById('badge-users');
  if (el) el.textContent = usersData.length;
  var kpi = document.getElementById('kpi-users-count');
  if (kpi) kpi.textContent = usersData.length;
}

function triggerManualReload() { reloadData({ isManual: true }); }
function triggerCategoryReload() {
  var btn = document.getElementById('btn-reload-current-cat');
  if (btn) btn.classList.add('spinning');
  reloadData({ isManual: true });
}
function loadData() { return reloadData({ isManual: false }); }
function syncWithApp() { showToast('Synchronizing with Supabase...'); loadData(); }

// ================================================================
// AUTO-RELOAD TIMER
// ================================================================
function startAutoReloadTimer() {
  if (countdownTimer) clearInterval(countdownTimer);
  remainingCountdown = autoReloadSeconds;
  updateAutoReloadDisplay();
  countdownTimer = setInterval(function() {
    if (isAutoReloadPaused || autoReloadSeconds <= 0) return;
    remainingCountdown--;
    updateAutoReloadDisplay();
    if (remainingCountdown <= 0) {
      remainingCountdown = autoReloadSeconds;
      reloadData({ isManual: false });
    }
  }, 1000);
}

function updateAutoReloadDisplay() {
  var el = document.getElementById('auto-reload-display');
  if (!el) return;
  if (autoReloadSeconds <= 0) el.textContent = 'Auto: Off';
  else if (isAutoReloadPaused) el.textContent = 'Auto: Paused';
  else el.textContent = 'Auto-Sync: ' + remainingCountdown + 's';
}

function toggleAutoReload() {
  isAutoReloadPaused = !isAutoReloadPaused;
  var btn  = document.getElementById('btn-toggle-auto');
  var pill = document.getElementById('auto-reload-pill');
  if (btn)  btn.textContent = isAutoReloadPaused ? '\u25B6' : '\u23F8';
  if (pill) pill.classList.toggle('paused', isAutoReloadPaused);
  updateAutoReloadDisplay();
  showToast(isAutoReloadPaused ? 'Auto-sync paused' : 'Auto-sync resumed');
}

function changeAutoReloadInterval(val) {
  autoReloadSeconds = parseInt(val, 10);
  remainingCountdown = autoReloadSeconds;
  isAutoReloadPaused = autoReloadSeconds <= 0;
  var btn  = document.getElementById('btn-toggle-auto');
  var pill = document.getElementById('auto-reload-pill');
  if (btn)  btn.textContent = isAutoReloadPaused ? '\u25B6' : '\u23F8';
  if (pill) pill.classList.toggle('paused', isAutoReloadPaused);
  updateAutoReloadDisplay();
  showToast(autoReloadSeconds > 0 ? 'Auto-sync: ' + autoReloadSeconds + 's' : 'Auto-sync off');
}

function updateLastSyncedDisplay() {
  var el  = document.getElementById('last-synced-indicator');
  if (!el) return;
  var sec = Math.floor((Date.now() - lastSyncTimestamp) / 1000);
  if (sec < 3)       el.textContent = 'Synced: Just now';
  else if (sec < 60) el.textContent = 'Synced: ' + sec + 's ago';
  else               el.textContent = 'Synced: ' + Math.floor(sec/60) + 'm ago';
}

function setServerStatus(online) {
  var badge = document.getElementById('server-status');
  if (!badge) return;
  if (online) {
    badge.innerHTML = '<span class="status-dot"></span><span class="status-label">Supabase Connected</span>';
    badge.style.borderColor = 'rgba(16,185,129,0.3)';
  } else {
    badge.innerHTML = '<span class="status-dot" style="background:#EF4444;box-shadow:0 0 8px #EF4444"></span><span class="status-label" style="color:#FCA5A5">Reconnecting...</span>';
    badge.style.borderColor = 'rgba(239,68,68,0.3)';
  }
}

// ================================================================
// COUNTERS & KPI
// ================================================================
function updateCounters() {
  Object.keys(CATEGORY_CONFIG).forEach(function(cat) {
    var el = document.getElementById('badge-' + cat);
    if (el && cat !== 'users' && cat !== 'notifications' && cat !== 'wallet') {
      el.textContent = (appData[cat] || []).length;
    }
  });

  var revenue = (appData.bookings || []).reduce(function(acc, b) {
    return acc + (Number(b.totalAmount || b.total_amount) || 0);
  }, 0);
  var revEl = document.getElementById('kpi-revenue');
  if (revEl) revEl.textContent = '\u20B9' + revenue.toLocaleString('en-IN');

  var bkEl = document.getElementById('kpi-bookings-count');
  if (bkEl) bkEl.textContent = (appData.bookings || []).length;

  var inv = 0;
  Object.keys(CATEGORY_CONFIG).forEach(function(k) {
    if (k !== 'bookings' && k !== 'users' && k !== 'notifications' && k !== 'wallet') inv += (appData[k] || []).length;
  });
  var invEl = document.getElementById('kpi-inventory-count');
  if (invEl) invEl.textContent = inv;

  updateWalletUI();
}

// ================================================================
// NAVIGATION
// ================================================================
function switchCategory(cat) {
  currentCategory = cat;
  currentSearch   = '';
  var searchInput = document.getElementById('filter-input');
  if (searchInput) searchInput.value = '';

  document.querySelectorAll('.nav-item').forEach(function(btn) {
    btn.classList.toggle('active', btn.getAttribute('data-category') === cat);
  });

  var config = CATEGORY_CONFIG[cat];
  document.getElementById('current-category-title').textContent = config.title;
  document.getElementById('current-category-desc').textContent  = config.desc;

  var addBtn = document.getElementById('btn-add-item');
  if (addBtn) addBtn.style.display = config.showAdd ? 'inline-flex' : 'none';

  var notifPanel  = document.getElementById('notifications-panel');
  var usersPanel  = document.getElementById('users-panel');
  var walletPanel = document.getElementById('wallet-panel');
  var tableEl     = document.getElementById('table-container');
  var emptyEl     = document.getElementById('empty-state');

  if (notifPanel)  notifPanel.style.display  = (cat === 'notifications') ? 'block' : 'none';
  if (usersPanel)  usersPanel.style.display  = (cat === 'users')         ? 'block' : 'none';
  if (walletPanel) walletPanel.style.display = (cat === 'wallet')        ? 'block' : 'none';
  if (tableEl)     tableEl.style.display     = (cat === 'notifications' || cat === 'users' || cat === 'wallet') ? 'none' : 'block';
  if (emptyEl)     emptyEl.style.display     = 'none';

  if (cat === 'users') renderUsersPanel();
  else if (cat === 'notifications') loadNotificationsLog();
  else if (cat === 'wallet') loadWalletSettings();
  else renderContent();
}

function onSearchFilter(val) {
  currentSearch = val.toLowerCase().trim();
  if (currentCategory === 'users') renderUsersPanel();
  else renderContent();
}

// ================================================================
// TABLE RENDERING
// ================================================================
function renderContent() {
  var items    = appData[currentCategory] || [];
  var filtered = items.filter(function(it) {
    if (!currentSearch) return true;
    return JSON.stringify(it).toLowerCase().indexOf(currentSearch) !== -1;
  });

  var tableEl = document.getElementById('table-container');
  var emptyEl = document.getElementById('empty-state');

  if (filtered.length === 0) {
    tableEl.innerHTML = '';
    emptyEl.style.display = 'block';
    return;
  }

  emptyEl.style.display = 'none';
  var config = CATEGORY_CONFIG[currentCategory];
  var html   = '<table><thead><tr>';
  config.columns.forEach(function(col) { html += '<th>' + col + '</th>'; });
  html += '</tr></thead><tbody>';
  filtered.forEach(function(it) { html += renderRow(currentCategory, it); });
  html += '</tbody></table>';
  tableEl.innerHTML = html;
}

function esc(s) {
  if (s == null) return '';
  return String(s).replace(/&/g,'&amp;').replace(/"/g,'&quot;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}

function getThumb(item) {
  var url = item.imageUrl || item.image_url;
  if (!url) return '';
  return '<img src="' + esc(url) + '" class="table-img-thumb" alt="thumb" onerror="this.style.display=\'none\';">';
}

function renderRow(cat, item) {
  var cells = '';
  switch(cat) {
    case 'bookings': {
      var id     = item.id || item.bookingId || item.booking_id || '';
      var status = item.status || 'Pending';
      cells = '<td><strong>' + esc(item.booking_id || item.bookingId || id) + '</strong></td>'
        + '<td><span class="badge-status" style="background:#EBF3FF;color:#146EF5;">' + esc(item.serviceType || item.service_category || 'Package') + '</span></td>'
        + '<td><strong>' + esc(item.title || item.destinationTitle || item.service_name || 'Booking') + '</strong><br><small style="color:#64748B">' + esc(item.details || item.duration || item.travel_date || '') + '</small></td>'
        + '<td>' + esc(item.travellerName || item.customer_name || 'N/A') + '<br><small style="color:#64748B">' + esc(item.phone || item.email || '') + '</small></td>'
        + '<td><strong style="color:#10B981">\u20B9' + (Number(item.totalAmount || item.total_amount) || 0).toLocaleString('en-IN') + '</strong></td>'
        + '<td>' + esc(item.bookingDate || item.booking_date || (item.created_at ? item.created_at.split('T')[0] : 'Recent')) + '</td>'
        + '<td><select onchange="updateBookingStatus(\'' + esc(id) + '\',this.value)" style="padding:4px 8px;border-radius:6px;border:1px solid #CBD5E1;font-size:12px;font-weight:600;">'
        + ['Confirmed','In Progress','Completed','Cancelled','Pending'].map(function(s){ return '<option value="'+s+'"'+(status===s?' selected':'')+'>'+s+'</option>'; }).join('')
        + '</select></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-delete" onclick="promptDelete(\'' + esc(id) + '\',\'Booking ' + esc(id) + '\')">Delete</button></div></td>';
      break;
    }
    case 'flights':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.airline) + '</strong><br><small style="color:#64748B">' + esc(item.flightNumber || item.flight_number) + '</small></div></div></td>'
        + '<td>' + esc(item.origin) + ' &rarr; <strong>' + esc(item.destination) + '</strong></td>'
        + '<td>' + esc(item.departureTime || item.departure_time) + ' - ' + esc(item.arrivalTime || item.arrival_time) + '</td>'
        + '<td>' + esc(item.duration) + '</td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.price || 0).toLocaleString('en-IN') + '</strong></td>'
        + '<td><span class="badge-status badge-confirmed">' + esc(item.stops || 'Non-stop') + '</span></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.airline) + '\')">Delete</button></div></td>';
      break;
    case 'hotels':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.name) + '</strong></div></div></td>'
        + '<td>' + esc(item.location) + '</td>'
        + '<td>&#9733; <strong>' + esc(item.rating) + '</strong></td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.pricePerNight || item.price_per_night || 0).toLocaleString('en-IN') + '</strong></td>'
        + '<td><small style="color:#64748B">' + (item.amenities || []).slice(0,3).join(', ') + '</small></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.name) + '\')">Delete</button></div></td>';
      break;
    case 'villas':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.name) + '</strong></div></div></td>'
        + '<td>' + esc(item.location) + '</td>'
        + '<td>' + (item.bedrooms||3) + ' Beds / ' + (item.guests||6) + ' Guests</td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.pricePerNight || item.price_per_night || 0).toLocaleString('en-IN') + '</strong></td>'
        + '<td><small style="color:#64748B">' + (item.amenities || []).slice(0,3).join(', ') + '</small></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.name) + '\')">Delete</button></div></td>';
      break;
    case 'holidays':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.title) + '</strong></div></div></td>'
        + '<td>' + esc(item.location) + '</td>'
        + '<td>' + esc(item.duration) + '</td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.price||0).toLocaleString('en-IN') + '</strong></td>'
        + '<td><span class="badge-status badge-confirmed">' + esc(item.badge||'Popular') + '</span></td>'
        + '<td><small style="color:#64748B">' + (item.inclusions||[]).slice(0,2).join(', ') + '</small></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.title) + '\')">Delete</button></div></td>';
      break;
    case 'trains':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.trainName||item.train_name) + '</strong><br><small style="color:#64748B">#' + esc(item.trainNumber||item.train_number) + '</small></div></div></td>'
        + '<td>' + esc(item.origin) + ' &rarr; <strong>' + esc(item.destination) + '</strong></td>'
        + '<td>' + esc(item.departureTime||item.departure_time) + ' - ' + esc(item.arrivalTime||item.arrival_time) + ' (' + esc(item.duration) + ')</td>'
        + '<td><small style="color:#64748B">' + (item.classes||[]).join(', ') + '</small></td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.price||0).toLocaleString('en-IN') + '</strong></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.trainName||item.train_name) + '\')">Delete</button></div></td>';
      break;
    case 'buses':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.operator) + '</strong><br><small style="color:#64748B">' + esc(item.busType||item.bus_type) + '</small></div></div></td>'
        + '<td>' + esc(item.origin) + ' &rarr; <strong>' + esc(item.destination) + '</strong></td>'
        + '<td>' + esc(item.departureTime||item.departure_time) + ' - ' + esc(item.arrivalTime||item.arrival_time) + '</td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.price||0).toLocaleString('en-IN') + '</strong></td>'
        + '<td>&#9733; ' + (item.rating||4.7) + '</td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.operator) + '\')">Delete</button></div></td>';
      break;
    case 'cabs':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.carModel||item.car_model) + '</strong></div></div></td>'
        + '<td>' + esc(item.serviceType||item.service_type) + '</td>'
        + '<td>' + esc(item.category) + '</td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.baseFare||item.base_fare||0).toLocaleString('en-IN') + '</strong></td>'
        + '<td>\u20B9' + (item.perKm||item.per_km||14) + ' / km</td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.carModel||item.car_model) + '\')">Delete</button></div></td>';
      break;
    case 'tours':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.title) + '</strong></div></div></td>'
        + '<td>' + esc(item.location) + '</td>'
        + '<td>' + esc(item.duration) + '</td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.price||0).toLocaleString('en-IN') + '</strong></td>'
        + '<td>&#9733; ' + (item.rating||4.9) + '</td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.title) + '\')">Delete</button></div></td>';
      break;
    case 'visa':
      cells = '<td><strong>' + esc(item.country) + '</strong></td>'
        + '<td>' + esc(item.visaType||item.visa_type) + '</td>'
        + '<td>&#9889; ' + esc(item.processingTime||item.processing_time) + '</td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.price||0).toLocaleString('en-IN') + '</strong></td>'
        + '<td><span class="badge-status badge-confirmed">' + esc(item.successRate||item.success_rate||'99%') + '</span></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.country) + '\')">Delete</button></div></td>';
      break;
    case 'cruise':
      cells = '<td><div style="display:flex;align-items:center;">' + getThumb(item) + '<div><strong>' + esc(item.cruiseName||item.cruise_name) + '</strong><br><small style="color:#64748B">' + esc(item.shipClass||item.ship_class||'') + '</small></div></div></td>'
        + '<td>' + esc(item.sailingRoute||item.sailing_route) + '</td>'
        + '<td>' + esc(item.duration) + '</td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.price||0).toLocaleString('en-IN') + '</strong></td>'
        + '<td>&#9733; ' + (item.rating||4.8) + '</td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.cruiseName||item.cruise_name) + '\')">Delete</button></div></td>';
      break;
    case 'forex':
      cells = '<td><strong>' + esc(item.title) + '</strong></td>'
        + '<td>' + esc(item.type) + '</td>'
        + '<td>&#9889; ' + esc(item.deliveryTime||item.delivery_time) + '</td>'
        + '<td>\u20B9' + (item.cardFee||item.card_fee||0) + '</td>'
        + '<td><small style="color:#64748B">' + (item.currenciesSupported||item.currencies_supported||[]).join(', ') + '</small></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.title) + '\')">Delete</button></div></td>';
      break;
    case 'insurance':
      cells = '<td><strong>' + esc(item.planName||item.plan_name) + '</strong></td>'
        + '<td>' + esc(item.provider) + '</td>'
        + '<td><strong style="color:#10B981">' + esc(item.medicalCover||item.medical_cover) + '</strong></td>'
        + '<td><strong style="color:#146EF5">\u20B9' + (item.pricePerDay||item.price_per_day||0) + ' / day</strong></td>'
        + '<td><small style="color:#64748B">' + (item.benefits||[]).slice(0,2).join(', ') + '</small></td>'
        + '<td><div class="action-btns"><button class="btn-action btn-edit" onclick="openEditModal(\'' + item.id + '\')">Edit</button><button class="btn-action btn-delete" onclick="promptDelete(\'' + item.id + '\',\'' + esc(item.planName||item.plan_name) + '\')">Delete</button></div></td>';
      break;
  }
  return '<tr>' + cells + '</tr>';
}

// ================================================================
// USERS PANEL
// ================================================================
function renderUsersPanel() {
  var panel    = document.getElementById('users-panel');
  var filtered = usersData.filter(function(u) {
    if (!currentSearch) return true;
    return JSON.stringify(u).toLowerCase().indexOf(currentSearch) !== -1;
  });

  if (filtered.length === 0) {
    panel.innerHTML = '<div class="users-empty"><div style="font-size:48px;margin-bottom:12px;">&#128101;</div><h3>No Users Found</h3><p>No registered users match your search, or no one has signed up yet.</p><p style="margin-top:8px;color:#94A3B8;font-size:12px;">Users appear automatically once they sign up through the mobile app.</p></div>';
    return;
  }

  var colors = ['#146EF5','#10B981','#8B5CF6','#F59E0B','#EF4444','#06B6D4'];
  var html = '<div class="users-grid">';
  filtered.forEach(function(u, i) {
    var name     = u.full_name || 'App User';
    var initials = name.split(' ').map(function(w){ return w[0]; }).join('').toUpperCase().slice(0,2);
    var color    = colors[i % colors.length];
    var joinDate = u.created_at ? new Date(u.created_at).toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' }) : 'N/A';
    var bkCount  = u.total_bookings || (appData.bookings||[]).filter(function(b){ return b.email === u.email || b.user_id === u.id; }).length;
    var uid      = esc(String(u.id || u.email || i));
    var uemail   = esc(u.email || '');
    var uname    = esc(name);
    html += '<div class="user-card" onclick="openUserDetail(\'' + uid + '\')">'
      + '<div class="user-card-top">'
      + '<div class="user-avatar" style="background:' + color + '20;color:' + color + ';border:2px solid ' + color + '40;">' + initials + '</div>'
      + '<div class="user-card-info"><strong>' + uname + '</strong><span>' + uemail + '</span></div>'
      + '<button class="user-notif-btn" onclick="event.stopPropagation();openUserNotif(\'' + uid + '\',\'' + uemail + '\',\'' + uname + '\')" title="Send Notification">&#128276;</button>'
      + '</div>'
      + '<div class="user-card-stats">'
      + '<div class="user-stat"><span class="user-stat-label">Bookings</span><strong class="user-stat-val">' + bkCount + '</strong></div>'
      + '<div class="user-stat"><span class="user-stat-label">Phone</span><strong class="user-stat-val">' + esc(u.phone || '—') + '</strong></div>'
      + '<div class="user-stat"><span class="user-stat-label">Joined</span><strong class="user-stat-val">' + joinDate + '</strong></div>'
      + '</div></div>';
  });
  html += '</div>';
  panel.innerHTML = html;
}

function openUserDetail(userId) {
  var u = usersData.find(function(x){ return String(x.id) === String(userId) || x.email === userId; });
  if (!u) return;

  var name     = u.full_name || 'App User';
  var initials = name.split(' ').map(function(w){ return w[0]; }).join('').toUpperCase().slice(0,2);
  var joinDate = u.created_at ? new Date(u.created_at).toLocaleDateString('en-IN', { weekday:'long', year:'numeric', month:'long', day:'numeric' }) : 'N/A';
  var userBks  = (appData.bookings || []).filter(function(b){ return b.email === u.email || b.user_id === u.id; });

  document.getElementById('user-modal-title').textContent    = name;
  document.getElementById('user-modal-subtitle').textContent = u.email || 'Mobile App Account';
  document.getElementById('user-detail-modal').dataset.userId = userId;

  var rows = userBks.length > 0
    ? userBks.map(function(b) {
        var bStatus = b.status || 'Pending';
        var cls     = bStatus === 'Confirmed' ? 'badge-confirmed' : bStatus === 'Cancelled' ? 'badge-cancelled' : 'badge-pending';
        return '<tr><td>' + esc(b.booking_id||b.bookingId||b.id) + '</td><td>' + esc(b.title||b.service_name||b.serviceType||'Booking') + '</td><td>\u20B9' + (Number(b.totalAmount||b.total_amount)||0).toLocaleString('en-IN') + '</td><td><span class="badge-status ' + cls + '">' + bStatus + '</span></td><td>' + esc(b.bookingDate||b.booking_date||(b.created_at?b.created_at.split('T')[0]:'N/A')) + '</td></tr>';
      }).join('')
    : '<tr><td colspan="5" style="text-align:center;color:#94A3B8;padding:20px;">No bookings yet</td></tr>';

  document.getElementById('user-detail-body').innerHTML =
    '<div class="user-detail-profile">'
    + '<div class="user-detail-avatar">' + initials + '</div>'
    + '<div><h2>' + esc(name) + '</h2><p>' + esc(u.email||'No email') + '</p></div>'
    + '</div>'
    + '<div class="user-detail-meta">'
    + '<div class="user-meta-item"><span>&#128241; Phone</span><strong>' + esc(u.phone||'—') + '</strong></div>'
    + '<div class="user-meta-item"><span>&#128197; Joined</span><strong>' + joinDate + '</strong></div>'
    + '<div class="user-meta-item"><span>&#128273; User ID</span><strong style="font-size:11px;color:#94A3B8">' + esc(u.id||'—') + '</strong></div>'
    + '<div class="user-meta-item"><span>&#128203; Bookings</span><strong style="color:#146EF5">' + userBks.length + '</strong></div>'
    + '</div>'
    + '<h4 style="font-size:14px;font-weight:700;margin-bottom:12px;">Booking History</h4>'
    + '<div style="overflow-x:auto;">'
    + '<table><thead><tr><th>Booking ID</th><th>Service</th><th>Amount</th><th>Status</th><th>Date</th></tr></thead><tbody>' + rows + '</tbody></table>'
    + '</div>';

  document.getElementById('user-detail-modal').style.display = 'flex';
}

function closeUserModal() {
  document.getElementById('user-detail-modal').style.display = 'none';
}

function notifyThisUser() {
  var userId = document.getElementById('user-detail-modal').dataset.userId;
  var u = usersData.find(function(x){ return String(x.id) === String(userId) || x.email === userId; });
  if (u) { closeUserModal(); openUserNotif(u.id || u.email, u.email || '', u.full_name || 'User'); }
}

function openUserNotif(userId, email, name) {
  switchCategory('notifications');
  var targetSel = document.getElementById('notif-target');
  if (targetSel) { targetSel.value = 'specific'; document.getElementById('notif-specific-user-field').style.display = 'block'; }
  var emailIn = document.getElementById('notif-specific-email');
  if (emailIn) emailIn.value = email;
  var titleIn = document.getElementById('notif-title');
  if (titleIn) titleIn.focus();
  showToast('Composing notification for ' + name);
}

// ================================================================
// NOTIFICATIONS
// ================================================================
async function handleSendNotification(event) {
  event.preventDefault();
  var title   = document.getElementById('notif-title').value.trim();
  var body    = document.getElementById('notif-body').value.trim();
  var type    = document.getElementById('notif-type').value;
  var target  = document.getElementById('notif-target').value;
  var spEmail = document.getElementById('notif-specific-email').value.trim();
  var btn     = document.getElementById('btn-send-notif');

  btn.disabled    = true;
  btn.textContent = 'Sending...';

  try {
    var payload = {
      title:    title,
      body:     body,
      type:     type,
      target:   target === 'specific' ? spEmail : 'all',
      sent_by:  currentAdminUser ? currentAdminUser.email : ADMIN_EMAIL,
      sent_at:  new Date().toISOString(),
      status:   'sent'
    };
    try { await db.from('notifications').insert(payload); } catch(e) { /* table may not exist yet */ }
    try {
      await fetch('/api/notifications', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
    } catch(e) {}
    notificationsLog.unshift(Object.assign({ id: Date.now() }, payload));
    renderNotificationsLog();
    document.getElementById('notif-form').reset();
    document.getElementById('notif-specific-user-field').style.display = 'none';
    showToast('Notification "' + title + '" sent to ' + (target === 'all' ? 'all users' : spEmail) + '!');
  } catch(err) {
    showToast('Failed to send: ' + err.message);
  } finally {
    btn.disabled    = false;
    btn.innerHTML   = '&#9993; Send Notification';
  }
}

async function loadNotificationsLog() {
  try {
    var res = await fetch('/api/notifications');
    if (res.ok) {
      notificationsLog = await res.json();
      renderNotificationsLog();
    }
  } catch(e) {}
}

function renderNotificationsLog() {
  var list    = document.getElementById('notif-log-list');
  var countEl = document.getElementById('notif-log-count');
  if (countEl) countEl.textContent = notificationsLog.length + ' sent';
  if (!list) return;
  if (notificationsLog.length === 0) {
    list.innerHTML = '<div class="notif-empty">No notifications sent yet. Compose one to get started.</div>';
    return;
  }
  var icons = { promotion:'&#127873;', booking_update:'&#128203;', alert:'&#9888;', welcome:'&#128075;', custom:'&#9999;' };
  list.innerHTML = notificationsLog.map(function(n) {
    var d   = n.sent_at ? new Date(n.sent_at).toLocaleString('en-IN', { day:'numeric', month:'short', hour:'2-digit', minute:'2-digit' }) : 'Just now';
    var ico = icons[n.type] || '&#128276;';
    var tgt = n.target === 'all' ? '&#128226; All Users' : '&#128100; ' + n.target;
    return '<div class="notif-log-item"><div class="notif-log-icon">' + ico + '</div><div class="notif-log-content"><strong>' + esc(n.title) + '</strong><p>' + esc(n.body) + '</p><div class="notif-log-meta"><span class="notif-tag">' + tgt + '</span><span class="notif-date">' + d + '</span></div></div><span class="notif-sent-badge">&#10003; Sent</span></div>';
  }).join('');
}

// ================================================================
// WALLET & SIGNUP BONUS CONTROLLER
// ================================================================
async function loadWalletSettings() {
  try {
    var res = await fetch('/api/settings');
    if (res.ok) {
      var data = await res.json();
      if (data && data.signupBonus !== undefined) {
        walletSettings.signupBonus = data.signupBonus;
        updateWalletUI();
      }
    }
  } catch(e) {}
}

function updateWalletUI() {
  var amt = (walletSettings && walletSettings.signupBonus !== undefined) ? walletSettings.signupBonus : 2500;
  var badge = document.getElementById('badge-wallet');
  if (badge) badge.textContent = '\u20B9' + amt.toLocaleString('en-IN');
  var disp = document.getElementById('display-current-bonus');
  if (disp) disp.textContent = '\u20B9' + amt.toLocaleString('en-IN');
  var inp = document.getElementById('input-signup-bonus');
  if (inp && document.activeElement !== inp) inp.value = amt;

  document.querySelectorAll('.preset-pill').forEach(function(pill) {
    var pillAmt = pill.textContent.replace(/[^\d]/g, '');
    var isActive = parseInt(pillAmt, 10) === amt;
    pill.style.background  = isActive ? '#084D5C' : '#f1f5f9';
    pill.style.color       = isActive ? '#fff' : '#0f172a';
    pill.style.borderColor = isActive ? '#084D5C' : '#cbd5e1';
  });
}

function setPresetBonus(val) {
  var inp = document.getElementById('input-signup-bonus');
  if (inp) inp.value = val;
  document.querySelectorAll('.preset-pill').forEach(function(pill) {
    var pillAmt = pill.textContent.replace(/[^\d]/g, '');
    var isActive = parseInt(pillAmt, 10) === val;
    pill.style.background  = isActive ? '#084D5C' : '#f1f5f9';
    pill.style.color       = isActive ? '#fff' : '#0f172a';
    pill.style.borderColor = isActive ? '#084D5C' : '#cbd5e1';
  });
}

async function handleSaveWalletBonus(e) {
  e.preventDefault();
  var inp = document.getElementById('input-signup-bonus');
  var amt = parseInt(inp.value, 10);
  if (isNaN(amt) || amt < 0) {
    showToast('Please enter a valid bonus amount');
    return;
  }
  var btn = document.getElementById('btn-save-wallet-bonus');
  if (btn) {
    btn.disabled = true;
    btn.textContent = 'Saving & Syncing...';
  }
  try {
    var res = await fetch('/api/settings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ signupBonus: amt })
    });
    if (res.ok) {
      walletSettings.signupBonus = amt;
      updateWalletUI();
      showToast('\u2705 Signup Bonus set to \u20B9' + amt.toLocaleString('en-IN') + '! Mobile app is now synced.');
    } else {
      showToast('Error updating signup bonus');
    }
  } catch(err) {
    showToast('Failed to save: ' + err.message);
  } finally {
    if (btn) {
      btn.disabled = false;
      btn.innerHTML = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/></svg> Save Signup Bonus &amp; Sync to Mobile App';
    }
  }
}

// ================================================================
// FORM SCHEMAS
// ================================================================
function getFieldsForCategory(cat, item) {
  item = item || {};
  switch(cat) {
    case 'flights': return [
      { name:'airline',       label:'Airline Name',         type:'text',   value:item.airline||'',                      required:true },
      { name:'flightNumber',  label:'Flight Number',        type:'text',   value:item.flightNumber||item.flight_number||'', required:true },
      { name:'imageUrl',      label:'Flight / Airline Image URL', type:'image', value:item.imageUrl||item.image_url||'', fullWidth:true },
      { name:'origin',        label:'Origin Airport',       type:'text',   value:item.origin||'',                       required:true },
      { name:'destination',   label:'Destination Airport',  type:'text',   value:item.destination||'',                  required:true },
      { name:'departureTime', label:'Departure Time',       type:'text',   value:item.departureTime||item.departure_time||'08:00', required:true },
      { name:'arrivalTime',   label:'Arrival Time',         type:'text',   value:item.arrivalTime||item.arrival_time||'10:15', required:true },
      { name:'duration',      label:'Duration (eg 2h 15m)', type:'text',   value:item.duration||'2h 15m',              required:true },
      { name:'price',         label:'Fare in INR',          type:'number', value:item.price||4999,                      required:true },
      { name:'stops',         label:'Stops',                type:'text',   value:item.stops||'Non-stop' }
    ];
    case 'hotels': return [
      { name:'name',          label:'Hotel Name',             type:'text',   value:item.name||'',                        required:true, fullWidth:true },
      { name:'imageUrl',      label:'Hotel Photo / Banner URL', type:'image', value:item.imageUrl||item.image_url||'',  fullWidth:true },
      { name:'location',      label:'Location / City',        type:'text',   value:item.location||'',                    required:true },
      { name:'pricePerNight', label:'Price per Night (INR)',  type:'number', value:item.pricePerNight||item.price_per_night||4500, required:true },
      { name:'rating',        label:'Star Rating (1-5)',      type:'number', step:'0.1', value:item.rating||4.8 },
      { name:'amenities',     label:'Amenities (comma-sep)',  type:'text',   value:(item.amenities||[]).join(', '),       fullWidth:true }
    ];
    case 'villas': return [
      { name:'name',          label:'Villa Name',             type:'text',   value:item.name||'',                        required:true, fullWidth:true },
      { name:'imageUrl',      label:'Villa Photo / Banner URL', type:'image', value:item.imageUrl||item.image_url||'',  fullWidth:true },
      { name:'location',      label:'Location',               type:'text',   value:item.location||'',                    required:true },
      { name:'pricePerNight', label:'Price per Night (INR)',  type:'number', value:item.pricePerNight||item.price_per_night||12000, required:true },
      { name:'bedrooms',      label:'Bedrooms',               type:'number', value:item.bedrooms||3 },
      { name:'guests',        label:'Max Guests',             type:'number', value:item.guests||8 },
      { name:'amenities',     label:'Amenities (comma-sep)',  type:'text',   value:(item.amenities||[]).join(', '),       fullWidth:true }
    ];
    case 'holidays': return [
      { name:'title',      label:'Package Title',          type:'text',     value:item.title||'',                        required:true, fullWidth:true },
      { name:'imageUrl',   label:'Package & Destination Image URL', type:'image', value:item.imageUrl||item.image_url||'', fullWidth:true },
      { name:'location',   label:'Destination Route',      type:'text',     value:item.location||'',                    required:true },
      { name:'duration',   label:'Duration',               type:'text',     value:item.duration||'5 Days / 4 Nights',   required:true },
      { name:'price',      label:'Package Price (INR)',     type:'number',   value:item.price||19999,                    required:true },
      { name:'badge',      label:'Highlight Badge',        type:'text',     value:item.badge||'Best Seller' },
      { name:'inclusions', label:'Inclusions (comma-sep)', type:'textarea', value:(item.inclusions||[]).join(', '),      fullWidth:true }
    ];
    case 'trains': return [
      { name:'trainName',     label:'Train Name',           type:'text',   value:item.trainName||item.train_name||'',   required:true },
      { name:'trainNumber',   label:'Train Number',         type:'text',   value:item.trainNumber||item.train_number||'', required:true },
      { name:'imageUrl',      label:'Train Image URL',      type:'image',  value:item.imageUrl||item.image_url||'',     fullWidth:true },
      { name:'origin',        label:'Source Station',       type:'text',   value:item.origin||'',                       required:true },
      { name:'destination',   label:'Destination Station',  type:'text',   value:item.destination||'',                  required:true },
      { name:'departureTime', label:'Departure Time',       type:'text',   value:item.departureTime||item.departure_time||'06:00', required:true },
      { name:'arrivalTime',   label:'Arrival Time',         type:'text',   value:item.arrivalTime||item.arrival_time||'14:00', required:true },
      { name:'duration',      label:'Duration',             type:'text',   value:item.duration||'8h 00m' },
      { name:'price',         label:'Base Fare (INR)',       type:'number', value:item.price||1450,                      required:true }
    ];
    case 'buses': return [
      { name:'operator',      label:'Bus Operator',         type:'text',   value:item.operator||'',                     required:true },
      { name:'busType',       label:'Bus Type / Model',     type:'text',   value:item.busType||item.bus_type||'Volvo AC Sleeper', required:true },
      { name:'imageUrl',      label:'Bus Photo URL',        type:'image',  value:item.imageUrl||item.image_url||'',     fullWidth:true },
      { name:'origin',        label:'Boarding City',        type:'text',   value:item.origin||'',                       required:true },
      { name:'destination',   label:'Dropping City',        type:'text',   value:item.destination||'',                  required:true },
      { name:'departureTime', label:'Departure Time',       type:'text',   value:item.departureTime||item.departure_time||'20:00' },
      { name:'arrivalTime',   label:'Arrival Time',         type:'text',   value:item.arrivalTime||item.arrival_time||'07:30' },
      { name:'price',         label:'Ticket Price (INR)',   type:'number', value:item.price||999,                       required:true }
    ];
    case 'cabs': return [
      { name:'carModel',     label:'Car Model',            type:'text',   value:item.carModel||item.car_model||'',      required:true },
      { name:'serviceType',  label:'Service Type',         type:'text',   value:item.serviceType||item.service_type||'Outstation & Airport', required:true },
      { name:'imageUrl',     label:'Vehicle Photo URL',    type:'image',  value:item.imageUrl||item.image_url||'',      fullWidth:true },
      { name:'category',     label:'Seating Category',     type:'text',   value:item.category||'4+1 Premium Sedan' },
      { name:'baseFare',     label:'Base Fare (INR)',       type:'number', value:item.baseFare||item.base_fare||1400,   required:true },
      { name:'perKm',        label:'Per KM Rate (INR)',    type:'number', value:item.perKm||item.per_km||14,           required:true }
    ];
    case 'tours': return [
      { name:'title',    label:'Experience Title',          type:'text',   value:item.title||'',                        required:true, fullWidth:true },
      { name:'imageUrl', label:'Experience Image URL',      type:'image',  value:item.imageUrl||item.image_url||'',     fullWidth:true },
      { name:'location', label:'Location',                  type:'text',   value:item.location||'',                    required:true },
      { name:'duration', label:'Duration',                  type:'text',   value:item.duration||'3 Hours',             required:true },
      { name:'price',    label:'Pass Price (INR)',           type:'number', value:item.price||1200,                     required:true },
      { name:'rating',   label:'Rating (1-5)',              type:'number', step:'0.1', value:item.rating||4.9 }
    ];
    case 'visa': return [
      { name:'country',        label:'Country Destination', type:'text',   value:item.country||'',                      required:true },
      { name:'visaType',       label:'Visa Category',       type:'text',   value:item.visaType||item.visa_type||'Tourist eVisa 30 Days', required:true },
      { name:'processingTime', label:'Processing Speed',    type:'text',   value:item.processingTime||item.processing_time||'48 Hours', required:true },
      { name:'price',          label:'Visa Fee (INR)',       type:'number', value:item.price||4500,                     required:true },
      { name:'successRate',    label:'Success Guarantee',   type:'text',   value:item.successRate||item.success_rate||'99.5%' }
    ];
    case 'cruise': return [
      { name:'cruiseName',   label:'Cruise Liner Name',    type:'text',   value:item.cruiseName||item.cruise_name||'', required:true, fullWidth:true },
      { name:'imageUrl',     label:'Cruise Liner Image URL', type:'image', value:item.imageUrl||item.image_url||'',   fullWidth:true },
      { name:'sailingRoute', label:'Sailing Route',        type:'text',   value:item.sailingRoute||item.sailing_route||'', required:true },
      { name:'duration',     label:'Duration',             type:'text',   value:item.duration||'3 Nights / 4 Days',   required:true },
      { name:'price',        label:'Cabin Price (INR)',     type:'number', value:item.price||24000,                    required:true },
      { name:'shipClass',    label:'Ship Class',           type:'text',   value:item.shipClass||item.ship_class||'5-Star Luxury Ocean Liner' }
    ];
    case 'forex': return [
      { name:'title',        label:'Product Name',         type:'text',   value:item.title||'',                        required:true, fullWidth:true },
      { name:'type',         label:'Card or Cash Type',    type:'text',   value:item.type||'Zero Markup Smart Card',  required:true },
      { name:'deliveryTime', label:'Delivery Guarantee',   type:'text',   value:item.deliveryTime||item.delivery_time||'24 Hours Doorstep' },
      { name:'cardFee',      label:'Issuance Fee (INR)',   type:'number', value:item.cardFee||item.card_fee||0 }
    ];
    case 'insurance': return [
      { name:'planName',     label:'Plan Name',            type:'text',   value:item.planName||item.plan_name||'',     required:true, fullWidth:true },
      { name:'provider',     label:'Underwriter / Insurer',type:'text',   value:item.provider||'WonderLight Assurance', required:true },
      { name:'medicalCover', label:'Medical Coverage',     type:'text',   value:item.medicalCover||item.medical_cover||'$250,000 USD', required:true },
      { name:'pricePerDay',  label:'Price per Day (INR)',  type:'number', value:item.pricePerDay||item.price_per_day||99, required:true }
    ];
    default: return [];
  }
}

// ================================================================
// MODAL HANDLERS
// ================================================================
function openAddModal() {
  editingItemId = null;
  document.getElementById('modal-title').textContent    = 'Add New ' + CATEGORY_CONFIG[currentCategory].title.split('&')[0].trim();
  document.getElementById('modal-subtitle').textContent = 'Configure live parameters for the mobile app';
  renderModalFields(getFieldsForCategory(currentCategory));
  document.getElementById('edit-modal').style.display = 'flex';
}

function openEditModal(id) {
  editingItemId = id;
  var item = (appData[currentCategory] || []).find(function(it){ return String(it.id) === String(id); });
  if (!item) return;
  document.getElementById('modal-title').textContent    = 'Edit Service Item';
  document.getElementById('modal-subtitle').textContent = 'Update parameters — syncs to mobile app';
  renderModalFields(getFieldsForCategory(currentCategory, item));
  document.getElementById('edit-modal').style.display = 'flex';
}

function renderModalFields(fields) {
  var container = document.getElementById('form-dynamic-fields');
  var html = '';
  fields.forEach(function(f) {
    var cls = f.fullWidth ? 'form-group full-width' : 'form-group';
    html += '<div class="' + cls + '"><label>' + f.label + '</label>';
    if (f.type === 'textarea') {
      html += '<textarea name="' + f.name + '"' + (f.required ? ' required' : '') + '>' + (f.value || '') + '</textarea>';
    } else if (f.type === 'image') {
      var val = esc(String(f.value || ''));
      html += '<div class="image-uploader-field">'
        + '<div style="display:flex;gap:10px;align-items:center;margin-bottom:8px;">'
        + '<input type="text" id="modal-img-input" name="' + f.name + '" value="' + val + '" placeholder="Paste image URL or pick preset / upload" oninput="updateModalImagePreview(this.value)" style="flex:1;">'
        + '<label class="btn-secondary" style="margin:0;cursor:pointer;padding:8px 12px;font-size:12px;white-space:nowrap;display:inline-flex;align-items:center;gap:4px;">'
        + '&#128194; Upload Photo<input type="file" accept="image/*" style="display:none;" onchange="handleModalImageUpload(this)">'
        + '</label>'
        + '</div>'
        + '<div id="modal-img-preview-box" style="margin-bottom:10px;border-radius:10px;overflow:hidden;border:1px dashed #cbd5e1;background:#f8fafc;height:140px;display:flex;align-items:center;justify-content:center;text-align:center;">'
        + (val ? '<img src="' + val + '" style="width:100%;height:100%;object-fit:cover;" onerror="this.onerror=null;this.parentElement.innerHTML=\'<span style=\\\'color:#94a3b8;font-size:12px;\\\'>Image could not be loaded</span>\';">' : '<span style="color:#94a3b8;font-size:12px;">&#128247; Live image preview will appear here</span>')
        + '</div>'
        + '<div class="quick-image-presets" style="display:flex;flex-wrap:wrap;gap:6px;align-items:center;">'
        + '<span style="font-size:11px;color:#64748b;font-weight:600;margin-right:2px;">Presets:</span>'
        + '<button type="button" class="preset-img-btn" onclick="applyImagePreset(\'https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=800&auto=format&fit=crop\')">&#127958; Goa</button>'
        + '<button type="button" class="preset-img-btn" onclick="applyImagePreset(\'https://images.unsplash.com/photo-1595815771614-ade9d652a65d?w=800&auto=format&fit=crop\')">&#127954; Kashmir</button>'
        + '<button type="button" class="preset-img-btn" onclick="applyImagePreset(\'https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?w=800&auto=format&fit=crop\')">&#127984; Rajasthan</button>'
        + '<button type="button" class="preset-img-btn" onclick="applyImagePreset(\'https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=800&auto=format&fit=crop\')">&#127796; Kerala</button>'
        + '<button type="button" class="preset-img-btn" onclick="applyImagePreset(\'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&auto=format&fit=crop\')">&#127976; Hotel</button>'
        + '<button type="button" class="preset-img-btn" onclick="applyImagePreset(\'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=800&auto=format&fit=crop\')">&#127969; Villa</button>'
        + '<button type="button" class="preset-img-btn" onclick="applyImagePreset(\'https://images.unsplash.com/photo-1548574505-5e239809ee19?w=800&auto=format&fit=crop\')">&#128674; Cruise</button>'
        + '<button type="button" class="preset-img-btn" onclick="applyImagePreset(\'https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=800&auto=format&fit=crop\')">&#9992; Flight</button>'
        + '</div>'
        + '</div>';
    } else {
      html += '<input type="' + f.type + '" name="' + f.name + '" value="' + (f.value !== undefined ? esc(String(f.value)) : '') + '" step="' + (f.step || 'any') + '"' + (f.required ? ' required' : '') + '>';
    }
    html += '</div>';
  });
  container.innerHTML = html;
}

function updateModalImagePreview(url) {
  var box = document.getElementById('modal-img-preview-box');
  if (!box) return;
  if (!url || !url.trim()) {
    box.innerHTML = '<span style="color:#94a3b8;font-size:12px;">&#128247; Live image preview will appear here</span>';
    return;
  }
  box.innerHTML = '<img src="' + url.trim() + '" style="width:100%;height:100%;object-fit:cover;" onerror="this.onerror=null;this.parentElement.innerHTML=\'<span style=\\\'color:#ef4444;font-size:12px;\\\'>Invalid image link</span>\';">';
}

function applyImagePreset(url) {
  var inp = document.getElementById('modal-img-input');
  if (inp) {
    inp.value = url;
    updateModalImagePreview(url);
  }
}

async function handleModalImageUpload(fileInput) {
  if (!fileInput.files || !fileInput.files[0]) return;
  var file = fileInput.files[0];
  var reader = new FileReader();
  reader.onload = async function(e) {
    var base64 = e.target.result;
    try {
      showToast('Uploading image...');
      var res = await fetch('/api/upload', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ image: base64, name: file.name })
      });
      if (res.ok) {
        var data = await res.json();
        if (data.url) {
          var inp = document.getElementById('modal-img-input');
          if (inp) {
            inp.value = data.url;
            updateModalImagePreview(data.url);
          }
          showToast('Image uploaded successfully!');
          return;
        }
      }
    } catch(err) {}
    var inp = document.getElementById('modal-img-input');
    if (inp) {
      inp.value = base64;
      updateModalImagePreview(base64);
    }
    showToast('Image loaded');
  };
  reader.readAsDataURL(file);
}

function closeModal() {
  document.getElementById('edit-modal').style.display = 'none';
  editingItemId = null;
}

// ================================================================
// FORM SUBMIT
// ================================================================
async function handleFormSubmit(e) {
  e.preventDefault();
  var form     = document.getElementById('service-form');
  var formData = new FormData(form);
  var payload  = {};
  var arrFields = ['amenities','inclusions','currenciesSupported','benefits','highlights','classes'];
  var numFields = ['price','pricePerNight','baseFare','perKm','cardFee','pricePerDay','bedrooms','guests'];

  for (var pair of formData.entries()) {
    var key = pair[0], val = pair[1];
    if (arrFields.indexOf(key) !== -1) {
      payload[key] = val.split(',').map(function(s){ return s.trim(); }).filter(Boolean);
    } else if (numFields.indexOf(key) !== -1) {
      payload[key] = Number(val);
    } else if (key === 'rating') {
      payload[key] = parseFloat(val);
    } else {
      payload[key] = val;
    }
  }

  try {
    var snakePayload = toSnakeCase(payload);
    // 1. Write directly to Supabase
    if (editingItemId) {
      var sbUp = await db.from(currentCategory).update(snakePayload).eq('id', editingItemId);
      if (sbUp.error) console.warn('Supabase update note:', sbUp.error.message);
    } else {
      var sbIns = await db.from(currentCategory).insert(snakePayload).select();
      if (sbIns.data && sbIns.data[0] && sbIns.data[0].id) {
        payload.id = sbIns.data[0].id;
      }
      if (sbIns.error) console.warn('Supabase insert note:', sbIns.error.message);
    }

    // 2. Also keep local server cache and store.json synchronized
    try {
      if (editingItemId) {
        await fetch('/api/services/' + currentCategory + '/' + editingItemId, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      } else {
        await fetch('/api/services/' + currentCategory, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      }
    } catch(errNode) {}

    closeModal();
    showToast(editingItemId ? 'Item updated & synced to Supabase + App!' : 'New item saved to Supabase & published to App!');
    await loadData();
  } catch(err) {
    alert('Error saving item: ' + err.message);
  }
}

// ================================================================
// BOOKING STATUS UPDATE
// ================================================================
async function updateBookingStatus(id, newStatus) {
  try {
    // 1. Update in Supabase
    var sbRes = await db.from('bookings').update({ status: newStatus }).or('id.eq.' + id + ',booking_id.eq.' + id);
    if (sbRes.error) console.warn('Supabase booking update note:', sbRes.error.message);

    // 2. Update local server
    try {
      await fetch('/api/bookings/' + id, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus })
      });
    } catch(e) {}

    showToast('Booking status updated to "' + newStatus + '" in Supabase & App');
    await loadData();
  } catch(err) {
    alert('Failed to update status: ' + err.message);
  }
}

// ================================================================
// DELETE
// ================================================================
function promptDelete(id, title) {
  deletingItemId = id;
  document.getElementById('delete-item-title').textContent = title || 'this item';
  document.getElementById('delete-modal').style.display = 'flex';
}

function closeDeleteModal() {
  document.getElementById('delete-modal').style.display = 'none';
  deletingItemId = null;
}

async function confirmDelete() {
  if (!deletingItemId) return;
  try {
    var tbl = currentCategory === 'bookings' ? 'bookings' : currentCategory;
    // 1. Delete from Supabase
    var sbDel = await db.from(tbl).delete().eq('id', deletingItemId);
    if (sbDel.error) console.warn('Supabase delete note:', sbDel.error.message);

    // 2. Delete from local server
    try {
      var ep = currentCategory === 'bookings' ? '/api/bookings/' + deletingItemId : '/api/services/' + currentCategory + '/' + deletingItemId;
      await fetch(ep, { method: 'DELETE' });
    } catch(e) {}

    closeDeleteModal();
    showToast('Item deleted from Supabase & App');
    await loadData();
  } catch(err) {
    alert('Failed to delete: ' + err.message);
  }
}

// ================================================================
// TOAST
// ================================================================
function showToast(msg) {
  var toast = document.getElementById('toast');
  if (!toast) return;
  toast.textContent = msg;
  toast.classList.add('show');
  setTimeout(function(){ toast.classList.remove('show'); }, 3200);
}

// ================================================================
// DOM READY — BOOT
// ================================================================
document.addEventListener('DOMContentLoaded', function() {
  // Notification target toggle
  var targetSel = document.getElementById('notif-target');
  if (targetSel) {
    targetSel.addEventListener('change', function() {
      var sf = document.getElementById('notif-specific-user-field');
      if (sf) sf.style.display = targetSel.value === 'specific' ? 'block' : 'none';
    });
  }

  // Boot authentication
  initAuth();
});
