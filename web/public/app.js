// MUET Point Track Client Application
let state = {
  shuttles: [],
  routes: [],
  stops: [],
  alerts: [],
  boardingPass: null,
  stopRequests: [],
  selectedBusId: 'POINT_27',
  activeShift: 'MORNING'
};

// Canvas animation pulse
let pulseRadius = 10;
let pulseAlpha = 0.8;
let pulseGrowing = true;

// DOM Ready
document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  setupModals();
  fetchInitialData();

  // Polling loop
  setInterval(fetchStatus, 3000);

  // Map Animation Loop
  requestAnimationFrame(drawMapLoop);
});

// Navigation Tabs
function setupNavigation() {
  const navBtns = document.querySelectorAll('.nav-btn');
  navBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      navBtns.forEach(b => b.classList.remove('active'));
      document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));

      btn.classList.add('active');
      const targetId = btn.getAttribute('data-tab');
      document.getElementById(targetId)?.classList.add('active');
    });
  });

  // Driver point select listener
  const driverSelect = document.getElementById('driverPointSelect');
  if (driverSelect) {
    driverSelect.addEventListener('change', (e) => {
      state.selectedBusId = e.target.value;
      updateDriverView();
    });
  }
}

// Fetch Status from Backend REST API
async function fetchStatus() {
  try {
    const res = await fetch('/api/status');
    if (!res.ok) return;
    const data = await res.json();
    state.shuttles = data.shuttles || [];
    state.routes = data.routes || [];
    state.stops = data.stops || [];
    state.alerts = data.alerts || [];
    state.boardingPass = data.boardingPass;
    state.stopRequests = data.stopRequests || [];

    renderActiveBuses();
    renderSelectedBusDetails();
    updateDriverView();
    renderPass();
    renderAlerts();
    renderAdmin();
  } catch (err) {
    console.error('Fetch error:', err);
  }
}

async function fetchInitialData() {
  await fetchStatus();
  renderRoutes();
}

// Render Bus Cards
function renderActiveBuses() {
  const container = document.getElementById('busList');
  if (!container) return;

  const countBadge = document.getElementById('activePointCount');
  if (countBadge) {
    countBadge.textContent = `${state.shuttles.length} On Route`;
  }

  container.innerHTML = state.shuttles.map(bus => {
    const isSelected = bus.id === state.selectedBusId;
    const available = Math.max(0, bus.totalSeats - bus.occupiedSeats);
    const pct = Math.min(100, Math.round((bus.occupiedSeats / bus.totalSeats) * 100));

    let statusClass = 'available';
    let statusText = `${available} SEATS FREE`;
    if (available === 0) {
      statusClass = 'full';
      statusText = 'BUS FULL';
    } else if (available <= 8) {
      statusClass = 'moderate';
      statusText = 'FEW SEATS';
    }

    return `
      <div class="bus-chip ${isSelected ? 'selected' : ''}" onclick="selectBus('${bus.id}')">
        <div class="bus-chip-top">
          <span class="bus-num">${bus.pointNumber}</span>
          <span class="bus-status-tag ${statusClass}">${statusText}</span>
        </div>
        <div class="bus-route-sub">${bus.routeName}</div>
        <div class="bus-bar-track">
          <div class="bus-bar-fill ${statusClass}" style="width: ${pct}%;"></div>
        </div>
        <div class="bus-chip-bottom">
          <span>Stop: <strong>${bus.currentStopName}</strong></span>
          <span>${bus.speedKmph} km/h</span>
        </div>
      </div>
    `;
  }).join('');
}

function selectBus(id) {
  state.selectedBusId = id;
  const driverSelect = document.getElementById('driverPointSelect');
  if (driverSelect) driverSelect.value = id;
  renderActiveBuses();
  renderSelectedBusDetails();
  updateDriverView();
}

