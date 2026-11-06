package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

public record VentaRequest(
    @NotNull Long clienteId,
    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal descuento,
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal iva,
    @Size(max = 20) String estado,
    @NotEmpty List<@Valid VentaDetalleRequest> detalles,
    byte[] qrCode) {
}
