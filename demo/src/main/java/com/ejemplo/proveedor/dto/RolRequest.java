package com.ejemplo.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RolRequest(
    @NotBlank @Size(max = 50) String nombre,
    @Size(max = 200) String descripcion) {
}
