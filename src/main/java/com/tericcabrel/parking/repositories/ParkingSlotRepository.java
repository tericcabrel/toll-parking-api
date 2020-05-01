package com.tericcabrel.parking.repositories;

import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ParkingSlotRepository extends MongoRepository<ParkingSlot, ObjectId> {
    List<ParkingSlot> findAllByCarTypeAndStateOrderByLastUsedTimeAsc(ObjectId carTypeId, ParkingSlotStateEnum state);
}
