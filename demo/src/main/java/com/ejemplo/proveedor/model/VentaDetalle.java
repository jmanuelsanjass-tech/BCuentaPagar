package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detalle_venta", schema = "public")
@Getter
@Setter
public class VentaDetalle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_detalle", nullable = false)
  private Long detalleId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_venta", nullable = false, foreignKey = @ForeignKey(name = "fk_venta"))
  private Venta venta;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_producto", nullable = false, foreignKey = @ForeignKey(name = "fk_producto"))
  private Producto producto;

  @Column(name = "cantidad", nullable = false)
  @Min(1)
  private Integer cantidad;

  @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
  private BigDecimal precioUnitario;

  @Column(name = "subtotal", precision = 12, scale = 2, insertable = false, updatable = false)
  private BigDecimal subtotal;

  @Column(name = "descuento", precision = 12, scale = 2, columnDefinition = "numeric(12,2) default 0.00")
  private BigDecimal descuento = BigDecimal.ZERO.setScale(2);

  @Column(name = "iva", precision = 12, scale = 2, insertable = false, updatable = false)
  private BigDecimal iva;

  @Column(name = "total", precision = 12, scale = 2, insertable = false, updatable = false)
  private BigDecimal total;

  @Size(max = 50)
  @Column(name = "usuario_auditoria", length = 50)
  private String usuarioAuditoria;

  @Column(name = "fecha_auditoria", insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
  private LocalDateTime fechaAuditoria;
}
