package com.prestamo.dalp.controller;

import com.prestamo.dalp.DTO.GroupPaymentDTO;
import com.prestamo.dalp.mapper.GroupPaymentMapper;
import com.prestamo.dalp.model.GroupPayment;
import com.prestamo.dalp.model.GroupPaymentContribution;
import com.prestamo.dalp.response.CustomApiResponse;
import com.prestamo.dalp.response.ErrorDetail;
import com.prestamo.dalp.service.GroupPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/group-payments")
@Tag(name = "Pagos Grupales", description = "Operaciones relacionadas con pagos grupales")
public class GroupPaymentController {

    @Autowired
    private GroupPaymentService groupPaymentService;

    @Autowired
    private GroupPaymentMapper groupPaymentMapper;

    @PostMapping
    @Operation(summary = "Crear un nuevo pago grupal", description = "Crea un nuevo pago grupal con sus contribuciones")
    @ApiResponse(responseCode = "200", description = "Pago grupal creado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<GroupPaymentDTO>> createGroupPayment(@RequestBody GroupPaymentDTO dto) {
        try {
            GroupPayment savedPayment = groupPaymentService.createGroupPayment(dto);
            GroupPaymentDTO responseDTO = groupPaymentMapper.toDTO(savedPayment);
            CustomApiResponse<GroupPaymentDTO> response = new CustomApiResponse<>(200, "Pago grupal creado con éxito", responseDTO, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<GroupPaymentDTO> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("groupPayment", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los pagos grupales", description = "Obtiene una lista de todos los pagos grupales")
    @ApiResponse(responseCode = "200", description = "Lista de pagos grupales obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<GroupPaymentDTO>>> getAllGroupPayments() {
        try {
            List<GroupPayment> payments = groupPaymentService.getAllGroupPayments();
            List<GroupPaymentDTO> dtos = payments.stream()
                    .map(groupPaymentMapper::toDTO)
                    .collect(Collectors.toList());
            CustomApiResponse<List<GroupPaymentDTO>> response = new CustomApiResponse<>(200, "Lista de pagos grupales obtenida con éxito", dtos, null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            CustomApiResponse<List<GroupPaymentDTO>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("groupPayments", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pago grupal por ID", description = "Obtiene un pago grupal específico por su ID")
    @ApiResponse(responseCode = "200", description = "Pago grupal obtenido con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<GroupPaymentDTO>> getGroupPaymentById(@PathVariable Long id) {
        try {
            GroupPayment payment = groupPaymentService.getGroupPaymentById(id);
            if (payment != null) {
                GroupPaymentDTO dto = groupPaymentMapper.toDTO(payment);
                CustomApiResponse<GroupPaymentDTO> response = new CustomApiResponse<>(200, "Pago grupal obtenido con éxito", dto, null);
                return ResponseEntity.ok(response);
            } else {
                CustomApiResponse<GroupPaymentDTO> response = new CustomApiResponse<>(404, "Pago grupal no encontrado", null, List.of(new ErrorDetail("id", "Pago grupal no encontrado")));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (RuntimeException e) {
            CustomApiResponse<GroupPaymentDTO> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("id", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}/contributions")
    @Operation(summary = "Obtener contribuciones de un pago grupal", description = "Obtiene las contribuciones de un pago grupal específico")
    @ApiResponse(responseCode = "200", description = "Lista de contribuciones obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<List<GroupPaymentContribution>>> getContributionsByGroupPayment(@PathVariable Long id) {
        try {
            GroupPayment payment = groupPaymentService.getGroupPaymentById(id);
            if (payment != null) {
                List<GroupPaymentContribution> contributions = payment.getContributions();
                CustomApiResponse<List<GroupPaymentContribution>> response = new CustomApiResponse<>(200, "Lista de contribuciones obtenida con éxito", contributions, null);
                return ResponseEntity.ok(response);
            } else {
                CustomApiResponse<List<GroupPaymentContribution>> response = new CustomApiResponse<>(404, "Pago grupal no encontrado", null, List.of(new ErrorDetail("id", "Pago grupal no encontrado")));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (RuntimeException e) {
            CustomApiResponse<List<GroupPaymentContribution>> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("id", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pago grupal", description = "Actualiza un pago grupal existente")
    @ApiResponse(responseCode = "200", description = "Pago grupal actualizado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<GroupPaymentDTO>> updateGroupPayment(@PathVariable Long id, @RequestBody GroupPaymentDTO updatedPaymentDTO) {
        try {
            GroupPayment payment = groupPaymentService.updateGroupPayment(id, updatedPaymentDTO);
            if (payment != null) {
                GroupPaymentDTO responseDTO = groupPaymentMapper.toDTO(payment);
                CustomApiResponse<GroupPaymentDTO> response = new CustomApiResponse<>(200, "Pago grupal actualizado con éxito", responseDTO, null);
                return ResponseEntity.ok(response);
            } else {
                CustomApiResponse<GroupPaymentDTO> response = new CustomApiResponse<>(404, "Pago grupal no encontrado", null, List.of(new ErrorDetail("id", "Pago grupal no encontrado")));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (RuntimeException e) {
            CustomApiResponse<GroupPaymentDTO> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("id", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pago grupal", description = "Elimina un pago grupal por su ID")
    @ApiResponse(responseCode = "200", description = "Pago grupal eliminado con éxito")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    public ResponseEntity<CustomApiResponse<String>> deleteGroupPayment(@PathVariable Long id) {
        try {
            boolean deleted = groupPaymentService.deleteGroupPayment(id);
            if (deleted) {
                CustomApiResponse<String> response = new CustomApiResponse<>(200, "Pago grupal eliminado con éxito", "Pago grupal eliminado", null);
                return ResponseEntity.ok(response);
            } else {
                CustomApiResponse<String> response = new CustomApiResponse<>(404, "Pago grupal no encontrado", null, List.of(new ErrorDetail("id", "Pago grupal no encontrado")));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (RuntimeException e) {
            CustomApiResponse<String> response = new CustomApiResponse<>(400, "Error en la solicitud: " + e.getMessage(), null, List.of(new ErrorDetail("id", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }
}