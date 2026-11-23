package com.ejemplo.proveedor.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "bancos", schema = "public")
@Getter
@Setter
public class Banco {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "codigo_banco", nullable = false, unique = true, length = 20)
  private String codigoBanco;

  @Column(name = "nombre", nullable = false, length = 150)
  private String nombre;

  @Column(name = "nombre_corto", length = 50)
  private String nombreCorto;

  @Column(name = "codigo_swift_bic", length = 11)
  private String codigoSwiftBic;

  @Column(name = "pais", nullable = false, length = 2)
  private String pais = "MX";

  @Column(name = "activo", nullable = false)
  private boolean activo = true;

  @Generated(event = EventType.INSERT)
  @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
  private OffsetDateTime creadoEn;

  @Column(name = "actualizado_en", nullable = false)
  private OffsetDateTime actualizadoEn;
}
