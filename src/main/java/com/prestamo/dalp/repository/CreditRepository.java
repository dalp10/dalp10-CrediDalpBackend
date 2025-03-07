package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByClientId(Long clientId);

    @Query("SELECT MAX(c.id) FROM Credit c")
    Optional<Long> findMaxId();
}