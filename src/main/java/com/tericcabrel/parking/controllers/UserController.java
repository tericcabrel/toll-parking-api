package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.exceptions.UserNotActiveException;
import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.dtos.CreateUserDto;
import com.tericcabrel.parking.models.dtos.LoginUserDto;
import com.tericcabrel.parking.models.dtos.UpdatePasswordDto;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.models.responses.AuthToken;
import com.tericcabrel.parking.models.responses.AuthTokenResponse;
import com.tericcabrel.parking.models.responses.UserListResponse;
import com.tericcabrel.parking.models.responses.UserResponse;
import com.tericcabrel.parking.services.interfaces.RoleService;
import com.tericcabrel.parking.services.interfaces.UserService;
import com.tericcabrel.parking.utils.JwtTokenUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import javax.validation.Valid;
import java.util.*;

import static com.tericcabrel.parking.utils.Constants.ROLE_USER;


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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserDto createUserDto) {
        Role role = roleService.findByName(ROLE_USER);

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        createUserDto.setRoles(roles);

        User user = userService.save(createUserDto);

        // eventPublisher.publishEvent(new OnCreateUserCompleteEvent(user));

        return ResponseEntity.ok(new UserResponse(user));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthTokenResponse> login(
        @Valid @RequestBody LoginUserDto loginUserDto
    ) throws AuthenticationException {
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<UserListResponse> all(){
        return ResponseEntity.ok(new UserListResponse(userService.findAll()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> currentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(new UserResponse(userService.findByEmail(authentication.getName())));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> one(@PathVariable String id){
        return ResponseEntity.ok(new UserResponse(userService.findById(id)));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable String id, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(new UserResponse(userService.update(id, updateUserDto)));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}/password")
    public ResponseEntity<UserResponse> updatePassword(
        @PathVariable String id, @Valid @RequestBody UpdatePasswordDto updatePasswordDto
    ) {
        return ResponseEntity.ok(new UserResponse(userService.updatePassword(id, updatePasswordDto)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
