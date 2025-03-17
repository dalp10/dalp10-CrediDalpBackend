package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.ClientDTO;
import com.prestamo.dalp.model.Client;
import com.prestamo.dalp.model.Credit;
import com.prestamo.dalp.model.Loan;
import com.prestamo.dalp.response.CustomApiResponse;
import com.prestamo.dalp.response.ErrorDetail;
import com.prestamo.dalp.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clientes", description = "Operaciones relacionadas con clientes")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    @Operation(summary = "Obtener todos los clientes", description = "Devuelve una lista de todos los clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida con éxito")
    public ResponseEntity<CustomApiResponse<List<ClientDTO>>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients().stream()
                .map(client -> {
                    boolean hasCredits = clientService.hasCredits(client.getId());
                    return new ClientDTO(client, hasCredits);
                })
                .collect(Collectors.toList());
        CustomApiResponse<List<ClientDTO>> response = new CustomApiResponse<>(200, "Lista de clientes obtenida con éxito", clients, null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por ID", description = "Obtiene los detalles de un cliente por su ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<CustomApiResponse<Client>> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        if (client.isPresent()) {
            CustomApiResponse<Client> response = new CustomApiResponse<>(200, "Cliente encontrado", client.get(), null);
            return ResponseEntity.ok(response);
        } else {
            CustomApiResponse<Client> response = new CustomApiResponse<>(404, "Cliente no encontrado", null, List.of(new ErrorDetail("clientId", "Cliente no encontrado")));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente", description = "Crea un nuevo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente creado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<Client>> createClient(@RequestBody Client client) {
        try {
            String message = clientService.createClient(client);
            CustomApiResponse<Client> response = new CustomApiResponse<>(201, message, client, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            CustomApiResponse<Client> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("client", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente", description = "Actualiza los datos de un cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<Client>> updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        try {
            String message = clientService.updateClient(id, updatedClient);
            CustomApiResponse<Client> response = new CustomApiResponse<>(200, message, updatedClient, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<Client> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("client", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente", description = "Elimina un cliente por su ID")
    @ApiResponse(responseCode = "200", description = "Cliente eliminado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<String>> deleteClient(@PathVariable Long id) {
        try {
            String message = clientService.deleteClient(id);
            CustomApiResponse<String> response = new CustomApiResponse<>(200, message, null, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<String> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("clientId", "Cliente no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/exists")
    @Operation(summary = "Verificar si el número de documento existe", description = "Verifica si un número de documento ya está registrado")
    @ApiResponse(responseCode = "200", description = "Verificación completada")
    public ResponseEntity<CustomApiResponse<Boolean>> isDocumentNumberExists(@RequestParam String documentNumber) {
        boolean exists = clientService.isDocumentNumberExists(documentNumber);
        CustomApiResponse<Boolean> response = new CustomApiResponse<>(200, "Verificación completada", exists, null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clientId}/credits")
    @Operation(summary = "Obtener créditos por cliente", description = "Obtiene todos los créditos asociados a un cliente")
    @ApiResponse(responseCode = "200", description = "Lista de créditos obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<Credit>>> getCreditsByClientId(@PathVariable Long clientId) {
        try {
            List<Credit> credits = clientService.getCreditsByClientId(clientId);
            CustomApiResponse<List<Credit>> response = new CustomApiResponse<>(200, "Lista de créditos obtenida con éxito", credits, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<List<Credit>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("clientId", "Cliente no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{clientId}/loans")
    @Operation(summary = "Obtener préstamos por cliente", description = "Obtiene todos los préstamos asociados a un cliente")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<Loan>>> getLoansByClientId(@PathVariable Long clientId) {
        try {
            List<Loan> loans = clientService.getLoansByClientId(clientId);
            CustomApiResponse<List<Loan>> response = new CustomApiResponse<>(200, "Lista de préstamos obtenida con éxito", loans, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<List<Loan>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("clientId", "Cliente no encontrado")));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar clientes", description = "Busca clientes por número de documento o nombre")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida con éxito")
    public ResponseEntity<CustomApiResponse<List<ClientDTO>>> searchClients(@RequestParam String query) {
        List<ClientDTO> clients = clientService.searchClients(query);
        CustomApiResponse<List<ClientDTO>> response = new CustomApiResponse<>(200, "Lista de clientes obtenida con éxito", clients, null);
        return ResponseEntity.ok(response);
    }
}