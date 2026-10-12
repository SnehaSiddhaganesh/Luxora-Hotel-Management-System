/**
 * LUXORA — Dining Menu & In-Room Food Ordering Engine
 */

const DINING_MENU_DATA = [
    // Breakfast
    {
        id: 'food-101',
        name: 'Masala Dosa',
        category: 'breakfast',
        categoryName: 'Breakfast',
        image: 'images/dish-breakfast.jpg',
        price: 180,
        rating: 4.8,
        reviewsCount: 142,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Crispy golden rice crepe stuffed with spiced potato masala, served with coconut chutney & hot sambar.',
        ingredients: ['Rice Batter', 'Spiced Potato', 'Curry Leaves', 'Coconut Chutney', 'Sambar']
    },
    {
        id: 'food-102',
        name: 'Idli (3 Pcs)',
        category: 'breakfast',
        categoryName: 'Breakfast',
        image: 'images/dish-breakfast.jpg',
        price: 140,
        rating: 4.7,
        reviewsCount: 98,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Steamed fluffy rice & lentil cakes served with traditional South Indian chutneys and lentil sambar.',
        ingredients: ['Fermented Rice & Urad Dal', 'Mustard Seeds', 'Coconut Chutney', 'Sambar']
    },
    {
        id: 'food-103',
        name: 'Indori Poha',
        category: 'breakfast',
        categoryName: 'Breakfast',
        image: 'images/dish-breakfast.jpg',
        price: 120,
        rating: 4.6,
        reviewsCount: 74,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Flattened rice tempered with mustard, turmeric, curry leaves, crunchy peanuts, and fresh pomegranate.',
        ingredients: ['Flattened Rice', 'Peanuts', 'Turmeric', 'Green Chilli', 'Sev']
    },
    {
        id: 'food-104',
        name: 'Artisan Veg Sandwich',
        category: 'breakfast',
        categoryName: 'Breakfast',
        image: 'images/dish-breakfast.jpg',
        price: 160,
        rating: 4.7,
        reviewsCount: 65,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Grilled multi-grain sourdough bread stuffed with cucumber, tomato, paneer, and fresh mint pesto.',
        ingredients: ['Sourdough Bread', 'Paneer Slices', 'Mint Pesto', 'Cucumber & Tomato']
    },

    // Main Course
    {
        id: 'food-201',
        name: 'Paneer Butter Masala',
        category: 'main',
        categoryName: 'Main Course',
        image: 'images/dish-main-course.jpg',
        price: 340,
        rating: 4.9,
        reviewsCount: 210,
        isVeg: true,
        spiceLevel: '🌶️ Medium',
        description: 'Fresh cottage cheese cubes simmered in a rich, velvety tomato, cashew nut & butter gravy.',
        ingredients: ['Cottage Cheese (Paneer)', 'Cashew Butter', 'Tomato Puree', 'Kasoori Methi', 'Fresh Cream']
    },
    {
        id: 'food-202',
        name: 'Royal Veg Biryani',
        category: 'main',
        categoryName: 'Main Course',
        image: 'images/dish-main-course.jpg',
        price: 320,
        rating: 4.8,
        reviewsCount: 185,
        isVeg: true,
        spiceLevel: '🌶️ Medium',
        description: 'Aromatic long-grain basmati rice dum-cooked with farm-fresh vegetables, saffron, and whole spices.',
        ingredients: ['Basmati Rice', 'Assorted Vegetables', 'Saffron', 'Desi Ghee', 'Fried Onions']
    },
    {
        id: 'food-203',
        name: 'Dal Tadka (Desi Ghee)',
        category: 'main',
        categoryName: 'Main Course',
        image: 'images/dish-main-course.jpg',
        price: 240,
        rating: 4.7,
        reviewsCount: 130,
        isVeg: true,
        spiceLevel: '🌶️ Medium',
        description: 'Yellow arhar lentils tempered with garlic, cumin, red chillies, and aromatic cow ghee.',
        ingredients: ['Yellow Lentils', 'Cow Ghee', 'Cumin Seeds', 'Garlic & Ginger', 'Coriander']
    },
    {
        id: 'food-204',
        name: 'Butter Roti (2 Pcs)',
        category: 'main',
        categoryName: 'Main Course',
        image: 'images/dish-main-course.jpg',
        price: 40,
        rating: 4.9,
        reviewsCount: 310,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Freshly baked whole wheat tandoori flatbread brushed generously with farm butter.',
        ingredients: ['Whole Wheat Atta', 'Farm Butter']
    },

    // Snacks
    {
        id: 'food-301',
        name: 'Charbroiled Paneer Tikka',
        category: 'snacks',
        categoryName: 'Snacks',
        image: 'images/dish-spicy.jpg',
        price: 280,
        rating: 4.8,
        reviewsCount: 160,
        isVeg: true,
        spiceLevel: '🌶️ Medium',
        description: 'Juicy paneer cubes marinated in hung curd, garlic, and artisan spices, clay-oven roasted to perfection.',
        ingredients: ['Hung Curd', 'Paneer Cubes', 'Bell Peppers', 'Mustard Oil', 'Mint Dip']
    },
    {
        id: 'food-302',
        name: 'Chilli Paneer (Dry/Gravy)',
        category: 'snacks',
        categoryName: 'Snacks',
        image: 'images/dish-spicy.jpg',
        price: 290,
        rating: 4.7,
        reviewsCount: 145,
        isVeg: true,
        spiceLevel: '🌶️🌶️ Spicy',
        description: 'Crispy fried cottage cheese tossed with bell peppers, spring onions, and spicy Indo-Chinese soy sauce.',
        ingredients: ['Paneer', 'Capsicum', 'Spring Onion', 'Dark Soy Sauce', 'Chilli Paste']
    },
    {
        id: 'food-303',
        name: 'Wok Schezwan Noodles',
        category: 'snacks',
        categoryName: 'Snacks',
        image: 'images/dish-spicy.jpg',
        price: 260,
        rating: 4.6,
        reviewsCount: 112,
        isVeg: true,
        spiceLevel: '🌶️🌶️ Spicy',
        description: 'High-flame wok tossed noodles loaded with crunchy julienne vegetables in fiery Schezwan chilli garlic sauce.',
        ingredients: ['Eggless Noodles', 'Schezwan Sauce', 'Carrot & Cabbage', 'Garlic']
    },

    // Spicy
    {
        id: 'food-401',
        name: 'Fiery Chef Spicy Biryani',
        category: 'spicy',
        categoryName: 'Spicy Special',
        image: 'images/dish-spicy.jpg',
        price: 360,
        rating: 4.9,
        reviewsCount: 195,
        isVeg: true,
        spiceLevel: '🌶️🌶️🌶️ Fiery Hot',
        description: 'For spice connoisseurs! Fire-tempered basmati rice infused with Guntur red chillies, black pepper & biryani masala.',
        ingredients: ['Basmati Rice', 'Guntur Red Chillies', 'Black Pepper', 'Mint & Coriander', 'Desi Ghee']
    },

    // Sweet & Desserts
    {
        id: 'food-501',
        name: 'Warm Gulab Jamun (2 Pcs)',
        category: 'sweet',
        categoryName: 'Sweet & Desserts',
        image: 'images/dish-sweets.jpg',
        price: 150,
        rating: 4.9,
        reviewsCount: 230,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Soft melt-in-mouth milk solid dumplings fried in ghee and soaked in warm cardamom rose syrup.',
        ingredients: ['Khoya/Mawa', 'Rose Syrup', 'Cardamom', 'Pistachio Flakes']
    },
    {
        id: 'food-502',
        name: 'Saffron Rasmalai (2 Pcs)',
        category: 'sweet',
        categoryName: 'Sweet & Desserts',
        image: 'images/dish-sweets.jpg',
        price: 180,
        rating: 4.9,
        reviewsCount: 190,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Soft cottage cheese patties soaked in chilled, saffron-infused cardamom rabri milk topped with nuts.',
        ingredients: ['Chhena Discs', 'Saffron Milk (Rabri)', 'Almonds & Pistachios', 'Cardamom']
    },
    {
        id: 'food-503',
        name: 'Belgian Chocolate Cake',
        category: 'desserts',
        categoryName: 'Sweet & Desserts',
        image: 'images/dish-sweets.jpg',
        price: 220,
        rating: 4.8,
        reviewsCount: 140,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Multi-layer Belgian dark chocolate sponge cake with rich ganache frosting and chocolate curls.',
        ingredients: ['Dark Cocoa', 'Belgian Chocolate Ganache', 'Butter', 'Vanilla Extract']
    },
    {
        id: 'food-504',
        name: 'Artisan Gourmet Ice Cream',
        category: 'desserts',
        categoryName: 'Sweet & Desserts',
        image: 'images/dish-sweets.jpg',
        price: 160,
        rating: 4.7,
        reviewsCount: 120,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Double scoop of handcrafted Madagascar vanilla bean and rich Belgian dark chocolate gelato.',
        ingredients: ['Whole Cream', 'Vanilla Bean Pods', 'Dark Chocolate Chips']
    },

    // Beverages
    {
        id: 'food-601',
        name: 'Filter Coffee / Cappuccino',
        category: 'beverages',
        categoryName: 'Beverages',
        image: 'images/food-breakfast.png',
        price: 120,
        rating: 4.8,
        reviewsCount: 280,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Freshly decocted South Indian filter coffee or frothy Italian espresso cappuccino.',
        ingredients: ['Arabica Coffee Beans', 'Steamed Whole Milk', 'Sugar']
    },
    {
        id: 'food-602',
        name: 'Masala Ginger Tea',
        category: 'beverages',
        categoryName: 'Beverages',
        image: 'images/food-breakfast.png',
        price: 90,
        rating: 4.9,
        reviewsCount: 310,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Assam tea leaves simmered with fresh crushed ginger, lemongrass, cardamom, and milk.',
        ingredients: ['Assam Black Tea', 'Crushed Ginger', 'Cardamom', 'Milk']
    },
    {
        id: 'food-603',
        name: 'Cold-Pressed Fresh Juice',
        category: 'beverages',
        categoryName: 'Beverages',
        image: 'images/food-breakfast.png',
        price: 150,
        rating: 4.8,
        reviewsCount: 175,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Choice of 100% natural cold-pressed Valencia Orange, Watermelon, or Green Detox juice.',
        ingredients: ['100% Fresh Fruit (No added sugar or water)']
    },
    {
        id: 'food-604',
        name: 'Belgian Chocolate Milkshake',
        category: 'beverages',
        categoryName: 'Beverages',
        image: 'images/food-breakfast.png',
        price: 180,
        rating: 4.9,
        reviewsCount: 205,
        isVeg: true,
        spiceLevel: '🌶️ Mild',
        description: 'Thick creamy milkshake blended with dark Belgian chocolate fudge and vanilla scoop.',
        ingredients: ['Belgian Chocolate', 'Whole Milk', 'Vanilla Ice Cream', 'Whipped Cream']
    }
];

