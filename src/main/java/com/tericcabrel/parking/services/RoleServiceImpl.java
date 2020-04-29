package com.tericcabrel.parking.services;

import com.tericcabrel.parking.exceptions.ResourceNotFoundException;
import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dtos.RoleDto;
import com.tericcabrel.parking.repositories.RoleRepository;
import com.tericcabrel.parking.services.interfaces.RoleInterface;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service(value = "roleService")
public class RoleServiceImpl implements RoleInterface {
    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role save(RoleDto roleDto) {
        Role role = Role.builder()
                            .name(roleDto.getName())
                            .description(roleDto.getDescription())
                            .build();

        return roleRepository.save(role);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public void delete(String id) {
        roleRepository.deleteById(new ObjectId(id));
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Role findById(String id) {
        Optional<Role> optionalRole = roleRepository.findById(new ObjectId(id));

        if (optionalRole.isPresent()) {
            return optionalRole.get();
        }

        throw new ResourceNotFoundException("Role not found!");
    }

    @Override
    public Role update(String id, RoleDto roleDto) {
        Role role = findById(id);

        role.setName(roleDto.getName());

        if (role.getDescription() != null) {
            role.setDescription(roleDto.getDescription());
        }

        roleRepository.save(role);

        return role;
    }
}
