package com.luxora.hotel.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.luxora.hotel.entity.Room;
import com.luxora.hotel.model.RoomStatus;
import com.luxora.hotel.repository.RoomRepository;

@Component
@Profile("local")
public class LocalRoomDataInitializer implements ApplicationRunner {

    private final RoomRepository roomRepository;

    public LocalRoomDataInitializer(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (roomRepository.count() > 0) {
            return;
        }

        roomRepository.saveAll(List.of(
                new Room("101", "STANDARD", 1500, 2, RoomStatus.AVAILABLE, "Comfortable standard room"),
                new Room("201", "DELUXE", 2500, 2, RoomStatus.AVAILABLE, "Deluxe room with private balcony"),
                new Room("301", "PREMIUM", 3500, 3, RoomStatus.AVAILABLE, "Premium oceanfront room"),
                new Room("401", "EXECUTIVE", 5000, 3, RoomStatus.AVAILABLE, "Executive business suite"),
                new Room("501", "PRESIDENTIAL", 10000, 4, RoomStatus.AVAILABLE, "Presidential royal suite")
        ));
    }
}
