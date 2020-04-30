package com.tericcabrel.parking.services;

import com.tericcabrel.parking.exceptions.ResourceNotFoundException;
import com.tericcabrel.parking.exceptions.ResourceAlreadyExistsException;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.models.dtos.UserDto;
import com.tericcabrel.parking.repositories.UserRepository;
import com.tericcabrel.parking.services.interfaces.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptEncoder; // Fails when injected by the constructor

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail());

        if (user != null) {
            throw new ResourceAlreadyExistsException("A user with this email already exists!");
        }

        user = User.builder()
                    .email(userDto.getEmail())
                    .name(userDto.getName())
                    .password(bCryptEncoder.encode(userDto.getPassword()))
                    .gender(userDto.getGenderEnum())
                    .enabled(userDto.isEnabled())
                    .roles(userDto.getRoles())
                    .build();

        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(new ObjectId(id));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(String id) {
        Optional<User> optionalUser = userRepository.findById(new ObjectId(id));

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        throw new ResourceNotFoundException("User not found!");
    }

    @Override
    public User update(String id, UpdateUserDto updateUserDto) {
        User user = findById(id);

        if(user != null) {
            // All properties must exists in the DTO even if you don't intend to update it
            // Otherwise, it will set to null
            // BeanUtils.copyProperties(userDto, user, "password");

            if(updateUserDto.getName() != null) {
                user.setName(updateUserDto.getName());
            }

            if(updateUserDto.getGender() != null) {
                user.setGender(updateUserDto.getGenderEnum());
            }

            if (updateUserDto.getEnabled() < 0) {
                user.setEnabled(updateUserDto.getEnabled() != 0);
            }

            return userRepository.save(user);
        }

        return null;
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(String id, String newPassword) {
        User user = findById(id);

        user.setPassword(bCryptEncoder.encode(newPassword));

        return userRepository.save(user);
    }
}
