package com.ejemplo.proveedor.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.Map;

public record BitacoraAuditoriaResponse(
    Long id,
    String tabla,
    String operacion,
    UUID registroId,
    Map<String, Object> datosAnteriores,
    Map<String, Object> datosNuevos,
    OffsetDateTime cambiadoEn,
    String cambiadoPor,
    Long idTransaccion) {
}
