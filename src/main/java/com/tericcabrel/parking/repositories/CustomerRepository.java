package com.tericcabrel.parking.repositories;

import com.tericcabrel.parking.models.dbs.Customer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, ObjectId> {
    Customer findByEmail(String email);
}
