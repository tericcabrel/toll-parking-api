package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.TestUtility;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CalculatePricingDto;
import com.tericcabrel.parking.models.dtos.CreateParkingSlotDto;
import com.tericcabrel.parking.models.dtos.PricingPolicyDto;
import com.tericcabrel.parking.models.dtos.UpdateParkingSlotDto;
import com.tericcabrel.parking.models.responses.ParkingSlotListResponse;
import com.tericcabrel.parking.models.responses.ParkingSlotResponse;
import com.tericcabrel.parking.models.responses.GenericResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.repositories.ParkingSlotRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.tericcabrel.parking.utils.Constants.CAR_TYPE_50KW;
import static com.tericcabrel.parking.utils.Constants.CAR_TYPE_GASOLINE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParkingSlotControllerIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private TestUtility testUtility;

    private HttpHeaders headers;

    private ParkingSlot parkingSlot;

    private User user;

    private CreateParkingSlotDto createParkingSlotDtoTest;

    @BeforeAll
    void beforeAll() {
        headers = testUtility.createHeaders();

        user = testUtility.createTestUser();

        parkingSlot = testUtility.createParkingSlot();

        String token = testUtility.getAccessToken(
            restTemplate, headers, user.getEmail(), testUtility.getCreateUserDto().getPassword()
        );

        headers.setBearerAuth(token);
    }

    @AfterAll
    void afterAll() {
        testUtility.deleteUser(user.getId());
    }

    @DisplayName("CreateParkingSlot - Fail: Invalid data")
    @Test
    @Order(1)
    void failToCreateParkingSlotCauseInvalidData() {
        HttpEntity<CreateParkingSlotDto> request = new HttpEntity<>(new CreateParkingSlotDto(), headers);

        ResponseEntity<InvalidDataResponse> result = restTemplate.postForEntity("/parking-slots", request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        Map<String, Map<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors")).isTrue();

        Map<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("label")).isTrue();
        assertThat(errors.containsKey("state")).isTrue();
        assertThat(errors.containsKey("pricingPolicy")).isTrue();
        assertThat(errors.containsKey("carTypeId")).isTrue();
    }

    @DisplayName("CreateParkingSlot - Fail: Already exists")
    @Test
    @Order(2)
    void failToCreateParkingSlotCauseAlreadyExists() {
        CreateParkingSlotDto createParkingSlotDto = testUtility.getCreateParkingSlotDto();

        HttpEntity<CreateParkingSlotDto> request = new HttpEntity<>(createParkingSlotDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity("/parking-slots", request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        Map<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");
    }

    @DisplayName("CreateParkingSlot - Success")
    @Test
    @Order(3)
    void createParkingSlotSuccess() {
        CarType carType = testUtility.getCarType(CAR_TYPE_50KW);

        createParkingSlotDtoTest = testUtility.getCreateParkingSlotDto();

        HashMap<String, Double> parameters = new HashMap<>();

        parameters.put("numberOfHour", -1d);
        parameters.put("priceOfHour", 100d);
        parameters.put("taxes", 200d);

        String evaluation = "numberOfHour * priceOfHour + tax";

        createParkingSlotDtoTest
            .setCarTypeId(carType.getId())
            .setLabel("Slot 50KW")
            .setState("FREE")
            .setPricingPolicy(new PricingPolicyDto(parameters, evaluation));

        HttpEntity<CreateParkingSlotDto> request = new HttpEntity<>(createParkingSlotDtoTest, headers);

        ResponseEntity<ParkingSlotResponse> result = restTemplate.postForEntity("/parking-slots", request, ParkingSlotResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        parkingSlot = Objects.requireNonNull(result.getBody()).getData();

        assertThat(parkingSlot.getId()).isNotNull();
        assertThat(parkingSlot.getLabel()).isEqualTo(createParkingSlotDtoTest.getLabel());
        assertThat(parkingSlot.getState().toString()).isEqualTo(createParkingSlotDtoTest.getState());
        assertThat(parkingSlot.getPricingPolicy().getEvaluation()).isEqualTo(evaluation);
        assertThat(parkingSlot.getPricingPolicy().getParameters()).hasSize(3);
        assertThat(parkingSlot.getCarType().getName()).isEqualTo(carType.getName());
    }

    @DisplayName("GetAllParkingSlots - Success")
    @Test
    @Order(4)
    void getAllParkingSlotSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<ParkingSlotListResponse> result = restTemplate.exchange("/parking-slots", HttpMethod.GET, request, ParkingSlotListResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        List<ParkingSlot> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data).hasSize(2);
        assertThat(data.get(0).getLabel()).isIn("Slot 20KW", "Slot Gasoline");
    }

    @DisplayName("GetOneParkingSlot - Fail: Not exists")
    @Test
    @Order(5)
    void failToGetOneParkingSlotCauseNotExists() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/parking-slots/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("GetOneParkingSlot - Success")
    @Test
    @Order(6)
    void getOneParkingSlotSuccess() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/parking-slots/" + parkingSlot.getId();

        ResponseEntity<ParkingSlotResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, ParkingSlotResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        ParkingSlot data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.getId()).isEqualTo(parkingSlot.getId());
        assertThat(data.getLabel()).isEqualTo(parkingSlot.getLabel());
    }

    @DisplayName("CalculateParkingSlotRent - Fail: Invalid format")
    @Test
    @Order(7)
    void failToCalculateThePriceCauseInvalidFormat() {
        HashMap<String, Double> parameters = new HashMap<>();

        parameters.put("numberOfHour", -1d);
        parameters.put("priceOfHour", 100d);
        parameters.put("taxes", 200d);

        CalculatePricingDto calculatePricingDto = new CalculatePricingDto(parameters);

        HttpEntity<CalculatePricingDto> request = new HttpEntity<>(calculatePricingDto, headers);
        String url = "/parking-slots/" + parkingSlot.getId() + "/price";

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity(url, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        Map<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");
    }

    @DisplayName("UpdateParkingSlot - Fail: Invalid Data")
    @Test
    @Order(8)
    void failToUpdateCauseInvalidData() {
        HttpEntity<UpdateParkingSlotDto> request = new HttpEntity<>(new UpdateParkingSlotDto(), headers);
        String url = "/parking-slots/" + parkingSlot.getId();

        ResponseEntity<InvalidDataResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        Map<String, Map<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors")).isTrue();

        Map<String, List<String>> errors = data.get("errors");

        // errors.keySet().forEach(System.out::println);

        assertThat(errors.containsKey("pricingPolicy")).isTrue();
    }

    @DisplayName("UpdateParkingSlot - Fail: Not found")
    @Test
    @Order(9)
    void failToUpdateCauseNotExists() {
        UpdateParkingSlotDto updateParkingSlotDto = UpdateParkingSlotDto.builder()
            .state("BUSY")
            .pricingPolicyDto(createParkingSlotDtoTest.getPricingPolicy())
            .label(createParkingSlotDtoTest.getLabel())
            .build();

        HttpEntity<UpdateParkingSlotDto> request = new HttpEntity<>(updateParkingSlotDto, headers);
        String url = "/parking-slots/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("UpdateParkingSlot - Success")
    @Test
    @Order(10)
    void updateSuccess() {
        String invalidEvaluation = "numberOfHour )*( priceOfHour /+ taxes";

        createParkingSlotDtoTest.getPricingPolicy().setEvaluation(invalidEvaluation);

        CarType carType = testUtility.getCarType(CAR_TYPE_GASOLINE);

        UpdateParkingSlotDto updateParkingSlotDto = UpdateParkingSlotDto.builder()
            .state("FREE")
            .carTypeId(carType.getId())
            .pricingPolicyDto(createParkingSlotDtoTest.getPricingPolicy())
            .label("Slot Gasoline")
            .build();

        HttpEntity<UpdateParkingSlotDto> request = new HttpEntity<>(updateParkingSlotDto, headers);
        String url = "/parking-slots/" + parkingSlot.getId();

        ResponseEntity<ParkingSlotResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, ParkingSlotResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        ParkingSlot parkingSlotUpdated = result.getBody().getData();

        assertThat(parkingSlotUpdated.getId()).isEqualTo(parkingSlot.getId());
        assertThat(parkingSlotUpdated.getLabel()).isNotEqualTo(parkingSlot.getLabel());
        assertThat(parkingSlotUpdated.getCarType().getName()).isNotEqualTo(parkingSlot.getCarType().getName());
    }

    @DisplayName("CalculateParkingSlotRent - Fail: Invalid expression")
    @Test
    @Order(11)
    void failToCalculateThePriceCauseInvalidExpression() {
        HashMap<String, Double> parameters = new HashMap<>();

        parameters.put("numberOfHour", -1d);
        parameters.put("priceOfHour", 100d);
        parameters.put("taxes", 200d);

        CalculatePricingDto calculatePricingDto = new CalculatePricingDto(parameters);

        HttpEntity<CalculatePricingDto> request = new HttpEntity<>(calculatePricingDto, headers);
        String url = "/parking-slots/" + parkingSlot.getId() + "/price";

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity(url, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        Map<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");


        // Pricing policy of the parking's slot with a valid expression. Required for the next test case to pass
        PricingPolicyDto pricingPolicyDto = createParkingSlotDtoTest.getPricingPolicy();

        String validEvaluation = "numberOfHour * priceOfHour + taxes";
        pricingPolicyDto.setEvaluation(validEvaluation);

        UpdateParkingSlotDto updateParkingSlotDto = UpdateParkingSlotDto.builder()
            .pricingPolicyDto(pricingPolicyDto)
            .build();

        parkingSlot = testUtility.updateParkingSlotDto(parkingSlot.getId(), updateParkingSlotDto);
    }

    @DisplayName("CalculateParkingSlotRent - Success")
    @Test
    @Order(12)
    void calculateThePriceSuccess() {
        HashMap<String, Double> parameters = new HashMap<>();

        parameters.put("numberOfHour", 3d);
        parameters.put("priceOfHour", 100d);
        parameters.put("taxes", 200d);

        CalculatePricingDto calculatePricingDto = CalculatePricingDto.builder().parameters(parameters).build();

        HttpEntity<CalculatePricingDto> request = new HttpEntity<>(calculatePricingDto, headers);
        String url = "/parking-slots/" + parkingSlot.getId() + "/price";

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity(url, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        Map<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("price");
        assertThat((double) response.get("price")).isEqualTo(500d);
    }

    @DisplayName("DeleteParkingSlot - Success")
    @Test
    @Order(13)
    void deleteParkingSlotSuccess() {
        HttpEntity request = new HttpEntity(headers);
        String url = "/parking-slots/" + parkingSlot.getId();

        ResponseEntity result = restTemplate.exchange(url, HttpMethod.DELETE, request, ResponseEntity.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        List<String> parkingSlotLabels = parkingSlotRepository.findAll().stream()
            .map(ParkingSlot::getLabel)
            .collect(Collectors.toList());

        assertThat(parkingSlotLabels).hasSize(1);
        assertThat(parkingSlot.getLabel()).isNotIn(parkingSlotLabels);
    }
}