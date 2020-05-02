package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.TestUtility;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CreateCarTypeDto;
import com.tericcabrel.parking.models.responses.*;
import com.tericcabrel.parking.repositories.CarTypeRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static com.tericcabrel.parking.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarTypeControllerIT {
    private static String CAR_TYPE_NAME = "FlyCar";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Autowired
    private TestUtility testUtility;

    private HttpHeaders headers;

    private CarType carType;

    @BeforeAll
    void beforeAll() {
        headers = testUtility.createHeaders();

        User user = testUtility.createTestUser();

        String token = testUtility.getAccessToken(
            restTemplate, headers, user.getEmail(), testUtility.getCreateUserDto().getPassword()
        );

        headers.setBearerAuth(token);
    }

    @DisplayName("CreateCarType - Fail: Invalid data")
    @Test
    @Order(1)
    void failToCreateCarTypeCauseInvalidData() {
        HttpEntity<CreateCarTypeDto> request = new HttpEntity<>(new CreateCarTypeDto(), headers);

        ResponseEntity<InvalidDataResponse> result = restTemplate.postForEntity("/cars-types", request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("name")).isTrue();
    }

    @DisplayName("CreateCarType - Fail: Already exists")
    @Test
    @Order(2)
    void failToCreateCarTypeCauseAlreadyExists() {
        CreateCarTypeDto createCarTypeDto = new CreateCarTypeDto(CAR_TYPE_20KW);

        HttpEntity<CreateCarTypeDto> request = new HttpEntity<>(createCarTypeDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity("/cars-types", request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        HashMap<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");
    }

    @DisplayName("CreateCarType - Success")
    @Test
    @Order(3)
    void createCarTypeSuccess() {
        CreateCarTypeDto createCarTypeDto = new CreateCarTypeDto(CAR_TYPE_NAME);

        HttpEntity<CreateCarTypeDto> request = new HttpEntity<>(createCarTypeDto, headers);

        ResponseEntity<CarTypeResponse> result = restTemplate.postForEntity("/cars-types", request, CarTypeResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        carType = Objects.requireNonNull(result.getBody()).getData();

        assertThat(carType.getId()).isNotNull();
        assertThat(carType.getName()).isEqualTo(CAR_TYPE_NAME);
    }

    @DisplayName("GetAllCarTypes - Success")
    @Test
    @Order(4)
    void getAllCarTypeSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<CarTypeListResponse> result = restTemplate.exchange("/cars-types", HttpMethod.GET, request, CarTypeListResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        List<CarType> data = Objects.requireNonNull(result.getBody()).getData();

        // Three car's type are created on application startup
        assertThat(data).hasSize(4);
        assertThat(data.get(0).getName()).isIn(CAR_TYPE_GASOLINE, CAR_TYPE_20KW, CAR_TYPE_50KW, CAR_TYPE_NAME);
    }

    @DisplayName("GetOneCarType - Fail: Not exists")
    @Test
    @Order(5)
    void failToGetOneCarTypeCauseNotExists() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/cars-types/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("GetOneCarType - Success")
    @Test
    @Order(6)
    void getOneCarTypeSuccess() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/cars-types/" + carType.getId();

        ResponseEntity<CarTypeResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, CarTypeResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        CarType data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.getId()).isEqualTo(carType.getId());
        assertThat(data.getName()).isEqualTo(carType.getName());
    }

    @DisplayName("UpdateCarType - Fail: Invalid Data")
    @Test
    @Order(7)
    void failToUpdateCauseInvalidData() {
        HttpEntity<CreateCarTypeDto> request = new HttpEntity<>(new CreateCarTypeDto(), headers);
        String url = "/cars-types/" + carType.getId();

        ResponseEntity<InvalidDataResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("name")).isTrue();
    }

    @DisplayName("UpdateCarType - Fail: Not found")
    @Test
    @Order(8)
    void failToUpdateCauseNotExists() {
        CreateCarTypeDto createCarTypeDto = new CreateCarTypeDto(CAR_TYPE_NAME);

        HttpEntity<CreateCarTypeDto> request = new HttpEntity<>(createCarTypeDto, headers);
        String url = "/cars-types/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("UpdateCarType - Success")
    @Test
    @Order(9)
    void updateSuccess() {
        CreateCarTypeDto carTypeDto = new CreateCarTypeDto(CAR_TYPE_NAME + "_UPDATE");

        HttpEntity<CreateCarTypeDto> request = new HttpEntity<>(carTypeDto, headers);
        String url = "/cars-types/" + carType.getId();

        ResponseEntity<CarTypeResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, CarTypeResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        CarType carTypeUpdated = result.getBody().getData();

        assertThat(carTypeUpdated.getId()).isEqualTo(carType.getId());
        assertThat(carTypeUpdated.getName()).isNotEqualTo(carType.getName());

        carType = carTypeUpdated;
    }

    @DisplayName("DeleteCarType - Success")
    @Test
    @Order(10)
    void deleteCarTypeSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity result = restTemplate.exchange("/cars-types/" + carType.getId(), HttpMethod.DELETE, request, ResponseEntity.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        List<String> carTypeNames = carTypeRepository.findAll().stream().map(CarType::getName).collect(Collectors.toList());

        assertThat(carTypeNames).hasSize(3);
        assertThat(carType.getName()).isNotIn(carTypeNames);
    }
}