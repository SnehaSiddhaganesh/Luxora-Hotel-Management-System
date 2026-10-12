/**
 * LUXORA — Customer Dashboard & Portal Script
 */

const MOCK_SERVICE_REQUESTS = [
    {
        reqId: 'SRV-839201',
        serviceName: 'Aura Spa & Wellness Massage',
        time: 'Today - 04:00 PM',
        roomNum: 'Suite 501',
        status: 'CONFIRMED',
        badgeColor: 'bg-info text-dark'
    },
    {
        reqId: 'SRV-419205',
        serviceName: 'Airport Pickup (Rolls-Royce)',
        time: 'Oct 10 - 11:30 AM',
        roomNum: 'Suite 501',
        status: 'IN-PROGRESS',
        badgeColor: 'bg-warning text-dark'
    }
];

document.addEventListener('DOMContentLoaded', () => {
    initTabSwitcher();
    renderBookingsList();
    renderOrdersList();
    loadUserDashboardData();
});

/**
 * Load User Details from Session
 */
function loadUserDashboardData() {
    fetch('/api/auth/me', { credentials: 'same-origin' })
        .then(response => {
            if (!response.ok) throw new Error('Sign in required');
            return response.json();
        })
        .then(user => {
            if (user.role !== 'CUSTOMER') throw new Error('Customer account required');
            const profile = {
                fullName: `${user.firstName} ${user.lastName}`,
                email: user.email,
                phone: user.phone || '',
                role: user.role
            };
            localStorage.setItem('luxora_user_session', JSON.stringify(profile));
            populateDashboardProfile(profile);
        })
        .catch(() => {
            localStorage.removeItem('luxora_user_session');
            window.location.href = 'login.html';
        });
}

function populateDashboardProfile(user) {
    const nameElems = document.querySelectorAll('.user-name-display');
    const emailElems = document.querySelectorAll('.user-email-display');
    const phoneElems = document.querySelectorAll('.user-phone-display');
    const vipElems = document.querySelectorAll('.user-vip-display');

    nameElems.forEach(el => el.textContent = user.fullName);
    emailElems.forEach(el => el.textContent = user.email);
    phoneElems.forEach(el => el.textContent = user.phone);
    vipElems.forEach(el => el.textContent = user.vipTier);

    // Populate Edit Profile Form
    const editName = document.getElementById('edit-profile-name');
    const editEmail = document.getElementById('edit-profile-email');
    const editPhone = document.getElementById('edit-profile-phone');

    if (editName) editName.value = user.fullName;
    if (editEmail) editEmail.value = user.email;
    if (editPhone) editPhone.value = user.phone || '';
}

/**
 * Handle Dashboard Tab Switching (Overview, My Bookings, My Orders, Profile)
 */
function initTabSwitcher() {
    const tabLinks = document.querySelectorAll('.dash-nav-link');
    const tabPanes = document.querySelectorAll('.dash-tab-pane');

    tabLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = link.getAttribute('data-tab');

            tabLinks.forEach(l => l.classList.remove('active', 'btn-gold'));
            tabLinks.forEach(l => l.classList.add('btn-outline-gold'));

            link.classList.remove('btn-outline-gold');
            link.classList.add('active', 'btn-gold');

            tabPanes.forEach(pane => {
                if (pane.id === targetId) {
                    pane.style.display = 'block';
                } else {
                    pane.style.display = 'none';
                }
            });
        });
    });

    if (window.location.hash === '#bookings-pane') {
        document.querySelector('[data-tab="bookings-pane"]')?.click();
    }
}

/**
 * Render Bookings List
 */
