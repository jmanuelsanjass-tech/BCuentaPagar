package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FacturaProveedorLineaRequest(
    @NotNull @Positive Integer numeroLinea,
    UUID ordenCompraLineaId,
    @NotBlank String descripcion,
    @NotNull @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 15, fraction = 4) BigDecimal cantidad,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4)
        BigDecimal precioUnitario,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4) BigDecimal montoNeto,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4)
        BigDecimal montoImpuestos,
    @NotNull UUID cuentaContableId) {
}
