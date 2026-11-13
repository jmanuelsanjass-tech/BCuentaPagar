package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PoliticaAprobacionRequest(
    @NotNull UUID empresaId,
    @NotBlank @Pattern(regexp = "FACTURA|PAGO") @Size(max = 30) String tipoTransaccion,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4)
        BigDecimal montoMinimo,
    @DecimalMin("0.0") @Digits(integer = 15, fraction = 4) BigDecimal montoMaximo,
    @NotBlank @Size(max = 50) String rolRequerido,
    @Positive Short ordenSecuencia,
    Boolean activo) {
}
