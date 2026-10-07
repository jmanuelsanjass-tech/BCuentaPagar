package com.ejemplo.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
    @NotBlank @Size(max = 150) String nombreComercial,
    @NotBlank @Size(max = 200) String razonSocial,
    @NotBlank @Pattern(regexp = "^[\\s\\S]{12,13}$") String rfc,
    @NotBlank @Size(max = 100) String situacionFiscal,
    @NotBlank @Pattern(regexp = "Escuela privada|Persona física|Persona moral|Otro") String tipoCliente,
    @Size(max = 200) String direccion,
    @Size(max = 100) String colonia,
    @Size(max = 100) String ciudad,
    @Size(max = 150) String contacto,
    @Size(max = 100) String banco,
    @Size(max = 20) String cuentaBancaria,
    @Size(min = 18, max = 18) String clabe,
    @Size(max = 50) String formaPago) {
}
