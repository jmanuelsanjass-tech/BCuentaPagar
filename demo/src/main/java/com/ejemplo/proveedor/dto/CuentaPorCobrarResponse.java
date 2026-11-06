package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaPorCobrarResponse(
    Long idCxc,
    Long clienteId,
    Long ventaId,
    LocalDate fechaVenta,
    LocalDate fechaVencimiento,
    BigDecimal importeOriginal,
    BigDecimal saldoPendiente,
    Integer diasVencidos,
    BigDecimal limiteCredito,
    String estatus,
    LocalDate fechaUltimoPago) {
}
