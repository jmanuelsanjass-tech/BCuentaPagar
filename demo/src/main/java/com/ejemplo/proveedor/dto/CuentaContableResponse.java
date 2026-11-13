package com.ejemplo.proveedor.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CuentaContableResponse(
    UUID id,
    UUID empresaId,
    String codigoCuenta,
    String nombreCuenta,
    String tipoCuenta,
    boolean activo,
    OffsetDateTime creadoEn) {
}
