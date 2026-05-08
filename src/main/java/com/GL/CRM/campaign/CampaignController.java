package com.GL.CRM.campaign;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaign")
@RequiredArgsConstructor
public class CampaignController {
    private final  CampaignService campaignService  ;

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAll () {
        return ResponseEntity.ok(campaignService.getAll () );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getById (@PathVariable Long id) {
        return ResponseEntity.ok ( campaignService.getById ( id ) );
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> create (@Valid @RequestBody CampaignRequest request) {
        return ResponseEntity .status (201).body( campaignService.create ( request ) );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> update (@PathVariable Long id,
                                                    @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok (campaignService.update ( id, request ) );
    }
    @PutMapping("/{id}/send")
    public ResponseEntity<CampaignResponse> send (@PathVariable Long id){
        return ResponseEntity.ok ( campaignService.send ( id ) );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CampaignResponse> delete (@PathVariable Long id) {
        campaignService.deleteById ( id );
        return ResponseEntity .noContent ().build();
    }



}
