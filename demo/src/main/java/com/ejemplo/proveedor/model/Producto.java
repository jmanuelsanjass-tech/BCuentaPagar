package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "productos", schema = "public")
@Data
public class Producto {

  @Id
  @NotBlank(message = "El código de barras es obligatorio")
  @Size(max = 50)
  @Column(name = "codigo_barras", nullable = false, length = 50)
  private String codigoBarras;

  @NotBlank(message = "El modelo es obligatorio")
  @Size(max = 20)
  @Column(name = "modelo", nullable = false, length = 20)
  private String modelo;

  @NotBlank(message = "El nombre es obligatorio")
  @Size(max = 255)
  @Column(name = "nombre", nullable = false, length = 255)
  private String nombre;

  @NotNull(message = "El costo es obligatorio")
  @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
  @Digits(integer = 8, fraction = 2, message = "El costo debe tener como máximo 10 dígitos y 2 decimales")
  @Column(name = "costo", nullable = false, precision = 10, scale = 2)
  private BigDecimal costo;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "codigo_barras",
      referencedColumnName = "codigo_barras",
      insertable = false,
      updatable = false)
  private InventarioAlmacen inventario;
}
