package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ventas_detalle", schema = "public")
@Getter
@Setter
public class VentaDetalle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "detalle_id")
  private Long detalleId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "venta_id", nullable = false)
  private Venta venta;

  @Column(name = "codigo_barras", nullable = false, length = 50)
  private String codigoBarras;

  @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidad;

  @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
  private BigDecimal precioUnitario;

  @Column(name = "importe", precision = 14, scale = 2, insertable = false, updatable = false)
  private BigDecimal importe;
}
