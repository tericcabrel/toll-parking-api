package com.tericcabrel.parking;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CreateCustomerDto;
import com.tericcabrel.parking.models.dtos.CreateUserDto;
import com.tericcabrel.parking.models.dtos.LoginUserDto;
import com.tericcabrel.parking.models.enums.GenderEnum;
import com.tericcabrel.parking.models.responses.AuthTokenResponse;
import com.tericcabrel.parking.services.interfaces.CarTypeService;
import com.tericcabrel.parking.services.interfaces.CustomerService;
import com.tericcabrel.parking.services.interfaces.RoleService;
import com.tericcabrel.parking.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
     * @return
     */
    public Customer createCustomer() {
        CreateCustomerDto createCustomerDto = getCreateCustomerDto();

        Customer customer = customerService.findByEmail(createCustomerDto.getEmail());

        if (customer == null) {
            customer = customerService.save(createCustomerDto);
        }

        return customer;
    }
}
