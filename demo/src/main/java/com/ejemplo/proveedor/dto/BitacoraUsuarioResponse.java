package com.ejemplo.proveedor.dto;

import java.time.LocalDateTime;

public record BitacoraUsuarioResponse(
    Long idEvento,
    String administrador,
    Long idUsuarioObjetivo,
    String usernameObjetivo,
    String accion,
    String detalle,
    LocalDateTime fechaEvento) {
}
