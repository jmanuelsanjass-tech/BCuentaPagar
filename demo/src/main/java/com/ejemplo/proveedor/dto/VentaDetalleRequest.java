package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record VentaDetalleRequest(
    @NotNull Long idProducto,
    @NotNull @Min(1) Integer cantidad,
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal precioUnitario,
    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal descuento) {
}
