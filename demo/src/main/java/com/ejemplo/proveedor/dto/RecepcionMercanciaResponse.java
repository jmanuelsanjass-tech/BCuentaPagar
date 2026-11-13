package com.ejemplo.proveedor.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RecepcionMercanciaResponse(
    UUID id,
    UUID empresaId,
    String numeroRecepcion,
    UUID ordenCompraId,
    LocalDate fechaRecepcion,
    UUID recibidoPor,
    OffsetDateTime creadoEn) {
}
