package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;

public record VentaDetalleResponse(
    Long detalleId,
    String codigoBarras,
    BigDecimal cantidad,
    BigDecimal precioUnitario,
    BigDecimal importe) {
}
