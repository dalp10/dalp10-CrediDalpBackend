package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.GroupPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupPaymentRepository extends JpaRepository<GroupPayment, Long> {
    // Métodos personalizados si es necesario
}
