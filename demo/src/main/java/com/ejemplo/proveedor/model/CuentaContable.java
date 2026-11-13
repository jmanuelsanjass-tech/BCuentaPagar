package com.ejemplo.proveedor.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "cuenta_contable", schema = "public")
@Getter
@Setter
public class CuentaContable {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "empresa_id", nullable = false)
  private UUID empresaId;

  @Column(name = "codigo_cuenta", nullable = false, length = 20)
  private String codigoCuenta;

  @Column(name = "nombre_cuenta", nullable = false, length = 100)
  private String nombreCuenta;

  @Column(name = "tipo_cuenta", nullable = false, length = 20)
  private String tipoCuenta;

  @Column(name = "activo", nullable = false)
  private boolean activo;

  @Generated(event = EventType.INSERT)
  @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime creadoEn;
}
