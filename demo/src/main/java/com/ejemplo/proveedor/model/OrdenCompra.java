package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "orden_compra", schema = "public")
@Getter
@Setter
public class OrdenCompra {

  @Id
  @Column(name = "id", nullable = false)
  private UUID idOrdenCompra;

  @Column(name = "empresa_id", nullable = false)
  private UUID empresaId;

  @Column(name = "numero_oc", nullable = false, length = 50)
  private String numeroOc;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "proveedor_id", nullable = false)
  private Proveedor proveedor;

  @Column(name = "fecha_orden", nullable = false)
  private LocalDate fechaOrden;

  @Column(name = "estado", nullable = false, length = 20)
  private String estado = "BORRADOR";

  @Column(name = "moneda", nullable = false, length = 3)
  private String moneda;

  @Column(name = "monto_total", nullable = false, precision = 19, scale = 4)
  private BigDecimal montoTotal = BigDecimal.ZERO;

  @Column(name = "solicitado_por", nullable = false)
  private UUID solicitadoPor;

  @Column(name = "aprobado_por")
  private UUID aprobadoPor;

  @Column(name = "aprobado_en")
  private OffsetDateTime aprobadoEn;

  @Generated(event = EventType.INSERT)
  @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime creadoEn;
}
