package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.TestUtility;
import com.tericcabrel.parking.models.dbs.*;
import com.tericcabrel.parking.models.dtos.*;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;
import com.tericcabrel.parking.models.responses.GenericResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.models.responses.CarRechargeSessionListResponse;
import com.tericcabrel.parking.models.responses.CarRechargeSessionResponse;
import com.tericcabrel.parking.repositories.CarRechargeSessionRepository;
import com.tericcabrel.parking.services.interfaces.PricingPolicyService;
import com.tericcabrel.parking.utils.Helpers;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.tericcabrel.parking.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarRechargeSessionControllerIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private CarRechargeSessionRepository carRechargeSessionRepository;

    @Autowired
    PricingPolicyService pricingPolicyService;

    @Autowired
    private TestUtility testUtility;

    @MockBean
    private JavaMailSender mailSender;

    @Captor
    ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    private HttpHeaders headers;

    private CarRechargeSession carRecharge;

    private User user;
    
    private Customer customer;

    private Customer customer2;

    private ParkingSlot parkingSlot;

    private HashMap<String, Object> carRechargeDuration;

    @BeforeAll
    void beforeAll() {
        headers = testUtility.createHeaders();

        user = testUtility.createTestUser();
        
        customer = testUtility.createCustomer(CAR_TYPE_GASOLINE); // Car type => Gasoline

        CreateCustomerDto createCustomerDto = testUtility.getCreateCustomerDto(CAR_TYPE_20KW); // Car type => Gasoline
        createCustomerDto
            .setName("Test2 Customer")
            .setEmail("two-test@customer")
            .setPhone("+487834392")
            .setGender(GenderEnum.OTHER.toString());

        customer2 = testUtility.createCustomer(createCustomerDto);

        parkingSlot = testUtility.createParkingSlot(); // Car type: 20 KW

        String token = testUtility.getAccessToken(
            restTemplate, headers, user.getEmail(), testUtility.getCreateUserDto().getPassword()
        );

        headers.setBearerAuth(token);
    }

    @AfterAll
    void afterAll() {
        testUtility.deleteUser(user.getId());

        testUtility.deleteCustomer(customer.getId());

        testUtility.deleteCustomer(customer2.getId());

        testUtility.deleteParkingSlot(parkingSlot.getId());

        carRechargeSessionRepository.deleteAll();
    }

    @DisplayName("CreateCarRecharge - Fail: Invalid data")
    @Test
    @Order(1)
    void failToCreateCarRechargeCauseInvalidData() {
        HttpEntity<CreateCarRechargeSessionDto> request = new HttpEntity<>(new CreateCarRechargeSessionDto(), headers);

        ResponseEntity<InvalidDataResponse> result = restTemplate.postForEntity("/cars-recharges", request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("customerId")).isTrue();
    }

    @DisplayName("CreateCarRecharge - Fail: Car's type of customer and parking slot doesn't match")
    @Test
    @Order(2)
    void failToCreateCarRechargeCauseCarTypeOfCustomerAndParkingSlotDoesntMatch() {
        CreateCarRechargeSessionDto createCarRechargeDto = testUtility.getCreateCarRechargeSessionDto(customer.getId());

        HttpEntity<CreateCarRechargeSessionDto> request = new HttpEntity<>(createCarRechargeDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity("/cars-recharges", request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        HashMap<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");

        UpdateCustomerDto updateCustomerDto = UpdateCustomerDto.builder()
            .carType(testUtility.getCarType(CAR_TYPE_20KW))
            .build();
        
        customer = testUtility.updateCustomer(customer.getId(), updateCustomerDto);
    }

    @DisplayName("CreateCarRecharge - Success")
    @Test
    @Order(3)
    void createCarRechargeSuccess() {
        CreateCarRechargeSessionDto createCarRechargeDto = testUtility.getCreateCarRechargeSessionDto(customer.getId());


        HttpEntity<CreateCarRechargeSessionDto> request = new HttpEntity<>(createCarRechargeDto, headers);

        ResponseEntity<CarRechargeSessionResponse> result = restTemplate.postForEntity("/cars-recharges", request, CarRechargeSessionResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        carRecharge = Objects.requireNonNull(result.getBody()).getData();

        assertThat(carRecharge.getId()).isNotNull();
        assertThat(carRecharge.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(carRecharge.getStartTime().toString()).isNotNull();
        assertThat(carRecharge.getEndTime()).isNull();

        parkingSlot = testUtility.getParkingSlot(parkingSlot.getId());

        assertThat(parkingSlot.getState()).isEqualTo(ParkingSlotStateEnum.BUSY);
    }

    @DisplayName("GetAllCarRecharges - Success")
    @Test
    @Order(4)
    void getAllCarRechargeSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<CarRechargeSessionListResponse> result = restTemplate.exchange("/cars-recharges", HttpMethod.GET, request, CarRechargeSessionListResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        List<CarRechargeSession> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data).hasSize(1);
        assertThat(data.get(0).getCustomer().getId()).isEqualTo(customer.getId());
    }

    @DisplayName("GetOneCarRecharge - Fail: Not exists")
    @Test
    @Order(5)
    void failToGetOneCarRechargeCauseNotExists() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/cars-recharges/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("GetOneCarRecharge - Success")
    @Test
    @Order(6)
    void getOneCarRechargeSuccess() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/cars-recharges/" + carRecharge.getId();

        ResponseEntity<CarRechargeSessionResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, CarRechargeSessionResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        CarRechargeSession data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.getId()).isEqualTo(carRecharge.getId());
        assertThat(data.getCustomer().getId()).isEqualTo(carRecharge.getCustomer().getId());

    }

    @DisplayName("GetCarRechargeDuration - Fail: Car recharge Not found")
    @Test
    @Order(7)
    void failToGetDurationCauseCarRechargeNotFound() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/cars-recharges/" + ObjectId.get().toString() + "/duration";

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);

        HashMap<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");
    }

    @DisplayName("GetCarRechargeDuration - Success")
    @Test
    @Order(8)
    void getDurationSuccess() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/cars-recharges/" + carRecharge.getId() + "/duration";

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        carRechargeDuration = result.getBody().getData();

        System.out.println("Duration => " + carRechargeDuration.get("duration"));

        assertThat(carRechargeDuration).containsKey("duration");
        assertThat(carRechargeDuration).containsKey("time");
    }

    @DisplayName("UpdateCarRecharge - Fail: Invalid Data")
    @Test
    @Order(9)
    void failToUpdateCauseInvalidData() {
        HttpEntity<UpdateCarRechargeSessionDto> request = new HttpEntity<>(new UpdateCarRechargeSessionDto(), headers);
        String url = "/cars-recharges/" + carRecharge.getId() + "/complete";

        ResponseEntity<InvalidDataResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("endTime")).isTrue();
    }

    @DisplayName("UpdateCarRecharge - Fail: Not found")
    @Test
    @Order(10)
    void failToUpdateCauseNotExists() {
        Date endTime = Helpers.isoStringToDate(carRechargeDuration.get("time").toString());

        UpdateCarRechargeSessionDto updateCarRechargeDto = UpdateCarRechargeSessionDto.builder()
            .endTime(endTime)
            .price(10)
            .build();

        HttpEntity<UpdateCarRechargeSessionDto> request = new HttpEntity<>(updateCarRechargeDto, headers);
        String url = "/cars-recharges/" + ObjectId.get().toString() + "/complete";

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("CreateCarRecharge - Fail: Car's type of customer and parking slot match but none available")
    @Test
    @Order(11)
    void failToCreateCarRechargeCauseCarTypeOfCustomerAndParkingSlotMatchButNoneAvailable() {
        CreateCarRechargeSessionDto createCarRechargeDto = testUtility.getCreateCarRechargeSessionDto(customer2.getId());

        HttpEntity<CreateCarRechargeSessionDto> request = new HttpEntity<>(createCarRechargeDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity("/cars-recharges", request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        HashMap<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");
    }

    @DisplayName("UpdateCarRecharge - Success")
    @Test
    @Order(12)
    void updateSuccess() {
        // In CarRechargeSessionListener class, mailSender.createMimeMessage() is called before mailSender.send() and
        // since we have mocked JavaMailSender we need to define the result to be returned when this method will be called
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // Block the sending of email to customer
        doNothing().when(mailSender).send(mimeMessageCaptor.capture());

        HashMap<String, Double> parameters = new HashMap<>();
        parameters.put("numberOfHour", (double) carRechargeDuration.get("duration"));

        double price = pricingPolicyService.calculate(parkingSlot.getPricingPolicy(), parameters);
        Date endTime = Helpers.isoStringToDate(carRechargeDuration.get("time").toString());

        UpdateCarRechargeSessionDto updateCarRechargeDto = UpdateCarRechargeSessionDto.builder()
            .endTime(endTime)
            .price(price)
            .build();

        HttpEntity<UpdateCarRechargeSessionDto> request = new HttpEntity<>(updateCarRechargeDto, headers);
        String url = "/cars-recharges/" + carRecharge.getId() + "/complete";

        ResponseEntity<CarRechargeSessionResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, CarRechargeSessionResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        CarRechargeSession carRechargeUpdated = result.getBody().getData();

        assertThat(carRechargeUpdated.getId()).isEqualTo(carRecharge.getId());
        assertThat(carRechargeUpdated.getEndTime()).isNotNull();
        assertThat(carRechargeUpdated.getPrice()).isEqualTo(price);

        parkingSlot = testUtility.getParkingSlot(parkingSlot.getId());

        assertThat(parkingSlot.getState()).isEqualTo(ParkingSlotStateEnum.FREE);
        assertThat(parkingSlot.getLastUsedTime()).isNotNull();
        assertThat(parkingSlot.getLastUsedTime()).isEqualTo(updateCarRechargeDto.getEndTime());

        verify(mailSender, times(1)).send(mimeMessageCaptor.capture());

        try {
            Address[] addresses = mimeMessageCaptor.getValue().getAllRecipients();

            assertThat(addresses).hasSize(1);
            assertThat(addresses[0].toString()).isEqualTo(customer.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @DisplayName("DeleteCarRecharge - Success")
    @Test
    @Order(13)
    void deleteCarRechargeSuccess() {
        HttpEntity request = new HttpEntity(headers);
        String url = "/cars-recharges/" + carRecharge.getId();

        ResponseEntity result = restTemplate.exchange(url, HttpMethod.DELETE, request, ResponseEntity.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        List<CarRechargeSession> list = carRechargeSessionRepository.findAll();

        assertThat(list).hasSize(0);
    }
}