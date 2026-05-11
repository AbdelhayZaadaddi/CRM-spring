package com.GL.CRM.customer.controller;

import com.GL.CRM.customer.dto.CustomerRequest;
import com.GL.CRM.customer.dto.CustomerResponse;
import com.GL.CRM.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {


    private final CustomerService customerService  ;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAll( ) {
        return ResponseEntity.ok(customerService.getAll(  ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id ) {
        return ResponseEntity. ok(customerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        // create new customer, return 201 status
        return ResponseEntity.status( 201).body(customerService.create(request) );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok (customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();   // --->  return 204 no content
    }
}
