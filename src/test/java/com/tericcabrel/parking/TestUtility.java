package com.tericcabrel.parking;

import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CreateUserDto;
import com.tericcabrel.parking.models.dtos.LoginUserDto;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.models.responses.AuthTokenResponse;
import com.tericcabrel.parking.services.interfaces.RoleService;
import com.tericcabrel.parking.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.tericcabrel.parking.utils.Constants.ROLE_ADMIN;
import static com.tericcabrel.parking.utils.Constants.ROLE_USER;

@Component
public class TestUtility {
    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    public CreateUserDto getCreateUserDto() {
        return CreateUserDto.builder()
            .email("tericcabrel@yahoo.com")
            .enabled(true)
            .name("John Doe")
            .gender(GenderEnum.MALE.toString())
            .password("123456")
            .confirmPassword("123456")
            .roleNames(new String[]{ ROLE_ADMIN, ROLE_USER })
            .build();
    }

    public User createTestUser() {
        CreateUserDto createUserDto = getCreateUserDto();

        createUserDto.setRoles(roleService.findAll());

        User user = userService.findByEmail(createUserDto.getEmail());

        if (user == null) {
            user = userService.save(createUserDto);
        }

        return user;
    }

    public void deleteUser(User user) {
        userService.delete(user.getId());
    }

    public String getAccessToken(TestRestTemplate restTemplate, HttpHeaders headers, String email, String password) {
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        HttpEntity<LoginUserDto> request = new HttpEntity<>(loginUserDto, headers);

        ResponseEntity<AuthTokenResponse> resultLogin = restTemplate.postForEntity("/users/login", request, AuthTokenResponse.class);

        return resultLogin.getBody().getData().getAccessToken();
    }
}
