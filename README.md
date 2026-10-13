# 👑 LUXORA — Smart Hotel Management & Hospitality System

> **Final-Year Java Enterprise & Web Application Project**  
> *Initial Phase: Professional Luxury Frontend Foundation & Standard Spring Boot Directory Setup.*

---

## 📌 Project Overview
**LUXORA** is a 5-star luxury hotel management & reservation web platform designed with high-end aesthetic standards (dark gold glassmorphism theme, smooth micro-interactions, responsive grid layout, and live pricing calculation engine).

The project is structured according to **Spring Boot 3 / Maven** standards, making it immediately ready for seamless backend integration (Java 17/21, Spring Data JPA, Hibernate, and MySQL).

---

## 🛠️ Technology Stack

| Layer | Technologies & Tools |
| :--- | :--- |
| **Frontend UI** | HTML5, CSS3 (Custom Glassmorphism & Gold Theme), JavaScript ES6+, Bootstrap 5.3, Font Awesome 6 Icons |
| **Backend Architecture** | Java 17, Spring Boot 3.2.4, Spring MVC / REST Controllers, Spring Security, Maven |
| **Database (Phase 2 Integration)** | MySQL 8.0+, Spring Data JPA, Hibernate ORM |
| **Testing & Tools** | IntelliJ IDEA / VS Code, Git/GitHub, Postman, JUnit 5 |

---

## 📁 Directory & Folder Structure

```
LUXORA - Hotel Management System/
├── pom.xml                                   # Maven dependencies for Java 17 & Spring Boot 3.2.4
├── README.md                                  # Complete project documentation & run guide
├── .gitignore                                 # Git configuration for Java/Maven/IDE outputs
└── src/
    └── main/
        ├── java/
        │   └── com/luxora/hotel/
        │       ├── LuxoraApplication.java    # Spring Boot Main Entry Point Class
        │       ├── controller/
        │       │   └── ViewController.java   # Spring MVC View Routing Controller
        │       ├── model/                    # (Phase 2: JPA Entities - Room, Booking, Guest)
        │       ├── repository/               # (Phase 2: Spring Data JPA Repositories)
        │       └── service/                  # (Phase 2: Service Layer & Business Logic)
        │
        └── resources/
            ├── application.properties        # Spring Boot Server & Database Configurations
            └── static/                       # Frontend Static Web Application
                ├── index.html                # Main Luxury Landing Page & Quick Booking Bar
                ├── rooms.html                # Suites & Accommodations Gallery with Filter
                ├── booking.html              # Multi-Step Reservation Wizard & Price Engine
                ├── contact.html              # Concierge Desk Inquiry & Interactive FAQ
                ├── admin.html                # Hotel Operations & Staff Dashboard Preview
                ├── css/
                │   └── style.css             # Glassmorphism Design System & CSS Variables
                ├── js/
                │   ├── main.js               # Global UI, sticky nav, toast notifications, filters
                │   ├── booking.js            # Live rate calculator, taxes breakdown & receipt modal
                │   └── admin.js              # Table search filter & room housekeeping status toggles
                └── images/                   # Generated High-Res Luxury Hotel Visuals
                    ├── hero-bg.png
                    ├── presidential-suite.png
                    ├── royal-penthouse.png
                    └── deluxe-ocean.png
```

---

## 🚀 How to Run the Project

You can run and test this project using **any** of the following 3 methods:

### Option 1: Direct Browser Launch (Quickest Frontend Preview)
1. Open your file explorer and navigate to `c:\Hotel Management System\src\main\resources\static\`.
2. Double-click **`index.html`** to open it directly in Google Chrome, Microsoft Edge, Firefox, or Safari.
3. Navigate between pages (**Suites**, **Reservations**, **Concierge**, **Admin Dashboard**) directly.
   
   This is a frontend preview only. Login, registration, and other API-backed features will not work when opened as a local file.

### Option 2: Live Server (VS Code / WebStorm Extension)
1. Open the project folder in **VS Code** or **IntelliJ IDEA**.
2. Install or right-click `src/main/resources/static/index.html`.
3. Select **"Open with Live Server"** (usually runs on `http://127.0.0.1:5500/`).
   
   This is also a frontend preview. Authentication requests need the Spring Boot server; for local use, start Spring Boot and open `http://localhost:8080/login.html` instead.

