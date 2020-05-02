package com.tericcabrel.parking;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.*;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.models.responses.AuthTokenResponse;
import com.tericcabrel.parking.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.tericcabrel.parking.utils.Constants.*;

@Component
public class TestUtility {
    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CarTypeService carTypeService;

    @Autowired
    private ParkingSlotService parkingSlotService;

    /**
     * @return instance of CreateUserDto
     */
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

    /**
     * @return instance of User
     */
    public User createTestUser() {
        CreateUserDto createUserDto = getCreateUserDto();

        createUserDto.setRoles(roleService.findAll());

        User user = userService.findByEmail(createUserDto.getEmail());

        if (user == null) {
            user = userService.save(createUserDto);
        }

        return user;
    }

    public void deleteUser(String userId) {
        userService.delete(userId);
    }

    /**
     * @param restTemplate TestRestTemplate
     * @param headers HttpHeaders
     * @param email user's email
     * @param password user's password
     *
     * @return a token if successful
     */
    public String getAccessToken(TestRestTemplate restTemplate, HttpHeaders headers, String email, String password) {
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        HttpEntity<LoginUserDto> request = new HttpEntity<>(loginUserDto, headers);

        ResponseEntity<AuthTokenResponse> resultLogin = restTemplate.postForEntity("/users/login", request, AuthTokenResponse.class);

        return resultLogin.getBody().getData().getAccessToken();
    }

    /**
     * @return HttpHeaders
     */
    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    /**
     * @param name car's type
     *
     * @return instance of CarType
     */
    public CarType getCarType(String name) {
        return carTypeService.findByName(name);
    }

    /**
     * @return
     */
    public CreateCustomerDto getCreateCustomerDto() {
        return CreateCustomerDto.builder()
            .email("test@customer.com")
            .name("Test Customer")
            .gender(GenderEnum.MALE.toString())
            .phone("+2354544234")
            .carTypeId(getCarType(CAR_TYPE_20KW).getId())
            .build();
    }

    /**
     * @return instance of Customer
     */
    public Customer createCustomer() {
        CreateCustomerDto createCustomerDto = getCreateCustomerDto();

        Customer customer = customerService.findByEmail(createCustomerDto.getEmail());

        if (customer == null) {
            customer = customerService.save(createCustomerDto);
        }

        return customer;
    }

    /**
     * @return instance of CreateParkingSlotDto
     */
    public PricingPolicyDto getPricingPolicyDto(HashMap<String, Double> parameters, String evaluation) {
        return PricingPolicyDto.builder()
            .parameters(parameters)
            .evaluation(evaluation)
            .build();
    }

    /**
     * @return instance of CreateParkingSlotDto
     */
    public CreateParkingSlotDto getCreateParkingSlotDto() {
        HashMap<String, Double> parameters = new HashMap<>();

        parameters.put("numberOfHour", -1d);
        parameters.put("priceOfHour", 100d);

        PricingPolicyDto pricingPolicyDto = getPricingPolicyDto(parameters, "numberOfHour * priceOfHour");

        return CreateParkingSlotDto.builder()
            .label("Slot 20KW")
            .state("FREE")
            .pricingPolicyDto(pricingPolicyDto)
            .carTypeId(getCarType(CAR_TYPE_20KW).getId())
            .build();
    }

    /**
     * @return instance of ParkingSlot
     */
    public ParkingSlot createParkingSlot() {
        CreateParkingSlotDto createParkingSlotDto = getCreateParkingSlotDto();

        ParkingSlot parkingSlot = parkingSlotService.findByLabel(createParkingSlotDto.getLabel());

        if (parkingSlot == null) {
            parkingSlot = parkingSlotService.save(createParkingSlotDto);
        }

        return parkingSlot;
    }

    /**
     * @param id String
     * @param updateParkingSlotDto UpdateParkingSlotDto
     *
     * @return ParkingSlot
     */
    public ParkingSlot updateParkingSlotDto(String id, UpdateParkingSlotDto updateParkingSlotDto) {
        return parkingSlotService.update(id, updateParkingSlotDto);
    }
}
