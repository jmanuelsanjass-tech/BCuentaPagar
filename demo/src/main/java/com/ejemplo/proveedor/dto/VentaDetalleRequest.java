package com.ejemplo.proveedor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record VentaDetalleRequest(
    Long idProducto, // Quitamos el @NotNull aquí para que no falle si viene nulo
    
    @NotBlank
    String codigoBarras,
    
    @NotNull
    @Min(1)
    Integer cantidad,
    
    @NotNull
    BigDecimal precioUnitario,
    
    // AGREGA ESTE CAMPO AQUÍ:
    BigDecimal descuento 
) {}