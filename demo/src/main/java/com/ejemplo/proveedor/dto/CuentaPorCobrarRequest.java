package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CuentaPorCobrarRequest(
    @NotNull Long clienteId,
    @NotNull Long ventaId,
    @NotNull LocalDate fechaVenta,
    @NotNull LocalDate fechaVencimiento,
    @DecimalMin("0.00") @Digits(integer = 16, fraction = 2) BigDecimal importeOriginal,
    @DecimalMin("0.00") @Digits(integer = 16, fraction = 2) BigDecimal saldoPendiente,
    @Min(0) Integer diasVencidos,
    @DecimalMin("0.00") @Digits(integer = 16, fraction = 2) BigDecimal limiteCredito,
    @Pattern(regexp = "Pendiente|Pagado|Cancelado") @Size(max = 20) String estatus,
    LocalDate fechaUltimoPago) {
}
