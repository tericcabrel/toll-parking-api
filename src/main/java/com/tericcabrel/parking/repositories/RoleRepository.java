package com.tericcabrel.parking.repositories;

import com.tericcabrel.parking.models.dbs.Role;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, ObjectId> {
    Role findByName(String name);
}
