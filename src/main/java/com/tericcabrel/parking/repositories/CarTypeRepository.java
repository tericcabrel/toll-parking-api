package com.tericcabrel.parking.repositories;

import com.tericcabrel.parking.models.dbs.CarType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarTypeRepository extends MongoRepository<CarType, ObjectId> {
    CarType findByName(String name);
}
