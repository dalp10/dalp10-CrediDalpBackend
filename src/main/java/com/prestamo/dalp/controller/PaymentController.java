package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.PaymentDTO;
import com.prestamo.dalp.model.Installment;
import com.prestamo.dalp.service.InstallmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private InstallmentService installmentService;

    @PostMapping("/pay")
    public ResponseEntity<Installment> payInstallment(@RequestBody PaymentDTO paymentDTO) {
        Installment updatedInstallment = installmentService.processPayment(paymentDTO);
        return ResponseEntity.ok(updatedInstallment);
    }
}