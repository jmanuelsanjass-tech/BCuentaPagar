package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RecepcionMercanciaLineaResponse(
    UUID id,
    UUID recepcionId,
    UUID ordenCompraLineaId,
    BigDecimal cantidadRecibida,
    String notas) {
}
