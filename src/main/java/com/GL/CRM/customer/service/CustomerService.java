package com.GL.CRM.customer.service;

import com.GL.CRM.customer.dto.CustomerRequest;
import com.GL.CRM.customer.dto.CustomerResponse;
import com.GL.CRM.customer.entity.Customer;
import com.GL.CRM.customer.mapper.CustomerMapper;
import com.GL.CRM.customer.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository ;
    private final CustomerMapper customerMapper ;           // mapper to convert entities <--> DTOs

    public List<CustomerResponse> getAll( ) {
        // get all customers from DB and map them to response objects
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList()) ;
    }

    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(" Customer not found")) ;
        return customerMapper.toResponse(customer);
    }

    public CustomerResponse create(CustomerRequest request) {
        // check if email already exists ; simple validation
        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already used") ;
        }
        Customer customer = customerMapper.toEntity(request);
        return customerMapper.toResponse(customerRepository.save(customer))  ;
    }

    public CustomerResponse update(Long id, CustomerRequest request) {
        // fetch existing customer, throw if not foundd
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found")) ;
        // update fields using mapper
        customerMapper.updateEntity(customer, request);
        return customerMapper.toResponse(customerRepository.save(customer ));
    }

    public void delete(Long id) {
        if ( !customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found");
        }

         customerRepository.deleteById(id );
    }
}
