package com.tericcabrel.parking.bootstrap;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CreateCarTypeDto;
import com.tericcabrel.parking.models.dtos.CreateUserDto;
import com.tericcabrel.parking.models.dtos.RoleDto;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.services.interfaces.CarTypeService;
import com.tericcabrel.parking.services.interfaces.RoleService;
import com.tericcabrel.parking.services.interfaces.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.tericcabrel.parking.utils.Constants.*;


/**
 * This classes will insert in the database :
 * - Main roles: ROLE_USER and ROLE_ADMIN
 * - The first user with the role ROLE_ADMIN
 * - 3 Car's types: Gasoline, Electric 20Kw, Electric 50Kw
 */
@Component
public class DataSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private RoleService roleService;

    private UserService userService;

    private CarTypeService carTypeService;

    public DataSeeder(RoleService roleService, UserService userService, CarTypeService carTypeService) {
        this.roleService = roleService;
        this.userService = userService;
        this.carTypeService = carTypeService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();

        this.loadUsers();

        this.loadCarTypes();
    }

    /**
     * Load default roles of the application
     */
    public void loadRoles() {
        HashMap<String, String> roles = new HashMap<>();
        roles.put(ROLE_USER, "User role");
        roles.put(ROLE_ADMIN, "Admin role");

        roles.forEach((key, value) -> {
            Role role = roleService.findByName(key);

            if (role == null) {
                RoleDto roleDto = RoleDto.builder()
                                    .name(key)
                                    .description(value)
                                    .build();

                roleService.save(roleDto);
            }
        });
    }

    /**
     * Load the default user
     */
    public void loadUsers() {
        List<CreateUserDto> users = new ArrayList<CreateUserDto>() {};

        CreateUserDto admin = CreateUserDto.builder()
            .email("admin@admin.com")
            .name("Admin User")
            .enabled(true)
            .gender(GenderEnum.MALE.toString())
            .password("qwerty")
            .build();

        users.add(admin);

        // Assign the role ROLE_ADMIN to all users created
        users.forEach(userDto -> {
            User obj = userService.findByEmail(userDto.getEmail());
            Role role;

            if (obj == null ){
                role = roleService.findByName(ROLE_ADMIN);

                List<Role> userRoles = new ArrayList<>();
                userRoles.add(role);

                userDto.setRoles(userRoles);

                userService.save(userDto);
            }
        });
    }

    /**
     * Load default car's types of the application
     */
    public void loadCarTypes() {
        List<String> carTypesList = Arrays.asList(CAR_TYPE_GASOLINE, CAR_TYPE_20KW, CAR_TYPE_50KW);

        carTypesList.forEach(name -> {
            CarType carType = carTypeService.findByName(name);

            if (carType == null) {
                CreateCarTypeDto createCarTypeDto = CreateCarTypeDto.builder()
                    .name(name)
                    .build();

                carTypeService.save(createCarTypeDto);
            }
        });
    }
}
