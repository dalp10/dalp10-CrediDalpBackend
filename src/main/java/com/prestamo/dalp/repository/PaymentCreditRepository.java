package com.prestamo.dalp.repository;

import com.prestamo.dalp.model.PaymentCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentCreditRepository extends JpaRepository<PaymentCredit, Long> {
    List<PaymentCredit> findByCreditId(Long creditId);
}
