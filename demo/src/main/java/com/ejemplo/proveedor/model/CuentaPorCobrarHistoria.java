package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cuentas_por_cobrar_historia", schema = "public")
@Getter
@Setter
public class CuentaPorCobrarHistoria {

  @Id
  @Column(name = "id_historia", nullable = false)
  private Long idHistoria;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_cxc", nullable = false,
      foreignKey = @ForeignKey(name = "cuentas_por_cobrar_historia_id_cxc_fkey"))
  private CuentaPorCobrar cuentaPorCobrar;

  @Column(name = "fecha_movimiento", insertable = false, updatable = false,
      columnDefinition = "timestamp default current_timestamp")
  private LocalDateTime fechaMovimiento;

  @NotBlank
  @Pattern(regexp = "ALTA|ABONO|Cancelado|PAGO_TOTAL|REESTRUCTURA|PRORROGA|"
      + "CANCELACION|CASTIGO|BLOQUEO_CREDITO|DESBLOQUEO_CREDITO",
      message = "El tipo de movimiento no está permitido")
  @Size(max = 30)
  @Column(name = "tipo_movimiento", nullable = false, length = 30)
  private String tipoMovimiento;

  @Digits(integer = 16, fraction = 2)
  @Column(name = "importe_original", precision = 18, scale = 2)
  private BigDecimal importeOriginal;

  @Digits(integer = 16, fraction = 2)
  @Column(name = "saldo_anterior", precision = 18, scale = 2)
  private BigDecimal saldoAnterior;

  @Digits(integer = 16, fraction = 2)
  @Column(name = "saldo_nuevo", precision = 18, scale = 2)
  private BigDecimal saldoNuevo;

  @Digits(integer = 16, fraction = 2)
  @Column(name = "monto_movimiento", precision = 18, scale = 2)
  private BigDecimal montoMovimiento;

  @Column(name = "fecha_vencimiento_ant")
  private LocalDate fechaVencimientoAnterior;

  @Column(name = "fecha_vencimiento_nva")
  private LocalDate fechaVencimientoNueva;

  @Column(name = "dias_vencidos")
  private Integer diasVencidos;

  @Size(max = 100)
  @Column(name = "usuario_movimiento", length = 100)
  private String usuarioMovimiento;

  @Size(max = 100)
  @Column(name = "equipo_movimiento", length = 100)
  private String equipoMovimiento;

  @Size(max = 500)
  @Column(name = "observaciones", length = 500)
  private String observaciones;

  @Column(name = "fecha_log", insertable = false, updatable = false,
      columnDefinition = "timestamp default current_timestamp")
  private LocalDateTime fechaLog;
}