### Option 3: Java / Spring Boot Maven Server Execution
1. Open terminal inside `c:\Hotel Management System\`.
2. Ensure Java 17 or later is installed. The Maven Wrapper downloads and runs the required Maven version.
3. Run the following command:
   ```bash
   .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
   ```
4. Once booted, open your browser and navigate to:
   ```
   http://localhost:8080/
   ```

The `local` profile uses a file-backed H2 database at `./data/luxora` for development, so it does not require a separately configured MySQL account and customer accounts remain available after the app stops and restarts. Other profiles continue to use the MySQL settings.

The local profile seeds any missing default food items on startup without replacing existing catalog entries. Dining orders calculate prices from the saved menu, apply 5% tax, and charge a ₹50 delivery fee below a ₹500 subtotal (free delivery at ₹500 or more). Payment handling is a local simulation only; no card or UPI payment is processed.

---

## 🔐 Authentication & Authorization

- Registration is `POST /api/auth/register`. Bean Validation checks required names, email, and password length. The controller normalizes the email, BCrypt-hashes the password before persistence, and creates the account with the `CUSTOMER` role. The registration DTO does not accept a role, and the response DTO never includes the password hash.
- Login is `POST /api/auth/login`. `AuthenticationManager` delegates to `CustomerUserDetailsService`, which loads the account's saved role and BCrypt hash; Spring Security compares the submitted password with that hash. On success, the server rotates the session ID and saves the authenticated security context in the HTTP session. The browser receives an HttpOnly `JSESSIONID`; it does not store the password or a bearer token. `GET /api/auth/me` returns only the authenticated user's public profile and server-assigned role.
- State-changing requests require a CSRF token. Fetch `GET /api/auth/csrf`, then send the returned token in its named header. The login page does this automatically. Logout is `POST /api/auth/logout` and invalidates the session.
- `CUSTOMER` accounts can create bookings, orders, and payments for themselves and read only records tied to their authenticated email. The customer dashboard is CUSTOMER-only. `ADMIN` and `RECEPTIONIST` can access the admin dashboard, customer directory, management lists, status changes, and inventory/service writes. Public room, food, and hotel-service catalog reads remain available without login.
- Staff roles are never granted through public registration or the login-page selector. Public registration always creates a `CUSTOMER`; the Admin / Receptionist option on the login form only selects the expected destination after the server authenticates the account.
- To create the first administrator, set `LUXORA_ADMIN_EMAIL` and `LUXORA_ADMIN_PASSWORD` in the environment before starting Spring Boot. These are not default credentials: the email you enter at login must match `LUXORA_ADMIN_EMAIL`, and the password must be the value you set (12 to 72 characters). On startup, the application creates that account with the `ADMIN` role and a BCrypt password hash if the email does not already exist. If the account already has an `ADMIN` or `RECEPTIONIST` role, the configured password replaces its current password; an existing customer account is never promoted to staff. The bootstrap is disabled when both variables are unset. Do not commit the password to source control.
- Example in PowerShell (replace the sample values, then start the app in the same terminal):
   ```powershell
   $env:LUXORA_ADMIN_EMAIL = "admin@luxora.com"
   $env:LUXORA_ADMIN_PASSWORD = "Use-a-long-unique-password"
   .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
   ```
- Open `http://localhost:8080/login.html`, choose **Admin / Receptionist**, and sign in with those credentials. Customers use the **Register** tab; there is no public staff registration. Additional receptionist accounts must be provisioned by trusted hotel management with a BCrypt password and the `RECEPTIONIST` role.
- After staff sign-in, use **Customers / Guests** to see registered accounts and **Reservations & Bookings** to review real customer bookings and their special requests. The concierge service-ticket panel is currently sample data; customer service-ticket requests are not yet connected to persistent storage.
- For HTTPS deployments set `LUXORA_SESSION_COOKIE_SECURE=true`; the session cookie is HttpOnly and SameSite=Lax.

Authorization is enforced in Spring Security before a controller runs; the selected role in the browser is never trusted to grant access. Customer-owned record endpoints additionally compare the record's customer email with the authenticated principal, so changing an ID in a URL or request body cannot expose or mutate another customer's records. A customer who tries a staff operation receives `403 Forbidden`; an unauthenticated API request receives `401 Unauthorized`. Integration tests cover password hashing, role assignment, session login/logout, validation, and customer/staff access boundaries.

## 📄 Explanation of Created Files

1. **`pom.xml`**: Pre-configures Maven dependencies for Java 17 and Spring Boot Web & Data JPA.
2. **`LuxoraApplication.java`**: Spring Boot application entry point (`@SpringBootApplication`).
3. **`ViewController.java`**: Maps root URL routes (`/`, `/rooms`, `/booking`, `/contact`, `/admin`) to static HTML templates.
4. **`application.properties`**: Server port (`8080`), static resource handler, and commented MySQL credentials for Phase 2.
5. **`index.html`**: Grand luxury homepage featuring hero background, quick availability checker bar, signature room cards, amenities, and guest reviews.
6. **`rooms.html`**: Comprehensive room showcase with interactive category filter buttons (*Presidential*, *Penthouses*, *Ocean View*, *Executive*).
7. **`booking.html`**: Reservation engine featuring step-by-step room selection, add-on checkboxes, live tax & service fee calculator, and booking receipt modal.
8. **`contact.html`**: Concierge inquiry form and interactive Bootstrap 5 FAQ accordion.
9. **`admin.html`**: Hotel management staff control panel with real-time KPI metrics, revenue counters, guest table search, and interactive housekeeping status badges.
10. **`css/style.css`**: Complete luxury design system with custom CSS variables, glassmorphism, golden gradients, and micro-animations.
11. **`js/main.js`, `booking.js`, `admin.js`**: ES6 modular JavaScript scripts handling dynamic interactions, live pricing, filter logic, and notifications.

---

## 🔮 Further Backend Work
Additional work can extend the existing JPA, REST, and Spring Security implementation with password reset and account recovery flows, email verification, and account lockout policies.


run 
cd "C:\Hotel Management System"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"


Admin 

Set-Location "C:\Hotel Management System"
$env:LUXORA_ADMIN_EMAIL = Read-Host "Admin email"
$adminPassword = Read-Host "Choose a new admin password (12+ characters)" -AsSecureString
$env:LUXORA_ADMIN_PASSWORD = [System.Net.NetworkCredential]::new("", $adminPassword).Password
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"