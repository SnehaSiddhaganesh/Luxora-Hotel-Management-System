/**
 * LUXORA — Admin & Receptionist Management Control Panel Script
 * Customer accounts and reservations load from the secured Spring Boot APIs.
 */

// Mock Datasets
let ADMIN_ROOMS = [
    { id: '101', roomNum: '101', category: 'Standard Room', price: 1500, capacity: 2, bed: 'Queen Bed', status: 'Available', badge: 'bg-success' },
    { id: '201', roomNum: '201', category: 'Deluxe Room', price: 2500, capacity: 2, bed: 'King Bed', status: 'Occupied', badge: 'bg-warning text-dark' },
    { id: '304', roomNum: '304', category: 'Premium Oceanfront', price: 3500, capacity: 3, bed: 'King Bed + Sofa', status: 'Maintenance', badge: 'bg-danger' },
    { id: '402', roomNum: '402', category: 'Executive Suite', price: 5000, capacity: 3, bed: 'Super King Bed', status: 'Occupied', badge: 'bg-warning text-dark' },
    { id: '501', roomNum: '501', category: 'Presidential Royal Suite', price: 10000, capacity: 4, bed: 'Dual King Beds', status: 'Occupied', badge: 'bg-warning text-dark' }
];

let ADMIN_BOOKINGS = [];

let ADMIN_FOOD_ITEMS = [
    { id: 'f1', name: 'Masala Dosa', category: 'Breakfast', price: 180, veg: '100% Veg', available: true },
    { id: 'f2', name: 'Paneer Butter Masala', category: 'Main Course', price: 340, veg: '100% Veg', available: true },
    { id: 'f3', name: 'Charbroiled Paneer Tikka', category: 'Snacks', price: 280, veg: '100% Veg', available: true },
    { id: 'f4', name: 'Warm Gulab Jamun', category: 'Sweet', price: 150, veg: '100% Veg', available: true },
    { id: 'f5', name: 'Filter Coffee', category: 'Beverages', price: 120, veg: '100% Veg', available: true }
];

let ADMIN_FOOD_ORDERS = [
    { orderId: 'ORD-739201', roomNum: '501', guestName: 'Alexander Wright', items: 'Masala Dosa (x2), Paneer Butter Masala (x1)', total: '₹735', status: 'Placed', badge: 'bg-warning text-dark' },
    { orderId: 'ORD-519203', roomNum: '304', guestName: 'Victoria Sterling', items: 'Gourmet Ribeye Steak (x1)', total: '₹1,450', status: 'Preparing', badge: 'bg-info text-dark' },
    { orderId: 'ORD-310492', roomNum: '201', guestName: 'Marcus Vance', items: 'Warm Gulab Jamun (x2), Filter Coffee (x2)', total: '₹540', status: 'Delivered', badge: 'bg-success' }
];

let ADMIN_SERVICE_REQUESTS = [
    { reqId: 'SRV-839201', serviceName: 'Aura Spa & Wellness', roomNum: '501', time: 'ASAP (Immediate)', status: 'Pending', badge: 'bg-warning text-dark' },
    { reqId: 'SRV-419205', serviceName: 'Express Laundry & Pressing', roomNum: '304', time: 'Evening (6:00 PM)', status: 'In-Progress', badge: 'bg-info text-dark' },
    { reqId: 'SRV-210492', serviceName: 'Airport Pickup (Rolls-Royce)', roomNum: '201', time: 'Morning (9:00 AM)', status: 'Completed', badge: 'bg-success' }
];

document.addEventListener('DOMContentLoaded', () => {
    initAdminSidebarNavigation();
    renderAllAdminTables();
    loadAdminCustomerData();
});

/**
 * Handle Sidebar Menu Switching (Dashboard, Rooms, Customers, Bookings, Food Items, Orders, Services, etc.)
 */
function initAdminSidebarNavigation() {
    const navLinks = document.querySelectorAll('.admin-sidebar-link');
    const sections = document.querySelectorAll('.admin-section-pane');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetPaneId = link.getAttribute('data-pane');

            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            sections.forEach(pane => {
                if (pane.id === targetPaneId) {
                    pane.style.display = 'block';
                } else {
                    pane.style.display = 'none';
                }
            });
        });
    });
}

/**
 * Render All Admin Tables
 */
function renderAllAdminTables() {
    renderAdminRoomsTable();
    renderAdminBookingsTable();
    renderAdminFoodItemsTable();
    renderAdminFoodOrdersTable();
    renderAdminServicesTable();
}

