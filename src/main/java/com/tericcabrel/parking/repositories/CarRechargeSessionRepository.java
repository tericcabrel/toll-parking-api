package com.tericcabrel.parking.repositories;

import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRechargeSessionRepository extends MongoRepository<CarRechargeSession, ObjectId> {
    List<CarRechargeSession> findByCustomer(ObjectId customerId);

    List<CarRechargeSession> findByParkingSlot(ObjectId parkingSlotId);
}
