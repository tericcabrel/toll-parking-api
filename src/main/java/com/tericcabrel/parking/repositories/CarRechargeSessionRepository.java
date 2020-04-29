package com.tericcabrel.parking.repositories;

import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRechargeSessionRepository extends MongoRepository<CarRechargeSession, ObjectId> {

}
