package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ventas", schema = "public")
@Getter
@Setter
public class Venta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "venta_id")
  private Long ventaId;

  @Column(name = "cliente_id", nullable = false)
  private Long clienteId;

  @Column(name = "fecha_venta", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fechaVenta;

  @Column(name = "total", nullable = false, precision = 12, scale = 2)
  private BigDecimal total;

  @Column(name = "estado", nullable = false, length = 20)
  private String estado = "Pendiente";

  @Column(name = "forma_pago", length = 50)
  private String formaPago = "Transferencia";

  @Column(name = "usuario_registro", length = 100, insertable = false, updatable = false)
  private String usuarioRegistro;

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
