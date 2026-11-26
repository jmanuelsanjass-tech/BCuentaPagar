package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;

public record ProductoRequest(

    String codigoBarras,
    String nombre,
    String descripcion,
    String categoria,
    BigDecimal precio,
    Integer stock,
    String estado,

    Long idProveedor

) {
}