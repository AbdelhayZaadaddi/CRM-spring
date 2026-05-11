package com.GL.CRM.campaign;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampaignRequest{
    @NotBlank
    private String name;
    private String subject;
    private String body;

}
