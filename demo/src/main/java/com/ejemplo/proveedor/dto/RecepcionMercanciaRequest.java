package com.ejemplo.proveedor.dto;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecepcionMercanciaRequest(
    @NotNull UUID empresaId,
    @NotBlank @Size(max = 50) String numeroRecepcion,
    @NotNull UUID ordenCompraId,
    @NotNull LocalDate fechaRecepcion,
    @NotNull UUID recibidoPor) {
}
