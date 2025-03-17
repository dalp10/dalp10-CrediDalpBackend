package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.LoanDTO;
import com.prestamo.dalp.response.CustomApiResponse;
import com.prestamo.dalp.response.ErrorDetail;
import com.prestamo.dalp.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Préstamos", description = "Operaciones relacionadas con préstamos")
public class LoanController {

    @Autowired
    private LoanService loanService;

    /**
     * Obtener todos los préstamos.
     *
     * @return Lista de préstamos en formato DTO.
     */
    @GetMapping
    @Operation(summary = "Obtener todos los préstamos", description = "Devuelve una lista de todos los préstamos")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida con éxito")
    public ResponseEntity<CustomApiResponse<List<LoanDTO>>> getAllLoans() {
        List<LoanDTO> loans = loanService.getAllLoans();
        CustomApiResponse<List<LoanDTO>> response = new CustomApiResponse<>(200, "Lista de préstamos obtenida con éxito", loans, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Crear un nuevo préstamo.
     *
     * @param loanDTO Datos del préstamo en formato DTO.
     * @return Respuesta con el préstamo creado en formato DTO.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear un préstamo", description = "Crea un nuevo préstamo para un cliente")
    @ApiResponse(responseCode = "201", description = "Préstamo creado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<LoanDTO>> createLoan(@RequestBody LoanDTO loanDTO) {
        try {
            LoanDTO createdLoan = loanService.createLoan(loanDTO);
            CustomApiResponse<LoanDTO> response = new CustomApiResponse<>(201, "Préstamo creado con éxito", createdLoan, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            List<ErrorDetail> errors = List.of(new ErrorDetail("clientId", "El cliente no existe o es inválido"));
            CustomApiResponse<LoanDTO> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, errors);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Actualizar un préstamo existente.
     *
     * @param id ID del préstamo a actualizar.
     * @param loanDTO Datos actualizados del préstamo en formato DTO.
     * @return Respuesta con el resultado de la operación.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un préstamo", description = "Actualiza los datos de un préstamo existente")
    @ApiResponse(responseCode = "200", description = "Préstamo actualizado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<LoanDTO>> updateLoan(@PathVariable Long id, @RequestBody LoanDTO loanDTO) {
        try {
            LoanDTO updatedLoan = loanService.updateLoan(id, loanDTO);
            CustomApiResponse<LoanDTO> response = new CustomApiResponse<>(200, "Préstamo actualizado con éxito", updatedLoan, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            List<ErrorDetail> errors = List.of(new ErrorDetail("loanId", "Préstamo no encontrado o inválido"));
            CustomApiResponse<LoanDTO> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, errors);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Eliminar un préstamo.
     *
     * @param id ID del préstamo a eliminar.
     * @return Respuesta con el resultado de la operación.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un préstamo", description = "Elimina un préstamo por su ID")
    @ApiResponse(responseCode = "200", description = "Préstamo eliminado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<String>> deleteLoan(@PathVariable Long id) {
        try {
            String message = loanService.deleteLoan(id);
            CustomApiResponse<String> response = new CustomApiResponse<>(200, message, null, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            List<ErrorDetail> errors = List.of(new ErrorDetail("loanId", "Préstamo no encontrado o inválido"));
            CustomApiResponse<String> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, errors);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtener un préstamo por su ID.
     *
     * @param id ID del préstamo.
     * @return Préstamo en formato DTO.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un préstamo por ID", description = "Obtiene los detalles de un préstamo por su ID")
    @ApiResponse(responseCode = "200", description = "Préstamo encontrado")
    @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    public ResponseEntity<CustomApiResponse<LoanDTO>> getLoanById(@PathVariable Long id) {
        try {
            LoanDTO loanDTO = loanService.getLoanById(id);
            CustomApiResponse<LoanDTO> response = new CustomApiResponse<>(200, "Préstamo encontrado", loanDTO, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            List<ErrorDetail> errors = List.of(new ErrorDetail("loanId", "Préstamo no encontrado"));
            CustomApiResponse<LoanDTO> response = new CustomApiResponse<>(404, e.getMessage(), null, errors);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Obtener todos los préstamos de un cliente.
     *
     * @param clientId ID del cliente.
     * @return Lista de préstamos en formato DTO.
     */
    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtener préstamos por cliente", description = "Obtiene todos los préstamos asociados a un cliente")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<LoanDTO>>> getLoansByClient(@PathVariable Long clientId) {
        try {
            List<LoanDTO> loans = loanService.getLoansByClient(clientId);
            CustomApiResponse<List<LoanDTO>> response = new CustomApiResponse<>(200, "Lista de préstamos obtenida con éxito", loans, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            List<ErrorDetail> errors = List.of(new ErrorDetail("clientId", "Cliente no encontrado o inválido"));
            CustomApiResponse<List<LoanDTO>> response = new CustomApiResponse<>(400, e.getMessage(), null, errors);
            return ResponseEntity.badRequest().body(response);
        }
    }
}