// Render Selected Bus Details
function renderSelectedBusDetails() {
  const container = document.getElementById('selectedBusDetails');
  if (!container) return;

  const bus = state.shuttles.find(b => b.id === state.selectedBusId) || state.shuttles[0];
  if (!bus) return;

  const available = Math.max(0, bus.totalSeats - bus.occupiedSeats);

  container.innerHTML = `
    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
      <div>
        <h3 style="font-size: 1.3rem; color: #0B2E60; font-weight: 800;">${bus.pointNumber} — ${bus.busPlate}</h3>
        <p style="color: #64748B; font-size: 0.9rem;">${bus.routeName}</p>
      </div>
      <div style="background: #EFF6FF; padding: 0.4rem 0.85rem; border-radius: 8px; font-weight: 700; color: #0B2E60;">
        ⚡ ${bus.speedKmph} km/h • Live GPS Active
      </div>
    </div>

    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin: 1.25rem 0;">
      <div style="background: #F8FAFC; padding: 1rem; border-radius: 12px; border: 1px solid #E2E8F0;">
        <span style="font-size: 0.75rem; color: #64748B; font-weight: 700;">CURRENT STOP</span>
        <div style="font-size: 1.15rem; font-weight: 800; color: #0B2E60;">${bus.currentStopName}</div>
        <div style="font-size: 0.8rem; color: #64748B;">Next: ${bus.nextStopName}</div>
      </div>
      <div style="background: #F8FAFC; padding: 1rem; border-radius: 12px; border: 1px solid #E2E8F0;">
        <span style="font-size: 0.75rem; color: #64748B; font-weight: 700;">SEAT OCCUPANCY</span>
        <div style="font-size: 1.15rem; font-weight: 800; color: ${available === 0 ? '#EF4444' : '#10B981'};">
          ${bus.occupiedSeats} / ${bus.totalSeats} seats (${available} free)
        </div>
        <div style="font-size: 0.8rem; color: #64748B;">+ ${bus.standingPassengers} standing</div>
      </div>
      <div style="background: #F8FAFC; padding: 1rem; border-radius: 12px; border: 1px solid #E2E8F0;">
        <span style="font-size: 0.75rem; color: #64748B; font-weight: 700;">ASSIGNED DRIVER</span>
        <div style="font-size: 1.1rem; font-weight: 800; color: #0F172A;">${bus.driverName}</div>
        <div style="font-size: 0.8rem; color: #0B2E60; font-weight: 600;">📞 ${bus.driverPhone}</div>
      </div>
    </div>

    <button class="btn btn-primary" onclick="openRequestModalFor('${bus.id}')" style="width: 100%;">
      🚏 Request Pickup / Drop-off on ${bus.pointNumber}
    </button>
  `;
}

// Canvas Map Animation Loop
function drawMapLoop() {
  const canvas = document.getElementById('hyderabadMapCanvas');
  if (canvas) {
    const ctx = canvas.getContext('2d');
    drawMap(canvas, ctx);
  }

  // Animate pulse
  if (pulseGrowing) {
    pulseRadius += 0.2;
    pulseAlpha -= 0.015;
    if (pulseRadius >= 20) pulseGrowing = false;
  } else {
    pulseRadius = 8;
    pulseAlpha = 0.8;
    pulseGrowing = true;
  }

  requestAnimationFrame(drawMapLoop);
}

