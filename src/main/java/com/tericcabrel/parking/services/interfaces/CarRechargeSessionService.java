package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dtos.CreateCarRechargeSessionDto;
import com.tericcabrel.parking.models.dtos.UpdateCarRechargeSessionDto;

import java.util.List;

public interface CarRechargeSessionService {
    CarRechargeSession save(CreateCarRechargeSessionDto createCarRechargeSessionDto);

    List<CarRechargeSession> findAll();

    void delete(String id);

    CarRechargeSession findById(String id);

    CarRechargeSession update(String id, UpdateCarRechargeSessionDto updateCarRechargeSessionDto);
}
