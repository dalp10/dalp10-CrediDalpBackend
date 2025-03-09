package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.LoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanHistoryRepository extends JpaRepository<LoanHistory, Long> {
    // Puedes agregar métodos personalizados si lo necesitas
}
