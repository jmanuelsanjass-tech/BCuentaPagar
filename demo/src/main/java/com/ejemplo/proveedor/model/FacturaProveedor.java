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
@Table(name = "factura_proveedor", schema = "public")
@Getter
@Setter
public class FacturaProveedor {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "empresa_id", nullable = false)
  private UUID empresaId;

  @Column(name = "numero_factura", nullable = false, length = 50)
  private String numeroFactura;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "proveedor_id", nullable = false)
  private Proveedor proveedor;

  @Column(name = "fecha_factura", nullable = false)
  private LocalDate fechaFactura;

  @Column(name = "fecha_vencimiento", nullable = false)
  private LocalDate fechaVencimiento;

  @Column(name = "moneda", nullable = false, length = 3)
  private String moneda;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orden_compra_id")
  private OrdenCompra ordenCompra;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recepcion_id")
  private RecepcionMercancia recepcion;

  @Column(name = "monto_neto", nullable = false, precision = 19, scale = 4)
  private BigDecimal montoNeto;

  @Column(name = "monto_impuestos", nullable = false, precision = 19, scale = 4)
  private BigDecimal montoImpuestos = BigDecimal.ZERO;

  @Column(name = "monto_total", nullable = false, precision = 19, scale = 4)
  private BigDecimal montoTotal;

  @Column(name = "monto_pagado", nullable = false, precision = 19, scale = 4)
  private BigDecimal montoPagado = BigDecimal.ZERO;

  @Generated(event = {EventType.INSERT, EventType.UPDATE})
  @Column(name = "saldo_pendiente", insertable = false, updatable = false,
      precision = 19, scale = 4)
  private BigDecimal saldoPendiente;

  @Column(name = "estado", nullable = false, length = 30)
  private String estado = "RECIBIDA";

  @Column(name = "estado_conciliacion", nullable = false, length = 20)
  private String estadoConciliacion = "PENDIENTE";

  @Column(name = "aprobado_pago_por")
  private UUID aprobadoPagoPor;

  @Column(name = "aprobado_pago_en")
  private OffsetDateTime aprobadoPagoEn;

  @Generated(event = EventType.INSERT)
  @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime creadoEn;

  @Column(name = "creado_por", nullable = false)
  private UUID creadoPor;
}
