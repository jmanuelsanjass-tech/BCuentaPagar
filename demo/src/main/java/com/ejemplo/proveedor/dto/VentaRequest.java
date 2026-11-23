package com.ejemplo.proveedor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record VentaRequest(
        @NotNull Long clienteId,
        @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal descuento,
        @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal iva, // <-- AGREGADO
        @Size(max = 20) String estado,
        @NotEmpty List<@Valid VentaDetalleRequest> detalles,
        byte[] qrCode,
        @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal montoRecibido,
        @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal montoSobrante, // <-- AGREGADO
        @Size(max = 20) String tipoMoneda,
        @Size(max = 30) String bancoCorto) {
}