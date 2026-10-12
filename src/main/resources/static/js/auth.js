document.addEventListener('DOMContentLoaded', () => {
    initAuthForms();
    refreshSession();
});

window.addEventListener('pageshow', event => {
    if (event.persisted) refreshSession();
});

async function requestWithCsrf(url, options = {}) {
    try {
        const csrfResponse = await fetch('/api/auth/csrf', { credentials: 'same-origin' });
        if (!csrfResponse.ok) throw new Error('Unable to start a secure session. Please reload and try again.');
        const csrf = await csrfResponse.json();

        return await fetch(url, {
            ...options,
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                [csrf.headerName]: csrf.token,
                ...options.headers
            }
        });
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Cannot connect to the hotel server. Start the Spring Boot app and open this page from the same server (locally: http://localhost:8080/login.html), not from a file or Live Server URL.');
        }
        throw error;
    }
}

window.requestWithCsrf = requestWithCsrf;

/**
 * Initialize Login & Register Forms with Validation
 */
function initAuthForms() {
    const loginForm = document.getElementById('lux-login-form');
    const registerForm = document.getElementById('lux-register-form');

    if (loginForm) {
        loginForm.addEventListener('submit', handleLoginSubmit);
    }

    if (registerForm) {
        registerForm.addEventListener('submit', handleRegisterSubmit);
    }

    document.querySelectorAll('[data-luxora-login-form]').forEach(form => {
        form.addEventListener('submit', handleEmbeddedLoginSubmit);
    });
    document.querySelectorAll('[data-luxora-register-form]').forEach(form => {
        form.addEventListener('submit', handleEmbeddedRegisterSubmit);
    });
}

