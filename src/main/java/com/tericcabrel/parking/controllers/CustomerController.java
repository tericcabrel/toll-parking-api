package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dtos.CreateCustomerDto;
import com.tericcabrel.parking.models.dtos.UpdateCustomerDto;
import com.tericcabrel.parking.models.responses.*;
import com.tericcabrel.parking.services.interfaces.CarTypeService;
import com.tericcabrel.parking.services.interfaces.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tericcabrel.parking.utils.Constants.*;


@Api(tags = "Customer management", description = "Operations pertaining to customer creation, update, fetch and delete")
@RestController
@RequestMapping(value = "/customers")
public class CustomerController {
    private CustomerService customerService;

    private CarTypeService carTypeService;

    public CustomerController(CustomerService customerService, CarTypeService carTypeService) {
        this.carTypeService = carTypeService;
        this.customerService = customerService;
    }

    @ApiOperation(value = "Create a customer", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Customer created successfully!", response = CustomerResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerDto createCustomerDto) {
        CarType carType = carTypeService.findById(createCustomerDto.getCarTypeId());

        createCustomerDto.setCarType(carType);

        Customer customer = customerService.save(createCustomerDto);

        return ResponseEntity.ok(new CustomerResponse(customer));
    }

    @ApiOperation(value = "Get all customers", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List retrieved successfully!", response = CustomerListResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<CustomerListResponse> all(){
        return ResponseEntity.ok(new CustomerListResponse(customerService.findAll()));
    }

    @ApiOperation(value = "Get one customer", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Item retrieved successfully!", response = CustomerResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> one(@PathVariable String id){
        return ResponseEntity.ok(new CustomerResponse(customerService.findById(id)));
    }

    @ApiOperation(value = "Update a customer", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Customer updated successfully!", response = CustomerResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
        @PathVariable String id, @Valid @RequestBody UpdateCustomerDto updateCustomerDto
    ) {
        if (updateCustomerDto.getCarTypeId() != null) {
            updateCustomerDto.setCarType(carTypeService.findById(updateCustomerDto.getCarTypeId()));
        }

        return ResponseEntity.ok(new CustomerResponse(customerService.update(id, updateCustomerDto)));
    }

    @ApiOperation(value = "Delete a customer", response = GenericResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Customer deleted successfully!", response = GenericResponse.class),
        @ApiResponse(code = 401, message = UNAUTHORIZED_MESSAGE, response = GenericResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN_MESSAGE, response = GenericResponse.class),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        customerService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
