package com.ejemplo.proveedor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bitacora_usuarios", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class BitacoraUsuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_evento")
  private Long idEvento;

  @Column(name = "administrador", nullable = false, length = 100)
  private String administrador;

  @Column(name = "id_usuario_objetivo")
  private Long idUsuarioObjetivo;

  @Column(name = "username_objetivo", nullable = false, length = 100)
  private String usernameObjetivo;

  @Column(name = "accion", nullable = false, length = 50)
  private String accion;

  @Column(name = "detalle", length = 500)
  private String detalle;

  @Column(name = "fecha_evento", insertable = false, updatable = false)
  private LocalDateTime fechaEvento;
}
