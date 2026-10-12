package com.luxora.hotel.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.Room;
import com.luxora.hotel.model.RoomStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select room from Room room where room.id = :roomId")
    Optional<Room> findByIdForUpdate(@Param("roomId") Long roomId);

    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByStatusIn(Collection<RoomStatus> statuses);
    List<Room> findByRoomType(String roomType);
    List<Room> findByRoomTypeAndStatus(String roomType, RoomStatus status);
    List<Room> findByStatusNot(RoomStatus status);
}
