package com.ejemplo.proveedor.dto;

import java.time.LocalDateTime;

public record ClienteResponse(
    Long id,
    String nombreComercial,
    String razonSocial,
    String rfc,
    String situacionFiscal,
    String tipoCliente,
    String direccion,
    String colonia,
    String ciudad,
    String contacto,
    String banco,
    String cuentaBancaria,
    String clabe,
    String formaPago,
    LocalDateTime fechaAlta) {
}
