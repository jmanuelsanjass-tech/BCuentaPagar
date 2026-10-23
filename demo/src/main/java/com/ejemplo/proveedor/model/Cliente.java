package com.ejemplo.proveedor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "clientes", schema = "public", indexes = {
    @Index(name = "idx_clientes_nombre", columnList = "nombre"),
    @Index(name = "idx_clientes_correo", columnList = "correo")
})
@Check(name = "chk_estado_direccion", constraints = "estado_direccion IN ('Aguascalientes','Baja California','Baja California Sur',"
    + "'Campeche','Coahuila','Colima','Chiapas','Chihuahua','Ciudad de México',"
    + "'Durango','Guanajuato','Guerrero','Hidalgo','Jalisco','México','Michoacán',"
    + "'Morelos','Nayarit','Nuevo León','Oaxaca','Puebla','Querétaro','Quintana Roo',"
    + "'San Luis Potosí','Sinaloa','Sonora','Tabasco','Tamaulipas','Tlaxcala',"
    + "'Veracruz','Yucatán','Zacatecas')")
@Check(name = "chk_telefono", constraints = "telefono ~ '^[0-9]{10}$'")
@Check(name = "chk_correo", constraints = "correo ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'")
@Data
public class Cliente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_cliente")
  private Long idCliente;

  @NotBlank(message = "El nombre es obligatorio")
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String nombre;

  @NotBlank(message = "El teléfono es obligatorio")
  @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe contener exactamente 10 dígitos")
  @Column(nullable = false, length = 10)
  private String telefono;

  @NotBlank(message = "El correo es obligatorio")
  @Pattern(regexp = "(?i)^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "El correo electrónico no tiene un formato válido")
  @Column(nullable = false, unique = true, length = 100)
  private String correo;

  @Size(max = 200)
  @Column(length = 200)
  private String direccion;

  @NotBlank(message = "El estado de la dirección es obligatorio")
  @Pattern(regexp = "Aguascalientes|Baja California|Baja California Sur|Campeche|Coahuila|Colima|"
      + "Chiapas|Chihuahua|Ciudad de México|Durango|Guanajuato|Guerrero|Hidalgo|"
      + "Jalisco|México|Michoacán|Morelos|Nayarit|Nuevo León|Oaxaca|Puebla|Querétaro|"
      + "Quintana Roo|San Luis Potosí|Sinaloa|Sonora|Tabasco|Tamaulipas|Tlaxcala|"
      + "Veracruz|Yucatán|Zacatecas", message = "El estado de la dirección no es válido")
  @Column(name = "estado_direccion", nullable = false, length = 50)
  private String estadoDireccion;

  @Column(name = "fecha_registro", insertable = false, updatable = false, columnDefinition = "date default current_date")
  private LocalDate fechaRegistro;

  @Pattern(regexp = "activo|inactivo|prospecto", message = "El estado del cliente debe ser activo, inactivo o prospecto")
  @Column(name = "estado", nullable = false, length = 20, columnDefinition = "varchar(20) default 'activo'")
  private String estado = "activo";

  @Column(name = "historial_compras", columnDefinition = "text")
  private String historialCompras;

  @Column(name = "fecha_auditoria", insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
  private LocalDateTime fechaAuditoria;

  @Size(max = 50)
  @Column(name = "usuario_auditoria", length = 50)
  private String usuarioAuditoria;

  @Column(name = "qr_code", columnDefinition = "bytea")
  private byte[] qrCode;
}
