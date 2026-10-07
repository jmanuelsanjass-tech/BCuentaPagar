package com.ejemplo.proveedor.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VentaRequest(
    @NotNull Long clienteId,
    @Pattern(regexp = "Pendiente|Pagado|Cancelado|Parcial") String estado,
    @Size(max = 50) String formaPago,
    @NotEmpty List<@Valid VentaDetalleRequest> detalles) {
}
