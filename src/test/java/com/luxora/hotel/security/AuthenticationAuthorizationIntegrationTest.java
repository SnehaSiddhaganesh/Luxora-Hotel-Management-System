package com.luxora.hotel.security;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.Room;
import com.luxora.hotel.model.UserRole;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.repository.RoomRepository;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:luxora-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AuthenticationAuthorizationIntegrationTest {
    private static final String PASSWORD = "LongPassphrase!123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

        @Autowired
        private RoomRepository roomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrationHashesPasswordIgnoresSubmittedRoleAndLoginCreatesSession() throws Exception {
        String email = "guest-" + UUID.randomUUID() + "@example.com";
        String registration = """
                {
                  "firstName": "Ada",
                  "lastName": "Guest",
                  "email": "%s",
                  "phone": "+1 555 0100",
                  "password": "%s",
                  "role": "ADMIN"
                }
                """.formatted(email, PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn();

        Customer customer = customerRepository.findByEmail(email).orElseThrow();
        assertEquals(UserRole.CUSTOMER, customer.getRole());
        assertNotEquals(PASSWORD, customer.getPasswordHash());
        assertTrue(passwordEncoder.matches(PASSWORD, customer.getPasswordHash()));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of("email", email, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        mockMvc.perform(post("/api/auth/logout").session(session).with(csrf()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedApiRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerCannotReadAdminCustomerDirectoryOrBookingList() throws Exception {
        mockMvc.perform(get("/api/customers").with(user("guest@example.com").roles("CUSTOMER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/bookings").with(user("guest@example.com").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void customerCannotOpenAdminDashboardAndStaffCannotOpenCustomerDashboard() throws Exception {
        mockMvc.perform(get("/admin.html").with(user("guest@example.com").roles("CUSTOMER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/dashboard.html").with(user("staff@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void receptionistCanReadAdminCustomerDirectory() throws Exception {
        mockMvc.perform(get("/api/customers").with(user("reception@example.com").roles("RECEPTIONIST")))
                .andExpect(status().isOk());
    }

    @Test
    void invalidRegistrationDataIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Guest",
                                  "email": "not-an-email",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void receptionistRoleFromDatabaseCanOpenAdminButNotCustomerDashboard() throws Exception {
        String email = "reception-" + UUID.randomUUID() + "@example.com";
        Customer receptionist = new Customer("Rae", "Staff", email, null, null);
        receptionist.setPasswordHash(passwordEncoder.encode(PASSWORD));
        receptionist.setRole(UserRole.RECEPTIONIST);
        customerRepository.save(receptionist);

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of("email", email, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("RECEPTIONIST"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(session);

        mockMvc.perform(get("/admin.html").session(session))
                .andExpect(status().isOk());
        mockMvc.perform(get("/dashboard.html").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void customerCannotCreateOrModifyRoomInventory() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .with(user("guest@example.com").roles("CUSTOMER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void bookingWorkflowPersistsServerCalculatedTotalAndCancellationRestoresAvailability() throws Exception {
        String email = "booking-" + UUID.randomUUID() + "@example.com";
        Customer customer = customerRepository.save(new Customer("Riley", "Guest", email, null, null));
        String roomNumber = "T" + UUID.randomUUID().toString().substring(0, 7);
        Room room = roomRepository.save(new Room(roomNumber, "TEST", 2500, 2,
                com.luxora.hotel.model.RoomStatus.AVAILABLE, "Workflow integration room"));
        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = checkIn.plusDays(2);
        String request = objectMapper.writeValueAsString(java.util.Map.of(
                "roomId", room.getId(),
                "checkInDate", checkIn,
                "checkOutDate", checkOut,
                "numberOfGuests", 2,
                "specialRequests", "Quiet room"
        ));

        MvcResult created = mockMvc.perform(post("/api/bookings")
                        .with(user(email).roles("CUSTOMER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customer.getId()))
                .andExpect(jsonPath("$.roomId").value(room.getId()))
                .andExpect(jsonPath("$.nights").value(2))
                .andExpect(jsonPath("$.nightlyRate").value(2500.0))
                .andExpect(jsonPath("$.subtotal").value(5000.0))
                .andExpect(jsonPath("$.tax").value(900.0))
                .andExpect(jsonPath("$.total").value(5900.0))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andReturn();

        long bookingId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/rooms/available")
                        .param("checkIn", checkIn.toString())
                        .param("checkOut", checkOut.toString())
                        .param("roomType", "TEST")
                        .param("guests", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + room.getId() + ")]").doesNotExist());

        mockMvc.perform(post("/api/bookings")
                        .with(user(email).roles("CUSTOMER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/bookings/mine").with(user(email).roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingCode").value(objectMapper.readTree(created.getResponse().getContentAsString()).get("bookingCode").asText()));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(
                                "/api/bookings/{id}/cancel", bookingId)
                        .with(user(email).roles("CUSTOMER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get("/api/rooms/available")
                        .param("checkIn", checkIn.toString())
                        .param("checkOut", checkOut.toString())
                        .param("roomType", "TEST")
                        .param("guests", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(room.getId()));
    }

    @Test
    void bookingRejectsPastDatesAndStaffCannotCreateCustomerReservations() throws Exception {
        String email = "datecheck-" + UUID.randomUUID() + "@example.com";
        customerRepository.save(new Customer("Rae", "Guest", email, null, null));
        Room room = roomRepository.findAll().get(0);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String request = objectMapper.writeValueAsString(java.util.Map.of(
                "roomId", room.getId(),
                "checkInDate", yesterday,
                "checkOutDate", yesterday.plusDays(1),
                "numberOfGuests", 1
        ));

        mockMvc.perform(post("/api/bookings")
                        .with(user(email).roles("CUSTOMER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/bookings")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());
    }

}
