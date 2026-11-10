package com.ejemplo.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambioPasswordRequest(
    @NotBlank @Size(max = 255) String password) {
}
