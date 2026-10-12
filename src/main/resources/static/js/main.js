/**
 * LUXORA — Smart Hotel Management & Hospitality System
 * Main Interactive Logic (ES6+)
 */

let cartCount = 0;
let cartItems = [];

document.addEventListener('DOMContentLoaded', () => {
    initNavbarScroll();
    initDateInputs();
    initQuickBookingForm();
    initRoomFilters();
    initFoodFilters();
    initCartSystem();
    initGalleryLightbox();
});

/**
 * Navbar Sticky Scroll Effect
 */
function initNavbarScroll() {
    const navbar = document.querySelector('.lux-navbar');
    if (!navbar) return;

    window.addEventListener('scroll', () => {
        if (window.scrollY > 40) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });
}

/**
 * Set Minimum Date Restrictions for Check-in / Check-out inputs
 */
function initDateInputs() {
    const checkinInput = document.getElementById('quick-checkin');
    const checkoutInput = document.getElementById('quick-checkout');

    if (checkinInput && checkoutInput) {
        const today = new Date().toISOString().split('T')[0];
        checkinInput.min = today;

        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        const tomorrowStr = tomorrow.toISOString().split('T')[0];

        checkinInput.value = today;
        checkoutInput.value = tomorrowStr;
        checkoutInput.min = tomorrowStr;

        checkinInput.addEventListener('change', (e) => {
            const selectedIn = new Date(e.target.value);
            selectedIn.setDate(selectedIn.getDate() + 1);
            const minOut = selectedIn.toISOString().split('T')[0];
            checkoutInput.min = minOut;
            if (checkoutInput.value <= e.target.value) {
                checkoutInput.value = minOut;
            }
        });
    }
}

/**
 * Quick Booking Search Handler
 */
function initQuickBookingForm() {
    const quickForm = document.getElementById('quick-booking-form');
    if (!quickForm) return;

    quickForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const dest = document.getElementById('quick-destination')?.value || 'LUXORA Grand';
        const roomType = document.getElementById('quick-room-type')?.value || 'all';
        const checkin = document.getElementById('quick-checkin')?.value;
        const checkout = document.getElementById('quick-checkout')?.value;
        const guests = document.getElementById('quick-guests')?.value;

        showToast(`Searching rooms at ${dest} for ${guests} guests...`, 'LUXORA Search');

        setTimeout(() => {
            window.location.href = `booking.html?type=${encodeURIComponent(roomType)}&checkin=${checkin}&checkout=${checkout}&guests=${guests}`;
        }, 1000);
    });
}

/**
 * Room Category Filter
 */
