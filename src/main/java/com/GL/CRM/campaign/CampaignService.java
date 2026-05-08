package com.GL.CRM.campaign;

import com.GL.CRM.customer.repository.CustomerRepository;
import com.GL.CRM.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //lombok ano generate const injection automaticly(no autowired needed)
public class CampaignService{
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    public List<CampaignResponse> getAll(){
        return  campaignRepository.findAll ()
                .stream ()
                .map (campaignMapper::toResponse)
                .collect( Collectors.toList ());
    }
    public CampaignResponse getById(Long id){
        Campaign campaign = campaignRepository.findById ( id )
                .orElseThrow (()-> new ResourceNotFoundException ("Campaign not found :( ") );

        return campaignMapper.toResponse(campaign);
    }

    public CampaignResponse create(CampaignRequest campaignRequest){
        Campaign campaign = campaignMapper.toEntity ( campaignRequest );
        campaign.setStatus ( CampaignStatus.DRAFT );
        return  campaignMapper.toResponse(campaignRepository.save(campaign));

    }
    public CampaignResponse update(Long id, CampaignRequest campaignRequest) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("campaign not found"));

        if (campaign.getStatus() == CampaignStatus.SENT) {
            throw new IllegalStateException("can not edit a sent campaign");
        }

        campaignMapper.updateEntity(campaignRequest,campaign); //  missing
        return campaignMapper.toResponse(campaignRepository.save(campaign));
    }
    public CampaignResponse send(Long id){
        Campaign campaign = campaignRepository.findById ( id )
                .orElseThrow (()-> new ResourceNotFoundException ("campaign not found"));

        if (campaign.getStatus ( ) == CampaignStatus.SENT ){
            throw new IllegalStateException ( "campaign already sent " );
        }
        customerRepository.findAll().forEach(customer -> {
            if (customer.getEmail() != null) {
                try {
                    emailService.sendEmail(
                            customer.getEmail(),
                            campaign.getSubject(),
                            campaign.getBody()
                    );
                    Thread.sleep(1100); // wait 1.1 seconds between emails
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        campaign.setStatus(CampaignStatus.SENT);
        campaign.setSentAt( LocalDateTime.now());
        return campaignMapper.toResponse(campaignRepository.save(campaign));
    }


    @Transactional
    public void deleteById(Long id){
        if (!campaignRepository.existsById ( id )) {
            throw new EntityNotFoundException ("campaign not found " );
        }
        campaignRepository.deleteById ( id );
    }

}
