package com.tericcabrel.parking.services;

import com.tericcabrel.parking.exceptions.ResourceNotFoundException;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.dbs.PricingPolicy;
import com.tericcabrel.parking.models.dtos.CreateParkingSlotDto;
import com.tericcabrel.parking.models.dtos.UpdateParkingSlotDto;
import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;
import com.tericcabrel.parking.repositories.ParkingSlotRepository;
import com.tericcabrel.parking.services.interfaces.ParkingSlotService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("parkingSlotService")
public class ParkingSlotServiceImpl implements ParkingSlotService {
    private ParkingSlotRepository parkingSlotRepository;

    public ParkingSlotServiceImpl(ParkingSlotRepository parkingSlotRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
    }

    @Override
    public ParkingSlot save(CreateParkingSlotDto createParkingSlotDto) {

        PricingPolicy pricingPolicy = PricingPolicy.builder()
                                                .parameters(createParkingSlotDto.getPricingPolicy().getParameters())
                                                .evaluation(createParkingSlotDto.getPricingPolicy().getEvaluation())
                                                .build();

        ParkingSlot parkingSlot = ParkingSlot.builder()
                                        .label(createParkingSlotDto.getLabel())
                                        .state(createParkingSlotDto.getParkingSlotStateEnum())
                                        .pricingPolicy(pricingPolicy)
                                        .carType(createParkingSlotDto.getCarType())
                                        .build();

        return parkingSlotRepository.save(parkingSlot);
    }

    @Override
    public List<ParkingSlot> findAll() {
        return parkingSlotRepository.findAll();
    }

    @Override
    public void delete(String id) {
        parkingSlotRepository.deleteById(new ObjectId(id));
    }

    @Override
    public ParkingSlot findByLabel(String label) {
        return null;
    }

    @Override
    public ParkingSlot findById(String id) {
        Optional<ParkingSlot> optionalRole = parkingSlotRepository.findById(new ObjectId(id));

        if (optionalRole.isPresent()) {
            return optionalRole.get();
        }

        throw new ResourceNotFoundException("Parking slot not found!");
    }

    @Override
    public ParkingSlot update(String id, UpdateParkingSlotDto updateParkingSlotDto) {
        ParkingSlot item = findById(id);

        if (updateParkingSlotDto.getLabel() != null) {
            item.setLabel(updateParkingSlotDto.getLabel());
        }

        if (updateParkingSlotDto.getState() != null) {
            item.setState(updateParkingSlotDto.getParkingSlotStateEnum());
        }

        if (updateParkingSlotDto.getCarType() != null) {
            item.setCarType(updateParkingSlotDto.getCarType());
        }

        PricingPolicy pricingPolicy = PricingPolicy.builder()
                                            .parameters(updateParkingSlotDto.getPricingPolicy().getParameters())
                                            .evaluation(updateParkingSlotDto.getPricingPolicy().getEvaluation())
                                            .build();

        item.setPricingPolicy(pricingPolicy);

        return parkingSlotRepository.save(item);
    }

    public ParkingSlot update(ParkingSlot parkingSlot) {
        return parkingSlotRepository.save(parkingSlot);
    }

    @Override
    public List<ParkingSlot> findAvailableByCarType(CarType carType) {
        return parkingSlotRepository.findAllByCarTypeAndStateOrderByLastUsedTimeAsc(carType, ParkingSlotStateEnum.FREE);
    }


}
