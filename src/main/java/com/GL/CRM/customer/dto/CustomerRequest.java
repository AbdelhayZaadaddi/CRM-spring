package com.GL.CRM.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email " )
    private String email;

    private  String phone;
    private String company;
    private String notes ;
}
