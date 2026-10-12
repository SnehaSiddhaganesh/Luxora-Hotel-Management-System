/**
 * LUXORA — Room Details & Interactive Booking Engine
 * Booking flow backed by the Spring Boot room and booking APIs
 */

const ROOM_DETAILS_CATALOG = {
    'standard': {
        name: 'Standard Room',
        price: 1500,
        rating: 4.7,
        reviewsCount: 42,
        capacity: 2,
        bedType: 'Queen Bed',
        size: '450 sq.ft',
        mainImage: 'images/hero-bg.png',
        gallery: ['images/hero-bg.png', 'images/deluxe-ocean.png', 'images/presidential-suite.png'],
        description: 'Designed for effortless comfort, the Standard Room offers smart room controls, an ergonomic work lounge, rain shower en-suite, and city skyline views.',
        facilities: [
            { icon: 'fa-snowflake', text: 'Central Air Conditioning' },
            { icon: 'fa-wifi', text: 'High-Speed Fiber Wi-Fi' },
            { icon: 'fa-tv', text: '43" 4K Smart TV' },
            { icon: 'fa-mug-hot', text: 'Nespresso Coffee Maker' },
            { icon: 'fa-shield-halved', text: 'In-Room Safe' },
            { icon: 'fa-shower', text: 'Rainfall Shower' }
        ]
    },
    'deluxe': {
        name: 'Deluxe Room',
        price: 2500,
        rating: 4.8,
        reviewsCount: 86,
        capacity: 2,
        bedType: 'King Bed',
        size: '650 sq.ft',
        mainImage: 'images/deluxe-ocean.png',
        gallery: ['images/deluxe-ocean.png', 'images/hero-bg.png', 'images/royal-penthouse.png'],
        description: 'Immerse yourself in refined elegance. The Deluxe Room features a private balcony overlooking lush gardens, plush pillow-top king bed, and mini bar.',
        facilities: [
            { icon: 'fa-snowflake', text: 'Climate Control AC' },
            { icon: 'fa-wifi', text: 'Gigabit Wi-Fi 6' },
            { icon: 'fa-tv', text: '55" OLED Smart TV' },
            { icon: 'fa-sun', text: 'Private Balcony' },
            { icon: 'fa-wine-glass', text: 'Stocked Mini Bar' },
            { icon: 'fa-utensils', text: 'Complimentary Breakfast' }
        ]
    },
    'premium': {
        name: 'Premium Oceanfront Room',
        price: 3500,
        rating: 4.9,
        reviewsCount: 124,
        capacity: 3,
        bedType: 'King Bed + Sofa',
        size: '850 sq.ft',
        mainImage: 'images/deluxe-ocean.png',
        gallery: ['images/deluxe-ocean.png', 'images/royal-penthouse.png', 'images/hero-bg.png'],
        description: 'Breathtaking panoramic ocean views with direct balcony access to ocean breezes, private marble jacuzzi, and complimentary Aura Spa access passes.',
        facilities: [
            { icon: 'fa-water', text: 'Panoramic Sea View Balcony' },
            { icon: 'fa-bath', text: 'En-suite Jacuzzi Tub' },
            { icon: 'fa-spa', text: 'Free Aura Spa Access' },
            { icon: 'fa-snowflake', text: 'Smart Automated AC' },
            { icon: 'fa-wifi', text: 'High-Speed Wi-Fi' },
            { icon: 'fa-champagne-glasses', text: 'Welcome Champagne' }
        ]
    },
    'executive': {
        name: 'Executive Business Suite',
        price: 5000,
        rating: 5.0,
        reviewsCount: 98,
        capacity: 3,
        bedType: 'Super King Bed',
        size: '1,100 sq.ft',
        mainImage: 'images/royal-penthouse.png',
        gallery: ['images/royal-penthouse.png', 'images/presidential-suite.png', 'images/deluxe-ocean.png'],
        description: 'Tailored for corporate leaders and luxury travelers. Features a separate executive lounge, video conference desk, and 24/7 butler service.',
        facilities: [
            { icon: 'fa-laptop', text: 'Ergonomic Workstation' },
            { icon: 'fa-crown', text: 'Executive Lounge Access' },
            { icon: 'fa-bell-concierge', text: '24/7 Butler Service' },
            { icon: 'fa-tv', text: '65" 4K Smart TV' },
            { icon: 'fa-shirt', text: 'Express Laundry' },
            { icon: 'fa-car', text: 'Chauffeur Valet' }
        ]
    },
    'presidential': {
        name: 'Presidential Royal Suite',
        price: 10000,
        rating: 5.0,
        reviewsCount: 154,
        capacity: 4,
        bedType: 'Dual King Beds',
        size: '2,200 sq.ft',
        mainImage: 'images/presidential-suite.png',
        gallery: ['images/presidential-suite.png', 'images/royal-penthouse.png', 'images/deluxe-ocean.png'],
        description: 'The pinnacle of grand opulence. Features private heated sky pool, floor-to-ceiling glass windows, dining room, and private elevator entry.',
        facilities: [
            { icon: 'fa-water-ladder', text: 'Private Sky Infinity Pool' },
            { icon: 'fa-crown', text: '24/7 Dedicated Butler' },
            { icon: 'fa-car', text: 'Rolls-Royce Chauffeur' },
            { icon: 'fa-champagne-glasses', text: 'Dom Pérignon Bar' },
            { icon: 'fa-utensils', text: 'Private Chef Dining' },
            { icon: 'fa-shield-halved', text: 'Helipad Access' }
        ]
    }
};

