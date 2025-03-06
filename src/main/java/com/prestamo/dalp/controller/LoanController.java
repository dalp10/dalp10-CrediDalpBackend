package com.prestamo.dalp.controller;

import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.service.LoanService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // Obtener todos los préstamos
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        List<Loan> loans = loanService.getAllLoans();

        // Forzar la carga completa de la relación 'client'
        for (Loan loan : loans) {
            Hibernate.initialize(loan.getClient());
        }

        // Agregar el id_cliente a cada préstamo
        for (Loan loan : loans) {
            // Aquí podrías crear un DTO si lo prefieres, pero simplemente incluir el id_cliente es suficiente
            loan.setClientId(loan.getClient() != null ? loan.getClient().getId() : null);
        }

        return ResponseEntity.ok(loans);
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
}
