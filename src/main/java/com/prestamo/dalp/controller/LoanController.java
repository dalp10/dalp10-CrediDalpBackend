package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.CreditDTO;
import com.prestamo.dalp.DTO.LoanDTO;
import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.service.LoanService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;


    // Obtener todos los préstamos
    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        List<LoanDTO> loanDTOs = loans.stream()
                .map(loanService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loanDTOs);
    }

    private LoanDTO convertToDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setAmount(loan.getAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setIssueDate(loan.getIssueDate());
        dto.setDueDate(loan.getDueDate());
        dto.setLoanCode(loan.getLoanCode());
        dto.setInterestAmount(loan.getInterestAmount());
        dto.setTotalAmount(loan.getTotalAmount());
        dto.setStatus(loan.getStatus());
        dto.setClientId(loan.getClient() != null ? loan.getClient().getId() : null);
        return dto;
    }

    // Crear un nuevo préstamo
    @PostMapping
    public ResponseEntity<String> createLoan(@RequestBody Loan loan) {
        String message = loanService.createLoan(loan);
        if (message.contains("Error")) {
            return ResponseEntity.badRequest().body(message);  // Si hay error, respuesta 400
        }
        return ResponseEntity.status(201).body(message);  // Si es exitoso, respuesta 201
    }

    // Actualizar un préstamo existente
    @PutMapping("/{id}")
    public ResponseEntity<String> updateLoan(@PathVariable Long id, @RequestBody Loan updatedLoan) {
        String message = loanService.updateLoan(id, updatedLoan);
        if (message.contains("Error")) {
            return ResponseEntity.badRequest().body(message);  // Si hay error, respuesta 400
        }
        return ResponseEntity.ok(message);  // Si es exitoso, respuesta 200
    }

    // Eliminar un préstamo
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLoan(@PathVariable Long id) {
        String message = loanService.deleteLoan(id);
        if (message.contains("Error")) {
            return ResponseEntity.badRequest().body(message);  // Si hay error, respuesta 400
        }
        return ResponseEntity.ok(message);  // Si es exitoso, respuesta 200
    }

    // Obtener un préstamo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        Optional<Loan> loan = loanService.getLoanById(id);
        return loan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener un préstamo por ID
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<LoanDTO>> getLoansByClient(@PathVariable Long clientId) {
        List<LoanDTO> loans = loanService.getCreditsByClient(clientId);
        return ResponseEntity.ok(loans);
    }
}