// Draw Hyderabad & Jamshoro Transit Corridor
function drawMap(canvas, ctx) {
  const w = canvas.width;
  const h = canvas.height;

  // Background Terrain
  ctx.fillStyle = '#E8EEF5';
  ctx.fillRect(0, 0, w, h);

  // 1. Draw River Indus (Sindhu Darya)
  ctx.fillStyle = '#B0D5F7';
  ctx.beginPath();
  ctx.moveTo(w * 0.40, 0);
  ctx.bezierCurveTo(w * 0.44, h * 0.28, w * 0.36, h * 0.65, w * 0.45, h);
  ctx.lineTo(w * 0.54, h);
  ctx.bezierCurveTo(w * 0.44, h * 0.65, w * 0.52, h * 0.28, w * 0.48, 0);
  ctx.closePath();
  ctx.fill();

  // River water waves
  ctx.strokeStyle = '#93C5FD';
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.moveTo(w * 0.43, h * 0.3);
  ctx.lineTo(w * 0.45, h * 0.3);
  ctx.moveTo(w * 0.41, h * 0.7);
  ctx.lineTo(w * 0.44, h * 0.7);
  ctx.stroke();

  // 2. Jamshoro Bridges (Connecting Hyderabad & Jamshoro)
  ctx.fillStyle = '#475569';
  ctx.fillRect(w * 0.37, h * 0.48, w * 0.16, 8);
  ctx.fillStyle = '#64748B';
  ctx.fillRect(w * 0.38, h * 0.52, w * 0.14, 5);

  // 3. Campus Boundary (Jamshoro west side)
  ctx.fillStyle = 'rgba(167, 243, 208, 0.45)';
  ctx.beginPath();
  ctx.roundRect(w * 0.05, h * 0.22, w * 0.24, h * 0.48, 16);
  ctx.fill();

  ctx.fillStyle = '#065F46';
  ctx.font = 'bold 10px Plus Jakarta Sans';
  ctx.fillText('MUET JAMSHORO CAMPUS', w * 0.07, h * 0.28);

  // 4. Main Transit Corridors (M-9 Bypass, Auto Bhan, Qasimabad)
  ctx.strokeStyle = '#CBD5E1';
  ctx.lineWidth = 14;
  ctx.beginPath();
  ctx.moveTo(w * 0.90, h * 0.85); // Latifabad Unit 7
  ctx.lineTo(w * 0.78, h * 0.72); // Auto Bhan
  ctx.lineTo(w * 0.68, h * 0.56); // Qasim Chowk
  ctx.lineTo(w * 0.60, h * 0.50); // Naseem Nagar
  ctx.lineTo(w * 0.45, h * 0.49); // Jamshoro Bridge
  ctx.lineTo(w * 0.32, h * 0.46); // Jamshoro Toll
  ctx.lineTo(w * 0.14, h * 0.40); // MUET Campus
  ctx.stroke();

  ctx.strokeStyle = '#0B2E60';
  ctx.lineWidth = 4;
  ctx.stroke();

  // 5. Draw Scheduled Stops
  const minLat = 25.3500;
  const maxLat = 25.4400;
  const minLng = 68.2500;
  const maxLng = 68.3800;

  state.stops.forEach(stop => {
    const x = ((stop.longitude - minLng) / (maxLng - minLng)) * w;
    const y = (1 - ((stop.latitude - minLat) / (maxLat - minLat))) * h;

    ctx.fillStyle = 'white';
    ctx.beginPath();
    ctx.arc(x, y, 6, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = stop.isCampusStop ? '#F59E0B' : '#0B2E60';
    ctx.beginPath();
    ctx.arc(x, y, 4, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = '#334155';
    ctx.font = '9px Plus Jakarta Sans';
    ctx.fillText(stop.stopName, x + 8, y + 3);
  });

  // 6. Draw Moving Point Buses
  state.shuttles.forEach(bus => {
    const x = ((bus.longitude - minLng) / (maxLng - minLng)) * w;
    const y = (1 - ((bus.latitude - minLat) / (maxLat - minLat))) * h;
    const isSelected = bus.id === state.selectedBusId;

    // Glowing Pulse
    ctx.strokeStyle = isSelected ? `rgba(245, 158, 11, ${pulseAlpha})` : `rgba(30, 136, 229, ${pulseAlpha})`;
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.arc(x, y, pulseRadius * (isSelected ? 1.4 : 1), 0, Math.PI * 2);
    ctx.stroke();

    // Bus Card Marker
    const mw = isSelected ? 48 : 40;
    const mh = isSelected ? 24 : 20;

    ctx.fillStyle = isSelected ? '#0B2E60' : 'white';
    ctx.strokeStyle = isSelected ? '#F59E0B' : '#0B2E60';
    ctx.lineWidth = isSelected ? 3 : 2;

    ctx.beginPath();
    ctx.roundRect(x - mw / 2, y - mh / 2, mw, mh, 6);
    ctx.fill();
    ctx.stroke();

    ctx.fillStyle = isSelected ? 'white' : '#0B2E60';
    ctx.font = `bold ${isSelected ? '12px' : '10px'} Plus Jakarta Sans`;
    ctx.textAlign = 'center';
    ctx.fillText(bus.pointNumber.replace('Point ', 'P-'), x, y + 4);
    ctx.textAlign = 'start';
  });
}

// Driver Seat Controls
async function updateDriverSeats(deltaSeats, deltaStanding) {
  try {
    const res = await fetch('/api/driver/seats', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        pointId: state.selectedBusId,
        deltaSeats,
        deltaStanding
      })
    });
    if (res.ok) fetchStatus();
  } catch (err) {
    console.error(err);
  }
}

