package com.tericcabrel.parking.services;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.dtos.ParkingSlotDto;
import com.tericcabrel.parking.services.interfaces.ParkingSlotService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("parkingSlotService")
public class ParkingSlotServiceImpl implements ParkingSlotService {
    @Override
    public ParkingSlot save(ParkingSlotDto parkingSlotDto) {
        return null;
    }

    @Override
    public List<CarType> findAll() {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public ParkingSlot findByLabel(String label) {
        return null;
    }

    @Override
    public ParkingSlot findById(String id) {
        return null;
    }

    @Override
    public ParkingSlot update(String id, ParkingSlotDto parkingSlotDto) {
        return null;
    }
}
