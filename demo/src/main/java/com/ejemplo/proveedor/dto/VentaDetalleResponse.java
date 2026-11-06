package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;

public record VentaDetalleResponse(
    Long detalleId,
    Long idProducto,
    String codigoBarras,
    Integer cantidad,
    BigDecimal precioUnitario,
    BigDecimal subtotal,
    BigDecimal descuento,
    BigDecimal iva,
    BigDecimal total) {
}
