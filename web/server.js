const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const PUBLIC_DIR = path.join(__dirname, 'public');

// In-Memory Database for MUET Transit Web Platform
const db = {
  shuttles: [
    {
      id: "POINT_27",
      pointNumber: "Point 27",
      busPlate: "EB-0109",
      routeId: "R_LATIFABAD",
      routeName: "Route 1: Latifabad / Qasimabad to MUET",
      driverName: "Ustad Ghulam Rasool",
      driverPhone: "+92 300 7862700",
      totalSeats: 52,
      occupiedSeats: 38,
      standingPassengers: 4,
      status: "ON_ROUTE",
      currentStopName: "Qasim Chowk",
      nextStopName: "Naseem Nagar",
      latitude: 25.3960,
      longitude: 68.3450,
      speedKmph: 42,
      delayMinutes: 0
    },
    {
      id: "POINT_23",
      pointNumber: "Point 23",
      busPlate: "EB-0102",
      routeId: "R_SADDAR",
      routeName: "Route 2: Saddar Cantt / Thandi Sarak to MUET",
      driverName: "Ustad Allah Dino Solangi",
      driverPhone: "+92 301 5552300",
      totalSeats: 52,
      occupiedSeats: 50,
      standingPassengers: 12,
      status: "ON_ROUTE",
      currentStopName: "Thandi Sarak",
      nextStopName: "Jamshoro Toll Plaza",
      latitude: 25.4120,
      longitude: 68.3180,
      speedKmph: 55,
      delayMinutes: 4
    },
    {
      id: "POINT_30",
      pointNumber: "Point 30",
      busPlate: "EB-0110",
      routeId: "R_AUTOBHAN",
      routeName: "Route 3: Auto Bhan & Citizen Colony to MUET",
      driverName: "Ustad Muhammad Ismail",
      driverPhone: "+92 302 9993000",
      totalSeats: 52,
      occupiedSeats: 26,
      standingPassengers: 0,
      status: "ON_ROUTE",
      currentStopName: "Auto Bhan Towers",
      nextStopName: "Wadhu Wah Road",
      latitude: 25.3800,
      longitude: 68.3540,
      speedKmph: 38,
      delayMinutes: 0
    },
    {
      id: "POINT_14",
      pointNumber: "Point 14",
      busPlate: "EB-0094",
      routeId: "R_KOTRI",
      routeName: "Route 4: Kotri Barrage / Al-Manzar to MUET",
      driverName: "Ustad Tariq Brohi",
      driverPhone: "+92 303 4441400",
      totalSeats: 52,
      occupiedSeats: 45,
      standingPassengers: 6,
      status: "ON_ROUTE",
      currentStopName: "Al-Manzar Point",
      nextStopName: "Sindh Univ Gate",
      latitude: 25.3650,
      longitude: 68.3050,
      speedKmph: 46,
      delayMinutes: 0
    }
  ],
  routes: [
    {
      id: "R_LATIFABAD",
      routeName: "Route 1: Latifabad & Qasimabad Express",
      code: "R-01",
      startLocation: "Latifabad Unit 7",
      endLocation: "MUET Auditorium / Admin",
      via: "Auto Bhan - Qasim Chowk - Naseem Nagar - Jamshoro Toll",
      totalStops: 10,
      morningDeparture: "07:15 AM",
      afternoonDeparture: "01:45 PM",
      eveningDeparture: "05:15 PM",
      distanceKm: 24.5,
      estimatedMinutes: 55
    },
    {
      id: "R_SADDAR",
      routeName: "Route 2: Saddar Cantt & Thandi Sarak",
      code: "R-02",
      startLocation: "Saddar Cantonment",
      endLocation: "MUET Central Library",
      via: "Gari Khata - Radio Pak - Thandi Sarak - Hyderabad Bypass",
      totalStops: 8,
      morningDeparture: "07:20 AM",
      afternoonDeparture: "01:45 PM",
      eveningDeparture: "05:15 PM",
      distanceKm: 21.0,
      estimatedMinutes: 48
    },
    {
      id: "R_AUTOBHAN",
      routeName: "Route 3: Auto Bhan & Citizen Colony",
      code: "R-03",
      startLocation: "Citizen Colony Gate",
      endLocation: "MUET Engineering Complex",
      via: "Auto Bhan Commercial - Wadhu Wah - Indus Highway",
      totalStops: 7,
      morningDeparture: "07:25 AM",
      afternoonDeparture: "01:45 PM",
      eveningDeparture: "05:15 PM",
      distanceKm: 22.8,
      estimatedMinutes: 50
    },
    {
      id: "R_KOTRI",
      routeName: "Route 4: Kotri Barrage & Al-Manzar",
      code: "R-04",
      startLocation: "Kotri Barrage Colony",
      endLocation: "MUET Main Gate & Girls Hostel",
      via: "Al-Manzar - Sindh University Rd - Zero Point",
      totalStops: 6,
      morningDeparture: "07:30 AM",
      afternoonDeparture: "01:45 PM",
      eveningDeparture: "05:15 PM",
      distanceKm: 16.4,
      estimatedMinutes: 35
    }
  ],
  stops: [
    { id: 1, routeId: "R_LATIFABAD", stopName: "Latifabad Unit 7", landmark: "Near Chandia Mor", orderIndex: 1, scheduledTime: "07:15 AM", latitude: 25.3620, longitude: 68.3680, isCampusStop: false },
    { id: 2, routeId: "R_LATIFABAD", stopName: "Unit 8 Market", landmark: "Al-Rahim Shopping", orderIndex: 2, scheduledTime: "07:22 AM", latitude: 25.3700, longitude: 68.3610, isCampusStop: false },
    { id: 3, routeId: "R_LATIFABAD", stopName: "Auto Bhan Towers", landmark: "Boulevard Mall Mor", orderIndex: 3, scheduledTime: "07:30 AM", latitude: 25.3800, longitude: 68.3540, isCampusStop: false },
    { id: 4, routeId: "R_LATIFABAD", stopName: "Qasim Chowk", landmark: "Main Roundabout", orderIndex: 4, scheduledTime: "07:40 AM", latitude: 25.3960, longitude: 68.3450, isCampusStop: false },
    { id: 5, routeId: "R_LATIFABAD", stopName: "Naseem Nagar Chowk", landmark: "Citizen Plaza", orderIndex: 5, scheduledTime: "07:48 AM", latitude: 25.4050, longitude: 68.3370, isCampusStop: false },
    { id: 6, routeId: "R_LATIFABAD", stopName: "Wadhu Wah Phir", landmark: "Canal Bridge", orderIndex: 6, scheduledTime: "07:55 AM", latitude: 25.4130, longitude: 68.3280, isCampusStop: false },
    { id: 7, routeId: "R_LATIFABAD", stopName: "Sheedi Goth Cut", landmark: "Highway Entry", orderIndex: 7, scheduledTime: "08:05 AM", latitude: 25.4210, longitude: 68.3120, isCampusStop: false },
    { id: 8, routeId: "R_LATIFABAD", stopName: "Jamshoro Toll Plaza", landmark: "River Indus Bridge", orderIndex: 8, scheduledTime: "08:15 AM", latitude: 25.4300, longitude: 68.2880, isCampusStop: false },
    { id: 9, routeId: "R_LATIFABAD", stopName: "MUET Main Gate", landmark: "Welcome Arch", orderIndex: 9, scheduledTime: "08:25 AM", latitude: 25.4050, longitude: 68.2620, isCampusStop: true },
    { id: 10, routeId: "R_LATIFABAD", stopName: "MUET Auditorium & Admin", landmark: "Central Campus", orderIndex: 10, scheduledTime: "08:30 AM", latitude: 25.4020, longitude: 68.2580, isCampusStop: true }
  ],
  boardingPass: {
    studentRollNo: "21SW089",
    studentName: "Gul Jabeen Gabol",
    department: "Software Engineering",
    batch: "21SW (Final Year)",
    routeName: "Route 1: Latifabad / Qasimabad",
    preferredStop: "Qasim Chowk",
    semesterPassType: "Regular Transport Pass",
    validUntil: "31-Dec-2026",
    passStatus: "ACTIVE",
    qrCodeHash: "MUET-PASS-21SW089-R01-VERIFIED-2026",
    feeReceiptNo: "TR-88921-MUET"
  },
  alerts: [
    {
      id: 1,
      pointNumber: "Point 27 & 23",
      routeName: "Route 1 & 2",
      title: "Jamshoro Old Bridge Congestion",
      message: "Heavy truck queue approaching Jamshoro Toll. Expect +8 to 12 minutes delay for morning shuttle points.",
      severity: "WARNING",
      timestamp: Date.now()
    },
    {
      id: 2,
      pointNumber: "Point 30",
      routeName: "Route 3",
      title: "Auto Bhan Signal Maintenance",
      message: "Traffic diverted through Citizen Colony backroad. Point will pause at Wadhu Wah canal junction.",
      severity: "INFO",
      timestamp: Date.now() - 3600000
    }
  ],
  stopRequests: [
    {
      id: 1,
      studentRollNo: "21SW089",
      studentName: "Gul Jabeen Gabol",
      pointId: "POINT_27",
      stopName: "Qasim Chowk",
      requestType: "PICKUP",
      timestamp: Date.now() - 600000,
      status: "PENDING",
      note: "Waiting near gate of pharmacy with 2 classmates"
    }
  ]
};

