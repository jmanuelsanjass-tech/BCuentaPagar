package com.ejemplo.proveedor.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClienteResponse(
    Long idCliente,
    String nombre,
    String telefono,
    String correo,
    String direccion,
    String estadoDireccion,
    LocalDate fechaRegistro,
    String estado,
    String historialCompras,
    LocalDateTime fechaAuditoria,
    String usuarioAuditoria,
    byte[] qrCode) {
}
