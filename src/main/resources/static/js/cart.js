/**
 * LUXORA — Food Cart & Live Order Tracking Engine
 * Structured for future Spring Boot REST API (/api/v1/orders) & MySQL Integration
 */

let cartData = [];
let activeOrderTracking = null;

document.addEventListener('DOMContentLoaded', () => {
    loadCartFromStorage();
    renderCartView();
    initOrderTrackingSimulation();
});

/**
 * Load Cart Items from LocalStorage
 */
function loadCartFromStorage() {
    const stored = localStorage.getItem('luxora_cart');
    if (stored) {
        const rawItems = JSON.parse(stored);
        
        // Group items by title to form structured items with quantities
        const itemMap = new Map();
        rawItems.forEach(item => {
            const key = `${item.itemCode || ''}:${item.title}`;
            if (itemMap.has(key)) {
                itemMap.get(key).quantity += 1;
            } else {
                itemMap.set(key, {
                    title: item.title,
                    price: item.price,
                    category: item.category || 'Gourmet Dish',
                    itemCode: item.itemCode || null,
                    quantity: 1,
                    image: 'images/food-breakfast.png'
                });
            }
        });
        cartData = Array.from(itemMap.values());
    } else {
        cartData = [];
    }
}

/**
 * Render Cart Items & Summary Table
 */
