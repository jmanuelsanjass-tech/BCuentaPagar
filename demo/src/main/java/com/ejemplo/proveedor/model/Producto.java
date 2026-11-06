package com.ejemplo.proveedor.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.Check;

@Entity
@Table(
    name = "inventario",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_inventario_codigo_barras",
        columnNames = "codigo_barras"),
    indexes = {
        @Index(name = "idx_productos_nombre", columnList = "nombre"),
        @Index(name = "idx_productos_categoria", columnList = "categoria"),
        @Index(name = "idx_productos_codigobarras", columnList = "codigo_barras")
    })
@Check(name = "chk_categoria",
    constraints = "categoria IN ('Abarrotes','Lácteos','Frutería')")
@Check(name = "chk_inventario_precio", constraints = "precio > 0")
@Check(name = "chk_inventario_stock", constraints = "stock >= 0")
@Data
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_producto", nullable = false)
  private Long idProducto;

  @NotBlank(message = "El nombre del producto es obligatorio")
  @Size(max = 100)
  @Column(name = "nombre", nullable = false, length = 100)
  private String nombre;

  @Column(name = "descripcion", columnDefinition = "text")
  private String descripcion;

  @Pattern(
      regexp = "Abarrotes|Lácteos|Frutería",
      message = "La categoría debe ser Abarrotes, Lácteos o Frutería")
  @Column(name = "categoria", nullable = false, length = 50,
      columnDefinition = "varchar(50) default 'Abarrotes'")
  private String categoria = "Abarrotes";

  @NotNull(message = "El precio es obligatorio")
  @DecimalMin(value = "0.00", inclusive = false, message = "El precio debe ser mayor que cero")
  @Digits(integer = 10, fraction = 2,
      message = "El precio debe tener hasta 10 dígitos enteros y 2 decimales")
  @Column(name = "precio", nullable = false, precision = 12, scale = 2)
  private BigDecimal precio;

  @Min(value = 0, message = "El stock no puede ser negativo")
  @Column(name = "stock", nullable = false, columnDefinition = "integer default 0")
  private Integer stock = 0;

  @Pattern(regexp = "activo|inactivo",
      message = "El estado debe ser activo o inactivo")
  @Size(max = 20)
  @Column(name = "estado", length = 20, columnDefinition = "varchar(20) default 'activo'")
  private String estado = "activo";

  @NotBlank(message = "El código de barras es obligatorio")
  @Size(max = 20)
  @Column(name = "codigo_barras", nullable = false, length = 20)
  private String codigoBarras;

  @Column(name = "fecha_registro", insertable = false, updatable = false,
      columnDefinition = "timestamp default current_timestamp")
  private LocalDateTime fechaRegistro;

  @Column(name = "fecha_auditoria", insertable = false, updatable = false,
      columnDefinition = "timestamp default current_timestamp")
  private LocalDateTime fechaAuditoria;

  @Size(max = 50)
  @Column(name = "usuario_auditoria", length = 50)
  private String usuarioAuditoria;

  @Column(name = "qr_code", columnDefinition = "bytea")
  private byte[] qrCode;
}