async function setDriverPreset(preset) {
  try {
    const res = await fetch('/api/driver/seats', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        pointId: state.selectedBusId,
        preset
      })
    });
    if (res.ok) fetchStatus();
  } catch (err) {
    console.error(err);
  }
}

async function advanceDriverStop() {
  try {
    const res = await fetch('/api/driver/advance', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pointId: state.selectedBusId })
    });
    if (res.ok) fetchStatus();
  } catch (err) {
    console.error(err);
  }
}

// Update Driver View
function updateDriverView() {
  const bus = state.shuttles.find(b => b.id === state.selectedBusId) || state.shuttles[0];
  if (!bus) return;

  const available = Math.max(0, bus.totalSeats - bus.occupiedSeats);
  const pct = Math.min(100, Math.round((bus.occupiedSeats / bus.totalSeats) * 100));

  const busName = document.getElementById('driverBusName');
  const routeName = document.getElementById('driverRouteName');
  const occSeats = document.getElementById('driverOccupiedSeats');
  const availSeats = document.getElementById('driverAvailableSeats');
  const standingSeats = document.getElementById('driverStandingSeats');
  const fill = document.getElementById('driverSeatFill');
  const curStop = document.getElementById('driverCurrentStop');
  const nxtStop = document.getElementById('driverNextStop');

  if (busName) busName.textContent = `${bus.pointNumber} (${bus.busPlate})`;
  if (routeName) routeName.textContent = bus.routeName;
  if (occSeats) occSeats.textContent = bus.occupiedSeats;
  if (availSeats) availSeats.textContent = available;
  if (standingSeats) standingSeats.textContent = bus.standingPassengers;
  if (fill) fill.style.width = `${pct}%`;
  if (curStop) curStop.textContent = bus.currentStopName;
  if (nxtStop) nxtStop.textContent = bus.nextStopName;

  // Requests feed for this bus
  const reqList = document.getElementById('driverRequestList');
  const reqCount = document.getElementById('driverRequestCount');
  const busReqs = state.stopRequests.filter(r => r.pointId === state.selectedBusId);

  if (reqCount) reqCount.textContent = `${busReqs.length} Requests`;
  if (reqList) {
    if (busReqs.length === 0) {
      reqList.innerHTML = '<p style="color: #64748B; font-size: 0.85rem; padding: 1rem 0;">No pending passenger pickup requests.</p>';
    } else {
      reqList.innerHTML = busReqs.map(r => `
        <div style="background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 10px; padding: 0.9rem; margin-bottom: 0.5rem; display: flex; justify-content: space-between; align-items: center;">
          <div>
            <strong>${r.studentName}</strong> (${r.studentRollNo})
            <div style="font-size: 0.8rem; color: #0B2E60; font-weight: 700;">${r.requestType} at ${r.stopName}</div>
            ${r.note ? `<div style="font-size: 0.75rem; color: #64748B;">"${r.note}"</div>` : ''}
          </div>
          <div>
            ${r.status !== 'COMPLETED' ? `
              <button class="btn btn-secondary" style="padding: 4px 8px; font-size: 0.75rem;" onclick="updateRequestStatus(${r.id}, 'ACKNOWLEDGED')">Acknowledge</button>
              <button class="btn btn-primary" style="padding: 4px 8px; font-size: 0.75rem; background: #10B981;" onclick="updateRequestStatus(${r.id}, 'COMPLETED')">Boarded ✓</button>
            ` : '<span style="color: #10B981; font-weight: 700; font-size: 0.8rem;">Completed ✓</span>'}
          </div>
        </div>
      `).join('');
    }
  }
}

