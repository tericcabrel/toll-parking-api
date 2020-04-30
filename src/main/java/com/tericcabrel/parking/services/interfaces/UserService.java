package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.models.dtos.UserDto;

import java.util.List;

public interface UserService {
    User save(UserDto userDto);

    List<User> findAll();

    void delete(String id);

    User findByEmail(String email);

    User findById(String id);

    User update(String id, UpdateUserDto updateUserDto);

    User update(User user);

    User updatePassword(String id, String newPassword);
}
