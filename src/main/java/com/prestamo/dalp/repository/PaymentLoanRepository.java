package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.PaymentLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentLoanRepository extends JpaRepository<PaymentLoan, Long> {
    List<PaymentLoan> findByLoanId(Long loanId);
}
