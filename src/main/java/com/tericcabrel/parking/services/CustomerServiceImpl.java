package com.tericcabrel.parking.services;

import com.tericcabrel.parking.exceptions.ResourceAlreadyExistsException;
import com.tericcabrel.parking.exceptions.ResourceNotFoundException;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dtos.UpdateCustomerDto;
import com.tericcabrel.parking.models.dtos.CreateCustomerDto;
import com.tericcabrel.parking.repositories.CustomerRepository;
import com.tericcabrel.parking.services.interfaces.CustomerService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service(value = "customerService")
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer save(CreateCustomerDto createCustomerDto) {
        Customer customer = customerRepository.findByEmail(createCustomerDto.getEmail());

        if (customer != null) {
            throw new ResourceAlreadyExistsException("A customer with this email already exists!");
        }

        customer = Customer.builder()
                    .email(createCustomerDto.getEmail())
                    .name(createCustomerDto.getName())
                    .gender(createCustomerDto.getGenderEnum())
                    .phone(createCustomerDto.getPhone())
                    .carType(createCustomerDto.getCarType())
                    .build();

        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public void delete(String id) {
        customerRepository.deleteById(new ObjectId(id));
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Customer findById(String id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(new ObjectId(id));

        if (optionalCustomer.isPresent()) {
            return optionalCustomer.get();
        }

        throw new ResourceNotFoundException("Customer not found!");
    }

    @Override
    public Customer update(String id, UpdateCustomerDto updateCustomerDto) {
        Customer customer = findById(id);

        if(updateCustomerDto.getName() != null) {
            customer.setName(updateCustomerDto.getName());
        }

        if(updateCustomerDto.getEmail() != null) {
            customer.setEmail(updateCustomerDto.getEmail());
        }

        if(updateCustomerDto.getGender() != null) {
            customer.setGender(updateCustomerDto.getGenderEnum());
        }

        if (updateCustomerDto.getPhone() != null) {
            customer.setPhone(updateCustomerDto.getPhone());
        }

        if (updateCustomerDto.getCarType() != null) {
            customer.setCarType(updateCustomerDto.getCarType());
        }

        return customerRepository.save(customer);
    }
}
