package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PagoResponse(
    UUID id,
    UUID empresaId,
    String numeroPago,
    Long proveedorId,
    UUID facturaId,
    LocalDate fechaPago,
    BigDecimal monto,
    String moneda,
    String metodoPago,
    String referencia,
    String estado,
    UUID aprobadoPor,
    UUID ejecutadoPor,
    OffsetDateTime creadoEn) {
}