let currentBookingState = {
    roomType: 'DELUXE',
    roomId: null,
    room: null,
    checkIn: '',
    checkOut: '',
    nights: 0,
    guests: 2,
    customer: null,
    rooms: []
};

let roomSearchSequence = 0;
const BOOKING_TAX_RATE = 0.18;

document.addEventListener('DOMContentLoaded', () => {
    parseUrlParams();
    populateRoomDetailsView();
    initBookingListeners();
    calculatePriceSummary();
    loadBookingCustomer();
    loadAvailableRooms();
});

/**
 * Read URL parameters if user arrived with specific room choice
 */
function parseUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const type = urlParams.get('type');
    const checkin = urlParams.get('checkin');
    const checkout = urlParams.get('checkout');
    const guests = urlParams.get('guests');

    if (type && ROOM_DETAILS_CATALOG[type.toLowerCase()]) {
        currentBookingState.roomType = type.toUpperCase();
        const roomSelect = document.getElementById('booking-room-type');
        if (roomSelect) roomSelect.value = type.toUpperCase();
    }

    if (checkin) {
        currentBookingState.checkIn = checkin;
        const checkinInput = document.getElementById('booking-checkin');
        if (checkinInput) checkinInput.value = checkin;
    }

    if (checkout) {
        currentBookingState.checkOut = checkout;
        const checkoutInput = document.getElementById('booking-checkout');
        if (checkoutInput) checkoutInput.value = checkout;
    }

    if (guests) {
        currentBookingState.guests = parseInt(guests, 10);
        const guestSelect = document.getElementById('booking-guests');
        if (guestSelect) guestSelect.value = guests;
    }
}

/**
 * Populate Room Details Header, Gallery, and Specifications
 */
function populateRoomDetailsView() {
    const room = ROOM_DETAILS_CATALOG[currentBookingState.roomType.toLowerCase()] || ROOM_DETAILS_CATALOG['deluxe'];

    // Update Header & Specs
    const elemName = document.getElementById('detail-room-name');
    const elemRating = document.getElementById('detail-room-rating');
    const elemDesc = document.getElementById('detail-room-desc');
    const elemBed = document.getElementById('detail-room-bed');
    const elemCapacity = document.getElementById('detail-room-capacity');
    const elemSize = document.getElementById('detail-room-size');
    const elemMainImg = document.getElementById('detail-main-image');
    const elemThumbGrid = document.getElementById('detail-gallery-thumbs');
    const elemFacilities = document.getElementById('detail-facilities-grid');

    if (elemName) elemName.textContent = room.name;
    if (elemRating) elemRating.innerHTML = `<i class="fa-solid fa-star text-gold"></i> ${room.rating} ★ <span class="text-muted font-sans font-normal">(${room.reviewsCount} verified guest reviews)</span>`;
    if (elemDesc) elemDesc.textContent = room.description;
    if (elemBed) elemBed.textContent = room.bedType;
    if (elemCapacity) elemCapacity.textContent = `Max ${room.capacity} Guests`;
    if (elemSize) elemSize.textContent = room.size;

    // Gallery Main Image & Thumbnails
    if (elemMainImg) elemMainImg.src = room.mainImage;

    if (elemThumbGrid && room.gallery) {
        elemThumbGrid.innerHTML = room.gallery.map((imgSrc, idx) => `
            <div class="col-4">
                <div class="gallery-thumb-box ${idx === 0 ? 'active' : ''}" onclick="switchGalleryImage('${imgSrc}', this)">
                    <img src="${imgSrc}" class="w-100 h-100 object-fit-cover rounded-3" alt="Thumbnail ${idx+1}">
                </div>
            </div>
        `).join('');
    }

    // Facilities Grid
    if (elemFacilities && room.facilities) {
        elemFacilities.innerHTML = room.facilities.map(fac => `
            <div class="col-md-4 col-sm-6">
                <div class="p-3 rounded-3 d-flex align-items-center gap-3" style="background: rgba(255,255,255,0.03); border: 1px solid var(--border-light);">
                    <i class="fa-solid ${fac.icon} text-gold fs-4"></i>
                    <span class="text-white small font-medium">${fac.text}</span>
                </div>
            </div>
        `).join('');
    }
}

