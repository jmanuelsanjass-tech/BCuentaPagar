package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VentaDetalleRequest(
    @NotBlank @Size(max = 50) String codigoBarras,
    @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal cantidad,
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal precioUnitario) {
}
