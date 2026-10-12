package com.luxora.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller mapping luxury frontend static routes when running on Spring Boot server.
 */
@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/dashboard.html";
    }

    @GetMapping("/rooms")
    public String rooms() {
        return "forward:/rooms.html";
    }

    @GetMapping("/dining")
    public String dining() {
        return "forward:/dining.html";
    }

    @GetMapping("/services")
    public String services() {
        return "forward:/services.html";
    }

    @GetMapping("/cart")
    public String cart() {
        return "forward:/cart.html";
    }

    @GetMapping("/booking")
    public String booking() {
        return "forward:/booking.html";
    }

    @GetMapping("/contact")
    public String contact() {
        return "forward:/contact.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "forward:/admin.html";
    }
}