async function loadAdminCustomerData() {
    try {
        const response = await fetch('/api/auth/me', { credentials: 'same-origin' });
        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = 'login.html';
                return;
            }
            throw new Error('Could not verify the signed-in account.');
        }

        const user = await response.json();
        if (user.role !== 'ADMIN' && user.role !== 'RECEPTIONIST') {
            window.location.href = 'login.html';
            return;
        }

        await Promise.all([loadAdminCustomers(), loadAdminBookings()]);
    } catch (error) {
        showAdminTableMessage('admin-customers-table', 6, error.message);
        showAdminTableMessage('admin-bookings-table', 7, error.message);
    }
}

async function loadAdminCustomers() {
    const response = await fetch('/api/customers', { credentials: 'same-origin' });
    if (!response.ok) {
        showAdminTableMessage('admin-customers-table', 6, 'Unable to load customer accounts.');
        return;
    }

    const customers = await response.json();
    const tbody = document.querySelector('#admin-customers-table tbody');
    if (!tbody) return;
    tbody.innerHTML = customers.length
        ? customers.map(customer => `
            <tr>
                <td><span class="badge bg-secondary font-monospace">USR-${escapeAdminText(customer.id)}</span></td>
                <td class="fw-bold text-white">${escapeAdminText(`${customer.firstName} ${customer.lastName}`)}</td>
                <td class="small text-muted">${escapeAdminText(customer.email)}</td>
                <td class="small text-muted">${escapeAdminText(customer.phone || '—')}</td>
                <td class="small text-muted">${escapeAdminText(customer.address || '—')}</td>
                <td class="fw-bold text-gold">${customer.bookingIds.length} / ${customer.orderIds.length}</td>
            </tr>
        `).join('')
        : '<tr><td colspan="6" class="text-center text-muted py-4">No customer accounts yet.</td></tr>';
}

async function loadAdminBookings() {
    const response = await fetch('/api/bookings', { credentials: 'same-origin' });
    if (!response.ok) {
        showAdminTableMessage('admin-bookings-table', 7, 'Unable to load customer reservations.');
        return;
    }

    ADMIN_BOOKINGS = await response.json();
    renderAdminBookingsTable();
}

function showAdminTableMessage(tableId, columnCount, message) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    if (tbody) {
        tbody.innerHTML = `<tr><td colspan="${columnCount}" class="text-center text-warning py-4">${escapeAdminText(message)}</td></tr>`;
    }
}

function escapeAdminText(value) {
    const element = document.createElement('span');
    element.textContent = value == null ? '' : String(value);
    return element.innerHTML;
}

/**
 * Render Rooms Inventory Table
 */
function renderAdminRoomsTable() {
    const tbody = document.querySelector('#admin-rooms-table tbody');
    if (!tbody) return;

    tbody.innerHTML = ADMIN_ROOMS.map(r => `
        <tr>
            <td class="fw-bold text-gold font-monospace">Room #${r.roomNum}</td>
            <td class="text-white">${r.category}</td>
            <td class="fw-bold text-white">₹${r.price.toLocaleString()}</td>
            <td class="small text-muted">${r.bed} (Max ${r.capacity})</td>
            <td>
                <span class="badge ${r.badge} cursor-pointer" onclick="toggleRoomStatus('${r.id}')">${r.status}</span>
            </td>
            <td class="text-end">
                <button class="btn btn-sm btn-outline-warning py-0 px-2 me-1" onclick="toggleRoomStatus('${r.id}')" title="Change Housekeeping Status"><i class="fa-solid fa-rotate"></i></button>
                <button class="btn btn-sm btn-outline-danger py-0 px-2" onclick="deleteAdminRoom('${r.id}')" title="Delete Room"><i class="fa-solid fa-trash-can"></i></button>
            </td>
        </tr>
    `).join('');
}

/**
 * Toggle Housekeeping Room Status (Available → Occupied → Maintenance)
 */
function toggleRoomStatus(roomId) {
    const room = ADMIN_ROOMS.find(r => r.id === roomId);
    if (room) {
        if (room.status === 'Available') {
            room.status = 'Occupied';
            room.badge = 'bg-warning text-dark';
        } else if (room.status === 'Occupied') {
            room.status = 'Maintenance';
            room.badge = 'bg-danger';
        } else {
            room.status = 'Available';
            room.badge = 'bg-success';
        }
        renderAdminRoomsTable();
        if (window.showToast) window.showToast(`Room #${room.roomNum} status updated to ${room.status}`, 'Room Inventory');
    }
}

/**
 * Add New Room Form Submit Handler
 */
