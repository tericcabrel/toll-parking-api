package com.tericcabrel.parking.services;

import com.tericcabrel.parking.exceptions.ResourceNotFoundException;
import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dtos.CreateCarRechargeSessionDto;
import com.tericcabrel.parking.models.dtos.UpdateCarRechargeSessionDto;
import com.tericcabrel.parking.repositories.CarRechargeSessionRepository;
import com.tericcabrel.parking.services.interfaces.CarRechargeSessionService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("carRechargeSessionService")
public class CarRechargeSessionServiceImpl implements CarRechargeSessionService {
    private CarRechargeSessionRepository carRechargeSessionRepository;

    public CarRechargeSessionServiceImpl(CarRechargeSessionRepository carRechargeSessionRepository) {
        this.carRechargeSessionRepository = carRechargeSessionRepository;
    }

    @Override
    public CarRechargeSession save(CreateCarRechargeSessionDto createCarRechargeSessionDto) {
        CarRechargeSession carRechargeSession = CarRechargeSession.builder()
                                                            .startTime(new Date())
                                                            .parkingSlot(createCarRechargeSessionDto.getParkingSlot())
                                                            .customer(createCarRechargeSessionDto.getCustomer())
                                                            .build();

        return carRechargeSessionRepository.save(carRechargeSession);
    }

    @Override
    public List<CarRechargeSession> findAll() {
        return carRechargeSessionRepository.findAll();
    }

    @Override
    public void delete(String id) {
        carRechargeSessionRepository.deleteById(new ObjectId(id));
    }

    @Override
    public List<CarRechargeSession> findByCustomer(String customerId) {
        return carRechargeSessionRepository.findByCustomer(new ObjectId(customerId));
    }

    @Override
    public List<CarRechargeSession> findByParkingSlot(String parkingSlotId) {
        return carRechargeSessionRepository.findByParkingSlot(new ObjectId(parkingSlotId));
    }

    @Override
    public CarRechargeSession findById(String id) {
        Optional<CarRechargeSession> optionalCustomer = carRechargeSessionRepository.findById(new ObjectId(id));

        if (optionalCustomer.isPresent()) {
            return optionalCustomer.get();
        }

        throw new ResourceNotFoundException("CarRechargeSession not found!");
    }

    @Override
    public CarRechargeSession update(String id, UpdateCarRechargeSessionDto updateCarRechargeSessionDto) {
        CarRechargeSession item = findById(id);


        if (updateCarRechargeSessionDto.getEndTime() != null) {
            // TODO Make sure startTime < endTime

            item.setEndTime(updateCarRechargeSessionDto.getEndTime());
        }

        if (updateCarRechargeSessionDto.getPrice() > 0) {
            item.setPrice(updateCarRechargeSessionDto.getPrice());
        }

        return carRechargeSessionRepository.save(item);
    }
}