function renderCartView() {
    const container = document.getElementById('cart-items-list');
    const badgeCount = document.getElementById('nav-cart-count');
    const totalItemsBadge = document.getElementById('cart-total-items-badge');

    if (badgeCount) badgeCount.textContent = cartData.reduce((sum, i) => sum + i.quantity, 0);
    if (totalItemsBadge) totalItemsBadge.textContent = `${cartData.reduce((sum, i) => sum + i.quantity, 0)} Items`;

    if (!container) return;

    if (cartData.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="fa-solid fa-cart-shopping text-gold fs-1 mb-3"></i>
                <h3 class="text-white font-serif mb-2">Your Dining Cart is Empty</h3>
                <p class="text-muted mb-4">Explore our Gourmet In-Room Dining menu to add delicious dishes!</p>
                <a href="dining.html" class="btn-gold"><i class="fa-solid fa-utensils me-2"></i> Browse Food Menu</a>
            </div>
        `;
        calculateCartTotals();
        return;
    }

    container.innerHTML = cartData.map((item, idx) => `
        <div class="p-3 mb-3 rounded-4 border border-secondary d-flex flex-wrap align-items-center justify-content-between gap-3" style="background: var(--bg-surface);">
            <div class="d-flex align-items-center gap-3">
                <img src="${item.image}" class="rounded-3 object-fit-cover" style="width: 70px; height: 70px;" alt="${item.title}">
                <div>
                    <h5 class="text-white font-serif mb-1 fs-6">${item.title}</h5>
                    <span class="badge bg-dark border border-secondary text-gold font-sans" style="font-size:0.7rem;">${item.category}</span>
                    <div class="text-gold fw-bold mt-1">₹${item.price}</div>
                </div>
            </div>

            <!-- Quantity Controls -->
            <div class="d-flex align-items-center gap-3">
                <div class="d-flex align-items-center gap-2 p-1 rounded-pill bg-dark border border-secondary">
                    <button class="btn btn-sm btn-outline-warning rounded-circle px-2 py-0 fw-bold" onclick="updateItemQuantity(${idx}, -1)">-</button>
                    <span class="text-white fw-bold px-2">${item.quantity}</span>
                    <button class="btn btn-sm btn-outline-warning rounded-circle px-2 py-0 fw-bold" onclick="updateItemQuantity(${idx}, 1)">+</button>
                </div>
                <div class="fs-5 font-serif fw-bold text-white text-nowrap" style="min-width: 80px; text-align: right;">
                    ₹${(item.price * item.quantity).toLocaleString()}
                </div>
                <button class="btn btn-sm text-danger fs-5 border-0 bg-transparent ms-2" onclick="removeCartItem(${idx})" title="Remove Item">
                    <i class="fa-solid fa-trash-can"></i>
                </button>
            </div>
        </div>
    `).join('');

    calculateCartTotals();
}

/**
 * Update Item Quantity
 */
function updateItemQuantity(index, delta) {
    if (cartData[index]) {
        cartData[index].quantity += delta;
        if (cartData[index].quantity <= 0) {
            cartData.splice(index, 1);
        }
        syncCartToStorage();
        renderCartView();
    }
}

/**
 * Remove Single Item
 */
function removeCartItem(index) {
    if (cartData[index]) {
        const title = cartData[index].title;
        cartData.splice(index, 1);
        syncCartToStorage();
        renderCartView();
        if (window.showToast) window.showToast(`Removed "${title}" from cart.`, 'Cart Updated');
    }
}

/**
 * Clear Entire Cart
 */
function clearEntireCart() {
    cartData = [];
    localStorage.removeItem('luxora_cart');
    renderCartView();
    if (window.showToast) window.showToast('Dining cart cleared.', 'Cart Reset');
}

/**
 * Sync Internal Array back to Flat LocalStorage Format
 */
function syncCartToStorage() {
    const flatItems = [];
    cartData.forEach(item => {
        for (let i = 0; i < item.quantity; i++) {
            flatItems.push({ title: item.title, price: item.price, category: item.category, itemCode: item.itemCode });
        }
    });
    localStorage.setItem('luxora_cart', JSON.stringify(flatItems));
}

/**
 * Calculate Food Subtotal, Tax (5%), Delivery Fee & Grand Total
 */
function calculateCartTotals() {
    const subtotal = cartData.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const deliveryFee = subtotal > 0 ? (subtotal >= 500 ? 0 : 50) : 0;
    const gstTax = subtotal * 0.05; // 5% GST on food
    const grandTotal = subtotal + deliveryFee + gstTax;

    const elemSubtotal = document.getElementById('cart-subtotal');
    const elemFee = document.getElementById('cart-delivery-fee');
    const elemTax = document.getElementById('cart-tax');
    const elemTotal = document.getElementById('cart-grand-total');

    if (elemSubtotal) elemSubtotal.textContent = `₹${subtotal.toLocaleString()}`;
    if (elemFee) elemFee.textContent = deliveryFee === 0 ? (subtotal > 0 ? 'FREE (VIP Perk)' : '₹0') : `₹${deliveryFee}`;
    if (elemTax) elemTax.textContent = `₹${gstTax.toLocaleString()}`;
    if (elemTotal) elemTotal.textContent = `₹${grandTotal.toLocaleString()}`;
}

/**
 * Submit the cart as a persisted in-room dining order
 */
async function handleFoodCheckout(e) {
    e.preventDefault();

    if (!window.requireCustomerLogin || !window.requireCustomerLogin()) {
        return;
    }

    if (cartData.length === 0) {
        alert('Your cart is empty. Please add items from the dining menu first.');
        return;
    }

    const roomNumber = document.getElementById('order-room-number')?.value;
    const guestName = document.getElementById('order-guest-name')?.value;
    const phone = document.getElementById('order-guest-phone')?.value;
    if (!roomNumber || !guestName || !phone) {
        alert('Please complete all required room and guest details.');
        return;
    }

    const submitButton = e.currentTarget.querySelector('button[type="submit"]');
    if (submitButton) submitButton.disabled = true;

    try {
        const [userResponse, bookingsResponse, foodResponse] = await Promise.all([
            fetch('/api/auth/me', { credentials: 'same-origin' }),
            fetch('/api/bookings/mine', { credentials: 'same-origin' }),
            fetch('/api/food-items/available', { credentials: 'same-origin' })
        ]);
        if (userResponse.status === 401) throw new Error('Please sign in again before placing your order.');
        if (!userResponse.ok || !bookingsResponse.ok || !foodResponse.ok) {
            throw new Error('Could not load your account, reservation, or menu. Please try again.');
        }

        const [user, bookings, foodItems] = await Promise.all([
            userResponse.json(), bookingsResponse.json(), foodResponse.json()
        ]);
        const normalizeRoom = value => String(value).toLowerCase().replace(/^(suite|room)\s*/i, '').replace(/^#/, '').trim();
        const booking = bookings.find(item =>
            item.status !== 'CANCELLED' &&
            normalizeRoom(item.roomNumber) === normalizeRoom(roomNumber)
        );
        if (!booking) {
            throw new Error('No reservation was found for that room. Enter the room number from your active reservation.');
        }

        const foodByCode = new Map(foodItems.map(item => [item.itemCode, item]));
        const orderItems = cartData.map(item => {
            const foodItem = item.itemCode
                ? foodByCode.get(item.itemCode)
                : foodItems.find(candidate => candidate.name === item.title);
            if (!foodItem) throw new Error(`"${item.title}" is not available for ordering right now.`);
            return { foodItemId: foodItem.id, quantity: item.quantity };
        });

        const response = await window.requestWithCsrf('/api/orders', {
            method: 'POST',
            body: JSON.stringify({
                customerId: user.id,
                bookingId: booking.id,
                items: orderItems
            })
        });
        if (!response.ok) {
            const message = response.headers.get('content-type')?.includes('application/json')
                ? (await response.json()).message || 'Could not place your dining order.'
                : `Could not place your dining order (HTTP ${response.status}).`;
            throw new Error(message);
        }
        const result = await response.json();

        const grandTotalText = new Intl.NumberFormat('en-IN', {
            style: 'currency', currency: 'INR', maximumFractionDigits: 2
        }).format(Number(result.total));
        activeOrderTracking = {
            orderId: result.orderCode,
            roomNumber: booking.roomNumber,
            guestName,
            itemsCount: cartData.reduce((sum, item) => sum + item.quantity, 0),
            total: grandTotalText,
            statusStep: 1,
            estMinutes: 25
        };
        localStorage.setItem('luxora_active_order', JSON.stringify(activeOrderTracking));
        cartData = [];
        localStorage.removeItem('luxora_cart');
        renderCartView();
        renderOrderTrackingUI();
        if (window.showToast) window.showToast('Your order has been sent to the kitchen.', 'Order Placed');
        if (submitButton) submitButton.disabled = false;
    } catch (error) {
        if (window.showToast) window.showToast(error.message, 'Order Not Placed');
        else window.alert(error.message);
        if (submitButton) submitButton.disabled = false;
    }
}

/**
 * Render Live Order Tracking Stepper UI
 */
function renderOrderTrackingUI() {
    const trackingContainer = document.getElementById('order-tracking-card');
    const storedOrder = localStorage.getItem('luxora_active_order');

    if (!trackingContainer) return;

    if (!storedOrder) {
        trackingContainer.style.display = 'none';
        return;
    }

    const order = JSON.parse(storedOrder);
    trackingContainer.style.display = 'block';

    document.getElementById('track-order-id').textContent = order.orderId;
    document.getElementById('track-room-num').textContent = `Room #${order.roomNumber}`;
    document.getElementById('track-guest-name').textContent = order.guestName;
    document.getElementById('track-total-amount').textContent = order.total;

    updateTrackingStepperVisuals(order.statusStep);
}

/**
 * Update Stepper UI Visuals
 */
function updateTrackingStepperVisuals(step) {
    const step1 = document.getElementById('step-placed');
    const step2 = document.getElementById('step-preparing');
    const step3 = document.getElementById('step-delivery');
    const step4 = document.getElementById('step-delivered');

    const statusBadge = document.getElementById('track-status-badge');

    // Reset styles
    [step1, step2, step3, step4].forEach((el, idx) => {
        if (!el) return;
        if (idx + 1 <= step) {
            el.classList.add('text-gold', 'fw-bold');
            el.querySelector('.stepper-icon').style.background = 'var(--gold-primary)';
            el.querySelector('.stepper-icon').style.color = '#000';
        } else {
            el.classList.remove('text-gold', 'fw-bold');
            el.querySelector('.stepper-icon').style.background = 'rgba(255,255,255,0.1)';
            el.querySelector('.stepper-icon').style.color = '#94a3b8';
        }
    });

    if (statusBadge) {
        if (step === 1) statusBadge.textContent = 'Order Placed (Received at Kitchen)';
        else if (step === 2) statusBadge.textContent = 'Preparing (Chef cooking your meal)';
        else if (step === 3) statusBadge.textContent = 'Out for Delivery (Butler on the way)';
        else if (step === 4) statusBadge.textContent = 'Delivered (Enjoy your meal!)';
    }
}

/**
 * Simulate Real-Time Order Status Progression
 */
function initOrderTrackingSimulation() {
    renderOrderTrackingUI();

    const storedOrder = localStorage.getItem('luxora_active_order');
    if (!storedOrder) return;

    let order = JSON.parse(storedOrder);

    // Auto-advance step every 8 seconds for demonstration
    const interval = setInterval(() => {
        if (order.statusStep < 4) {
            order.statusStep += 1;
            localStorage.setItem('luxora_active_order', JSON.stringify(order));
            updateTrackingStepperVisuals(order.statusStep);
            if (window.showToast) {
                const statuses = ['', 'Order Placed', 'Chef is preparing your food!', 'Butler is on the way to your room!', 'Food delivered to your room!'];
                window.showToast(statuses[order.statusStep], 'Order Status Update');
            }
        } else {
            clearInterval(interval);
        }
    }, 8000);
}

window.updateItemQuantity = updateItemQuantity;
window.removeCartItem = removeCartItem;
window.clearEntireCart = clearEntireCart;
window.handleFoodCheckout = handleFoodCheckout;