function handleAddRoomSubmit(e) {
    e.preventDefault();
    const roomNum = document.getElementById('new-room-num')?.value;
    const category = document.getElementById('new-room-category')?.value;
    const price = parseInt(document.getElementById('new-room-price')?.value, 10);
    const capacity = parseInt(document.getElementById('new-room-capacity')?.value, 10);

    if (!roomNum || !price) return;

    const newRoom = {
        id: roomNum,
        roomNum: roomNum,
        category: category,
        price: price,
        capacity: capacity,
        bed: 'King Bed',
        status: 'Available',
        badge: 'bg-success'
    };

    ADMIN_ROOMS.push(newRoom);
    renderAdminRoomsTable();

    const modalElem = document.getElementById('addRoomModal');
    if (modalElem) bootstrap.Modal.getInstance(modalElem).hide();

    if (window.showToast) window.showToast(`Room #${roomNum} added to inventory!`, 'Inventory Saved');
}

/**
 * Delete Room
 */
function deleteAdminRoom(roomId) {
    ADMIN_ROOMS = ADMIN_ROOMS.filter(r => r.id !== roomId);
    renderAdminRoomsTable();
    if (window.showToast) window.showToast('Room removed from inventory.', 'Delete Success');
}

/**
 * Render Bookings Table
 */
function renderAdminBookingsTable() {
    const tbody = document.querySelector('#admin-bookings-table tbody');
    if (!tbody) return;

    const statuses = ['PENDING', 'CONFIRMED', 'PAID', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED'];
    tbody.innerHTML = ADMIN_BOOKINGS.length ? ADMIN_BOOKINGS.map(b => `
        <tr>
            <td><span class="badge bg-secondary font-monospace">${escapeAdminText(b.bookingCode)}</span></td>
            <td>
                <div class="fw-bold text-white">${escapeAdminText(b.customerName)}</div>
                <div class="small text-muted">Customer #${escapeAdminText(b.customerId)} · ${b.numberOfGuests} guest(s)</div>
            </td>
            <td><span class="text-gold fw-bold">Room #${escapeAdminText(b.roomNumber || '—')}</span></td>
            <td class="small text-muted">${escapeAdminText(b.checkInDate)} → ${escapeAdminText(b.checkOutDate)}</td>
            <td class="fw-bold text-white">${new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(b.total)}</td>
            <td class="small text-muted">${escapeAdminText(b.specialRequests || 'No special request')}</td>
            <td>
                <select class="lux-input py-0 px-1 small" aria-label="Booking status" data-booking-status="${b.id}">
                    ${statuses.map(status => `<option value="${status}" ${b.status === status ? 'selected' : ''}>${status.replaceAll('_', ' ')}</option>`).join('')}
                </select>
            </td>
        </tr>
    `).join('') : '<tr><td colspan="7" class="text-center text-muted py-4">No customer reservations have been submitted.</td></tr>';
}

async function updateBookingStatus(bookingId, newStatus) {
    try {
        const csrfResponse = await fetch('/api/auth/csrf', { credentials: 'same-origin' });
        if (!csrfResponse.ok) throw new Error('Could not prepare a secure request. Reload and try again.');
        const csrf = await csrfResponse.json();
        const response = await fetch(`/api/bookings/${bookingId}/status`, {
            method: 'PATCH',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                [csrf.headerName]: csrf.token
            },
            body: JSON.stringify({ status: newStatus })
        });
        if (!response.ok) throw new Error('Could not update the booking status.');
        const updatedBooking = await response.json();
        ADMIN_BOOKINGS = ADMIN_BOOKINGS.map(booking => booking.id === updatedBooking.id ? updatedBooking : booking);
        renderAdminBookingsTable();
        if (window.showToast) window.showToast('Customer reservation updated.', 'Reception Status');
    } catch (error) {
        if (window.showToast) window.showToast(error.message, 'Reception Status');
        await loadAdminBookings();
    }
}

document.addEventListener('change', event => {
    const select = event.target.closest('[data-booking-status]');
    if (select) updateBookingStatus(select.dataset.bookingStatus, select.value);
});

/**
 * Render Food Items Table
 */
function renderAdminFoodItemsTable() {
    const tbody = document.querySelector('#admin-food-table tbody');
    if (!tbody) return;

    tbody.innerHTML = ADMIN_FOOD_ITEMS.map(f => `
        <tr>
            <td class="fw-bold text-white">${f.name}</td>
            <td><span class="badge bg-dark border border-secondary text-gold font-sans">${f.category}</span></td>
            <td class="fw-bold text-gold">₹${f.price}</td>
            <td><span class="badge bg-dark text-success border border-secondary"><i class="fa-solid fa-circle text-success me-1"></i> ${f.veg}</span></td>
            <td>
                <span class="badge ${f.available ? 'bg-success' : 'bg-danger'} cursor-pointer" onclick="toggleFoodAvailability('${f.id}')">
                    ${f.available ? 'In Stock' : 'Out of Stock'}
                </span>
            </td>
            <td class="text-end">
                <button class="btn btn-sm btn-outline-warning py-0 px-2" onclick="toggleFoodAvailability('${f.id}')"><i class="fa-solid fa-rotate"></i></button>
            </td>
        </tr>
    `).join('');
}

