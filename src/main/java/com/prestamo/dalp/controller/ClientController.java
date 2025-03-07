package com.prestamo.dalp.controller;

import com.prestamo.dalp.model.Client;
import com.prestamo.dalp.model.Credit;
import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // Obtener todos los clientes
    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo cliente
    @PostMapping
    public ResponseEntity<String> createClient(@RequestBody Client client) {
        String message = clientService.createClient(client);
        if (message.contains("Error")) {
            return ResponseEntity.badRequest().body(message);  // Error: devuelve código 400
        }
        return ResponseEntity.status(201).body(message);  // Éxito: devuelve código 201
    }

    // Actualizar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<String> updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        String message = clientService.updateClient(id, updatedClient);
        if (message.contains("Error")) {
            return ResponseEntity.badRequest().body(message);  // Error: devuelve código 400
        }
        return ResponseEntity.ok(message);  // Éxito: devuelve código 200
    }

    // Eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        String message = clientService.deleteClient(id);
        if (message.contains("Error")) {
            return ResponseEntity.badRequest().body(message);  // Error: devuelve código 400
        }
        return ResponseEntity.ok(message);  // Éxito: devuelve código 200
    }

    // Verificar si el número de documento ya está registrado
    @GetMapping("/exists")
    public ResponseEntity<Boolean> isDocumentNumberExists(@RequestParam String documentNumber) {
        boolean exists = clientService.isDocumentNumberExists(documentNumber);
        return ResponseEntity.ok(exists);  // Retorna true si el número de documento existe, false si no
    }

    // Obtener los créditos de un cliente por su ID
    @GetMapping("/{clientId}/credits")
    public ResponseEntity<List<Credit>> getCreditsByClientId(@PathVariable Long clientId) {
        List<Credit> credits = clientService.getCreditsByClientId(clientId);
        return ResponseEntity.ok(credits);  // Retorna la lista de créditos del cliente
    }

    // Obtener los créditos de un cliente por su ID
    @GetMapping("/{clientId}/loans")
    public ResponseEntity<List<Loan>> getLoansByClientId(@PathVariable Long clientId) {
        List<Loan> loans = clientService.getLoansByClientId(clientId);
        return ResponseEntity.ok(loans);  // Retorna la lista de créditos del cliente
    }
}
