package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrdenCompraLineaRequest(
    @NotNull @Positive Integer numeroLinea,
    @NotBlank String descripcion,
    @NotNull @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 15, fraction = 4) BigDecimal cantidadOrdenada,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4)
        BigDecimal precioUnitario,
    @DecimalMin("0.0") @Digits(integer = 15, fraction = 4) BigDecimal cantidadRecibida,
    @NotNull UUID cuentaContableId) {
}
