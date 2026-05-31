package com.GL.CRM.customer.repository;

import com.GL.CRM.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= :since")
    long countSince(@Param("since") LocalDateTime since);

    @Query("SELECT year(c.createdAt), month(c.createdAt), COUNT(c) FROM Customer c WHERE c.createdAt >= :since GROUP BY year(c.createdAt), month(c.createdAt) ORDER BY year(c.createdAt) ASC, month(c.createdAt) ASC")
    List<Object[]> countByMonthSince(@Param("since") LocalDateTime since);
}
