package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.events.OnCarRechargeSessionCompleteEvent;
import com.tericcabrel.parking.exceptions.NoParkingSlotAvailableException;
import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.models.dtos.CreateCarRechargeSessionDto;
import com.tericcabrel.parking.models.dtos.UpdateCarRechargeSessionDto;
import com.tericcabrel.parking.models.enums.ParkingSlotStateEnum;
import com.tericcabrel.parking.models.responses.CarRechargeSessionListResponse;
import com.tericcabrel.parking.models.responses.CarRechargeSessionResponse;
import com.tericcabrel.parking.models.responses.GenericResponse;
import com.tericcabrel.parking.models.responses.InvalidDataResponse;
import com.tericcabrel.parking.services.interfaces.CarRechargeSessionService;
import com.tericcabrel.parking.services.interfaces.CustomerService;
import com.tericcabrel.parking.services.interfaces.ParkingSlotService;
import com.tericcabrel.parking.utils.Helpers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.tericcabrel.parking.utils.Constants.*;

@Api(tags = "Car's recharge management", description = "Operations pertaining to car's recharge creation, update, fetch and delete")
@RestController
@RequestMapping(value = "/cars-recharges")
public class CarRechargeSessionController {
    private CarRechargeSessionService carRechargeSessionService;

    private CustomerService customerService;

    private ParkingSlotService parkingSlotService;

    private ApplicationEventPublisher eventPublisher;

    public CarRechargeSessionController(
        CarRechargeSessionService carRechargeSessionService,
        CustomerService customerService,
        ParkingSlotService parkingSlotService,
        ApplicationEventPublisher eventPublisher
    ) {
        this.carRechargeSessionService = carRechargeSessionService;
        this.customerService = customerService;
        this.parkingSlotService = parkingSlotService;
        this.eventPublisher = eventPublisher;
    }

    @ApiOperation(value = "Create car's recharge", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Car's recharge created successfully!", response = CarRechargeSessionResponse.class),
        @ApiResponse(code = 400, message = "No parking's slot available", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<CarRechargeSessionResponse> create(
        @Valid @RequestBody CreateCarRechargeSessionDto createCarRechargeSessionDto
    ) {
        Customer customer = customerService.findById(createCarRechargeSessionDto.getCustomerId());

        System.out.println(customer.getCarType());

        List<ParkingSlot> parkingSlotListAvailable = parkingSlotService.findAvailableByCarType(customer.getCarType());

        if (parkingSlotListAvailable.size() == 0) {
            throw new NoParkingSlotAvailableException(
                "No parking's slot available for the car's type " + customer.getCarType().getName() + " at the moment"
            );
        }

        createCarRechargeSessionDto
            .setCustomer(customer)
            .setParkingSlot(parkingSlotListAvailable.get(0));

        CarRechargeSession carRechargeSession = carRechargeSessionService.save(createCarRechargeSessionDto);

        // Mark the parking slot as busy
        parkingSlotListAvailable.get(0).setState(ParkingSlotStateEnum.BUSY);
        parkingSlotService.update(parkingSlotListAvailable.get(0));

        return ResponseEntity.ok(new CarRechargeSessionResponse(carRechargeSession));
    }

    @ApiOperation(value = "Get all car's recharges", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List retrieved successfully!", response = CarRechargeSessionListResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<CarRechargeSessionListResponse> all(){
        return ResponseEntity.ok(new CarRechargeSessionListResponse(carRechargeSessionService.findAll()));
    }

    @ApiOperation(value = "Get one car's recharge", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Item retrieved successfully!", response = CarRechargeSessionResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CarRechargeSessionResponse> one(@PathVariable String id){
        return ResponseEntity.ok(new CarRechargeSessionResponse(carRechargeSessionService.findById(id)));
    }

    @ApiOperation(value = "Update a car's recharge", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Car's recharge updated successfully!", response = CarRechargeSessionResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<CarRechargeSessionResponse> update(
        @PathVariable String id, @Valid @RequestBody UpdateCarRechargeSessionDto updateCarRechargeSessionDto
    ) {
        CarRechargeSession carRechargeSession = carRechargeSessionService.findById(id);

        if (carRechargeSession.getEndTime() == null) {
            carRechargeSession = carRechargeSessionService.update(id, updateCarRechargeSessionDto);

            ParkingSlot parkingSlot = parkingSlotService.findById(carRechargeSession.getParkingSlot().getId());

            parkingSlot.setState(ParkingSlotStateEnum.FREE);
            parkingSlot.setLastUsedTime(carRechargeSession.getEndTime());

            parkingSlotService.update(parkingSlot);

            // Notify the event to the listener to send the email
            eventPublisher.publishEvent(
                new OnCarRechargeSessionCompleteEvent(carRechargeSession.getCustomer(), carRechargeSession)
            );
        }

        return ResponseEntity.ok(new CarRechargeSessionResponse(carRechargeSession));
    }

    @ApiOperation(value = "Delete a car's recharge", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Car's recharge deleted successfully!", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        carRechargeSessionService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Update a car's recharge", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Car's recharge updated successfully!", response = CarRechargeSessionResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}/duration")
    public ResponseEntity<GenericResponse> getDuration(@PathVariable String id) {
        CarRechargeSession carRechargeSession = carRechargeSessionService.findById(id);
        HashMap<String, Object> content = new HashMap<>();

        Date dateNow = new Date();

        content.put("time", dateNow);
        content.put("duration", Helpers.calculateDuration(carRechargeSession.getStartTime(), dateNow));

        return ResponseEntity.ok(new GenericResponse(content));
    }
}
