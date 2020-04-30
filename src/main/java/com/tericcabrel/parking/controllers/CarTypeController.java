package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dtos.CreateCarTypeDto;
import com.tericcabrel.parking.models.responses.GenericResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.models.responses.CarTypeListResponse;
import com.tericcabrel.parking.models.responses.CarTypeResponse;
import com.tericcabrel.parking.services.interfaces.CarTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tericcabrel.parking.utils.Constants.*;

@Api(tags = "Car's type management", description = "Operations pertaining to car's type creation, update, fetch and delete")
@RestController
@RequestMapping(value = "/cars-types")
public class CarTypeController {
    private CarTypeService carTypeService;
    
    public CarTypeController(CarTypeService carTypeService) {
        this.carTypeService = carTypeService;
    }

    @ApiOperation(value = "Create car's type", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Car's type created successfully!", response = CarTypeResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CarTypeResponse> create(@Valid @RequestBody CreateCarTypeDto createCarTypeDto){
        CarType carType = carTypeService.save(createCarTypeDto);

        return ResponseEntity.ok(new CarTypeResponse(carType));
    }

    @ApiOperation(value = "Get all car's types", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List retrieved successfully!", response = CarTypeListResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<CarTypeListResponse> all(){
        return ResponseEntity.ok(new CarTypeListResponse(carTypeService.findAll()));
    }

    @ApiOperation(value = "Get one car's type", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Item retrieved successfully!", response = CarTypeResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CarTypeResponse> one(@PathVariable String id){
        return ResponseEntity.ok(new CarTypeResponse(carTypeService.findById(id)));
    }

    @ApiOperation(value = "Update a car's type", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Car's type updated successfully!", response = CarTypeResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CarTypeResponse> update(@PathVariable String id, @Valid @RequestBody CreateCarTypeDto carTypeDto) {
        return ResponseEntity.ok(new CarTypeResponse(carTypeService.update(id, carTypeDto)));
    }

    @ApiOperation(value = "Delete a car's type", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Car's type deleted successfully!", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        carTypeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
