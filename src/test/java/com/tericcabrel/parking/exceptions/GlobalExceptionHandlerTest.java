package com.tericcabrel.parking.exceptions;

import com.tericcabrel.parking.TestUtility;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CreateUserDto;
import com.tericcabrel.parking.models.dtos.LoginUserDto;
import com.tericcabrel.parking.models.responses.AuthToken;
import com.tericcabrel.parking.models.responses.AuthTokenResponse;
import com.tericcabrel.parking.models.responses.GenericResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static com.tericcabrel.parking.utils.Constants.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GlobalExceptionHandlerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtility testUtility;

    private HttpHeaders headers;

    private User user;

    @BeforeAll
    void beforeAll() {
        headers = testUtility.createHeaders();

        CreateUserDto createUserDto = testUtility.getCreateUserDto();
        createUserDto.setRoleNames(new String[] { ROLE_USER });

        user = testUtility.createTestUserWithRoleUser();
    }

    @AfterAll
    void afterAll() {
        testUtility.deleteUser(user.getId());
    }

    /*@DisplayName("Login User Fail: BadCredentials")
    @Test
    @Order(1)
    void requestFailCauseBadCredentials() {
        LoginUserDto loginUserDto = LoginUserDto.builder()
            .email("unregistered@email.com")
            .password("fake-pswd")
            .build();

        HttpEntity<LoginUserDto> request = new HttpEntity<>(loginUserDto, headers);
        ResponseEntity<Object> result = restTemplate.exchange("/users/login", HttpMethod.POST, request, Object.class);

        System.out.println(result.getBody());

        assertThat(result.getStatusCodeValue()).isEqualTo(401);
    }*/

    @DisplayName("Login User: Success")
    @Test
    @Order(2)
    void loginUserSuccess() {
        LoginUserDto loginUserDto = LoginUserDto.builder()
            .email(user.getEmail())
            .password("secret")
            .build();

        HttpEntity<LoginUserDto> request = new HttpEntity<>(loginUserDto, headers);
        ResponseEntity<AuthTokenResponse> resultLogin = restTemplate.postForEntity("/users/login", request, AuthTokenResponse.class);

        assertThat(resultLogin.getStatusCodeValue()).isEqualTo(200);

        AuthToken responseLogin = resultLogin.getBody().getData();

        assertThat(responseLogin.getAccessToken()).isNotNull();
        assertThat(responseLogin.getExpiresIn()).isGreaterThan(new Date().getTime());

        headers.setBearerAuth(responseLogin.getAccessToken());
    }

    @DisplayName("Login User Fail: BadCredentials")
    @Test
    @Order(3)
    void requestFailCauseAccessDenied() {
        LoginUserDto loginUserDto = LoginUserDto.builder()
            .email("unregistered@email.com")
            .password("fake-pswd")
            .build();

        HttpEntity<LoginUserDto> request = new HttpEntity<>(loginUserDto, headers);
        ResponseEntity<GenericResponse> resultLogin = restTemplate.exchange("/users", HttpMethod.GET, request, GenericResponse.class);

        assertThat(resultLogin.getStatusCodeValue()).isEqualTo(403);
    }

    /*@DisplayName("Throw internal server error")
    @Test
    @Order(4)
    void throwInternalServerError() {
        CreateUserDto createUserDto = testUtility.getCreateUserDto();
        createUserDto.setEmail("itita@test.com");

        when(mockParkingSlot.getPricingPolicy()).thenReturn(null);

        HttpEntity<CreateUserDto> request = new HttpEntity<>(createUserDto, headers);
        ResponseEntity<Object> result = restTemplate.postForEntity("/users/create", request, Object.class);

        System.out.println(result.getBody());

        assertThat(result.getStatusCodeValue()).isEqualTo(500);

        verify(mockParkingSlot, atLeastOnce()).getPricingPolicy();
    }*/
}