package com.GL.CRM.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id ;
    private String name;
    private String email;
    private String phone ;
    private  String company;
    private  String notes ;
    private LocalDateTime createdAt;
    private LocalDateTime  updatedAt ;
}
