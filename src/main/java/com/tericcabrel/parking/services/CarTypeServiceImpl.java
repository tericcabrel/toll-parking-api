package com.tericcabrel.parking.services;

import com.tericcabrel.parking.exceptions.ResourceAlreadyExistsException;
import com.tericcabrel.parking.exceptions.ResourceNotFoundException;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dtos.CarTypeDto;
import com.tericcabrel.parking.repositories.CarTypeRepository;
import com.tericcabrel.parking.services.interfaces.CarTypeService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service(value = "carTypeService")
public class CarTypeServiceImpl implements CarTypeService {
    private CarTypeRepository carTypeRepository;

    public CarTypeServiceImpl(CarTypeRepository carTypeRepository) {
        this.carTypeRepository = carTypeRepository;
    }

    @Override
    public CarType save(CarTypeDto carTypeDto) {
        CarType carType = carTypeRepository.findByName(carTypeDto.getName());

        if (carType != null) {
            throw new ResourceAlreadyExistsException("A car's type with this name already exists!");
        }

        carType = CarType.builder()
                            .name(carTypeDto.getName())
                            .build();

        return carTypeRepository.save(carType);
    }

    @Override
    public List<CarType> findAll() {
        return carTypeRepository.findAll();
    }

    @Override
    public void delete(String id) {
        carTypeRepository.deleteById(new ObjectId(id));
    }

    @Override
    public CarType findByName(String name) {
        return carTypeRepository.findByName(name);
    }

    @Override
    public CarType findById(String id) {
        Optional<CarType> optionalCarType = carTypeRepository.findById(new ObjectId(id));

        if (optionalCarType.isPresent()) {
            return optionalCarType.get();
        }

        throw new ResourceNotFoundException("CarType not found!");
    }

    @Override
    public CarType update(String id, CarTypeDto carTypeDto) {
        CarType item = findById(id);

        item.setName(carTypeDto.getName());

        return carTypeRepository.save(item);
    }
}