/**
 * Switch Active Main Gallery Photo
 */
function switchGalleryImage(imgSrc, thumbElem) {
    const mainImg = document.getElementById('detail-main-image');
    if (mainImg) mainImg.src = imgSrc;

    document.querySelectorAll('.gallery-thumb-box').forEach(el => el.classList.remove('active'));
    if (thumbElem) thumbElem.classList.add('active');
}

/**
 * Initialize Event Listeners for Reservation Widget
 */
function initBookingListeners() {
    const roomSelect = document.getElementById('booking-room-type');
    const roomIdSelect = document.getElementById('booking-room-id');
    const checkinInput = document.getElementById('booking-checkin');
    const checkoutInput = document.getElementById('booking-checkout');
    const guestSelect = document.getElementById('booking-guests');
    const bookingForm = document.getElementById('luxora-booking-wizard');

    if (roomSelect) {
        roomSelect.addEventListener('change', (e) => {
            currentBookingState.roomType = e.target.value;
            currentBookingState.roomId = null;
            currentBookingState.room = null;
            const categoryCapacity = ROOM_DETAILS_CATALOG[e.target.value.toLowerCase()]?.capacity || 1;
            currentBookingState.guests = Math.min(currentBookingState.guests, categoryCapacity);
            const guestSelect = document.getElementById('booking-guests');
            if (guestSelect) guestSelect.value = String(currentBookingState.guests);
            populateRoomDetailsView();
            calculatePriceSummary();
            loadAvailableRooms();
        });
    }

    if (roomIdSelect) {
        roomIdSelect.addEventListener('change', (e) => {
            currentBookingState.roomId = e.target.value ? Number(e.target.value) : null;
            currentBookingState.room = currentBookingState.rooms.find(room => room.id === currentBookingState.roomId) || null;
            updateGuestOptions();
            calculatePriceSummary();
        });
    }

    if (checkinInput && checkoutInput) {
        checkinInput.min = getLocalDateString(new Date());
        if (!checkinInput.value) checkinInput.value = getLocalDateString(new Date());
        if (!checkoutInput.value || checkoutInput.value <= checkinInput.value) {
            checkoutInput.value = addDaysToDate(checkinInput.value, 1);
        }

        const updateStay = () => {
            currentBookingState.checkIn = checkinInput.value;
            currentBookingState.checkOut = checkoutInput.value;
            checkoutInput.min = currentBookingState.checkIn
                ? addDaysToDate(currentBookingState.checkIn, 1)
                : '';
            currentBookingState.nights = getStayNights(currentBookingState.checkIn, currentBookingState.checkOut);
            calculatePriceSummary();
            loadAvailableRooms();
        };

        checkinInput.addEventListener('change', updateStay);
        checkoutInput.addEventListener('change', updateStay);
        updateStay();
    }

    if (guestSelect) {
        guestSelect.addEventListener('change', (e) => {
            currentBookingState.guests = parseInt(e.target.value, 10);
            loadAvailableRooms();
        });
    }

    if (bookingForm) {
        bookingForm.addEventListener('submit', handleReservationSubmit);
    }
}

