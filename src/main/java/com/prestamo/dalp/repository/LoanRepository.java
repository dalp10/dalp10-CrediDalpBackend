package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByClient_Id(Long clientId); // Correct way to query by client id

    // Consultar el último código de préstamo generado (si necesitas esta funcionalidad)
    @Query("SELECT l.loanCode FROM Loan l ORDER BY l.id DESC")
    String getLastLoanCode();  // Retorna el último código generado (puedes usarlo para generar secuenciales)
}
