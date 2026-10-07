package com.ejemplo.proveedor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(
    name = "inventario_almacen",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_inventario_almacen_codigo_barras",
        columnNames = "codigo_barras"))
@Data
public class InventarioAlmacen {

  @Id
  @NotBlank
  @Size(max = 50)
  @Column(name = "codigo_barras", nullable = false, length = 50)
  private String codigoBarras;

  @NotBlank
  @Size(max = 15)
  @Column(name = "id_almacen", nullable = false, length = 15)
  private String idAlmacen;

  @NotNull
  @Min(0)
  @Column(name = "stock_min", nullable = false)
  private Integer stockMin;

  @NotNull
  @Min(0)
  @Column(name = "stock_max", nullable = false)
  private Integer stockMax;

  @NotNull
  @Min(0)
  @Column(name = "talla_1", nullable = false)
  private Integer talla1 = 0;

  @NotNull
  @Min(0)
  @Column(name = "talla_2", nullable = false)
  private Integer talla2 = 0;

  @NotNull
  @Min(0)
  @Column(name = "talla_3", nullable = false)
  private Integer talla3 = 0;

  @NotNull
  @Min(0)
  @Column(name = "talla_4", nullable = false)
  private Integer talla4 = 0;
}
