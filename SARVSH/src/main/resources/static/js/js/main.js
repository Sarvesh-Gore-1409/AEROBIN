const bins = [
    { id: "BIN-104", area: "Sector 4 Market", x: 30, y: 35, fill: 88, odour: "Poor", time: "45 mins" },
    { id: "BIN-202", area: "Central Park Gate", x: 65, y: 25, fill: 65, odour: "Moderate", time: "3 hours" },
    { id: "BIN-305", area: "Railway Station", x: 80, y: 60, fill: 92, odour: "Poor", time: "20 mins" },
    { id: "BIN-109", area: "City Hospital", x: 45, y: 75, fill: 20, odour: "Good", time: "Safe" },
    { id: "BIN-412", area: "School District", x: 20, y: 15, fill: 45, odour: "Moderate", time: "5 hours" }
];

const depot = { x: 10, y: 90 }; // Fixed depot location in %

document.addEventListener('DOMContentLoaded', () => {
    renderList();
    renderMapMarkers();
});

function renderList() {
    const listContainer = document.getElementById('bin-list');
    listContainer.innerHTML = '';

    bins.forEach(bin => {
        const item = document.createElement('div');
        item.className = 'bin-item';
        item.dataset.id = bin.id;
        item.onclick = () => selectBin(bin.id);

        const statusClass = bin.odour === 'Good' ? 'status-good' : (bin.odour === 'Moderate' ? 'status-moderate' : 'status-poor');

        item.innerHTML = `
            <div class="bin-header">
                <span class="bin-id">${bin.id}</span>
                <span class="status-badge ${statusClass}">${bin.odour}</span>
            </div>
            <div style="font-size: 0.9rem; margin-bottom: 0.5rem; font-weight: 500;">${bin.area}</div>
            <div class="bin-stats">
                <span>Fill: <strong>${bin.fill}%</strong></span>
                <span>Est: ${bin.time}</span>
            </div>
            ${bin.fill > 80 ? `<button class="collect-btn" onclick="event.stopPropagation(); selectBin('${bin.id}');">⚠️ Alert: Collect Now</button>` : ''}
        `;
        listContainer.appendChild(item);
    });
}

function renderMapMarkers() {
    const map = document.getElementById('map');

    bins.forEach(bin => {
        const marker = document.createElement('div');
        marker.className = `dustbin-marker ${bin.fill > 80 ? 'alert' : ''}`;
        marker.style.left = `${bin.x}%`;
        marker.style.top = `${bin.y}%`;
        marker.title = `${bin.id} - ${bin.area}`;
        marker.dataset.id = bin.id;
        marker.onclick = () => selectBin(bin.id);

        map.appendChild(marker);
    });
}

function selectBin(id) {
    const selectedBin = bins.find(b => b.id === id);
    if (!selectedBin) return;

    // Highlight List
    document.querySelectorAll('.bin-item').forEach(el => el.classList.remove('active'));
    document.querySelector(`.bin-item[data-id="${id}"]`)?.classList.add('active');

    // Draw Route
    drawRoute(selectedBin);

    // Show Card
    showRouteInfo(selectedBin);
}

function drawRoute(bin) {
    const svg = document.getElementById('route-layer');
    svg.innerHTML = ''; // Clear previous routes

    // Quadratic Bezier Curve for smoother path
    // Control point to simulate an arc/turn
    // We'll vary the control point based on relative position to make it look 'routed'
    const controlX = depot.x;
    const controlY = bin.y;

    const path = document.createElementNS("http://www.w3.org/2000/svg", "path");

    // Path: Depot -> Quadratic Curve -> Destination 
    const d = `M ${depot.x} ${depot.y} Q ${controlX} ${controlY} ${bin.x} ${bin.y}`;

    path.setAttribute("d", d);
    path.setAttribute("stroke", "#2563eb");
    path.setAttribute("stroke-width", "4");
    path.setAttribute("fill", "none");
    path.setAttribute("stroke-dasharray", "15");
    path.setAttribute("stroke-linecap", "round");
    path.setAttribute("filter", "drop-shadow(0 0 4px rgba(37, 99, 235, 0.5))");

    // Animate the line
    const animate = document.createElementNS("http://www.w3.org/2000/svg", "animate");
    animate.setAttribute("attributeName", "stroke-dashoffset");
    animate.setAttribute("from", "100");
    animate.setAttribute("to", "0");
    animate.setAttribute("dur", "1.2s");
    animate.setAttribute("fill", "freeze");
    animate.setAttribute("calcMode", "spline");
    animate.setAttribute("keySplines", "0.4 0 0.2 1");

    path.appendChild(animate);
    svg.appendChild(path);
}

function showRouteInfo(bin) {
    const card = document.getElementById('route-card');
    const distEl = document.getElementById('route-dist');
    const timeEl = document.getElementById('route-time');
    const priorityEl = document.getElementById('route-priority');

    // Mock calculations
    const dist = Math.sqrt(Math.pow(bin.x - depot.x, 2) + Math.pow(bin.y - depot.y, 2)) * 0.1; // roughly km
    const time = dist * 3; // roughly mins per km units

    distEl.innerText = `${dist.toFixed(1)} km`;
    timeEl.innerText = `${Math.ceil(time)} mins`;

    if (bin.fill > 80 || bin.odour === 'Poor') {
        priorityEl.innerText = 'High';
        priorityEl.style.color = 'var(--danger)';
    } else if (bin.fill > 50) {
        priorityEl.innerText = 'Medium';
        priorityEl.style.color = 'var(--warning)';
    } else {
        priorityEl.innerText = 'Low';
        priorityEl.style.color = 'var(--success)';
    }

    card.classList.add('visible');
}

/* --- New Functionalities --- */

// Search/Filter Functionality
function filterBins(query) {
    const term = query.toLowerCase();
    const items = document.querySelectorAll('.bin-item');

    items.forEach(item => {
        const id = item.dataset.id.toLowerCase();
        // Since we don't have the area in the DOM directly searchable easily (it is there but let's just stick to ID for robust demo, 
        // OR we can search inner text which covers ID and Area)
        const text = item.innerText.toLowerCase();

        if (text.includes(term)) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
}

// Dispatch Action
function dispatchTruck() {
    const card = document.getElementById('route-card');

    // 1. Close Card
    card.classList.remove('visible');

    // 2. Clear Map Route
    const svg = document.getElementById('route-layer');
    svg.innerHTML = '';

    // 3. Show Success Message
    showToast("Truck Dispatch Initiated Successfully!", "success");

    // 4. Reset selected items in list
    document.querySelectorAll('.bin-item').forEach(el => el.classList.remove('active'));

    // Optional: Simulate status update
    setTimeout(() => {
        showToast("Truck #42 is en route to location.", "info");
    }, 2000);
}

// Toast Notification System
function showToast(message, type = "info") {
    const container = document.getElementById('toast-container');

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    // Icons based on type
    let icon = '';
    if (type === 'success') icon = '✔';
    if (type === 'error') icon = '✖';
    if (type === 'info') icon = 'ℹ';

    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;

    container.appendChild(toast);

    // Remove after 3 seconds
    setTimeout(() => {
        toast.style.animation = 'toastOut 0.4s forwards';
        toast.addEventListener('animationend', () => {
            if (container.contains(toast)) {
                container.removeChild(toast);
            }
        });
    }, 3000);
}