// Simulation ticker: smooth GPS bus movement in Hyderabad / Jamshoro
let step = 0;
setInterval(() => {
  step++;
  db.shuttles.forEach((bus) => {
    if (bus.status === "ON_ROUTE") {
      const deltaLat = Math.sin((step + bus.pointNumber.length * 7) * 0.25) * 0.0006;
      const deltaLng = Math.cos((step + bus.pointNumber.length * 7) * 0.25) * 0.0007;
      bus.latitude += deltaLat;
      bus.longitude += deltaLng;
      bus.speedKmph = Math.max(25, Math.min(65, 40 + (step % 20)));
    }
  });
}, 3000);

// Helper to parse JSON body
function parseBody(req) {
  return new Promise((resolve) => {
    let data = '';
    req.on('data', chunk => { data += chunk; });
    req.on('end', () => {
      try {
        resolve(data ? JSON.parse(data) : {});
      } catch (e) {
        resolve({});
      }
    });
  });
}

// HTTP Server
const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  const pathname = url.pathname;

  // CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  // API Endpoints
  if (pathname === '/api/status' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(db));
    return;
  }

  if (pathname === '/api/driver/seats' && req.method === 'POST') {
    const body = await parseBody(req);
    const bus = db.shuttles.find(b => b.id === body.pointId);
    if (bus) {
      if (body.deltaSeats !== undefined) {
        bus.occupiedSeats = Math.max(0, Math.min(bus.totalSeats, bus.occupiedSeats + body.deltaSeats));
      }
      if (body.deltaStanding !== undefined) {
        bus.standingPassengers = Math.max(0, bus.standingPassengers + body.deltaStanding);
      }
      if (body.preset) {
        if (body.preset === "FULL") { bus.occupiedSeats = bus.totalSeats; bus.standingPassengers = 8; }
        if (body.preset === "MODERATE") { bus.occupiedSeats = bus.totalSeats - 15; bus.standingPassengers = 0; }
        if (body.preset === "AVAILABLE") { bus.occupiedSeats = 15; bus.standingPassengers = 0; }
      }
    }
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, bus }));
    return;
  }

  if (pathname === '/api/driver/advance' && req.method === 'POST') {
    const body = await parseBody(req);
    const bus = db.shuttles.find(b => b.id === body.pointId);
    if (bus) {
      const routeStops = db.stops.filter(s => s.routeId === bus.routeId).sort((a, b) => a.orderIndex - b.orderIndex);
      if (routeStops.length > 0) {
        const curIdx = routeStops.findIndex(s => s.stopName === bus.currentStopName);
        const nextIdx = (curIdx >= 0 && curIdx + 1 < routeStops.length) ? curIdx + 1 : 0;
        const upcomingIdx = (nextIdx + 1 < routeStops.length) ? nextIdx + 1 : 0;
        bus.currentStopName = routeStops[nextIdx].stopName;
        bus.nextStopName = routeStops[upcomingIdx].stopName;
        bus.latitude = routeStops[nextIdx].latitude;
        bus.longitude = routeStops[nextIdx].longitude;
      }
    }
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, bus }));
    return;
  }

  if (pathname === '/api/driver/alert' && req.method === 'POST') {
    const body = await parseBody(req);
    const newAlert = {
      id: Date.now(),
      pointNumber: body.pointNumber || "Point 27",
      routeName: body.routeName || "Jamshoro-Hyderabad Route",
      title: body.title || "Traffic Advisory",
      message: body.message || "",
      severity: body.severity || "WARNING",
      timestamp: Date.now()
    };
    db.alerts.unshift(newAlert);
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, alert: newAlert }));
    return;
  }

  if (pathname === '/api/student/request' && req.method === 'POST') {
    const body = await parseBody(req);
    const newReq = {
      id: Date.now(),
      studentRollNo: db.boardingPass.studentRollNo,
      studentName: db.boardingPass.studentName,
      pointId: body.pointId || "POINT_27",
      stopName: body.stopName || "Qasim Chowk",
      requestType: body.requestType || "PICKUP",
      timestamp: Date.now(),
      status: "PENDING",
      note: body.note || ""
    };
    db.stopRequests.unshift(newReq);
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, request: newReq }));
    return;
  }

  if (pathname === '/api/requests/update' && req.method === 'POST') {
    const body = await parseBody(req);
    const r = db.stopRequests.find(reqItem => reqItem.id === Number(body.id));
    if (r) {
      r.status = body.status;
    }
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, request: r }));
    return;
  }

  // Serve Static Assets from web/public/
  let filePath = path.join(PUBLIC_DIR, pathname === '/' ? 'index.html' : pathname);
  if (!fs.existsSync(filePath)) {
    filePath = path.join(PUBLIC_DIR, 'index.html');
  }

  const ext = path.extname(filePath);
  const mimeTypes = {
    '.html': 'text/html',
    '.css': 'text/css',
    '.js': 'application/javascript',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.svg': 'image/svg+xml'
  };

  const contentType = mimeTypes[ext] || 'text/plain';
  fs.readFile(filePath, (err, content) => {
    if (err) {
      res.writeHead(500);
      res.end('Error loading asset');
    } else {
      res.writeHead(200, { 'Content-Type': contentType });
      res.end(content);
    }
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`MUET Transport Web Server running at http://0.0.0.0:${PORT}`);
});
