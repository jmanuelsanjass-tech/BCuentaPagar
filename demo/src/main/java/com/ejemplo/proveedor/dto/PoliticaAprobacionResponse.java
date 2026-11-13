package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PoliticaAprobacionResponse(
    UUID id,
    UUID empresaId,
    String tipoTransaccion,
    BigDecimal montoMinimo,
    BigDecimal montoMaximo,
    String rolRequerido,
    Short ordenSecuencia,
    boolean activo) {
}
