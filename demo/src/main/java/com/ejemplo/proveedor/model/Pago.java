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
@Table(name = "pago", schema = "public")
@Getter
@Setter
public class Pago {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "empresa_id", nullable = false)
  private UUID empresaId;

  @Column(name = "numero_pago", nullable = false, length = 50)
  private String numeroPago;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "proveedor_id", nullable = false)
  private Proveedor proveedor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "factura_id")
  private FacturaProveedor factura;

  @Column(name = "fecha_pago", nullable = false)
  private LocalDate fechaPago;

  @Column(name = "monto", nullable = false, precision = 19, scale = 4)
  private BigDecimal monto;

  @Column(name = "moneda", nullable = false, length = 3)
  private String moneda;

  @Column(name = "metodo_pago", nullable = false, length = 30)
  private String metodoPago;

  @Column(name = "referencia", length = 100)
  private String referencia;

  @Column(name = "estado", nullable = false, length = 20)
  private String estado = "PENDIENTE";

  @Column(name = "aprobado_por", nullable = false)
  private UUID aprobadoPor;

  @Column(name = "ejecutado_por")
  private UUID ejecutadoPor;

  @Generated(event = EventType.INSERT)
  @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime creadoEn;
}
