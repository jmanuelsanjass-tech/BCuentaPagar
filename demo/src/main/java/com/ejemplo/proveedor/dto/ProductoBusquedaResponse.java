package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;

public record ProductoBusquedaResponse(
    Long idProducto,
    String codigoBarras,
    String nombre,
    String descripcion,
    String categoria,
    BigDecimal precio,
    Integer stock) {
}
