package com.ejemplo.proveedor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "proveedores", schema = "public")
@Data
public class Proveedor {

  @Id
  @NotBlank(message = "La clave del proveedor es obligatoria")
  @Size(max = 15)
  @Column(name = "clvprv", nullable = false, length = 15)
  private String clvprv;

  @NotBlank(message = "El nombre del proveedor es obligatorio")
  @Size(max = 150)
  @Column(name = "nomprv", nullable = false, length = 150)
  private String nomprv;

  @NotBlank(message = "El tipo del proveedor es obligatorio")
  @Size(max = 50)
  @Column(name = "tipprv", nullable = false, length = 50)
  private String tipprv;

  @NotBlank(message = "El RFC del proveedor es obligatorio")
  @Size(max = 15)
  @Column(name = "rfcprv", nullable = false, length = 15)
  private String rfcprv;

  @Size(max = 15)
  @Column(name = "telprv", length = 15)
  private String telprv;

  @Size(max = 150)
  @Column(name = "corrprv", length = 150)
  private String corrprv;

  @Size(max = 200)
  @Column(name = "dirprv", length = 200)
  private String dirprv;

  @Size(max = 5)
  @Column(name = "codpos", length = 5)
  private String codpos;

  @NotBlank(message = "El estado del proveedor es obligatorio")
  @Size(max = 20)
  @Column(name = "estprv", nullable = false, length = 20)
  private String estprv = "Activo";

  @Size(min = 18, max = 18, message = "La CLABE debe contener exactamente 18 caracteres")
  @Column(name = "clabe", length = 18)
  private String clabe;
}
