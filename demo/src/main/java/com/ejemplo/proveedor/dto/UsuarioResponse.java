package com.ejemplo.proveedor.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioResponse(
    Long idUsuario,
    String username,
    boolean activo,
    LocalDateTime fechaAlta,
    List<RolResponse> roles) {
}
