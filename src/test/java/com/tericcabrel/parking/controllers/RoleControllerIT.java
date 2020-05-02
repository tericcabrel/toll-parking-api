package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.TestUtility;
import com.tericcabrel.parking.models.dtos.RoleDto;
import com.tericcabrel.parking.models.dtos.RoleUpdateDto;
import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.responses.*;
import com.tericcabrel.parking.repositories.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static com.tericcabrel.parking.utils.Constants.ROLE_ADMIN;
import static com.tericcabrel.parking.utils.Constants.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoleControllerIT {
    private static String ROLE_TEST = "ROLE_TEST";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestUtility testUtility;

    private HttpHeaders headers;

    private User user;

    private Role roleTest;

    @BeforeAll
    void beforeAll() {
        headers = testUtility.createHeaders();

        user = testUtility.createTestUser();

        String token = testUtility.getAccessToken(
            restTemplate, headers, user.getEmail(), testUtility.getCreateUserDto().getPassword()
        );

        headers.setBearerAuth(token);
    }

    @AfterAll
    void afterAll() {
        testUtility.deleteUser(user.getId());
    }

    @DisplayName("CreateRole - Fail: Invalid data")
    @Test
    @Order(1)
    void failToCreateRoleCauseInvalidData() {
        HttpEntity<RoleDto> request = new HttpEntity<>(new RoleDto(), headers);

        ResponseEntity<InvalidDataResponse> result = restTemplate.postForEntity("/roles", request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("name")).isTrue();
    }

    @DisplayName("CreateRole - Success")
    @Test
    @Order(2)
    void createRoleSuccess() {
        RoleDto roleDto = new RoleDto(ROLE_TEST, "Description of the role test");

        HttpEntity<RoleDto> request = new HttpEntity<>(roleDto, headers);

        ResponseEntity<RoleResponse> result = restTemplate.postForEntity("/roles", request, RoleResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        roleTest = Objects.requireNonNull(result.getBody()).getData();

        assertThat(roleTest.getId()).isNotNull();
        assertThat(roleTest.getName()).isEqualTo(ROLE_TEST);
    }

    @DisplayName("CreateUser - Fail: Already exists")
    @Test
    @Order(3)
    void failToCreateRoleCauseAlreadyExists() {
        RoleDto roleDto = new RoleDto(ROLE_TEST, "");

        HttpEntity<RoleDto> request = new HttpEntity<>(roleDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.postForEntity("/roles", request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(400);

        HashMap<String, Object> response = result.getBody().getData();

        assertThat(response).containsKey("message");
    }

    @DisplayName("GetAllRoles - Success")
    @Test
    @Order(4)
    void getAllRoleSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<RoleListResponse> result = restTemplate.exchange("/roles", HttpMethod.GET, request, RoleListResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        List<Role> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data).hasSize(3);
        assertThat(data.get(0).getName()).isIn(ROLE_ADMIN, ROLE_USER, ROLE_TEST);
    }

    @DisplayName("GetOneRole - Fail: Not exists")
    @Test
    @Order(5)
    void failToGetOneRoleCauseNotExists() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/roles/" + user.getId();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("GetOneRole - Success")
    @Test
    @Order(6)
    void getOneRoleSuccess() {
        HttpEntity request = new HttpEntity(headers);

        String url = "/roles/" + roleTest.getId();

        ResponseEntity<RoleResponse> result = restTemplate.exchange(url, HttpMethod.GET, request, RoleResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        Role data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.getId()).isEqualTo(roleTest.getId());
        assertThat(data.getName()).isEqualTo(roleTest.getName());
    }

    @DisplayName("UpdateRole - Fail: Invalid Data")
    @Test
    @Order(7)
    void failToUpdateCauseInvalidData() {
        HttpEntity<RoleDto> request = new HttpEntity<>(new RoleDto(), headers);
        String url = "/roles/" + roleTest.getId();

        ResponseEntity<InvalidDataResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("name")).isTrue();
    }

    @DisplayName("UpdateRole - Fail: Not found")
    @Test
    @Order(8)
    void failToUpdateCauseNotExists() {
        RoleDto roleDto = new RoleDto(ROLE_TEST, "");

        HttpEntity<RoleDto> request = new HttpEntity<>(roleDto, headers);
        String url = "/roles/" + user.getId();

        ResponseEntity<GenericResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("UpdateRole - Success")
    @Test
    @Order(9)
    void updateSuccess() {
        RoleDto roleDto = new RoleDto(ROLE_TEST+ "_UPDATE", "Description of the role test updated");

        HttpEntity<RoleDto> request = new HttpEntity<>(roleDto, headers);
        String url = "/roles/" + roleTest.getId();

        ResponseEntity<RoleResponse> result = restTemplate.exchange(url, HttpMethod.PUT, request, RoleResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        Role roleUpdated = result.getBody().getData();

        assertThat(roleUpdated.getId()).isEqualTo(roleTest.getId());
        assertThat(roleUpdated.getName()).isNotEqualTo(roleTest.getName());
        assertThat(roleUpdated.getDescription()).isNotEqualTo(roleTest.getDescription());

        roleTest = roleUpdated;
    }

    @DisplayName("AssignRole - Fail: Invalid data")
    @Test
    @Order(10)
    void failToAssignRolesToUserCauseInvalidData() {
        HttpEntity<RoleUpdateDto> request = new HttpEntity<>(new RoleUpdateDto(), headers);

        ResponseEntity<InvalidDataResponse> result = restTemplate.exchange("/roles/assign", HttpMethod.PUT, request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("userId")).isTrue();
        assertThat(errors.containsKey("roles")).isTrue();
    }

    @DisplayName("AssignRole - Fail: Invalid data")
    @Test
    @Order(11)
    void failToAssignRolesToUserCauseUserNotFound() {
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto(roleTest.getId(), new String[] { roleTest.getName() });

        HttpEntity<RoleUpdateDto> request = new HttpEntity<>(roleUpdateDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.exchange("/roles/assign", HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("AssignRole - Success")
    @Test
    @Order(12)
    void assignRolesToUserSuccess() {
        String roleUnexistant = "ROLE_UNEXISTANT";
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto(user.getId(), new String[] { roleTest.getName(), roleUnexistant });

        HttpEntity<RoleUpdateDto> request = new HttpEntity<>(roleUpdateDto, headers);

        ResponseEntity<UserResponse> result = restTemplate.exchange("/roles/assign", HttpMethod.PUT, request, UserResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        User userUpdated = result.getBody().getData();

        assertThat(userUpdated.getId()).isEqualTo(user.getId());
        assertThat(userUpdated.getRoles()).hasSize(3);

        List<String> roleNames = userUpdated.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        assertThat(roleNames).hasSize(3);
        assertThat(roleTest.getName()).isIn(roleNames);
        assertThat(roleUnexistant).isNotIn(roleNames);
    }

    @DisplayName("RevokeRole - Fail: Invalid data")
    @Test
    @Order(13)
    void failToRevokeRolesToUserCauseInvalidData() {
        HttpEntity<RoleUpdateDto> request = new HttpEntity<>(new RoleUpdateDto(), headers);

        ResponseEntity<InvalidDataResponse> result = restTemplate.exchange("/roles/revoke", HttpMethod.PUT, request, InvalidDataResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(422);

        HashMap<String, HashMap<String, List<String>>> data = Objects.requireNonNull(result.getBody()).getData();

        assertThat(data.containsKey("errors"));

        HashMap<String, List<String>> errors = data.get("errors");

        assertThat(errors.containsKey("userId")).isTrue();
        assertThat(errors.containsKey("roles")).isTrue();
    }

    @DisplayName("RevokeRole - Fail: Invalid data")
    @Test
    @Order(14)
    void failToRevokeRolesToUserCauseUserNotFound() {
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto(roleTest.getId(), new String[] { roleTest.getName() });

        HttpEntity<RoleUpdateDto> request = new HttpEntity<>(roleUpdateDto, headers);

        ResponseEntity<GenericResponse> result = restTemplate.exchange("/roles/revoke", HttpMethod.PUT, request, GenericResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("RevokeRole - Success")
    @Test
    @Order(15)
    void revokeRolesToUserSuccess() {
        String roleUnexistant = "ROLE_UNEXISTANT";
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto(user.getId(), new String[] { roleTest.getName(), roleUnexistant });

        HttpEntity<RoleUpdateDto> request = new HttpEntity<>(roleUpdateDto, headers);

        ResponseEntity<UserResponse> result = restTemplate.exchange("/roles/revoke", HttpMethod.PUT, request, UserResponse.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        User userUpdated = result.getBody().getData();

        assertThat(userUpdated.getId()).isEqualTo(user.getId());
        assertThat(userUpdated.getRoles()).hasSize(2);

        List<String> roleNames = userUpdated.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        assertThat(roleNames).hasSize(2);
        assertThat(roleTest.getName()).isNotIn(roleNames);
        assertThat(roleUnexistant).isNotIn(roleNames);
        assertThat(ROLE_USER).isIn(roleNames);
    }

    @DisplayName("DeleteRole - Success")
    @Test
    @Order(16)
    void deleteRoleSuccess() {
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity result = restTemplate.exchange("/roles/" + roleTest.getId(), HttpMethod.DELETE, request, ResponseEntity.class);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        List<String> roleNames = roleRepository.findAll().stream().map(Role::getName).collect(Collectors.toList());

        assertThat(roleNames).hasSize(2);
        assertThat(roleTest.getName()).isNotIn(roleNames);
    }
}