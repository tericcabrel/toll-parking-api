package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dtos.CarTypeDto;

import java.util.List;

public interface CarTypeService {
    CarType save(CarTypeDto roleDto);

    List<CarType> findAll();

    void delete(String id);

    CarType findByName(String name);

    CarType findById(String id);

    CarType update(String id, CarTypeDto roleDto);
}
