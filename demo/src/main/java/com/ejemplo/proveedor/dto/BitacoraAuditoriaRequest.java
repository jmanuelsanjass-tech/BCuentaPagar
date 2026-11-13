package com.ejemplo.proveedor.dto;

import java.util.UUID;
import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BitacoraAuditoriaRequest(
    @NotBlank @Size(max = 100) String tabla,
    @NotBlank @Pattern(regexp = "INSERT|UPDATE|DELETE") @Size(max = 10) String operacion,
    @NotNull UUID registroId,
    Map<String, Object> datosAnteriores,
    Map<String, Object> datosNuevos) {
}
