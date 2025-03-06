package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.CreditDTO;
import com.prestamo.dalp.model.Credit;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @PostMapping
    public ResponseEntity<Credit> createCredit(
            @RequestBody Credit credit,
            @RequestParam int numberOfInstallments,
            @RequestParam int gracePeriodDays,
            @RequestParam BigDecimal tea,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate firstPaymentDate // Recibir la fecha fija como parámetro
    ) {
        Credit createdCredit = creditService.createCredit(credit, numberOfInstallments, gracePeriodDays, tea, firstPaymentDate);
        return ResponseEntity.ok(createdCredit);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CreditDTO>> getCreditsByClient(@PathVariable Long clientId) {
        List<CreditDTO> credits = creditService.getCreditsByClient(clientId);
        return ResponseEntity.ok(credits);
    }

    @PostMapping("/installments/{installmentId}/pay")
    public ResponseEntity<Installment> payInstallment(@PathVariable Long installmentId) {
        Installment paidInstallment = creditService.payInstallment(installmentId);
        return ResponseEntity.ok(paidInstallment);
    }
}
