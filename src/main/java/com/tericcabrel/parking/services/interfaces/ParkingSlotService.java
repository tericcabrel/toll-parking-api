package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.dtos.CreateParkingSlotDto;
import com.tericcabrel.parking.models.dtos.UpdateParkingSlotDto;

import java.util.List;

public interface ParkingSlotService {
    ParkingSlot save(CreateParkingSlotDto createParkingSlotDto);

    List<ParkingSlot> findAll();

    void delete(String id);

    ParkingSlot findByLabel(String label);

    ParkingSlot findById(String id);

    ParkingSlot update(String id, UpdateParkingSlotDto updateParkingSlotDto);
}
