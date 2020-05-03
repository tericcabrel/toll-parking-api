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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {
    @InjectMocks
    DataSeeder dataSeeder;

    @Mock
    RoleService roleService;

    @Mock
    UserService userService;

    @Mock
    CarTypeService carTypeService;

    private Role role;

    @BeforeEach
    void beforeEach() {
        role = Role.builder().name("ROLE").description("Role description").build();
    }

    @Test
    void loadRoles() {
        when(roleService.findByName(anyString())).thenReturn(null);
        when(roleService.save(any(RoleDto.class))).thenReturn(role);

        dataSeeder.loadRoles();

        verify(roleService, times(2)).findByName(anyString());
        verify(roleService, times(2)).save(any(RoleDto.class));
    }

    @Test
    void loadUsers() {
        User user = User.builder()
            .email("test@user.com")
            .name("Test User")
            .password("qwerty")
            .gender(GenderEnum.MALE)
            .enabled(true)
            .build();

        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.save(any(CreateUserDto.class))).thenReturn(user);
        when(roleService.findByName(anyString())).thenReturn(role);

        dataSeeder.loadUsers();

        verify(userService).findByEmail(anyString());
        verify(roleService).findByName(anyString());
        verify(userService).save(any(CreateUserDto.class));
    }

    @Test
    void loadCarTypes() {
        CarType carType = CarType.builder().name("").build();

        when(carTypeService.findByName(anyString())).thenReturn(null);
        when(carTypeService.save(any(CreateCarTypeDto.class))).thenReturn(carType);

        dataSeeder.loadCarTypes();

        verify(carTypeService, times(3)).findByName(anyString());
        verify(carTypeService, times(3)).save(any(CreateCarTypeDto.class));
    }
}