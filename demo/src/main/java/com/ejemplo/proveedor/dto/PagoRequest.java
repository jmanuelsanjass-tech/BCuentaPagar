package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PagoRequest(
    @NotNull UUID empresaId,
    @NotBlank @Size(max = 50) String numeroPago,
    @NotNull Long proveedorId,
    UUID facturaId,
    @NotNull LocalDate fechaPago,
    @NotNull @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 15, fraction = 4) BigDecimal monto,
    @NotBlank @Pattern(regexp = "[A-Z]{3}") String moneda,
    @NotBlank @Pattern(regexp = "TRANSFERENCIA|CHEQUE|EFECTIVO|TARJETA|COMPENSACION")
        @Size(max = 30) String metodoPago,
    @Size(max = 100) String referencia,
    @Pattern(regexp = "PENDIENTE|APROBADO|ENVIADO|CONFIRMADO|FALLIDO|CANCELADO")
        @Size(max = 20) String estado,
    @NotNull UUID aprobadoPor,
    UUID ejecutadoPor) {
}
