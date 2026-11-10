package com.ejemplo.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UsuarioCreateRequest(
    @NotBlank @Size(max = 100) String username,
    @NotBlank @Size(max = 255) String password,
    Boolean activo,
    Set<@NotNull @Positive Long> roles) {
}
