/**
 * LUXORA — Rooms Listing & Live Filtering Engine
 */

const ROOMS_DATA = [
    {
        id: 'std-101',
        type: 'standard',
        name: 'Standard Room',
        categoryName: 'Standard',
        image: 'images/hero-bg.png',
        rating: 4.7,
        reviewsCount: 42,
        description: 'Cozy and modern smart room with city views, ergonomic work desk, rain shower, and premium bedding.',
        price: 1500,
        capacity: 2,
        bedType: 'Queen Bed',
        hasAC: true,
        hasWifi: true,
        hasTV: true,
        hasBalcony: false,
        hasBreakfast: true,
        hasJacuzzi: false,
        availability: 'Available',
        badgeColor: 'bg-success',
        availableCount: 5
    },
    {
        id: 'dlx-201',
        type: 'deluxe',
        name: 'Deluxe Room',
        categoryName: 'Deluxe',
        image: 'images/deluxe-ocean.png',
        rating: 4.8,
        reviewsCount: 86,
        description: 'Spacious deluxe room featuring a private balcony overlooking green gardens, plush king bed, and mini bar.',
        price: 2500,
        capacity: 2,
        bedType: 'King Bed',
        hasAC: true,
        hasWifi: true,
        hasTV: true,
        hasBalcony: true,
        hasBreakfast: true,
        hasJacuzzi: false,
        availability: 'Available',
        badgeColor: 'bg-success',
        availableCount: 3
    },
    {
        id: 'prm-301',
        type: 'premium',
        name: 'Premium Oceanfront Room',
        categoryName: 'Premium',
        image: 'images/deluxe-ocean.png',
        rating: 4.9,
        reviewsCount: 124,
        description: 'Breathtaking sea views, private sunset sun deck, rainfall shower, Nespresso coffee lounge, and free spa pass.',
        price: 3500,
        capacity: 3,
        bedType: 'King Bed + Sofa',
        hasAC: true,
        hasWifi: true,
        hasTV: true,
        hasBalcony: true,
        hasBreakfast: true,
        hasJacuzzi: true,
        availability: '2 Rooms Left',
        badgeColor: 'bg-warning text-dark',
        availableCount: 2
    },
    {
        id: 'exe-401',
        type: 'executive',
        name: 'Executive Business Suite',
        categoryName: 'Executive Suite',
        image: 'images/royal-penthouse.png',
        rating: 5.0,
        reviewsCount: 98,
        description: 'Tailored for luxury business travelers. Features dual work lounges, 4K video conferencing, and private club lounge access.',
        price: 5000,
        capacity: 3,
        bedType: 'Super King Bed',
        hasAC: true,
        hasWifi: true,
        hasTV: true,
        hasBalcony: true,
        hasBreakfast: true,
        hasJacuzzi: true,
        availability: 'Available',
        badgeColor: 'bg-success',
        availableCount: 4
    },
    {
        id: 'prs-501',
        type: 'presidential',
        name: 'Presidential Royal Suite',
        categoryName: 'Presidential Suite',
        image: 'images/presidential-suite.png',
        rating: 5.0,
        reviewsCount: 154,
        description: 'The pinnacle of luxury living. Features private heated sky pool, 24/7 personal butler service, and Rolls-Royce chauffeur pickups.',
        price: 10000,
        capacity: 4,
        bedType: 'Dual King Beds',
        hasAC: true,
        hasWifi: true,
        hasTV: true,
        hasBalcony: true,
        hasBreakfast: true,
        hasJacuzzi: true,
        availability: 'Only 1 Left',
        badgeColor: 'bg-danger',
        availableCount: 1
    },
    {
        id: 'vlla-601',
        type: 'presidential',
        name: 'Ambassador Ocean Villa',
        categoryName: 'Presidential Suite',
        image: 'images/deluxe-ocean.png',
        rating: 4.9,
        reviewsCount: 64,
        description: 'Standalone overwater villa with glass floor viewing tropical reefs, private sun deck deck, and personal chef dining.',
        price: 12500,
        capacity: 4,
        bedType: 'Super King Bed',
        hasAC: true,
        hasWifi: true,
        hasTV: true,
        hasBalcony: true,
        hasBreakfast: true,
        hasJacuzzi: true,
        availability: 'Available',
        badgeColor: 'bg-success',
        availableCount: 2
    }
];

