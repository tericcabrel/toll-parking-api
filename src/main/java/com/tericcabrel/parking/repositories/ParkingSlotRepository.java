package com.tericcabrel.parking.repositories;

import com.tericcabrel.parking.models.dbs.ParkingSlot;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ParkingSlotRepository extends MongoRepository<ParkingSlot, ObjectId> {

}
