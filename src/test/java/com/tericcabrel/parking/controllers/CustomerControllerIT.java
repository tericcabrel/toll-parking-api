package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.TestUtility;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CreateCustomerDto;
import com.tericcabrel.parking.models.dtos.UpdateCustomerDto;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.models.responses.CustomerListResponse;
import com.tericcabrel.parking.models.responses.CustomerResponse;
import com.tericcabrel.parking.models.responses.GenericResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.repositories.CustomerRepository;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static com.tericcabrel.parking.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestUtility testUtility;

    private HttpHeaders headers;

    private Customer customer;

    private User user;

    @BeforeAll
    void beforeAll() {
        headers = testUtility.createHeaders();

        user = testUtility.createTestUser();

        customer = testUtility.createCustomer();

        String token = testUtility.getAccessToken(
            restTemplate, headers, user.getEmail(), testUtility.getCreateUserDto().getPassword()
        );

        headers.setBearerAuth(token);
    }

    @AfterAll
    void afterAll() {
        testUtility.deleteUser(user.getId());
    }

    @DisplayName("CreateCustomer - Fail: Invalid data")
    @Test
    @Order(1)
    void failToCreateCustomerCauseInvalidData() {
        HttpEntity<CreateCustomerDto> request = new HttpEntity<>(new CreateCustomerDto(), headers);

        ResponseEntity<InvalidDataResponse> result = restTemplate.postForEntity("/customers", request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("name")).isTrue();
        assertThat(errors.containsKey("email")).isTrue();
        assertThat(errors.containsKey("phone")).isTrue();
        assertThat(errors.containsKey("gender")).isTrue();
        assertThat(errors.containsKey("carTypeId")).isTrue();
    }

    @DisplayName("CreateCustomer - Fail: Already exists")
    @Test
    @Order(2)
    void failToCreateCustomerCauseAlreadyExists() {
        CreateCustomerDto createCustomerDto = testUtility.getCreateCustomerDto();

        HttpEntity<CreateCustomerDto> request = new HttpEntity<>(createCustomerDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity("/customers", request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        HashMap<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");
    }

    @DisplayName("CreateCustomer - Success")
    @Test
    @Order(3)
    void createCustomerSuccess() {
        CarType carType = testUtility.getCarType(CAR_TYPE_GASOLINE);

        CreateCustomerDto createCustomerDto = testUtility.getCreateCustomerDto();

        createCustomerDto
            .setCarTypeId(carType.getId())
                                .setEmail("main@customer.com")
                                .setName("Main Customer")
                                .setPhone("+53434834222");

        HttpEntity<CreateCustomerDto> request = new HttpEntity<>(createCustomerDto, headers);

        ResponseEntity<CustomerResponse> result = restTemplate.postForEntity("/customers", request, CustomerResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        customer = Objects.requireNonNull(result.getBody()).getData();

        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getName()).isEqualTo(createCustomerDto.getName());
        assertThat(customer.getEmail()).isEqualTo(createCustomerDto.getEmail());
        assertThat(customer.getPhone()).isEqualTo(createCustomerDto.getPhone());
        assertThat(customer.getCarType().getName()).isEqualTo(carType.getName());
    }

    @DisplayName("GetAllCustomers - Success")
    @Test
    @Order(4)
    void getAllCustomerSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<CustomerListResponse> result = restTemplate.exchange("/customers", HttpMethod.GET, request, CustomerListResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        List<Customer> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data).hasSize(2);
        assertThat(data.get(0).getEmail()).isIn("test@customer.com", "main@customer.com");
    }

    @DisplayName("GetOneCustomer - Fail: Not exists")
    @Test
    @Order(5)
    void failToGetOneCustomerCauseNotExists() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/customers/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("GetOneCustomer - Success")
    @Test
    @Order(6)
    void getOneCustomerSuccess() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/customers/" + customer.getId();

        ResponseEntity<CustomerResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, CustomerResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        Customer data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.getId()).isEqualTo(customer.getId());
        assertThat(data.getName()).isEqualTo(customer.getName());
    }

    @DisplayName("UpdateCustomer - Fail: Invalid Data")
    @Test
    @Order(7)
    void failToUpdateCauseInvalidData() {
        HttpEntity<UpdateCustomerDto> request = new HttpEntity<>(new UpdateCustomerDto(), headers);
        String url = "/customers/" + customer.getId();

        ResponseEntity<InvalidDataResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("gender")).isTrue();
    }

    @DisplayName("UpdateCustomer - Fail: Not found")
    @Test
    @Order(8)
    void failToUpdateCauseNotExists() {
        UpdateCustomerDto updateCustomerDto = UpdateCustomerDto.builder()
            .name("Name Update")
            .gender(GenderEnum.FEMALE.toString())
            .build();

        HttpEntity<UpdateCustomerDto> request = new HttpEntity<>(updateCustomerDto, headers);
        String url = "/customers/" + ObjectId.get().toString();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("UpdateCustomer - Success")
    @Test
    @Order(9)
    void updateSuccess() {
        UpdateCustomerDto updateCustomerDto = UpdateCustomerDto.builder()
            .name("Name Update")
            .gender(GenderEnum.FEMALE.toString())
            .build();

        HttpEntity<UpdateCustomerDto> request = new HttpEntity<>(updateCustomerDto, headers);
        String url = "/customers/" + customer.getId();

        ResponseEntity<CustomerResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, CustomerResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        Customer customerUpdated = result.getBody().getData();

        assertThat(customerUpdated.getId()).isEqualTo(customer.getId());
        assertThat(customerUpdated.getName()).isNotEqualTo(customer.getName());

        customer = customerUpdated;
    }

    @DisplayName("DeleteCustomer - Success")
    @Test
    @Order(10)
    void deleteCustomerSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity result = restTemplate.exchange("/customers/" + customer.getId(), HttpMethod.DELETE, request, ResponseEntity.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        List<String> customerNames = customerRepository.findAll().stream().map(Customer::getName).collect(Collectors.toList());

        assertThat(customerNames).hasSize(1);
        assertThat(customer.getName()).isNotIn(customerNames);
    }
}