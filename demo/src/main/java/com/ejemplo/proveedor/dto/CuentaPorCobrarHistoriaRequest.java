package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CuentaPorCobrarHistoriaRequest(
    Long idHistoria,
    @NotNull Long cuentaPorCobrarId,
    @NotBlank @Pattern(regexp = "ALTA|ABONO|Cancelado|PAGO_TOTAL|REESTRUCTURA|PRORROGA|"
        + "CANCELACION|CASTIGO|BLOQUEO_CREDITO|DESBLOQUEO_CREDITO")
    @Size(max = 30) String tipoMovimiento,
    @DecimalMin("0.00") @Digits(integer = 16, fraction = 2) BigDecimal importeOriginal,
    @DecimalMin("0.00") @Digits(integer = 16, fraction = 2) BigDecimal saldoAnterior,
    @DecimalMin("0.00") @Digits(integer = 16, fraction = 2) BigDecimal saldoNuevo,
    @DecimalMin("0.00") @Digits(integer = 16, fraction = 2) BigDecimal montoMovimiento,
    LocalDate fechaVencimientoAnterior,
    LocalDate fechaVencimientoNueva,
    Integer diasVencidos,
    @Size(max = 100) String usuarioMovimiento,
    @Size(max = 100) String equipoMovimiento,
    @Size(max = 500) String observaciones) {
}
