package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dtos.CreateCarTypeDto;

import java.util.List;

public interface CarTypeService {
    CarType save(CreateCarTypeDto createCarTypeDto);

    List<CarType> findAll();

    void delete(String id);

    CarType findByName(String name);

    CarType findById(String id);

    CarType update(String id, CreateCarTypeDto createCarTypeDto);
}
