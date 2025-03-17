package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.model.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    /**
     * Busca todas las cuotas de un crédito por su ID.
     *
     * @param creditId ID del crédito.
     * @return Lista de cuotas asociadas al crédito.
     */
    List<Installment> findByCreditId(Long creditId);

    /**
     * Busca todas las cuotas de un crédito por su ID y estado.
     *
     * @param creditId ID del crédito.
     * @param status Estado de las cuotas.
     * @return Lista de cuotas que coinciden con el crédito y el estado.
     */
    List<Installment> findByCreditIdAndStatus(Long creditId, InstallmentStatus status);
}