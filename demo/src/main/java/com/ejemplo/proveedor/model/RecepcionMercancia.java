package com.ejemplo.proveedor.model;

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
@Table(name = "recepcion_mercancia", schema = "public")
@Getter
@Setter
public class RecepcionMercancia {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "empresa_id", nullable = false)
  private UUID empresaId;

  @Column(name = "numero_recepcion", nullable = false, length = 50)
  private String numeroRecepcion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "orden_compra_id", nullable = false)
  private OrdenCompra ordenCompra;

  @Column(name = "fecha_recepcion", nullable = false)
  private LocalDate fechaRecepcion;

  @Column(name = "recibido_por", nullable = false)
  private UUID recibidoPor;

  @Generated(event = EventType.INSERT)
  @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime creadoEn;
}
