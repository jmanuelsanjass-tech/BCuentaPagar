package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FacturaProveedorLineaResponse(
    UUID id,
    UUID facturaId,
    Integer numeroLinea,
    UUID ordenCompraLineaId,
    String descripcion,
    BigDecimal cantidad,
    BigDecimal precioUnitario,
    BigDecimal montoNeto,
    BigDecimal montoImpuestos,
    UUID cuentaContableId) {
}
