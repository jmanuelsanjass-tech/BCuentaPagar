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
@Table(name = "orden_compra_linea", schema = "public")
@Getter
@Setter
public class OrdenCompraLinea {

  @Id
  @Column(name = "id", nullable = false)
  private UUID idOrdenCompraLinea;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "orden_compra_id", nullable = false)
  private OrdenCompra ordenCompra;

  @Column(name = "numero_linea", nullable = false)
  private Integer numeroLinea;

  @Column(name = "descripcion", nullable = false, columnDefinition = "text")
  private String descripcion;

  @Column(name = "cantidad_ordenada", nullable = false, precision = 19, scale = 4)
  private BigDecimal cantidadOrdenada;

  @Column(name = "precio_unitario", nullable = false, precision = 19, scale = 4)
  private BigDecimal precioUnitario;

  @Column(name = "cantidad_recibida", nullable = false, precision = 19, scale = 4)
  private BigDecimal cantidadRecibida = BigDecimal.ZERO;

  @Column(name = "cuenta_contable_id", nullable = false)
  private UUID cuentaContableId;
}
