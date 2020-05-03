package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.configs.SwaggerConfiguration;
import com.tericcabrel.parking.models.dtos.RoleDto;
import com.tericcabrel.parking.models.dtos.RoleUpdateDto;
import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dbs.User;
import com.tericcabrel.parking.models.responses.*;
import com.tericcabrel.parking.services.interfaces.RoleService;
import com.tericcabrel.parking.services.interfaces.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

import static com.tericcabrel.parking.utils.Constants.*;


@Api(tags = { SwaggerConfiguration.TAG_ROLE })
@RestController
@RequestMapping(value = "/roles")
public class RoleController {
    private RoleService roleService;

    private UserService userService;

    public RoleController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @ApiOperation(value = "Create a role", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Role created successfully!", response = RoleResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleDto roleDto){
        Role role = roleService.save(roleDto);

        return ResponseEntity.ok(new RoleResponse(role));
    }

    @ApiOperation(value = "Get all roles", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List retrieved successfully!", response = RoleListResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<RoleListResponse> all(){
        return ResponseEntity.ok(new RoleListResponse(roleService.findAll()));
    }

    @ApiOperation(value = "Get one role", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Item retrieved successfully!", response = RoleResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> one(@PathVariable String id){
        return ResponseEntity.ok(new RoleResponse(roleService.findById(id)));
    }

    @ApiOperation(value = "Update a role", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Role updated successfully!", response = RoleResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable String id, @Valid @RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(new RoleResponse(roleService.update(id, roleDto)));
    }

    @ApiOperation(value = "Delete a role", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Role deleted successfully!", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        roleService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Assign roles to an user", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Roles successfully assigned to user!", response = UserResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/assign")
    public ResponseEntity assignRoles(@Valid @RequestBody RoleUpdateDto roleUpdateDto) {
        User user = userService.findById(roleUpdateDto.getUserId());

        Arrays.stream(roleUpdateDto.getRoles()).forEach(role -> {
            Role roleObject = roleService.findByName(role);

            if (roleObject != null && !user.hasRole(role)) {
                user.addRole(roleObject);
            }
        });

        return ResponseEntity.ok().body(new UserResponse(userService.update(user)));
    }

    @ApiOperation(value = "Assign roles to an user", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Roles successfully assigned to user!", response = UserResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/revoke")
    public ResponseEntity revokeRoles(@Valid @RequestBody RoleUpdateDto roleUpdateDto) {
        User user = userService.findById(roleUpdateDto.getUserId());

        Arrays.stream(roleUpdateDto.getRoles()).forEach(role -> {
            Role roleObject = roleService.findByName(role);

            if (roleObject != null && user.hasRole(role)) {
                user.removeRole(roleObject);
            }
        });

        return ResponseEntity.ok().body(new UserResponse(userService.update(user)));
    }
}
