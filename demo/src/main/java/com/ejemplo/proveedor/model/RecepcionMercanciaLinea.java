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
@Table(name = "recepcion_mercancia_linea", schema = "public")
@Getter
@Setter
public class RecepcionMercanciaLinea {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "recepcion_id", nullable = false)
  private RecepcionMercancia recepcion;

  @Column(name = "orden_compra_linea_id", nullable = false)
  private UUID ordenCompraLineaId;

  @Column(name = "cantidad_recibida", nullable = false, precision = 19, scale = 4)
  private BigDecimal cantidadRecibida;

  @Column(name = "notas", columnDefinition = "text")
  private String notas;
}
