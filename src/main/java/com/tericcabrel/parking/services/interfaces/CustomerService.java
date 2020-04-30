package com.tericcabrel.parking.services.interfaces;

import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dtos.UpdateCustomerDto;
import com.tericcabrel.parking.models.dtos.CustomerDto;

import java.util.List;

public interface CustomerService {
    Customer save(CustomerDto customerDto);

    List<Customer> findAll();

    void delete(String id);

    Customer findByEmail(String email);

    Customer findById(String id);

    Customer update(String id, UpdateCustomerDto updateCustomerDto);
}