async function renderBookingsList() {
    const container = document.getElementById('dash-bookings-container');
    if (!container) return;

    container.innerHTML = '<p class="text-muted">Loading reservations...</p>';
    try {
        const response = await fetch('/api/bookings/mine', { credentials: 'same-origin' });
        if (!response.ok) throw new Error('Could not load reservation history.');
        const bookings = await response.json();
        const today = getDashboardLocalDate();
        const upcoming = bookings
            .filter(booking => booking.status !== 'CANCELLED' && booking.status !== 'CHECKED_OUT' && booking.checkOutDate > today)
            .sort((first, second) => first.checkInDate.localeCompare(second.checkInDate))[0];
        renderNextBooking(upcoming);

        if (!bookings.length) {
            container.innerHTML = '<div class="p-4 text-center text-muted" style="background: var(--bg-surface); border: 1px solid var(--border-light);">No reservations yet. Choose a room to make your first booking.</div>';
            return;
        }

        container.innerHTML = bookings.map(booking => {
            const cancellable = ['CONFIRMED', 'PAID'].includes(booking.status) && booking.checkInDate > today;
            const statusClass = booking.status === 'CONFIRMED' || booking.status === 'PAID'
                ? 'bg-success'
                : booking.status === 'CANCELLED' ? 'bg-danger' : 'bg-secondary';
            return `
                <article class="p-4 mb-3 rounded-3" style="background: var(--bg-surface); border: 1px solid var(--border-light);">
                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2 border-bottom border-secondary pb-2">
                        <div>
                            <span class="badge ${statusClass} font-sans me-2">${escapeBookingHtml(booking.status)}</span>
                            <span class="text-gold font-monospace small">${escapeBookingHtml(booking.bookingCode)}</span>
                        </div>
                        <div class="fs-5 font-serif fw-bold text-white">${formatBookingMoney(booking.total)}</div>
                    </div>
                    <div class="row g-3 align-items-center">
                        <div class="col-md-5">
                            <h4 class="text-white font-serif mb-1 fs-5">Room ${escapeBookingHtml(booking.roomNumber)}</h4>
                            <small class="text-muted">${booking.numberOfGuests} guest${booking.numberOfGuests === 1 ? '' : 's'} · ${booking.nights} night${booking.nights === 1 ? '' : 's'}</small>
                        </div>
                        <div class="col-md-4 small text-muted">${escapeBookingHtml(booking.checkInDate)} → ${escapeBookingHtml(booking.checkOutDate)}</div>
                        <div class="col-md-3 text-md-end">
                            ${cancellable ? `<button type="button" class="btn-outline-gold btn-sm" data-cancel-booking="${booking.id}"><i class="fa-solid fa-ban me-1"></i> Cancel</button>` : ''}
                        </div>
                    </div>
                    <div class="small text-muted mt-3">Room ${formatBookingMoney(booking.subtotal)} · GST ${formatBookingMoney(booking.tax)} · ${escapeBookingHtml(booking.status)}</div>
                </article>
            `;
        }).join('');

        container.querySelectorAll('[data-cancel-booking]').forEach(button => {
            button.addEventListener('click', () => cancelCustomerBooking(Number(button.dataset.cancelBooking)));
        });
    } catch (error) {
        container.textContent = error.message;
    }
}

function renderNextBooking(booking) {
    const container = document.getElementById('dash-next-booking');
    const status = document.getElementById('dash-next-booking-status');
    if (!container || !status) return;
    if (!booking) {
        status.textContent = 'No reservation';
        status.className = 'badge bg-secondary font-sans';
        container.textContent = 'Your upcoming reservation will appear here.';
        return;
    }
    status.textContent = booking.status;
    status.className = 'badge bg-success font-sans';
    container.textContent = `Room ${booking.roomNumber} · ${booking.checkInDate} to ${booking.checkOutDate} · ${booking.nights} night${booking.nights === 1 ? '' : 's'} · ${formatBookingMoney(booking.total)} · ${booking.bookingCode}`;
}

async function cancelCustomerBooking(bookingId) {
    if (!window.confirm('Cancel this reservation?')) return;
    try {
        const response = await window.requestWithCsrf(`/api/bookings/${bookingId}/cancel`, { method: 'PATCH' });
        const result = await response.json();
        if (!response.ok) throw new Error(result.message || 'Could not cancel this reservation.');
        await renderBookingsList();
    } catch (error) {
        if (window.showToast) window.showToast(error.message, 'Reservation');
        else window.alert(error.message);
    }
}

