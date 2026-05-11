package com.GL.CRM;



import com.GL.CRM.campaign.*;
import com.GL.CRM.customer.entity.Customer;
import com.GL.CRM.customer.repository.CustomerRepository;
import com.GL.CRM.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
     CampaignRepository  campaignRepository;
    @Mock
    CampaignMapper campaignMapper;
    @Mock CustomerRepository customerRepository ;
     @Mock
    EmailService emailService  ;

    @InjectMocks
    CampaignService campaignService;

     Campaign draft;
    Campaign sent;
    CampaignRequest request;

    @BeforeEach
    void setUp() {
        draft = Campaign.builder( )
                .id(1L).name ("Summer Sale")
                .subject("50% off ").body("Check our offers")
                .status(CampaignStatus.DRAFT).build();

        sent = Campaign.builder()
                .id(2L).name("Old Campaign ")
                .status(CampaignStatus.SENT).build();

         request = new CampaignRequest()   ;
        request.setName("Summer Sale");
        request.setSubject("50% off!");
        request  .setBody("Check our offers" );
    }

    @Test
    void getAll_returnsList  () {
        when(campaignRepository.findAll()).thenReturn(List.of(draft));
         when(campaignMapper.toResponse(draft)).thenReturn(new CampaignResponse())    ;

        assertThat(campaignService.getAll()).hasSize(1);
    }

    @Test
    void getById_found () {
        when   (campaignRepository.  findById(1L)).thenReturn(Optional.of(draft))  ;
          when(campaignMapper.toResponse(draft)).thenReturn(new CampaignResponse());

        assertThat(campaignService.getById(1L)).isNotNull();
    }

     @Test
    void getById_notFound_throws() {
        when (campaignRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> campaignService.getById(99L));
    }

    @Test
    void create_savesDraft() {
        when
                (campaignMapper.toEntity(request)).thenReturn(draft);
        when(campaignRepository.save(any())).thenReturn(draft);
         when(campaignMapper.toResponse(draft)).thenReturn(new CampaignResponse());

        assertThat(campaignService.create(request)).isNotNull();
        verify  (campaignRepository).save(any());
    }

    @Test
    void update_draft_succeeds(    ) {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(draft));
        when (campaignRepository.save(draft)).thenReturn(draft);
        when(campaignMapper.toResponse(draft)).thenReturn(new CampaignResponse());

        campaignService .update(1L, request);

        verify(campaignMapper).updateEntity(request,draft);
    }

    @Test
    void update_sent_throws() {
        when(campaignRepository.findById(2L)).thenReturn(Optional.of(sent));

        assertThrows(IllegalStateException.class, () -> campaignService.update(2L, request));
    }

    @Test
    void send_draft_becomesSent () {
        Customer customer = Customer.builder().id(1L).email("test@gmail.com").build();

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(draft));
         when (customerRepository.findAll()).thenReturn(List.of(customer));
        when(campaignRepository .save(any())).thenReturn(draft );
        when (campaignMapper.toResponse(any())).thenReturn(new CampaignResponse());

        campaignService. send(1L);

        verify(emailService    ).sendEmail(eq("test@gmail.com"), any(), any());
    }

    @Test
    void send_alreadySent_throws () {
        when(campaignRepository.findById(2L)).thenReturn(Optional.of(sent));

        assertThrows(IllegalStateException.class, () -> campaignService.send(2L));
        verify(emailService, never())
                .sendEmail(any(), any(), any()     )
        ;
    }

    @Test
    void delete_draft_succeeds() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(draft));

        campaignService.deleteById(1L);

        verify(campaignRepository).deleteById(1L);
    }

    @Test
    void delete_sent_throws() {
        when(campaignRepository.findById(2L)).thenReturn(Optional.of(sent));

        assertThrows(IllegalStateException.class, () -> campaignService.deleteById(2L));
        verify(campaignRepository, never()).deleteById(any())  ;
    }
}