package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.models.dtos.CreateUserDto;
import com.tericcabrel.parking.models.dtos.LoginUserDto;
import com.tericcabrel.parking.models.dtos.UpdatePasswordDto;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.models.responses.*;
import com.tericcabrel.parking.repositories.RoleRepository;
import com.tericcabrel.parking.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

import static com.tericcabrel.parking.utils.Constants.ROLE_ADMIN;
import static com.tericcabrel.parking.utils.Constants.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

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

    @MockBean
    private JavaMailSender mailSender;

    @Captor
    ArgumentCaptor<MimeMessage> mimeMessageCaptor;
    
    private HttpHeaders headers;

    private CreateUserDto createUserDto;
    
    private User user;

    @BeforeAll
    void beforeAll() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        createUserDto = CreateUserDto.builder()
                            .email("tericcabrel@yahoo.com")
                            .enabled(false)
                            .name("John Doe")
                            .gender(GenderEnum.MALE.toString())
                            .password("123456")
                            .confirmPassword("123456")
                            .roleNames(new String[]{ ROLE_ADMIN, ROLE_USER })
                            .build();
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

    @DisplayName("CreateUser - Fail: Invalid data")
    @Test
    @Order(1)
    void failToCreateCauseInvalidData() {
        HttpEntity<CreateUserDto> request = new HttpEntity<>(new CreateUserDto(), headers);

        ResponseEntity<InvalidDataResponse> result = restTemplate.postForEntity("/users/create", request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        // errors.keySet().stream().forEach(System.out::println);

        assertThat(errors.containsKey("email")).isTrue();
        assertThat(errors.containsKey("name")).isTrue();
        assertThat(errors.containsKey("password")).isTrue();
        assertThat(errors.containsKey("confirmPassword")).isTrue();
        assertThat(errors.containsKey("roleNames")).isTrue();
    }

    @DisplayName("Register - Create User Successfully")
    @Test
    @Order(2)
    void createSuccess() {
        // Can't stub method with BBDMockito
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        doNothing().when(mailSender).send(mimeMessageCaptor.capture());

        HttpEntity<CreateUserDto> request = new HttpEntity<>(createUserDto, headers);

        ResponseEntity<UserResponse> result = restTemplate.postForEntity("/users/create", request, UserResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        user = Objects.requireNonNull(result.getBody()).getData();

        verify(mailSender, times(1)).send(mimeMessageCaptor.capture());

        try {
            Address[] addresses = mimeMessageCaptor.getValue().getAllRecipients();

            assertThat(addresses).hasSize(1);
            assertThat(addresses[0].toString()).isEqualTo(createUserDto.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        assertThat(user.getEmail()).isEqualTo(createUserDto.getEmail());
        assertThat(user.getId()).isNotNull();
    }

    @DisplayName("Login - Fail: User not enabled")
    @Test
    @Order(3)
    void failToLoginCauseNotEnabled() {
        LoginUserDto loginUserDto = LoginUserDto.builder()
            .email(user.getEmail())
            .password(createUserDto.getPassword())
            .build();

        HttpEntity<LoginUserDto> request = new HttpEntity<>(loginUserDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity("/users/login", request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        HashMap<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");

        String message = response.get("message").toString();

        assertThat(message).contains("deactivated");

        userRepository.findById(new ObjectId(user.getId())).ifPresent(u -> {
            u.setEnabled(true);

            userRepository.save(u);
        });
    }

    @DisplayName("Login - Success")
    @Test
    @Order(4)
    void loginSuccess() {
        LoginUserDto loginUserDto = LoginUserDto.builder()
            .email(createUserDto.getEmail())
            .password(createUserDto.getPassword())
            .build();

        HttpEntity<LoginUserDto> request = new HttpEntity<>(loginUserDto, headers);

        ResponseEntity<AuthTokenResponse> result = restTemplate.postForEntity("/users/login", request, AuthTokenResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        AuthToken response = result.getBody().getData();

        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getExpiresIn()).isGreaterThan(new Date().getTime());

        headers.setBearerAuth(response.getAccessToken());
    }

    @DisplayName("GetAllUsers - Success")
    @Test
    @Order(5)
    void getAllUserSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<UserListResponse> result = restTemplate.exchange("/users", HttpMethod.GET, request, UserListResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        List<User> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data).hasSize(2);
    }

    @DisplayName("GetCurrentUser - Success")
    @Test
    @Order(6)
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
    @Order(7)
    void failToGetOneUserCauseNotExists() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/users/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("GetOneUser - Success")
    @Test
    @Order(8)
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
    @Order(9)
    void failToUpdateUserCauseInvalidData() {
        HttpEntity<UpdateUserDto> request = new HttpEntity<>(new UpdateUserDto(), headers);
        String url = "/users/" + user.getId();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);
    }

    @DisplayName("UpdateUser - Fail: User not found")
    @Test
    @Order(10)
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
    @Order(11)
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
    @Order(12)
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
    @Order(13)
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
    @Order(14)
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
    @Order(15)
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
    @Order(16)
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
    @Order(17)
    void deleteUserSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity result = restTemplate.exchange("/users/" + user.getId(), HttpMethod.DELETE, request, ResponseEntity.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        List<String> userEmails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toList());

        assertThat(userEmails).hasSize(1);
        assertThat(user.getEmail()).isNotIn(userEmails);
    }
}