function getDashboardLocalDate() {
    const date = new Date();
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

function formatBookingMoney(value) {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(Number(value) || 0);
}

function escapeBookingHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, character => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[character]);
}

/**
 * Render Food Orders List
 */
async function renderOrdersList() {
    const container = document.getElementById('dash-orders-container');
    const recentContainer = document.getElementById('dash-recent-orders-container');
    if (!container && !recentContainer) return;

    if (container) container.innerHTML = '<p class="text-muted">Loading dining orders...</p>';
    if (recentContainer) recentContainer.innerHTML = '<p class="text-muted small mb-0">Loading dining orders...</p>';

    try {
        const response = await fetch('/api/orders/mine', { credentials: 'same-origin' });
        if (!response.ok) throw new Error('Could not load dining order history.');
        const orders = await response.json();
        const renderOrder = (order, compact = false) => {
            const items = (order.items || []).map(item =>
                `${escapeDashboardHtml(item.foodItemName)} (x${item.quantity})`
            ).join(', ');
            const statusClass = order.status === 'DELIVERED' ? 'bg-success'
                : order.status === 'CANCELLED' ? 'bg-danger' : 'bg-warning text-dark';
            const orderedAt = order.orderedAt
                ? new Intl.DateTimeFormat('en-IN', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(order.orderedAt))
                : 'Date unavailable';
            return `
                <div class="${compact ? 'p-3 mb-2' : 'p-3 mb-3'} rounded-4" style="background: var(--bg-surface); border: 1px solid var(--border-light);">
                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-2 gap-2">
                        <div>
                            <span class="badge ${statusClass} font-sans me-2">${escapeDashboardHtml(order.status)}</span>
                            <span class="text-gold font-monospace small">${escapeDashboardHtml(order.orderCode)}</span>
                        </div>
                        <strong class="text-white">${formatBookingMoney(order.total)}</strong>
                    </div>
                    <div class="small text-white mb-2">${items || 'Order items unavailable'}</div>
                    <div class="d-flex justify-content-between align-items-center small text-muted pt-2 border-top border-secondary">
                        <span>${orderedAt} · Room ${escapeDashboardHtml(order.roomNumber || '')}</span>
                    </div>
                </div>
            `;
        };

        const emptyMessage = '<p class="text-muted small mb-0">No dining orders yet. Choose a dish from the menu to place your first order.</p>';
        if (container) {
            container.innerHTML = orders.length
                ? orders.map(order => renderOrder(order)).join('')
                : `<div class="p-4 text-center" style="background: var(--bg-surface); border: 1px solid var(--border-light);">${emptyMessage}</div>`;
        }
        if (recentContainer) {
            recentContainer.innerHTML = orders.length
                ? renderOrder(orders[0], true)
                : emptyMessage;
        }
    } catch (error) {
        if (container) container.textContent = error.message;
        if (recentContainer) recentContainer.textContent = error.message;
    }
}

function escapeDashboardHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, character => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[character]);
}

/**
 * Handle Profile Edit Form Submit
 */
function handleProfileUpdate(e) {
    e.preventDefault();
    const name = document.getElementById('edit-profile-name')?.value;
    const phone = document.getElementById('edit-profile-phone')?.value;

    const stored = localStorage.getItem('luxora_user_session');
    if (stored) {
        const user = JSON.parse(stored);
        user.fullName = name;
        user.phone = phone;
        localStorage.setItem('luxora_user_session', JSON.stringify(user));
    }

    loadUserDashboardData();
    if (window.showToast) window.showToast('Profile information updated successfully!', 'Profile Saved');
}

/**
 * Mobile Digital Key Unlock Simulator
 */
function simulateKeylessUnlock() {
    if (window.showToast) {
        window.showToast('Unlocking Suite #501 via Mobile NFC Key...', 'Keyless Smart Entry');
        setTimeout(() => {
            window.showToast('Door Unlocked! Welcome to Suite #501', 'Access Granted');
        }, 1500);
    }
}

window.handleProfileUpdate = handleProfileUpdate;
window.simulateKeylessUnlock = simulateKeylessUnlock;
