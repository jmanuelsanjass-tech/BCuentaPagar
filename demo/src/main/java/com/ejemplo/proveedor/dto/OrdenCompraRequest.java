package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OrdenCompraRequest(
    @NotNull UUID empresaId,
    @NotBlank @Size(max = 50) String numeroOc,
    @NotNull Long proveedorId,
    @NotNull LocalDate fechaOrden,
    @Pattern(regexp = "BORRADOR|APROBADA|CERRADA|CANCELADA") @Size(max = 20) String estado,
    @NotBlank @Pattern(regexp = "[A-Z]{3}") String moneda,
    @DecimalMin("0.0") @Digits(integer = 15, fraction = 4) BigDecimal montoTotal,
    @NotNull UUID solicitadoPor,
    UUID aprobadoPor,
    OffsetDateTime aprobadoEn) {
}