function toggleFoodAvailability(foodId) {
    const item = ADMIN_FOOD_ITEMS.find(f => f.id === foodId);
    if (item) {
        item.available = !item.available;
        renderAdminFoodItemsTable();
        if (window.showToast) window.showToast(`"${item.name}" availability updated.`, 'Kitchen Stock');
    }
}

/**
 * Render Food Orders Table
 */
function renderAdminFoodOrdersTable() {
    const tbody = document.querySelector('#admin-food-orders-table tbody');
    if (!tbody) return;

    tbody.innerHTML = ADMIN_FOOD_ORDERS.map(o => `
        <tr>
            <td><span class="badge bg-secondary font-monospace">${o.orderId}</span></td>
            <td class="fw-bold text-gold">Room #${o.roomNum}</td>
            <td class="text-white">${o.guestName}</td>
            <td class="small text-muted">${o.items}</td>
            <td class="fw-bold text-white">${o.total}</td>
            <td><span class="badge ${o.badge}">${o.status}</span></td>
            <td class="text-end">
                <select class="lux-input py-0 px-1 small" style="width: auto; display: inline-block;" onchange="updateFoodOrderStatus('${o.orderId}', this.value)">
                    <option value="Placed" ${o.status === 'Placed' ? 'selected' : ''}>Placed</option>
                    <option value="Preparing" ${o.status === 'Preparing' ? 'selected' : ''}>Preparing</option>
                    <option value="Out for Delivery" ${o.status === 'Out for Delivery' ? 'selected' : ''}>Out for Delivery</option>
                    <option value="Delivered" ${o.status === 'Delivered' ? 'selected' : ''}>Delivered</option>
                </select>
            </td>
        </tr>
    `).join('');
}

function updateFoodOrderStatus(orderId, newStatus) {
    const order = ADMIN_FOOD_ORDERS.find(o => o.orderId === orderId);
    if (order) {
        order.status = newStatus;
        if (newStatus === 'Placed') order.badge = 'bg-warning text-dark';
        else if (newStatus === 'Preparing') order.badge = 'bg-info text-dark';
        else if (newStatus === 'Out for Delivery') order.badge = 'bg-primary';
        else if (newStatus === 'Delivered') order.badge = 'bg-success';

        renderAdminFoodOrdersTable();
        if (window.showToast) window.showToast(`Order ${orderId} updated to ${newStatus}`, 'Kitchen Alert');
    }
}

/**
 * Render Service Requests Table
 */
function renderAdminServicesTable() {
    const tbody = document.querySelector('#admin-services-table tbody');
    if (!tbody) return;

    tbody.innerHTML = ADMIN_SERVICE_REQUESTS.map(s => `
        <tr>
            <td><span class="badge bg-secondary font-monospace">${s.reqId}</span></td>
            <td class="fw-bold text-white">${s.serviceName}</td>
            <td class="fw-bold text-gold">Room #${s.roomNum}</td>
            <td class="small text-muted">${s.time}</td>
            <td><span class="badge ${s.badge}">${s.status}</span></td>
            <td class="text-end">
                <button class="btn btn-sm btn-outline-success py-0 px-2 me-1" onclick="updateServiceStatus('${s.reqId}', 'Completed')">Complete</button>
            </td>
        </tr>
    `).join('');
}

function updateServiceStatus(reqId, newStatus) {
    const req = ADMIN_SERVICE_REQUESTS.find(s => s.reqId === reqId);
    if (req) {
        req.status = newStatus;
        req.badge = 'bg-success';
        renderAdminServicesTable();
        if (window.showToast) window.showToast(`Service ticket ${reqId} marked as ${newStatus}`, 'Concierge Dispatch');
    }
}

window.toggleRoomStatus = toggleRoomStatus;
window.deleteAdminRoom = deleteAdminRoom;
window.handleAddRoomSubmit = handleAddRoomSubmit;
window.updateBookingStatus = updateBookingStatus;
window.toggleFoodAvailability = toggleFoodAvailability;
window.updateFoodOrderStatus = updateFoodOrderStatus;
window.updateServiceStatus = updateServiceStatus;
