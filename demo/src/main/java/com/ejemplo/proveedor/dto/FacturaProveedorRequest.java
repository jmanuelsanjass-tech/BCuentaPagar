package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FacturaProveedorRequest(
    @NotNull UUID empresaId,
    @NotBlank @Size(max = 50) String numeroFactura,
    @NotNull Long proveedorId,
    @NotNull LocalDate fechaFactura,
    @NotNull LocalDate fechaVencimiento,
    @NotBlank @Pattern(regexp = "[A-Z]{3}") String moneda,
    UUID ordenCompraId,
    UUID recepcionId,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4) BigDecimal montoNeto,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4)
        BigDecimal montoImpuestos,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4) BigDecimal montoTotal,
    @Pattern(regexp = "RECIBIDA|CONCILIADA|APROBADA_PAGO|PAGADA|EN_DISPUTA|CANCELADA")
        @Size(max = 30) String estado,
    @Pattern(regexp = "PENDIENTE|CONCILIADA|EXCEPCION") @Size(max = 20)
        String estadoConciliacion,
    UUID aprobadoPagoPor,
    OffsetDateTime aprobadoPagoEn,
    @NotNull UUID creadoPor) {
}
