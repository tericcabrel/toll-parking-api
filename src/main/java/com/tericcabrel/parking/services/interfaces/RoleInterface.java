package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dtos.RoleDto;

import java.util.List;

public interface RoleInterface {
    Role save(RoleDto roleDto);

    List<Role> findAll();

    void delete(String id);

    Role findByName(String name);

    Role findById(String id);

    Role update(String id, RoleDto roleDto);
}
