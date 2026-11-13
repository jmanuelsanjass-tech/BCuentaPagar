package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
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

@Entity
@Table(name = "factura_proveedor_linea", schema = "public")
@Getter
@Setter
public class FacturaProveedorLinea {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "factura_id", nullable = false)
  private FacturaProveedor factura;

  @Column(name = "numero_linea", nullable = false)
  private Integer numeroLinea;

  @Column(name = "orden_compra_linea_id")
  private UUID ordenCompraLineaId;

  @Column(name = "descripcion", nullable = false, columnDefinition = "text")
  private String descripcion;

  @Column(name = "cantidad", nullable = false, precision = 19, scale = 4)
  private BigDecimal cantidad;

  @Column(name = "precio_unitario", nullable = false, precision = 19, scale = 4)
  private BigDecimal precioUnitario;

  @Column(name = "monto_neto", nullable = false, precision = 19, scale = 4)
  private BigDecimal montoNeto;

  @Column(name = "monto_impuestos", nullable = false, precision = 19, scale = 4)
  private BigDecimal montoImpuestos = BigDecimal.ZERO;

  @Column(name = "cuenta_contable_id", nullable = false)
  private UUID cuentaContableId;
}