async function handleEmbeddedLoginSubmit(e) {
    e.preventDefault();
    const email = e.currentTarget.querySelector('input[type="email"]')?.value.trim();
    const password = e.currentTarget.querySelector('input[type="password"]')?.value;
    try {
        const response = await requestWithCsrf('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        const user = await response.json();
        if (!response.ok) throw new Error(user.message || 'Login failed. Check your email and password.');
        storeDisplayProfile(user);
        window.location.href = user.role === 'ADMIN' || user.role === 'RECEPTIONIST'
            ? 'admin.html'
            : 'dashboard.html';
    } catch (error) {
        showAuthToast(error.message);
    }
}

async function handleEmbeddedRegisterSubmit(e) {
    e.preventDefault();
    const form = e.currentTarget;
    const nameParts = (form.querySelector('input[type="text"]')?.value || '').trim().split(/\s+/);
    const firstName = nameParts.shift();
    const lastName = nameParts.join(' ');
    const email = form.querySelector('input[type="email"]')?.value.trim();
    const password = form.querySelector('input[type="password"]')?.value;
    if (!lastName) {
        showAuthToast('Please enter your first and last name.');
        return;
    }

    try {
        const response = await requestWithCsrf('/api/auth/register', {
            method: 'POST',
            body: JSON.stringify({ firstName, lastName, email, password })
        });
        if (response.status === 409) throw new Error('An account with this email already exists.');
        if (!response.ok) {
            const result = await response.json();
            throw new Error(result.details?.join(', ') || 'Registration failed. Check the supplied details.');
        }

        const loginResponse = await requestWithCsrf('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        const user = await loginResponse.json();
        if (!loginResponse.ok) throw new Error(user.message || 'Account created. Please sign in.');
        storeDisplayProfile(user);
        window.location.href = 'dashboard.html';
    } catch (error) {
        showAuthToast(error.message);
    }
}

function showAuthToast(message) {
    if (window.showToast) window.showToast(message, 'Authentication');
    else window.alert(message);
}

function getStoredUserProfile() {
    try {
        const raw = localStorage.getItem('luxora_user_session');
        return raw ? JSON.parse(raw) : null;
    } catch (error) {
        return null;
    }
}

function requireCustomerLogin() {
    const user = getStoredUserProfile();
    if (!user) {
        window.location.href = 'login.html';
        return false;
    }

    if (user.role !== 'CUSTOMER') {
        showAuthToast('Please sign in as a customer to book rooms or request hotel services.');
        window.location.href = 'login.html';
        return false;
    }

    return true;
}

window.requireCustomerLogin = requireCustomerLogin;

/**
 * Handle Login Form Submission
 */
async function handleLoginSubmit(e) {
    e.preventDefault();

    const email = document.getElementById('login-email')?.value.trim();
    const password = document.getElementById('login-password')?.value;
    const selectedRole = document.getElementById('login-role')?.value || 'CUSTOMER';

    if (!email || !password) {
        showAuthAlert('login-alert', 'Please provide both email and password.', 'danger');
        return;
    }

    try {
        const response = await requestWithCsrf('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        const result = await response.json();
        if (!response.ok) throw new Error(result.message || 'Login failed. Check your email and password.');

        if (selectedRole === 'ADMIN' && result.role !== 'ADMIN' && result.role !== 'RECEPTIONIST') {
            throw new Error('This account cannot access the admin portal. Please choose Customer login.');
        }

        if (selectedRole === 'CUSTOMER' && result.role !== 'CUSTOMER') {
            throw new Error('This account is set up for staff access. Please choose Admin / Receptionist login.');
        }

        storeDisplayProfile(result);
        window.location.href = result.role === 'ADMIN' || result.role === 'RECEPTIONIST'
            ? 'admin.html'
            : 'dashboard.html';
    } catch (error) {
        showAuthAlert('login-alert', error.message, 'danger');
    }
}

/**
 * Handle Registration Form Submission
 */
async function handleRegisterSubmit(e) {
    e.preventDefault();

    const fullName = document.getElementById('reg-name')?.value.trim();
    const email = document.getElementById('reg-email')?.value.trim();
    const phone = document.getElementById('reg-phone')?.value.trim();
    const password = document.getElementById('reg-password')?.value;
    const confirmPassword = document.getElementById('reg-confirm-password')?.value;

    if (!fullName || !email || !phone || !password || !confirmPassword) {
        showAuthAlert('reg-alert', 'Please complete all required fields.', 'danger');
        return;
    }

    if (password !== confirmPassword) {
        showAuthAlert('reg-alert', 'Passwords do not match! Please check again.', 'warning');
        return;
    }

    if (password.length < 12) {
        showAuthAlert('reg-alert', 'Password must be at least 12 characters long.', 'warning');
        return;
    }

    const nameParts = fullName.split(/\s+/);
    const firstName = nameParts.shift();
    const lastName = nameParts.join(' ');
    if (!lastName) {
        showAuthAlert('reg-alert', 'Please enter your first and last name.', 'warning');
        return;
    }

    try {
        const response = await requestWithCsrf('/api/auth/register', {
            method: 'POST',
            body: JSON.stringify({ firstName, lastName, email, phone, password })
        });
        if (response.status === 409) throw new Error('An account with this email already exists.');
        if (!response.ok) {
            const result = await response.json();
            throw new Error(result.details?.join(', ') || 'Registration failed. Check the supplied details.');
        }

        document.getElementById('login-email').value = email;
        document.getElementById('login-password').value = password;
        document.getElementById('lux-login-form').requestSubmit();
    } catch (error) {
        showAuthAlert('reg-alert', error.message, 'danger');
    }
}

/**
 * Display Alert Messages in Auth Forms
 */
function showAuthAlert(elementId, message, type = 'danger') {
    const alertBox = document.getElementById(elementId);
    if (!alertBox) return;

    alertBox.className = `alert alert-${type} py-2 px-3 mb-3 font-sans small`;
    alertBox.innerHTML = `<i class="fa-solid fa-circle-info me-2"></i> ${message}`;
    alertBox.style.display = 'block';
}

/**
 * Check if Session Exists to Update UI Header
 */
async function refreshSession() {
    try {
        const response = await fetch('/api/auth/me', { credentials: 'same-origin' });
        if (!response.ok) {
            if (response.status === 401) localStorage.removeItem('luxora_user_session');
            return;
        }

        const user = await response.json();
        storeDisplayProfile(user);

        const accountUrl = user.role === 'ADMIN' || user.role === 'RECEPTIONIST'
            ? 'admin.html'
            : 'dashboard.html';
        document.querySelectorAll('a[href="login.html"]').forEach(link => {
            link.href = accountUrl;
            link.setAttribute('aria-label', `Open ${user.firstName}'s account`);

            const icon = document.createElement('i');
            icon.className = 'fa-solid fa-user me-1';
            link.replaceChildren(icon, document.createTextNode(user.firstName));
        });
    } catch (error) {
        localStorage.removeItem('luxora_user_session');
    }
}

function storeDisplayProfile(user) {
    localStorage.setItem('luxora_user_session', JSON.stringify({
        userId: user.id,
        fullName: `${user.firstName} ${user.lastName}`,
        email: user.email,
        role: user.role
    }));
}

/**
 * Global Logout Function
 */
async function handleLogout() {
    try {
        await requestWithCsrf('/api/auth/logout', { method: 'POST' });
    } finally {
        localStorage.removeItem('luxora_user_session');
        window.location.href = 'index.html';
    }
}

window.handleLogout = handleLogout;
