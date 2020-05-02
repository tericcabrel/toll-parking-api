package com.tericcabrel.parking.configs;

import com.tericcabrel.parking.TestUtility;
import com.tericcabrel.parking.utils.JwtTokenUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomAuthenticationFilterIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtility testUtility;

    @SpyBean
    JwtTokenUtil jwtTokenUtil;

    private HttpHeaders headers;

    @BeforeAll
    void beforeAll() {
        headers = testUtility.createHeaders();
    }

    @DisplayName("Return JWT MALFORMED")
    @Test
    @Order(1)
    void throwJwtMalformed() {
        headers.setBearerAuth("fake-token");
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<Object> result = restTemplate.exchange("/users", HttpMethod.GET, request, Object.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(401);
    }

    @DisplayName("Return JWT ILLEGAL ARGUMENT")
    @Test
    @Order(2)
    void throwJwtIllegalArgument() {
        headers.setBearerAuth("fake-token");
        HttpEntity request = new HttpEntity(headers);

        doThrow(IllegalArgumentException.class).when(jwtTokenUtil).getUsernameFromToken(anyString());

        ResponseEntity<Object> result = restTemplate.exchange("/users", HttpMethod.GET, request, Object.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(401);

        verify(jwtTokenUtil).getUsernameFromToken(anyString());
    }

    @DisplayName("Return JWT EXPIRED")
    @Test
    @Order(3)
    void throwJwtExpired() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXJpY2NhYnJlbEB5YWhvby5jb20iLCJzY29wZXMiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImlhdCI6MTU4ODM1NTU1NCwiZXhwIjoxNTg4NDQxOTU0fQ.G2PGA0yBET029HdIRiBzk5HwYCMY_s9Pjvj4zZY2Gj0";

        headers.setBearerAuth(token);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<Object> result = restTemplate.exchange("/users", HttpMethod.GET, request, Object.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(401);
    }
}