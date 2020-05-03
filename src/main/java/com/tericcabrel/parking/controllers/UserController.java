package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.configs.SwaggerConfiguration;
import com.tericcabrel.parking.events.OnCreateUserCompleteEvent;
import com.tericcabrel.parking.exceptions.UserNotActiveException;
import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CreateUserDto;
import com.tericcabrel.parking.models.dtos.LoginUserDto;
import com.tericcabrel.parking.models.dtos.UpdatePasswordDto;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.models.responses.*;
import com.tericcabrel.parking.services.interfaces.RoleService;
import com.tericcabrel.parking.services.interfaces.UserService;
import com.tericcabrel.parking.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.tericcabrel.parking.utils.Constants.*;

@Api(tags = { SwaggerConfiguration.TAG_USER })
@RestController
@RequestMapping(value = "/users")
@Validated
public class UserController {
    private UserService userService;

    private RoleService roleService;

    private AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    private ApplicationEventPublisher eventPublisher;

    public UserController(
        UserService userService,
        RoleService roleService,
        AuthenticationManager authenticationManager,
        JwtTokenUtil jwtTokenUtil,
        ApplicationEventPublisher eventPublisher
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.eventPublisher = eventPublisher;
    }

    @ApiOperation(value = "Register a new user in the system", response = UserResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Registered successfully!", response = UserResponse.class),
        @ApiResponse(code = 400, message = "User already exists!", response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    // @PreAuthorize("hasRole('ROLE_ADMIN')") To make the test of API easy
    @PostMapping(value = "/create")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserDto createUserDto) {
        List<Role> roles = new ArrayList<>();

        Arrays.stream(createUserDto.getRoleNames()).forEach(roleName -> roles.add(roleService.findByName(roleName)));

        createUserDto.setRoles(roles);

        User user = userService.save(createUserDto);

        eventPublisher.publishEvent(new OnCreateUserCompleteEvent(user, createUserDto.getConfirmPassword()));

        return ResponseEntity.ok(new UserResponse(user));
    }

    @ApiOperation(value = "Authenticate a user", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Authenticated successfully!", response = AuthTokenResponse.class),
        @ApiResponse(code = 400, message = "Bad credentials | The account is deactivated ", response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PostMapping(value = "/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginUserDto loginUserDto) {
        final Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginUserDto.getEmail(),
                loginUserDto.getPassword()
            )
        );

        User user = userService.findByEmail(loginUserDto.getEmail());

        if (!user.isEnabled()) {
            throw new UserNotActiveException("Your account has been deactivated!");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = jwtTokenUtil.createTokenFromAuth(authentication);

        Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);

        return ResponseEntity.ok(new AuthTokenResponse(new AuthToken(token, expirationDate.getTime())));
    }

    @ApiOperation(value = "Get all users", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List retrieved successfully!", response = UserListResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = INVALID_DATA_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<UserListResponse> all(){
        return ResponseEntity.ok(new UserListResponse(userService.findAll()));
    }

    @ApiOperation(value = "Get the authenticated user", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "User retrieved successfully!", response = UserResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(new UserResponse(userService.findByEmail(authentication.getName())));
    }

    @ApiOperation(value = "Get one user", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Item retrieved successfully!", response = UserResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> one(@PathVariable String id) {
        return ResponseEntity.ok(new UserResponse(userService.findById(id)));
    }

    @ApiOperation(value = "Update an user", response = UserResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "User updated successfully!", response = UserResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable String id, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(new UserResponse(userService.update(id, updateUserDto)));
    }

    @ApiOperation(value = "Update user password", response = UserResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The password updated successfully!", response = UserResponse.class),
        @ApiResponse(code = 400, message = "The current password is invalid", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}/password")
    public ResponseEntity<UserResponse> updatePassword(
        @PathVariable String id, @Valid @RequestBody UpdatePasswordDto updatePasswordDto
    ) {
        return ResponseEntity.ok(new UserResponse(userService.updatePassword(id, updatePasswordDto)));
    }

    @ApiOperation(value = "Delete an user", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "User deleted successfully!", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