function getLocalDateString(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function addDaysToDate(dateString, days) {
    if (!dateString) return '';
    const [year, month, day] = dateString.split('-').map(Number);
    const date = new Date(year, month - 1, day);
    date.setDate(date.getDate() + days);
    return getLocalDateString(date);
}

function getStayNights(checkIn, checkOut) {
    if (!checkIn || !checkOut) return 0;
    const start = Date.parse(`${checkIn}T00:00:00Z`);
    const end = Date.parse(`${checkOut}T00:00:00Z`);
    const days = (end - start) / 86400000;
    return Number.isInteger(days) && days > 0 ? days : 0;
}

function showBookingMessage(message, type = 'danger') {
    const alert = document.getElementById('booking-alert');
    if (!alert) return;
    alert.className = `alert alert-${type} py-2 px-3 mb-3 small`;
    alert.textContent = message;
    alert.hidden = !message;
}

async function loadBookingCustomer() {
    const summary = document.getElementById('booking-customer-summary');
    const submitButton = document.querySelector('#luxora-booking-wizard button[type="submit"]');
    try {
        const response = await fetch('/api/auth/me', { credentials: 'same-origin' });
        if (!response.ok) throw new Error('Sign in with a customer account to make a reservation.');
        const customer = await response.json();
        if (customer.role !== 'CUSTOMER') throw new Error('Reservations can only be made with a customer account.');
        currentBookingState.customer = customer;
        if (summary) summary.textContent = `Booking for ${customer.firstName} ${customer.lastName} (${customer.email})`;
        if (submitButton) submitButton.disabled = false;
    } catch (error) {
        currentBookingState.customer = null;
        if (summary) {
            summary.textContent = error.message;
            const loginLink = document.createElement('a');
            loginLink.href = 'login.html';
            loginLink.className = 'text-gold ms-2';
            loginLink.textContent = 'Sign in';
            summary.append(loginLink);
        }
        if (submitButton) submitButton.disabled = true;
    }
}

async function loadAvailableRooms() {
    const select = document.getElementById('booking-room-id');
    if (!select) return;

    const requestNumber = ++roomSearchSequence;
    const nights = getStayNights(currentBookingState.checkIn, currentBookingState.checkOut);
    if (!nights || currentBookingState.checkIn < getLocalDateString(new Date())) {
        currentBookingState.rooms = [];
        currentBookingState.room = null;
        currentBookingState.roomId = null;
        setRoomOptions(select, [], 'Choose valid check-in and check-out dates');
        calculatePriceSummary();
        return;
    }

    const query = new URLSearchParams({
        checkIn: currentBookingState.checkIn,
        checkOut: currentBookingState.checkOut,
        roomType: currentBookingState.roomType,
        guests: String(currentBookingState.guests)
    });
    setRoomOptions(select, [], 'Checking availability...');

    try {
        const response = await fetch(`/api/rooms/available?${query}`, { credentials: 'same-origin' });
        if (!response.ok) throw new Error('Could not check room availability.');
        const rooms = await response.json();
        if (requestNumber !== roomSearchSequence) return;
        currentBookingState.rooms = rooms;
        setRoomOptions(select, rooms, rooms.length ? 'Select an available room' : 'No rooms available for these dates');
        const selectedRoom = rooms.find(room => room.id === currentBookingState.roomId);
        currentBookingState.room = selectedRoom || null;
        currentBookingState.roomId = selectedRoom ? selectedRoom.id : null;
        if (selectedRoom) select.value = String(selectedRoom.id);
        updateGuestOptions();
        calculatePriceSummary();
        showBookingMessage(rooms.length ? '' : 'No room of this type can accommodate your party for the selected dates.', 'warning');
    } catch (error) {
        if (requestNumber !== roomSearchSequence) return;
        currentBookingState.rooms = [];
        currentBookingState.room = null;
        currentBookingState.roomId = null;
        setRoomOptions(select, [], 'Availability could not be loaded');
        calculatePriceSummary();
        showBookingMessage(error.message);
    }
}

function setRoomOptions(select, rooms, placeholder) {
    select.replaceChildren(new Option(placeholder, ''));
    rooms.forEach(room => {
        const option = new Option(
            `Room ${room.roomNumber} · ${room.roomType} · up to ${room.maxGuests} guests · ${formatINR(room.pricePerNight)}/night`,
            String(room.id)
        );
        select.add(option);
    });
}

function updateGuestOptions() {
    const guestSelect = document.getElementById('booking-guests');
    const room = currentBookingState.room;
    if (!guestSelect || !room) return;

    const guestCount = Math.min(Math.max(currentBookingState.guests, 1), room.maxGuests);
    guestSelect.replaceChildren();
    for (let count = 1; count <= room.maxGuests; count += 1) {
        guestSelect.add(new Option(`${count} Guest${count === 1 ? '' : 's'}`, String(count)));
    }
    currentBookingState.guests = guestCount;
    guestSelect.value = String(guestCount);
}

function formatINR(amount) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
    }).format(amount);
}

