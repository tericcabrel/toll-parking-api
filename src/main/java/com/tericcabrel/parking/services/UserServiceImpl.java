package com.tericcabrel.parking.services;

import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.models.dtos.UserDto;
import com.tericcabrel.parking.services.interfaces.UserInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "userService")
public class UserServiceImpl implements UserInterface {
    @Override
    public User save(UserDto userDto) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public User findById(String id) {
        return null;
    }

    @Override
    public User update(String id, UpdateUserDto updateUserDto) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public User updatePassword(String id, String newPassword) {
        return null;
    }
}
