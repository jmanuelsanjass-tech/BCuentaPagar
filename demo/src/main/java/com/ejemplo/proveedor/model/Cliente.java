package com.ejemplo.proveedor.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "clientes", schema = "public")
@Data
public class Cliente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El nombre comercial es obligatorio")
  @Size(max = 150)
  @Column(name = "nombre_comercial", nullable = false, length = 150)
  private String nombreComercial;

  @NotBlank(message = "La razón social es obligatoria")
  @Size(max = 200)
  @Column(name = "razon_social", nullable = false, length = 200)
  private String razonSocial;

  @NotBlank(message = "El RFC es obligatorio")
  @Pattern(regexp = "^[\\s\\S]{12,13}$", message = "El RFC debe contener 12 o 13 caracteres")
  @Column(nullable = false, unique = true, length = 13)
  private String rfc;

  @NotBlank(message = "La situación fiscal es obligatoria")
  @Size(max = 100)
  @Column(name = "situacion_fiscal", nullable = false, length = 100)
  private String situacionFiscal;

  @NotBlank(message = "El tipo de cliente es obligatorio")
  @Pattern(
      regexp = "Escuela privada|Persona física|Persona moral|Otro",
      message = "El tipo de cliente no es válido")
  @Column(name = "tipo_cliente", nullable = false, length = 50)
  private String tipoCliente;

  @Size(max = 200)
  @Column(length = 200)
  private String direccion;

  @Size(max = 100)
  @Column(length = 100)
  private String colonia;

  @Size(max = 100)
  @Column(length = 100)
  private String ciudad;

  @Size(max = 150)
  @Column(length = 150)
  private String contacto;

  @Size(max = 100)
  @Column(length = 100)
  private String banco;

  @Size(max = 20)
  @Column(name = "cuenta_bancaria", length = 20)
  private String cuentaBancaria;

  @Size(min = 18, max = 18, message = "La CLABE debe contener exactamente 18 caracteres")
  @Column(length = 18)
  private String clabe;

  @Size(max = 50)
  @Column(name = "forma_pago", length = 50)
  private String formaPago = "Transferencia";

  @Column(name = "fecha_alta", insertable = false, updatable = false)
  private LocalDateTime fechaAlta;
}