function initRoomFilters() {
    const filterButtons = document.querySelectorAll('.room-filter-btn');
    const roomCards = document.querySelectorAll('.room-card-item');

    if (!filterButtons.length) return;

    filterButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            filterButtons.forEach(b => b.classList.remove('active', 'btn-gold'));
            filterButtons.forEach(b => b.classList.add('btn-outline-gold'));
            
            btn.classList.remove('btn-outline-gold');
            btn.classList.add('active', 'btn-gold');

            const category = btn.getAttribute('data-filter');

            roomCards.forEach(card => {
                if (category === 'all' || card.getAttribute('data-category') === category) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
}

/**
 * Food Menu Category Filter
 */
function initFoodFilters() {
    const foodBtnFilter = document.querySelectorAll('.food-filter-btn');
    const foodCards = document.querySelectorAll('.food-card-item');

    if (!foodBtnFilter.length) return;

    foodBtnFilter.forEach(btn => {
        btn.addEventListener('click', () => {
            foodBtnFilter.forEach(b => b.classList.remove('active', 'btn-gold'));
            foodBtnFilter.forEach(b => b.classList.add('btn-outline-gold'));

            btn.classList.remove('btn-outline-gold');
            btn.classList.add('active', 'btn-gold');

            const category = btn.getAttribute('data-food-filter');

            foodCards.forEach(card => {
                if (category === 'all' || card.getAttribute('data-food-category') === category) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
}

/**
 * Shopping Cart & Add to Cart Handler
 */
function initCartSystem() {
    const cartBadge = document.getElementById('nav-cart-count');
    const storedCart = localStorage.getItem('luxora_cart');
    if (storedCart) {
        cartItems = JSON.parse(storedCart);
        cartCount = cartItems.length;
        if (cartBadge) cartBadge.textContent = cartCount;
    }

    renderCartModal();

    window.addToCart = function(title, price, category, itemCode) {
        cartItems.push({ title, price, category, itemCode });
        cartCount = cartItems.length;
        localStorage.setItem('luxora_cart', JSON.stringify(cartItems));

        if (cartBadge) cartBadge.textContent = cartCount;
        renderCartModal();
        showToast(`"${title}" added to your Dining Order!`, 'Cart Updated');
    };
}

function renderCartModal() {
    const container = document.getElementById('cart-items-container');
    if (!container) return;

    let items = [];
    const storedCart = localStorage.getItem('luxora_cart');
    if (storedCart) items = JSON.parse(storedCart);

    if (!items.length) {
        container.innerHTML = '<div class="text-center text-muted py-3">Your dining cart is empty. Add dishes from our Gourmet Dining menu!</div>';
        return;
    }

    const groupedItems = new Map();
    items.forEach(item => {
        const key = `${item.itemCode || ''}:${item.title}`;
        const current = groupedItems.get(key);
        if (current) current.quantity += 1;
        else groupedItems.set(key, { ...item, quantity: 1 });
    });

    container.innerHTML = Array.from(groupedItems.values()).map(item => `
        <div class="d-flex justify-content-between gap-3 py-2 border-bottom border-secondary">
            <span>${escapeCartText(item.title)} <span class="text-muted">× ${item.quantity}</span></span>
            <span class="text-gold text-nowrap">₹${(Number(item.price) * item.quantity).toLocaleString('en-IN')}</span>
        </div>
    `).join('');
}

function escapeCartText(value) {
    return String(value ?? '').replace(/[&<>"']/g, character => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[character]);
}

function clearDiningCart() {
    cartItems = [];
    cartCount = 0;
    localStorage.removeItem('luxora_cart');
    const cartBadge = document.getElementById('nav-cart-count');
    if (cartBadge) cartBadge.textContent = '0';
    renderCartModal();
}

function openDiningCheckout() {
    if (!localStorage.getItem('luxora_cart')) {
        showToast('Add at least one dish before continuing to checkout.', 'Cart is Empty');
        return;
    }
    window.location.href = 'cart.html';
}

window.clearDiningCart = clearDiningCart;
window.openDiningCheckout = openDiningCheckout;

/**
 * Image Gallery Lightbox Modal Setup
 */
function initGalleryLightbox() {
    const galleryItems = document.querySelectorAll('.gallery-item');
    const modalImage = document.getElementById('galleryModalImage');
    const modalCaption = document.getElementById('galleryModalCaption');

    if (!galleryItems.length || !modalImage) return;

    galleryItems.forEach(item => {
        item.addEventListener('click', () => {
            const img = item.querySelector('img');
            const title = item.getAttribute('data-title') || 'LUXORA View';
            if (img) {
                modalImage.src = img.src;
                if (modalCaption) modalCaption.textContent = title;
                const modalElem = document.getElementById('galleryModal');
                if (modalElem) {
                    const bsModal = new bootstrap.Modal(modalElem);
                    bsModal.show();
                }
            }
        });
    });
}

/**
 * Toast Notification System
 */
function showToast(message, title = 'LUXORA Concierge') {
    let toast = document.getElementById('lux-global-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'lux-global-toast';
        toast.className = 'lux-toast';
        document.body.appendChild(toast);
    }

    toast.innerHTML = `
        <div style="font-size: 1.5rem; color: var(--gold-primary);">
            <i class="fa-solid fa-crown"></i>
        </div>
        <div>
            <div style="font-weight: 700; font-size: 0.9rem; color: var(--gold-light);">${title}</div>
            <div style="font-size: 0.85rem; color: #cbd5e1;">${message}</div>
        </div>
    `;

    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3200);
}

window.showToast = showToast;