/** Preview only; the server recalculates and persists the authoritative totals. */
function calculatePriceSummary() {
    const room = currentBookingState.room;
    const nights = getStayNights(currentBookingState.checkIn, currentBookingState.checkOut);
    const rate = room ? Number(room.pricePerNight) : 0;
    const subtotal = rate * nights;
    const tax = Math.round((subtotal * BOOKING_TAX_RATE + Number.EPSILON) * 100) / 100;
    const total = subtotal + tax;
    const elemRate = document.getElementById('summary-nightly-rate');
    const elemNights = document.getElementById('summary-nights');
    const elemRoomSubtotal = document.getElementById('summary-room-subtotal');
    const elemTax = document.getElementById('summary-tax');
    const elemTotal = document.getElementById('summary-grand-total');

    if (elemRate) elemRate.textContent = room ? formatINR(rate) : '--';
    if (elemNights) elemNights.textContent = nights ? `${nights} Night${nights === 1 ? '' : 's'}` : '--';
    if (elemRoomSubtotal) elemRoomSubtotal.textContent = room && nights ? formatINR(subtotal) : '--';
    if (elemTax) elemTax.textContent = room && nights ? formatINR(tax) : '--';
    if (elemTotal) elemTotal.textContent = room && nights ? formatINR(total) : '--';
}

async function handleReservationSubmit(e) {
    e.preventDefault();

    if (!currentBookingState.customer || currentBookingState.customer.role !== 'CUSTOMER') {
        showBookingMessage('Sign in with a customer account before making a reservation.');
        return;
    }

    const form = e.currentTarget;
    if (!form.reportValidity()) return;
    if (!currentBookingState.roomId || !currentBookingState.room) {
        showBookingMessage('Select an available room before confirming your reservation.');
        return;
    }
    const submitButton = form.querySelector('button[type="submit"]');
    if (submitButton) submitButton.disabled = true;
    showBookingMessage('');

    try {
        const response = await window.requestWithCsrf('/api/bookings', {
            method: 'POST',
            body: JSON.stringify({
                roomId: currentBookingState.roomId,
                checkInDate: currentBookingState.checkIn,
                checkOutDate: currentBookingState.checkOut,
                numberOfGuests: currentBookingState.guests,
                specialRequests: document.getElementById('guest-requests')?.value.trim() || null
            })
        });
        const booking = await response.json();
        if (!response.ok) {
            const detail = booking.details?.join(' ') || booking.message;
            throw new Error(detail || 'The reservation could not be completed.');
        }

        document.getElementById('res-modal-ref').textContent = booking.bookingCode;
        document.getElementById('res-modal-room').textContent = `${booking.roomNumber} · ${currentBookingState.room.roomType}`;
        document.getElementById('res-modal-name').textContent = `${currentBookingState.customer.firstName} ${currentBookingState.customer.lastName}`;
        document.getElementById('res-modal-dates').textContent = `${booking.checkInDate} to ${booking.checkOutDate} (${booking.nights} night${booking.nights === 1 ? '' : 's'})`;
        document.getElementById('res-modal-total').textContent = formatINR(booking.total);
        document.getElementById('res-modal-breakdown').textContent = `${formatINR(booking.nightlyRate)} × ${booking.nights} nights + GST ${formatINR(booking.tax)}`;

        const modalElement = document.getElementById('bookingConfirmationModal');
        if (modalElement) bootstrap.Modal.getOrCreateInstance(modalElement).show();
    } catch (error) {
        showBookingMessage(error.message);
        if (error.message.toLowerCase().includes('not available')) loadAvailableRooms();
    } finally {
        if (submitButton) submitButton.disabled = false;
    }
}

window.switchGalleryImage = switchGalleryImage;
