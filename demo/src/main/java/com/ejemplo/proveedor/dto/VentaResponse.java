package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
    Long ventaId,
    Long clienteId,
    LocalDateTime fechaVenta,
    BigDecimal total,
    String estado,
    String formaPago,
    String usuarioRegistro,
    List<VentaDetalleResponse> detalles) {
}
