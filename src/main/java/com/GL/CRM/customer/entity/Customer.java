package com.GL.CRM.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers" )
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id ;

    @Column(nullable = false )
    @NotBlank(message = " Name is required")
    private String name;

    @Column(unique = true)
    @Email( message = "Invalid email")
     private String email ;

    private String phone ;
    private String company;

    @Column(columnDefinition = "TEXT" )
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt ;

    @Column( name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate( ) {
        createdAt = LocalDateTime. now( );
        updatedAt =  LocalDateTime.now() ;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
