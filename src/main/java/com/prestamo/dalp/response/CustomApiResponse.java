package com.prestamo.dalp.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor // Genera un constructor con todos los campos
@Getter // Genera getters automáticamente
@Setter // Genera setters automáticamente
public class CustomApiResponse<T> {
    private int statusCode;
    private String message;
    private T data;
    private List<ErrorDetail> errors; // Campo opcional para manejar errores
}