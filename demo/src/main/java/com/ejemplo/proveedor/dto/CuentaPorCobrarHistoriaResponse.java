package com.ejemplo.proveedor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CuentaPorCobrarHistoriaResponse(
    Long idHistoria,
    Long cuentaPorCobrarId,
    LocalDateTime fechaMovimiento,
    String tipoMovimiento,
    BigDecimal importeOriginal,
    BigDecimal saldoAnterior,
    BigDecimal saldoNuevo,
    BigDecimal montoMovimiento,
    LocalDate fechaVencimientoAnterior,
    LocalDate fechaVencimientoNueva,
    Integer diasVencidos,
    String usuarioMovimiento,
    String equipoMovimiento,
    String observaciones,
    LocalDateTime fechaLog) {
}
