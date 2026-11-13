package com.ejemplo.proveedor.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.Map;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "bitacora_auditoria", schema = "public")
@Getter
@Setter
public class BitacoraAuditoria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "tabla", nullable = false, length = 100)
  private String tabla;

  @Column(name = "operacion", nullable = false, length = 10)
  private String operacion;

  @Column(name = "registro_id", nullable = false)
  private UUID registroId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "datos_anteriores")
  private Map<String, Object> datosAnteriores;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "datos_nuevos")
  private Map<String, Object> datosNuevos;

  @Generated(event = EventType.INSERT)
  @Column(name = "cambiado_en", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime cambiadoEn;

  @Generated(event = EventType.INSERT)
  @Column(name = "cambiado_por", nullable = false, length = 100,
      insertable = false, updatable = false)
  private String cambiadoPor;

  @Generated(event = EventType.INSERT)
  @Column(name = "id_transaccion", nullable = false, insertable = false, updatable = false)
  private Long idTransaccion;
}
