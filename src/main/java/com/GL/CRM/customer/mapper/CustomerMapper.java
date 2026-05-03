package com.GL.CRM.customer.mapper;

import com.GL.CRM.customer.dto.CustomerRequest;
import com.GL.CRM.customer.dto.CustomerResponse;
import com.GL.CRM.customer.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .name(request.getName( ))
                .email (request.getEmail())
                 .phone(request. getPhone() )
                .company (request.getCompany())
                .notes (request.getNotes())
                .build() ;
    }

    public CustomerResponse toResponse(Customer customer ) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer. getEmail( ))
                .phone (customer.getPhone())
                 .company(customer.getCompany( ))
                .notes(customer.getNotes())
                .createdAt(customer.getCreatedAt( ))
                .updatedAt (customer.getUpdatedAt())
                .build();
    }

    public void updateEntity(Customer customer, CustomerRequest request ) {
        customer.setName(request.getName());
        customer.setEmail(request.getEmail()) ;
        customer.setPhone(request.getPhone( )) ;
        customer. setCompany(request.getCompany());
         customer.setNotes(request.getNotes( ));
    }
}
