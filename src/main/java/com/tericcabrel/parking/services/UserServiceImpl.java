package com.tericcabrel.parking.services;

import com.tericcabrel.parking.exceptions.PasswordNotMatchException;
import com.tericcabrel.parking.exceptions.ResourceNotFoundException;
import com.tericcabrel.parking.exceptions.ResourceAlreadyExistsException;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.UpdatePasswordDto;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.models.dtos.CreateUserDto;
import com.tericcabrel.parking.repositories.UserRepository;
import com.tericcabrel.parking.services.interfaces.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; // Fails when injected by the constructor

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(CreateUserDto createUserDto) {
        User user = userRepository.findByEmail(createUserDto.getEmail());

        if (user != null) {
            throw new ResourceAlreadyExistsException("A user with this email already exists!");
        }

        user = User.builder()
                    .email(createUserDto.getEmail())
                    .name(createUserDto.getName())
                    .password(bCryptPasswordEncoder.encode(createUserDto.getPassword()))
                    .gender(createUserDto.getGenderEnum())
                    .enabled(createUserDto.isEnabled())
                    .roles(createUserDto.getRoles())
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
        User item = findById(id);

        // All properties must exists in the DTO even if you don't intend to update it
        // Otherwise, it will set to null
        // BeanUtils.copyProperties(userDto, user, "password");

        if(updateUserDto.getName() != null) {
            item.setName(updateUserDto.getName());
        }

        if(updateUserDto.getGender() != null) {
            item.setGender(updateUserDto.getGenderEnum());
        }

        if (updateUserDto.getEnabled() >= 0) {
            item.setEnabled(updateUserDto.getEnabled() != 0);
        }

        return userRepository.save(item);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(String id, UpdatePasswordDto updatePasswordDto) {
        User user = findById(id);

        if(user != null) {
            if (bCryptPasswordEncoder.matches(updatePasswordDto.getCurrentPassword(), user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(updatePasswordDto.getNewPassword()));

                userRepository.save(user);
            } else {
                throw new PasswordNotMatchException("The current password don't match!");
            }
        }

        return user;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), true, true, true, true, getAuthority(user)
        );
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

        return authorities;
    }
}
