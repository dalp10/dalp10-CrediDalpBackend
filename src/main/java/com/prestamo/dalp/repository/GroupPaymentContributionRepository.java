package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.GroupPaymentContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupPaymentContributionRepository extends JpaRepository<GroupPaymentContribution, Long> {
    // Métodos personalizados si es necesario
}
