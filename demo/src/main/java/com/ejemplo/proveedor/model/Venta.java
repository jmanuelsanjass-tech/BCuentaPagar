package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ventas", schema = "public")
@Getter
@Setter
public class Venta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_venta", nullable = false)
  private Long idVenta;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_cliente", nullable = false, foreignKey = @ForeignKey(name = "fk_cliente"))
  private Cliente cliente;

  @Column(name = "fecha_venta", insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
  private LocalDateTime fechaVenta;

  @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
  private BigDecimal subtotal;

  @Column(name = "descuento", precision = 12, scale = 2, columnDefinition = "numeric(12,2) default 0.00")
  private BigDecimal descuento = BigDecimal.ZERO.setScale(2);

  @Column(name = "iva", nullable = false, precision = 12, scale = 2)
  private BigDecimal iva;

  @Column(name = "total", nullable = false, precision = 12, scale = 2)
  private BigDecimal total;

  @Column(name = "estado", length = 20, columnDefinition = "varchar(20) default 'completada'")
  private String estado = "completada";

  @Size(max = 50)
  @Column(name = "usuario_auditoria", length = 50)
  private String usuarioAuditoria;

  @Column(name = "fecha_auditoria", insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
  private LocalDateTime fechaAuditoria;

  @Column(name = "qr_code", columnDefinition = "bytea")
  private byte[] qrCode;

  @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VentaDetalle> detalles = new ArrayList<>();

  public void reemplazarDetalles(List<VentaDetalle> nuevosDetalles) {
    detalles.clear();
    for (VentaDetalle detalle : nuevosDetalles) {
      detalle.setVenta(this);
      detalles.add(detalle);
    }
  }
}