async function updateRequestStatus(id, status) {
  try {
    await fetch('/api/requests/update', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id, status })
    });
    fetchStatus();
  } catch (err) {
    console.error(err);
  }
}

// Render Pass & Dynamic QR Code
function renderPass() {
  const p = state.boardingPass;
  if (!p) return;

  const sName = document.getElementById('passStudentName');
  const rNo = document.getElementById('passRollNo');
  const dept = document.getElementById('passDept');
  const rName = document.getElementById('passRouteName');
  const pStop = document.getElementById('passStop');
  const pToken = document.getElementById('passToken');
  const pExpiry = document.getElementById('passExpiry');

  if (sName) sName.textContent = p.studentName;
  if (rNo) rNo.textContent = p.studentRollNo;
  if (dept) dept.textContent = p.department;
  if (rName) rName.textContent = p.routeName;
  if (pStop) pStop.textContent = p.preferredStop;
  if (pToken) pToken.textContent = p.feeReceiptNo;
  if (pExpiry) pExpiry.textContent = p.validUntil;

  drawQRCode();
}

function drawQRCode() {
  const canvas = document.getElementById('qrCodeCanvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  const s = canvas.width;
  const cells = 15;
  const cs = s / cells;

  ctx.fillStyle = '#FFFFFF';
  ctx.fillRect(0, 0, s, s);

  // Finder Patterns
  const finders = [[0, 0], [cells - 5, 0], [0, cells - 5]];
  finders.forEach(([fx, fy]) => {
    ctx.fillStyle = '#0F172A';
    ctx.fillRect(fx * cs, fy * cs, 5 * cs, 5 * cs);
    ctx.fillStyle = '#FFFFFF';
    ctx.fillRect((fx + 1) * cs, (fy + 1) * cs, 3 * cs, 3 * cs);
    ctx.fillStyle = '#0F172A';
    ctx.fillRect((fx + 2) * cs, (fy + 2) * cs, 1 * cs, 1 * cs);
  });

  // Seeded matrix bits
  for (let r = 0; r < cells; r++) {
    for (let c = 0; c < cells; c++) {
      const inTL = r < 5 && c < 5;
      const inTR = r < 5 && c >= cells - 5;
      const inBL = r >= cells - 5 && c < 5;
      if (!inTL && !inTR && !inBL) {
        if (((r * 19 + c * 31) % 3) === 0) {
          ctx.fillStyle = '#0F172A';
          ctx.fillRect(c * cs, r * cs, cs - 1, cs - 1);
        }
      }
    }
  }
}

// Render Routes
function renderRoutes() {
  const container = document.getElementById('routesGrid');
  if (!container) return;

  container.innerHTML = state.routes.map(r => `
    <div class="route-card">
      <span class="route-badge">${r.code}</span>
      <h3 style="font-size: 1.1rem; color: #0B2E60; font-weight: 800; margin-bottom: 0.35rem;">${r.routeName}</h3>
      <p style="color: #64748B; font-size: 0.8rem; margin-bottom: 0.75rem;">${r.startLocation} → ${r.endLocation}</p>

      <div style="background: #F8FAFC; padding: 0.75rem; border-radius: 8px; font-size: 0.8rem; margin-bottom: 0.75rem;">
        <div>⏰ <strong>Morning:</strong> ${r.morningDeparture}</div>
        <div>☀️ <strong>Return:</strong> ${r.afternoonDeparture}</div>
        <div>🌆 <strong>Evening:</strong> ${r.eveningDeparture}</div>
      </div>

      <div style="font-size: 0.75rem; color: #64748B;">
        <span>🛣️ ${r.distanceKm} km</span> • <span>⏱️ ~${r.estimatedMinutes} mins</span> • <span>Via: ${r.via}</span>
      </div>
    </div>
  `).join('');
}

function switchShift(shift) {
  state.activeShift = shift;
  document.querySelectorAll('.shift-chip').forEach(c => c.classList.remove('active'));
  event.target.classList.add('active');
}

// Render Alerts & Ticker
function renderAlerts() {
  const ticker = document.getElementById('tickerText');
  if (state.alerts.length > 0 && ticker) {
    ticker.textContent = `${state.alerts[0].pointNumber}: ${state.alerts[0].message}`;
  }
}

// Render Admin Roster
function renderAdmin() {
  const tbody = document.getElementById('adminFleetTableBody');
  if (!tbody) return;

  tbody.innerHTML = state.shuttles.map(bus => `
    <tr>
      <td><strong>${bus.pointNumber}</strong></td>
      <td>${bus.busPlate}</td>
      <td>${bus.routeName}</td>
      <td>${bus.driverName}</td>
      <td>${bus.currentStopName} → ${bus.nextStopName}</td>
      <td><strong>${bus.occupiedSeats}/${bus.totalSeats}</strong></td>
      <td>${bus.speedKmph} km/h</td>
      <td><span style="color: #10B981; font-weight: 700;">● ${bus.status}</span></td>
    </tr>
  `).join('');
}

// Modals Setup
function setupModals() {
  const reqModal = document.getElementById('requestModal');
  const openReqBtn = document.getElementById('openRequestModalBtn');
  const closeReqBtn = document.getElementById('closeRequestModalBtn');
  const submitReqBtn = document.getElementById('submitRequestBtn');

  if (openReqBtn && reqModal) {
    openReqBtn.onclick = () => reqModal.classList.add('active');
  }
  if (closeReqBtn && reqModal) {
    closeReqBtn.onclick = () => reqModal.classList.remove('active');
  }

  if (submitReqBtn) {
    submitReqBtn.onclick = async () => {
      const pointId = document.getElementById('modalPointSelect').value;
      const stopName = document.getElementById('modalStopSelect').value;
      const typeRadio = document.querySelector('input[name="reqType"]:checked');
      const requestType = typeRadio ? typeRadio.value : 'PICKUP';
      const note = document.getElementById('modalNoteInput').value;

      await fetch('/api/student/request', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pointId, stopName, requestType, note })
      });

      reqModal.classList.remove('active');
      fetchStatus();
      alert(`Request submitted for ${stopName}! The driver has been alerted.`);
    };
  }

  const bModal = document.getElementById('broadcastModal');
  const openBBtn = document.getElementById('openBroadcastModalBtn');
  const closeBBtn = document.getElementById('closeBroadcastModalBtn');
  const submitBBtn = document.getElementById('submitBroadcastBtn');

  if (openBBtn && bModal) {
    openBBtn.onclick = () => bModal.classList.add('active');
  }
  if (closeBBtn && bModal) {
    closeBBtn.onclick = () => bModal.classList.remove('active');
  }

  if (submitBBtn) {
    submitBBtn.onclick = async () => {
      const title = document.getElementById('broadcastTitleInput').value;
      const message = document.getElementById('broadcastMsgInput').value;

      await fetch('/api/driver/alert', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          pointNumber: state.selectedBusId.replace('_', ' '),
          title,
          message
        })
      });

      bModal.classList.remove('active');
      fetchStatus();
      alert('Alert successfully broadcasted across university network!');
    };
  }
}

function openRequestModalFor(busId) {
  state.selectedBusId = busId;
  const sel = document.getElementById('modalPointSelect');
  if (sel) sel.value = busId;
  document.getElementById('requestModal')?.classList.add('active');
}

function setBroadcastPreset(title, msg) {
  const t = document.getElementById('broadcastTitleInput');
  const m = document.getElementById('broadcastMsgInput');
  if (t) t.value = title;
  if (m) m.value = msg;
}

function triggerSos() {
  alert('🚨 SOS Signal Transmitted: Transport Directorate Control Office at Jamshoro Campus alerted with your live location.');
}
