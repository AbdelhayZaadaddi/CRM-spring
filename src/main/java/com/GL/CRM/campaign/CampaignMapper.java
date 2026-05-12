package com.GL.CRM.campaign;

import org.hibernate.annotations.CollectionIdMutability;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper{

    public Campaign toEntity ( CampaignRequest request ) {
        return Campaign.builder ()
                .name ( request.getName () )
                .subject ( request.getSubject () )
                .body ( request.getBody () )
                .build ();
    }
    public CampaignResponse toResponse ( Campaign campaign ) {
        return CampaignResponse.builder ()
                .id( campaign.getId () )
                .name ( campaign.getName () )
                .subject ( campaign.getSubject () )
                .body ( campaign.getBody () )
                .status (campaign.getStatus () )
                .sentAt ( campaign.getSentAt ())
                .createdAt ( campaign.getCreatedAt())
                .updatedAt ( campaign.getUpdatedAt ())
                .build ();
    }
    public void updateEntity ( CampaignRequest request,Campaign campaign ) {
        campaign.setName ( request.getName () );
        campaign.setSubject ( request.getSubject () );
        campaign.setBody (request.getBody () );
    }


}