package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.dtos.CreateParkingSlotDto;
import com.tericcabrel.parking.models.dtos.UpdateParkingSlotDto;
import com.tericcabrel.parking.models.responses.ParkingSlotListResponse;
import com.tericcabrel.parking.models.responses.ParkingSlotResponse;
import com.tericcabrel.parking.models.responses.GenericResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.services.interfaces.CarTypeService;
import com.tericcabrel.parking.services.interfaces.ParkingSlotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tericcabrel.parking.utils.Constants.*;

@Api(tags = "Parking's slot management", description = "Operations pertaining to parking's slot creation, update, fetch and delete")
@RestController
@RequestMapping(value = "/parking-slots")
public class ParkingSlotController {
    private ParkingSlotService parkingSlotService;

    private CarTypeService carTypeService;

    public ParkingSlotController(ParkingSlotService parkingSlotService, CarTypeService carTypeService) {
        this.parkingSlotService = parkingSlotService;
        this.carTypeService = carTypeService;
    }

    @ApiOperation(value = "Create parking's slot", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Parking's slot created successfully!", response = ParkingSlotResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<ParkingSlotResponse> create(@Valid @RequestBody CreateParkingSlotDto createParkingSlotDto) {
        CarType carType = carTypeService.findById(createParkingSlotDto.getCarTypeId());

        createParkingSlotDto.setCarType(carType);

        ParkingSlot parkingSlot = parkingSlotService.save(createParkingSlotDto);

        return ResponseEntity.ok(new ParkingSlotResponse(parkingSlot));
    }

    @ApiOperation(value = "Get all parking's slots", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List retrieved successfully!", response = ParkingSlotListResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<ParkingSlotListResponse> all(){
        return ResponseEntity.ok(new ParkingSlotListResponse(parkingSlotService.findAll()));
    }

    @ApiOperation(value = "Get one parking's slot", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Item retrieved successfully!", response = ParkingSlotResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlotResponse> one(@PathVariable String id){
        return ResponseEntity.ok(new ParkingSlotResponse(parkingSlotService.findById(id)));
    }

    @ApiOperation(value = "Update a parking's slot", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Parking's slot updated successfully!", response = ParkingSlotResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ParkingSlotResponse> update(@PathVariable String id, @Valid @RequestBody UpdateParkingSlotDto updateParkingSlotDto) {
        if (updateParkingSlotDto.getCarTypeId() != null) {
            updateParkingSlotDto.setCarType(carTypeService.findById(updateParkingSlotDto.getCarTypeId()));
        }

        return ResponseEntity.ok(new ParkingSlotResponse(parkingSlotService.update(id, updateParkingSlotDto)));
    }

    @ApiOperation(value = "Delete a parking's slot", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Parking's slot deleted successfully!", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        parkingSlotService.delete(id);

        return ResponseEntity.noContent().build();
    }


}
