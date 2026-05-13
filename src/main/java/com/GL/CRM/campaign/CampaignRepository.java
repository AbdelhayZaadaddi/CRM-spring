package com.GL.CRM.campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStatus(CampaignStatus status);
    long countByStatus(CampaignStatus status);

    @Query("SELECT year(c.createdAt), month(c.createdAt), COUNT(c) FROM Campaign c WHERE c.createdAt >= :since GROUP BY year(c.createdAt), month(c.createdAt) ORDER BY year(c.createdAt) ASC, month(c.createdAt) ASC")
    List<Object[]> countByMonthSince(@Param("since") LocalDateTime since);
}
