package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record FacturaProveedorResponse(
    UUID id,
    UUID empresaId,
    String numeroFactura,
    Long proveedorId,
    LocalDate fechaFactura,
    LocalDate fechaVencimiento,
    String moneda,
    UUID ordenCompraId,
    UUID recepcionId,
    BigDecimal montoNeto,
    BigDecimal montoImpuestos,
    BigDecimal montoTotal,
    BigDecimal montoPagado,
    BigDecimal saldoPendiente,
    String estado,
    String estadoConciliacion,
    UUID aprobadoPagoPor,
    OffsetDateTime aprobadoPagoEn,
    OffsetDateTime creadoEn,
    UUID creadoPor) {
}
