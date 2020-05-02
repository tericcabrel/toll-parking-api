package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.models.dtos.LoginUserDto;
import com.tericcabrel.parking.models.dtos.UpdatePasswordDto;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.models.responses.*;
import com.tericcabrel.parking.repositories.RoleRepository;
import com.tericcabrel.parking.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static com.tericcabrel.parking.utils.Constants.ROLE_ADMIN;
import static com.tericcabrel.parking.utils.Constants.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptEncoder;

    private HttpHeaders headers;

    private User user;
    

    @BeforeAll
    void beforeAll() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Role> roles = new ArrayList<>(Arrays.asList(
            roleRepository.findByName(ROLE_ADMIN),
            roleRepository.findByName(ROLE_USER)
        ));

        User newUser = User.builder()
                            .email("tericcabrel@yahoo.com")
                            .enabled(true)
                            .name("John Doe")
                            .gender(GenderEnum.MALE)
                            .password(bCryptEncoder.encode("123456"))
                            .roles(roles)
                            .build();

        user = userRepository.save(newUser);

        LoginUserDto loginUserDto = new LoginUserDto(newUser.getEmail(), "123456");

        HttpEntity<LoginUserDto> request = new HttpEntity<>(loginUserDto, headers);

        ResponseEntity<AuthTokenResponse> resultLogin = restTemplate.postForEntity("/users/login", request, AuthTokenResponse.class);

        AuthToken response = resultLogin.getBody().getData();

        headers.setBearerAuth(response.getAccessToken());
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @DisplayName("GetAllUsers - Success")
    @Test
    @Order(1)
    void getAllUserSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<UserListResponse> result = restTemplate.exchange("/users", HttpMethod.GET, request, UserListResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        List<User> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data).hasSize(2);
    }

    @DisplayName("GetCurrentUser - Success")
    @Test
    @Order(2)
    void getCurrentUserSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<UserResponse> result = restTemplate.exchange("/users/me", HttpMethod.GET, request, UserResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        User data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data).isNotNull();
        assertThat(data.getId()).isEqualTo(user.getId());
    }

    @DisplayName("GetOneUser - Fail: Not exists")
    @Test
    @Order(3)
    void failToGetOneUserCauseNotExists() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/users/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("GetOneUser - Success")
    @Test
    @Order(4)
    void getOneUserSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<UserResponse> result = restTemplate.exchange("/users/" + user.getId(), HttpMethod.GET, request, UserResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        User data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data).isNotNull();
        assertThat(data.getId()).isEqualTo(user.getId());
    }

    @DisplayName("UpdateUser - Fail: Invalid data")
    @Test
    @Order(5)
    void failToUpdateUserCauseInvalidData() {
        HttpEntity<UpdateUserDto> request = new HttpEntity<>(new UpdateUserDto(), headers);
        String url = "/users/" + user.getId();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);
    }

    @DisplayName("UpdateUser - Fail: User not found")
    @Test
    @Order(6)
    void failToUpdateUserCauseUserNotFound() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
            .name("Jamie Stenton")
            .gender(GenderEnum.FEMALE.toString())
            .enabled(0)
            .build();

        HttpEntity<UpdateUserDto> request = new HttpEntity<>(updateUserDto, headers);
        String url = "/users/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
        assertThat(result.getBody().getData()).containsKey("message");
    }

    @DisplayName("UpdateUser - Success")
    @Test
    @Order(7)
    void updateUserSuccess() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
            .name("Jamie Stenton")
            .gender(GenderEnum.FEMALE.toString())
            .enabled(1)
            .build();

        HttpEntity<UpdateUserDto> request = new HttpEntity<>(updateUserDto, headers);
        String url = "/users/" + user.getId();

        ResponseEntity<UserResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, UserResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        user = result.getBody().getData();

        assertThat(user.getName()).isEqualTo(updateUserDto.getName());
        assertThat(user.getGender().toString()).isEqualTo(updateUserDto.getGender());
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isEnabled()).isEqualTo(updateUserDto.getEnabled() == 1);
    }

    @DisplayName("UpdateUserPassword - Fail: Invalid data")
    @Test
    @Order(8)
    void failToUpdateUserPasswordCauseInvalidData() {
        HttpEntity<UpdatePasswordDto> request = new HttpEntity<>(new UpdatePasswordDto(), headers);
        String url = "/users/" + user.getId() + "/password";

        ResponseEntity<InvalidDataResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("currentPassword")).isTrue();
        assertThat(errors.containsKey("newPassword")).isTrue();
    }

    @DisplayName("UpdateUserPassword - Fail: User not found")
    @Test
    @Order(9)
    void failToUpdateUserPasswordCauseUserNotFound() {
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
            .currentPassword("123456")
            .newPassword("fakePsswd")
            .confirmNewPassword("fakePsswd")
            .build();

        HttpEntity<UpdatePasswordDto> request = new HttpEntity<>(updatePasswordDto, headers);
        String url = "/users/" + ObjectId.get().toString() + "/password";

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
        assertThat(result.getBody().getData()).containsKey("message");
    }

    @DisplayName("UpdateUserPassword - Fail: Pssword not match")
    @Test
    @Order(10)
    void failToUpdateUserPasswordCausePasswordNotMatch() {
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
            .currentPassword("bad-paswd")
            .newPassword("fakePsswd")
            .confirmNewPassword("fakePsswd")
            .build();

        HttpEntity<UpdatePasswordDto> request = new HttpEntity<>(updatePasswordDto, headers);
        String url = "/users/" + user.getId() + "/password";

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);
        assertThat(result.getBody().getData()).containsKey("message");
    }

    @DisplayName("UpdateUserPassword - Success")
    @Test
    @Order(11)
    void updateUserPasswordSuccess() {
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
            .currentPassword("123456")
            .newPassword("NewPaswD")
            .confirmNewPassword("NewPaswD")
            .build();

        HttpEntity<UpdatePasswordDto> request = new HttpEntity<>(updatePasswordDto, headers);
        String url = "/users/" + user.getId() + "/password";

        ResponseEntity<UserResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, UserResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        User updatedUser = result.getBody().getData();

        assertThat(updatedUser.getName()).isEqualTo(user.getName());
        assertThat(updatedUser.getGender()).isEqualTo(user.getGender());
    }

    @DisplayName("UpdateUserPassword - Login Success")
    @Test
    @Order(12)
    void loginUserSuccess() {
        LoginUserDto loginUserDto = LoginUserDto.builder()
            .email(user.getEmail())
            .password("NewPaswD")
            .build();

        HttpEntity<LoginUserDto> requestLogin = new HttpEntity<>(loginUserDto, headers);
        ResponseEntity<AuthTokenResponse> resultLogin = restTemplate.postForEntity("/users/login", requestLogin, AuthTokenResponse.class);

        assertThat(resultLogin.getStatusCodeValue()).isEqualTo(200);

        AuthToken responseLogin = resultLogin.getBody().getData();

        assertThat(responseLogin.getAccessToken()).isNotNull();
        assertThat(responseLogin.getExpiresIn()).isGreaterThan(new Date().getTime());
    }

    @DisplayName("DeleteUser - Success")
    @Test
    @Order(13)
    void deleteUserSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity result = restTemplate.exchange("/users/" + user.getId(), HttpMethod.DELETE, request, ResponseEntity.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        List<String> userEmails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toList());

        assertThat(userEmails).hasSize(1);
        assertThat(user.getEmail()).isNotIn(userEmails);
    }
}
