package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
    Long ventaId,
    Long clienteId,
    LocalDateTime fechaVenta,
    BigDecimal subtotal,
    BigDecimal descuento,
    BigDecimal iva,
    BigDecimal total,
    String estado,
    String usuarioAuditoria,
    LocalDateTime fechaAuditoria,
    byte[] qrCode,
    List<VentaDetalleResponse> detalles) {
}
