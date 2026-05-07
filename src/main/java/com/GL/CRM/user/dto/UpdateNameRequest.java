package com.GL.CRM.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNameRequest {

    @NotBlank(message = "Name is required")
    private String name;
}
