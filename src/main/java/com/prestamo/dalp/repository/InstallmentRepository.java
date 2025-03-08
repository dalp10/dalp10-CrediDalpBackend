package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    // Buscar cuotas por creditId
    List<Installment> findByCreditId(Long creditId);
}