package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrdenCompraResponse(
    UUID idOrdenCompra,
    UUID empresaId,
    String numeroOc,
    Long proveedorId,
    LocalDate fechaOrden,
    String estado,
    String moneda,
    BigDecimal montoTotal,
    UUID solicitadoPor,
    UUID aprobadoPor,
    OffsetDateTime aprobadoEn,
    OffsetDateTime creadoEn) {
}
