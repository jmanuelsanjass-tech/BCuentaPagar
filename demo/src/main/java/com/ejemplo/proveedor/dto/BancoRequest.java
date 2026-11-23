package com.ejemplo.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BancoRequest(
    @NotBlank @Size(max = 20) String codigoBanco,
    @NotBlank @Size(max = 150) String nombre,
    @Size(max = 50) String nombreCorto,
    @Pattern(regexp = "^(?:[A-Za-z0-9]{8}(?:[A-Za-z0-9]{3})?)?$")
        @Size(max = 11) String codigoSwiftBic,
    @Pattern(regexp = "^(?:[A-Za-z]{2})?$") @Size(max = 2) String pais,
    Boolean activo) {
}
