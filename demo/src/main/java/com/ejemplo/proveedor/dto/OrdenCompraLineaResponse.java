package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrdenCompraLineaResponse(
    UUID idOrdenCompraLinea,
    UUID ordenCompraId,
    Integer numeroLinea,
    String descripcion,
    BigDecimal cantidadOrdenada,
    BigDecimal precioUnitario,
    BigDecimal cantidadRecibida,
    UUID cuentaContableId) {
}