let filteredFoodMenu = [...DINING_MENU_DATA];
let modalSelectedQty = 1;

document.addEventListener('DOMContentLoaded', () => {
    initDiningPage();
});

function initDiningPage() {
    renderFoodMenu(filteredFoodMenu);
    setupDiningFilterListeners();
}

/**
 * Render Food Menu Grid
 */
function renderFoodMenu(foodList) {
    const container = document.getElementById('food-grid-container');
    const countDisplay = document.getElementById('food-count-text');

    if (!container) return;

    if (countDisplay) {
        countDisplay.textContent = `Showing ${foodList.length} of ${DINING_MENU_DATA.length} Gourmet Dishes`;
    }

    if (foodList.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="p-5 rounded-4" style="background: var(--bg-surface); border: 1px dashed var(--border-gold);">
                    <i class="fa-solid fa-utensils text-gold fs-1 mb-3"></i>
                    <h3 class="text-white font-serif mb-2">No Dishes Found</h3>
                    <p class="text-muted mb-4">Try searching for a different dish name or changing the category filter.</p>
                    <button class="btn-gold" onclick="resetFoodFilters()">Reset All Filters</button>
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = foodList.map(item => `
        <div class="col-lg-3 col-md-6 food-card-item">
            <div class="food-card">
                <div class="food-img-wrapper">
                    <img src="${item.image}" alt="${item.name}">
                    <span class="food-badge"><i class="fa-solid fa-tag text-gold me-1"></i> ${item.categoryName}</span>
                    
                    <!-- Veg / Non-Veg indicator -->
                    <span class="position-absolute top-0 end-0 m-2 px-2 py-1 rounded-pill bg-dark border border-secondary text-light font-sans small">
                        <i class="fa-solid fa-circle text-success me-1" style="font-size:0.6rem;"></i> 100% Veg
                    </span>
                </div>
                
                <div class="p-3 d-flex flex-column flex-grow-1">
                    <div class="d-flex justify-content-between align-items-start mb-1">
                        <h4 class="text-white fs-6 font-serif mb-0">${item.name}</h4>
                        <span class="rating-stars text-nowrap"><i class="fa-solid fa-star"></i> ${item.rating}</span>
                    </div>

                    <div class="d-flex align-items-center gap-2 mb-2">
                        <span class="badge bg-dark border border-secondary text-warning font-sans" style="font-size:0.7rem;">${item.spiceLevel}</span>
                    </div>

                    <p class="text-muted small mb-3 flex-grow-1" style="font-size:0.8rem; line-height:1.4;">${item.description}</p>

                    <div class="mt-auto pt-2 border-top border-secondary d-flex align-items-center justify-content-between">
                        <div class="fs-5 fw-bold text-gold font-serif">₹${item.price}</div>
                        <div class="d-flex gap-1">
                            <button class="btn-outline-gold btn-sm py-1 px-2" onclick="openFoodDetailsModal('${item.id}')" title="View Ingredients & Customize">
                                <i class="fa-solid fa-circle-info"></i>
                            </button>
                            <button class="btn-gold btn-sm py-1 px-2" onclick="addToCart('${item.name}', ${item.price}, '${item.categoryName}', '${item.id}')">
                                <i class="fa-solid fa-plus me-1"></i> Add
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

/**
 * Setup Event Listeners for Filters
 */
function setupDiningFilterListeners() {
    const searchInput = document.getElementById('dining-search-input');
    const categoryFilterBtns = document.querySelectorAll('.dining-category-btn');

    const applyFilters = () => {
        const query = searchInput ? searchInput.value.toLowerCase().trim() : '';
        const activeBtn = document.querySelector('.dining-category-btn.active');
        const selectedCategory = activeBtn ? activeBtn.getAttribute('data-category') : 'all';

        filteredFoodMenu = DINING_MENU_DATA.filter(item => {
            const matchesSearch = item.name.toLowerCase().includes(query) || 
                                  item.description.toLowerCase().includes(query) ||
                                  item.ingredients.some(ing => ing.toLowerCase().includes(query));
            const matchesCategory = selectedCategory === 'all' || item.category === selectedCategory;

            return matchesSearch && matchesCategory;
        });

        renderFoodMenu(filteredFoodMenu);
    };

    if (searchInput) searchInput.addEventListener('input', applyFilters);

    categoryFilterBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            categoryFilterBtns.forEach(b => b.classList.remove('active', 'btn-gold'));
            categoryFilterBtns.forEach(b => b.classList.add('btn-outline-gold'));

            btn.classList.remove('btn-outline-gold');
            btn.classList.add('active', 'btn-gold');

            applyFilters();
        });
    });
}

function resetFoodFilters() {
    const searchInput = document.getElementById('dining-search-input');
    if (searchInput) searchInput.value = '';

    const firstBtn = document.querySelector('.dining-category-btn[data-category="all"]');
    if (firstBtn) firstBtn.click();

    filteredFoodMenu = [...DINING_MENU_DATA];
    renderFoodMenu(filteredFoodMenu);
}

/**
 * Open Food Details Modal
 */
function openFoodDetailsModal(foodId) {
    const item = DINING_MENU_DATA.find(f => f.id === foodId);
    if (!item) return;

    modalSelectedQty = 1;

    document.getElementById('modal-food-title').textContent = item.name;
    document.getElementById('modal-food-img').src = item.image;
    document.getElementById('modal-food-price').textContent = `₹${item.price}`;
    document.getElementById('modal-food-desc').textContent = item.description;
    document.getElementById('modal-food-spice').textContent = item.spiceLevel;
    document.getElementById('modal-food-category').textContent = item.categoryName;
    document.getElementById('modal-food-qty').textContent = modalSelectedQty;

    // Ingredients List
    const elemIng = document.getElementById('modal-food-ingredients');
    if (elemIng && item.ingredients) {
        elemIng.innerHTML = item.ingredients.map(ing => `
            <span class="badge bg-dark border border-secondary text-light font-sans p-2 me-1 mb-1">
                <i class="fa-solid fa-leaf text-success me-1"></i> ${ing}
            </span>
        `).join('');
    }

    // Modal Add Button Click
    const addBtn = document.getElementById('modal-food-add-btn');
    if (addBtn) {
        addBtn.onclick = () => {
            const instructions = document.getElementById('modal-food-instructions')?.value || '';
            for (let i = 0; i < modalSelectedQty; i++) {
                addToCart(`${item.name} ${instructions ? '(' + instructions + ')' : ''}`, item.price, item.categoryName, item.id);
            }
            const modalElem = document.getElementById('foodDetailsModal');
            if (modalElem) bootstrap.Modal.getInstance(modalElem).hide();
        };
    }

    const modalElem = document.getElementById('foodDetailsModal');
    if (modalElem) {
        const bsModal = new bootstrap.Modal(modalElem);
        bsModal.show();
    }
}

/**
 * Quantity increment/decrement helpers for food modal
 */
window.updateModalQty = function(change) {
    modalSelectedQty += change;
    if (modalSelectedQty < 1) modalSelectedQty = 1;
    const qtyElem = document.getElementById('modal-food-qty');
    if (qtyElem) qtyElem.textContent = modalSelectedQty;
};

window.resetFoodFilters = resetFoodFilters;
window.openFoodDetailsModal = openFoodDetailsModal;
