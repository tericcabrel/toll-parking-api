package com.tericcabrel.parking.services;

import com.tericcabrel.parking.exceptions.ResourceNotFoundException;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.dbs.PricingPolicy;
import com.tericcabrel.parking.models.dtos.ParkingSlotDto;
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
    public ParkingSlot save(ParkingSlotDto parkingSlotDto) {

        PricingPolicy pricingPolicy = PricingPolicy.builder()
                                                    .parameters(parkingSlotDto.getPricingPolicyDto().getParameters())
                                                    .evaluation(parkingSlotDto.getPricingPolicyDto().getEvaluation())
                                                    .build();

        ParkingSlot parkingSlot = ParkingSlot.builder()
                                            .label(parkingSlotDto.getLabel())
                                            .state(parkingSlotDto.getParkingSlotStateEnum())
                                            .pricingPolicy(pricingPolicy)
                                            .carType(parkingSlotDto.getCarType())
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
    public ParkingSlot update(String id, ParkingSlotDto parkingSlotDto) {
        ParkingSlot parkingSlot = findById(id);

        parkingSlot.setLabel(parkingSlotDto.getLabel());

        if (parkingSlotDto.getState() != null) {
            parkingSlot.setState(parkingSlotDto.getParkingSlotStateEnum());
        }

        PricingPolicy pricingPolicy = PricingPolicy.builder()
            .parameters(parkingSlotDto.getPricingPolicyDto().getParameters())
            .evaluation(parkingSlotDto.getPricingPolicyDto().getEvaluation())
            .build();

        parkingSlot.setPricingPolicy(pricingPolicy);

        return parkingSlotRepository.save(parkingSlot);
    }
}