let filteredRooms = [...ROOMS_DATA];

document.addEventListener('DOMContentLoaded', () => {
    initRoomsPage();
});

function initRoomsPage() {
    renderRooms(filteredRooms);
    setupFilterListeners();
}

/**
 * Render Rooms Grid
 */
function renderRooms(roomsList) {
    const container = document.getElementById('rooms-grid-container');
    const countDisplay = document.getElementById('rooms-count-text');

    if (!container) return;

    if (countDisplay) {
        countDisplay.textContent = `Showing ${roomsList.length} of ${ROOMS_DATA.length} Luxury Accommodations`;
    }

    if (roomsList.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="p-5 rounded-4" style="background: var(--bg-surface); border: 1px dashed var(--border-gold);">
                    <i class="fa-solid fa-bed text-gold fs-1 mb-3"></i>
                    <h3 class="text-white font-serif mb-2">No Rooms Matched Your Search</h3>
                    <p class="text-muted mb-4">Try adjusting your price range, guest count, or category filter criteria.</p>
                    <button class="btn-gold" onclick="resetAllFilters()">Reset All Filters</button>
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = roomsList.map(room => `
        <div class="col-lg-4 col-md-6 room-card-item">
            <div class="lux-card">
                <div class="room-img-wrapper">
                    <img src="${room.image}" alt="${room.name}">
                    <span class="room-tag"><i class="fa-solid fa-crown text-gold me-1"></i> ${room.categoryName}</span>
                    <span class="badge ${room.badgeColor} position-absolute bottom-0 start-0 m-3 font-sans px-2 py-1 fs-7">
                        ${room.availability}
                    </span>
                </div>
                
                <div class="room-card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <div>
                            <h3 class="room-title text-white mb-0 fs-5 font-serif">${room.name}</h3>
                            <small class="text-muted">${room.bedType} • Max ${room.capacity} Guests</small>
                        </div>
                        <div class="rating-stars text-end">
                            <div><i class="fa-solid fa-star"></i> ${room.rating}</div>
                            <small class="text-muted" style="font-size:0.7rem;">(${room.reviewsCount})</small>
                        </div>
                    </div>

                    <p class="text-muted small mb-3 flex-grow-1">${room.description}</p>

                    <!-- Feature Icons -->
                    <div class="d-flex flex-wrap gap-2 mb-3 text-gold small">
                        ${room.hasAC ? '<span class="badge bg-dark border border-secondary text-light font-sans"><i class="fa-solid fa-snowflake text-gold me-1"></i> AC</span>' : ''}
                        ${room.hasWifi ? '<span class="badge bg-dark border border-secondary text-light font-sans"><i class="fa-solid fa-wifi text-gold me-1"></i> Free Wi-Fi</span>' : ''}
                        ${room.hasTV ? '<span class="badge bg-dark border border-secondary text-light font-sans"><i class="fa-solid fa-tv text-gold me-1"></i> 4K TV</span>' : ''}
                        ${room.hasBalcony ? '<span class="badge bg-dark border border-secondary text-light font-sans"><i class="fa-solid fa-sun text-gold me-1"></i> Balcony</span>' : ''}
                        ${room.hasJacuzzi ? '<span class="badge bg-dark border border-secondary text-light font-sans"><i class="fa-solid fa-bath text-gold me-1"></i> Jacuzzi</span>' : ''}
                    </div>

                    <div class="room-price-wrapper">
                        <div class="room-price">₹${room.price.toLocaleString()} <span>/ night</span></div>
                        <div class="d-flex gap-2">
                            <button class="btn-outline-gold btn-sm" onclick="openRoomDetailsModal('${room.id}')">
                                <i class="fa-solid fa-circle-info me-1"></i> Details
                            </button>
                            <a href="booking.html?type=${room.type}" class="btn-gold btn-sm">
                                Book Now
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

/**
 * Setup Event Listeners for Live Filtering
 */
function setupFilterListeners() {
    const searchInput = document.getElementById('filter-search');
    const categorySelect = document.getElementById('filter-category');
    const priceSlider = document.getElementById('filter-price-range');
    const priceDisplay = document.getElementById('filter-price-display');
    const guestsSelect = document.getElementById('filter-guests');
    const availOnlyCheck = document.getElementById('filter-available-only');
    const sortSelect = document.getElementById('filter-sort');

    const applyFilters = () => {
        const query = searchInput ? searchInput.value.toLowerCase().trim() : '';
        const selectedCategory = categorySelect ? categorySelect.value : 'all';
        const maxPrice = priceSlider ? parseInt(priceSlider.value, 10) : 15000;
        const selectedGuests = guestsSelect ? parseInt(guestsSelect.value, 10) : 0;
        const availableOnly = availOnlyCheck ? availOnlyCheck.checked : false;
        const sortOption = sortSelect ? sortSelect.value : 'default';

        if (priceDisplay) {
            priceDisplay.textContent = `Up to ₹${maxPrice.toLocaleString()}`;
        }

        filteredRooms = ROOMS_DATA.filter(room => {
            const matchesSearch = room.name.toLowerCase().includes(query) || room.description.toLowerCase().includes(query);
            const matchesCategory = selectedCategory === 'all' || room.type === selectedCategory;
            const matchesPrice = room.price <= maxPrice;
            const matchesGuests = selectedGuests === 0 || room.capacity >= selectedGuests;
            const matchesAvail = !availableOnly || room.availability.includes('Available');

            return matchesSearch && matchesCategory && matchesPrice && matchesGuests && matchesAvail;
        });

        // Sorting
        if (sortOption === 'price-low') {
            filteredRooms.sort((a, b) => a.price - b.price);
        } else if (sortOption === 'price-high') {
            filteredRooms.sort((a, b) => b.price - a.price);
        } else if (sortOption === 'rating') {
            filteredRooms.sort((a, b) => b.rating - a.rating);
        }

        renderRooms(filteredRooms);
    };

    if (searchInput) searchInput.addEventListener('input', applyFilters);
    if (categorySelect) categorySelect.addEventListener('change', applyFilters);
    if (priceSlider) priceSlider.addEventListener('input', applyFilters);
    if (guestsSelect) guestsSelect.addEventListener('change', applyFilters);
    if (availOnlyCheck) availOnlyCheck.addEventListener('change', applyFilters);
    if (sortSelect) sortSelect.addEventListener('change', applyFilters);
}

/**
 * Reset All Filters
 */
function resetAllFilters() {
    const searchInput = document.getElementById('filter-search');
    const categorySelect = document.getElementById('filter-category');
    const priceSlider = document.getElementById('filter-price-range');
    const guestsSelect = document.getElementById('filter-guests');
    const availOnlyCheck = document.getElementById('filter-available-only');
    const sortSelect = document.getElementById('filter-sort');

    if (searchInput) searchInput.value = '';
    if (categorySelect) categorySelect.value = 'all';
    if (priceSlider) priceSlider.value = 15000;
    if (guestsSelect) guestsSelect.value = '0';
    if (availOnlyCheck) availOnlyCheck.checked = false;
    if (sortSelect) sortSelect.value = 'default';

    filteredRooms = [...ROOMS_DATA];
    renderRooms(filteredRooms);
}

/**
 * Open Room Details Modal with Full Specs
 */
function openRoomDetailsModal(roomId) {
    const room = ROOMS_DATA.find(r => r.id === roomId);
    if (!room) return;

    document.getElementById('modal-room-title').textContent = room.name;
    document.getElementById('modal-room-img').src = room.image;
    document.getElementById('modal-room-price').textContent = `₹${room.price.toLocaleString()} / night`;
    document.getElementById('modal-room-desc').textContent = room.description;
    document.getElementById('modal-room-capacity').textContent = `Max ${room.capacity} Guests`;
    document.getElementById('modal-room-bed').textContent = room.bedType;
    document.getElementById('modal-room-rating').textContent = `${room.rating} ★ (${room.reviewsCount} reviews)`;
    document.getElementById('modal-room-avail').textContent = room.availability;
    document.getElementById('modal-room-book-btn').href = `booking.html?type=${room.type}`;

    const modalElem = document.getElementById('roomDetailsModal');
    if (modalElem) {
        const bsModal = new bootstrap.Modal(modalElem);
        bsModal.show();
    }
}

window.resetAllFilters = resetAllFilters;
window.openRoomDetailsModal = openRoomDetailsModal;
