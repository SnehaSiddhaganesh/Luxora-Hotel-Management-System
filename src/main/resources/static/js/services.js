/**
 * LUXORA — Hotel Services & Concierge Service Request Engine
 * Structured for future Spring Boot REST API (/api/v1/service-requests) & MySQL Integration
 */

const SERVICES_CATALOG = [
    {
        id: 'srv-101',
        title: '24/7 Room Service',
        icon: 'fa-bell-concierge',
        image: 'images/food-dinner.png',
        badge: '24/7 Available',
        description: 'Personal butler delivery of gourmet culinary meals, snacks, and beverages directly to your suite door at any hour.',
        category: 'Dining'
    },
    {
        id: 'srv-102',
        title: 'Housekeeping',
        icon: 'fa-broom',
        image: 'images/hero-bg.png',
        badge: 'Complimentary',
        description: 'Flawless daily suite cleaning, premium linen refresh, pillow menu customization, and evening turn-down service.',
        category: 'Maintenance'
    },
    {
        id: 'srv-103',
        title: 'Aura Spa & Wellness',
        icon: 'fa-spa',
        image: 'images/hero-bg.png',
        badge: 'VIP Reserve',
        description: 'Holistic hot stone massages, organic aromatherapy, facial treatments, and private steam saunas by certified therapists.',
        category: 'Wellness'
    },
    {
        id: 'srv-104',
        title: 'Rooftop Swimming Pool',
        icon: 'fa-water-ladder',
        image: 'images/deluxe-ocean.png',
        badge: 'Heated Pool',
        description: 'Panoramic heated infinity pool featuring private luxury cabanas, swim-up cocktail lounge, and sunset views.',
        category: 'Recreation'
    },
    {
        id: 'srv-105',
        title: 'Airport Pickup & Transfer',
        icon: 'fa-car',
        image: 'images/royal-penthouse.png',
        badge: 'Chauffeur Service',
        description: 'Chauffeur-driven Rolls-Royce Phantom and Maybach airport pickups & luxury city transfers available 24/7.',
        category: 'Transport'
    },
    {
        id: 'srv-106',
        title: 'Valet Parking',
        icon: 'fa-square-parking',
        image: 'images/hero-bg.png',
        badge: 'Secure 24/7',
        description: 'Covered climate-controlled valet parking with 24/7 security surveillance and fast EV charging stations.',
        category: 'Transport'
    },
    {
        id: 'srv-107',
        title: 'Express Laundry & Pressing',
        icon: 'fa-shirt',
        image: 'images/hero-bg.png',
        badge: 'Same-Day Return',
        description: 'Executive suit pressing, dry cleaning, delicate fabric steaming, and same-day laundry return service.',
        category: 'Maintenance'
    },
    {
        id: 'srv-108',
        title: 'High-Speed Wi-Fi 6',
        icon: 'fa-wifi',
        image: 'images/hero-bg.png',
        badge: 'Gigabit Speed',
        description: 'Unlimited high-speed fiber Wi-Fi 6 connectivity across all guest suites, meeting lounges, and pool decks.',
        category: 'Technology'
    },
    {
        id: 'srv-109',
        title: 'Michelin Restaurant Dining',
        icon: 'fa-utensils',
        image: 'images/food-dinner.png',
        badge: 'Fine Dining',
        description: 'Award-winning fine dining hall offering multi-course Michelin chef tasting menus and vintage wine pairings.',
        category: 'Dining'
    },
    {
        id: 'srv-110',
        title: '24/7 Fitness Center',
        icon: 'fa-dumbbell',
        image: 'images/hero-bg.png',
        badge: 'Technogym Equipped',
        description: 'State-of-the-art cardio machines, free weights, yoga studio, and certified personal fitness trainers.',
        category: 'Wellness'
    }
];

document.addEventListener('DOMContentLoaded', () => {
    renderServicesGrid();
});

/**
 * Render Services Grid Cards
 */
function renderServicesGrid() {
    const container = document.getElementById('services-grid-container');
    if (!container) return;

    container.innerHTML = SERVICES_CATALOG.map(srv => `
        <div class="col-lg-4 col-md-6">
            <div class="lux-card">
                <div class="room-img-wrapper">
                    <img src="${srv.image}" alt="${srv.title}">
                    <span class="room-tag"><i class="fa-solid ${srv.icon} text-gold me-1"></i> ${srv.badge}</span>
                </div>

                <div class="room-card-body text-center">
                    <div class="service-icon mb-2" style="width: 55px; height: 55px; font-size: 1.3rem;">
                        <i class="fa-solid ${srv.icon}"></i>
                    </div>

                    <h3 class="text-white font-serif mb-2 fs-5">${srv.title}</h3>
                    <p class="text-muted small mb-4 flex-grow-1" style="line-height:1.5;">${srv.description}</p>

                    <div class="pt-3 border-top border-secondary">
                        <button class="btn-gold w-100 justify-content-center" onclick="openServiceRequestModal('${srv.title}')">
                            <i class="fa-solid fa-bell me-1"></i> Request Service
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

/**
 * Open Service Request Modal pre-filled with selected service
 */
function openServiceRequestModal(serviceTitle) {
    if (!window.requireCustomerLogin || !window.requireCustomerLogin()) {
        return;
    }

    const serviceSelect = document.getElementById('req-service-title');
    if (serviceSelect) {
        serviceSelect.value = serviceTitle;
    }

    const modalElem = document.getElementById('serviceRequestModal');
    if (modalElem) {
        const bsModal = new bootstrap.Modal(modalElem);
        bsModal.show();
    }
}

/**
 * Handle Service Request Form Submission & Spring Boot DTO Payload Mock
 */
function handleServiceRequestSubmit(e) {
    e.preventDefault();

    if (!window.requireCustomerLogin || !window.requireCustomerLogin()) {
        return;
    }

    const serviceTitle = document.getElementById('req-service-title')?.value;
    const roomNumber = document.getElementById('req-room-number')?.value;
    const preferredTime = document.getElementById('req-preferred-time')?.value;
    const specialRequest = document.getElementById('req-special-notes')?.value;

    if (!serviceTitle || !roomNumber || !preferredTime) {
        alert('Please complete all required fields.');
        return;
    }

    const requestId = 'SRV-' + Math.floor(100000 + Math.random() * 900000);

    // Construct Spring Boot REST DTO Payload
    const servicePayload = {
        requestId: requestId,
        serviceTitle: serviceTitle,
        roomNumber: roomNumber,
        preferredTime: preferredTime,
        specialRequestNotes: specialRequest,
        status: 'PENDING',
        timestamp: new Date().toISOString()
    };

    console.log('Spring Boot REST API Service Payload (Ready for POST /api/v1/service-requests):', servicePayload);

    // Show Confirmation Modal
    document.getElementById('srv-modal-ref').textContent = requestId;
    document.getElementById('srv-modal-title').textContent = serviceTitle;
    document.getElementById('srv-modal-room').textContent = `Room #${roomNumber}`;
    document.getElementById('srv-modal-time').textContent = preferredTime;

    // Hide input modal & open confirmation receipt
    const reqModal = document.getElementById('serviceRequestModal');
    if (reqModal) bootstrap.Modal.getInstance(reqModal).hide();

    const confirmModal = document.getElementById('serviceConfirmModal');
    if (confirmModal) {
        const bsModal = new bootstrap.Modal(confirmModal);
        bsModal.show();
    }
}

window.openServiceRequestModal = openServiceRequestModal;
window.handleServiceRequestSubmit = handleServiceRequestSubmit;
