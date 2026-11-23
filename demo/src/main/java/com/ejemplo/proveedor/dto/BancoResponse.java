package com.ejemplo.proveedor.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BancoResponse(
    UUID id,
    String codigoBanco,
    String nombre,
    String nombreCorto,
    String codigoSwiftBic,
    String pais,
    boolean activo,
    OffsetDateTime creadoEn,
    OffsetDateTime actualizadoEn) {
}
