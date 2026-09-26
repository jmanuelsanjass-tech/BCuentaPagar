package com.ejemplo.proveedor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "proveedores")
@Data
public class Proveedor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El nombre es obligatorio")
  @Column(nullable = false)
  private String nombre;

  @NotBlank(message = "El RUC es obligatorio")
  @Column(nullable = false, unique = true, length = 11)
  private String ruc;

  @NotBlank(message = "El teléfono es obligatorio")
  private String telefono;

  @Email(message = "El email debe ser válido")
  @NotBlank(message = "El email es obligatorio")
  @Column(nullable = false)
  private String email;

  private String direccion;
}
