package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "politica_aprobacion", schema = "public")
@Getter
@Setter
public class PoliticaAprobacion {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "empresa_id", nullable = false)
  private UUID empresaId;

  @Column(name = "tipo_transaccion", nullable = false, length = 30)
  private String tipoTransaccion;

  @Column(name = "monto_minimo", nullable = false, precision = 19, scale = 4)
  private BigDecimal montoMinimo;

  @Column(name = "monto_maximo", precision = 19, scale = 4)
  private BigDecimal montoMaximo;

  @Column(name = "rol_requerido", nullable = false, length = 50)
  private String rolRequerido;

  @Column(name = "orden_secuencia", nullable = false)
  private Short ordenSecuencia = 1;

  @Column(name = "activo", nullable = false)
  private boolean activo = true;
}
