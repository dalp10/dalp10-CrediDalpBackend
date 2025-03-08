package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.PaymentDTO;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.model.Payment;
import com.prestamo.dalp.service.InstallmentService;
import com.prestamo.dalp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private InstallmentService installmentService;

    @Autowired
    private PaymentService paymentService;

    // Realizar un pago sobre un préstamo Credit
    @PostMapping("/pay")
    public ResponseEntity<Installment> payInstallment(@RequestBody PaymentDTO paymentDTO) {
        Installment updatedInstallment = installmentService.processPayment(paymentDTO);
        return ResponseEntity.ok(updatedInstallment);
    }

    // Realizar un pago sobre un préstamo Loan
    @PostMapping("/{loanId}")
    public ResponseEntity<String> makePayment(@PathVariable Long loanId, @RequestBody Payment payment) {
        String message = paymentService.makePayment(loanId, payment);
        if (message.contains("Error")) {
            return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.ok(message);
    }

    // Obtener los pagos de un préstamo por su ID
    @GetMapping
    public ResponseEntity<List<Payment>> getPaymentsByLoanId(@RequestParam Long loanId) {
        List<Payment> payments = paymentService.getPaymentsByLoanId(loanId);
        return ResponseEntity.ok(payments);
    }
}