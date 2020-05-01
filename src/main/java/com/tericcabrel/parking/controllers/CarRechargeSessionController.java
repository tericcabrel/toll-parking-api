package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dtos.CreateCarRechargeSessionDto;
import com.tericcabrel.parking.models.dtos.UpdateCarRechargeSessionDto;
import com.tericcabrel.parking.models.responses.CarRechargeSessionListResponse;
import com.tericcabrel.parking.models.responses.CarRechargeSessionResponse;
import com.tericcabrel.parking.models.responses.GenericResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.services.interfaces.CarRechargeSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tericcabrel.parking.utils.Constants.*;

@Api(tags = "Car's recharge session management", description = "Operations pertaining to car's recharge session creation, update, fetch and delete")
@RestController
@RequestMapping(value = "/cars-types")
public class CarRechargeSessionController {
    private CarRechargeSessionService carRechargeSessionService;
    
    public CarRechargeSessionController(CarRechargeSessionService carRechargeSessionService) {
        this.carRechargeSessionService = carRechargeSessionService;
    }

    @ApiOperation(value = "Create car's recharge session", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Car's recharge session created successfully!", response = CarRechargeSessionResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CarRechargeSessionResponse> create(
        @Valid @RequestBody CreateCarRechargeSessionDto createCarRechargeSessionDto
    ) {
        CarRechargeSession carRechargeSession = carRechargeSessionService.save(createCarRechargeSessionDto);

        return ResponseEntity.ok(new CarRechargeSessionResponse(carRechargeSession));
    }

    @ApiOperation(value = "Get all car's recharge sessions", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List retrieved successfully!", response = CarRechargeSessionListResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<CarRechargeSessionListResponse> all(){
        return ResponseEntity.ok(new CarRechargeSessionListResponse(carRechargeSessionService.findAll()));
    }

    @ApiOperation(value = "Get one car's recharge session", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Item retrieved successfully!", response = CarRechargeSessionResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CarRechargeSessionResponse> one(@PathVariable String id){
        return ResponseEntity.ok(new CarRechargeSessionResponse(carRechargeSessionService.findById(id)));
    }

    @ApiOperation(value = "Update a car's recharge session", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Car's recharge session updated successfully!", response = CarRechargeSessionResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CarRechargeSessionResponse> update(
        @PathVariable String id, @Valid @RequestBody UpdateCarRechargeSessionDto updateCarRechargeSessionDto
    ) {
        return ResponseEntity.ok(
            new CarRechargeSessionResponse(carRechargeSessionService.update(id, updateCarRechargeSessionDto))
        );
    }

    @ApiOperation(value = "Delete a car's recharge session", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Car's recharge session deleted successfully!", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        carRechargeSessionService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
