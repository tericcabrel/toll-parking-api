package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.models.dtos.UpdatePasswordDto;
import com.tericcabrel.parking.models.dtos.UpdateUserDto;
import com.tericcabrel.parking.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/users")
@Validated
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
