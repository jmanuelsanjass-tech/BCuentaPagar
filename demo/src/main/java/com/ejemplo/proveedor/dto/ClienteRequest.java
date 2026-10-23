package com.ejemplo.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Pattern(regexp = "^[0-9]{10}$") String telefono,
    @NotBlank
        @Pattern(regexp = "(?i)^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        @Size(max = 100)
        String correo,
    @Size(max = 200) String direccion,
    @NotBlank
        @Pattern(
            regexp =
                "Aguascalientes|Baja California|Baja California Sur|Campeche|Coahuila|Colima|"
                    + "Chiapas|Chihuahua|Ciudad de México|Durango|Guanajuato|Guerrero|Hidalgo|"
                    + "Jalisco|México|Michoacán|Morelos|Nayarit|Nuevo León|Oaxaca|Puebla|Querétaro|"
                    + "Quintana Roo|San Luis Potosí|Sinaloa|Sonora|Tabasco|Tamaulipas|Tlaxcala|"
                    + "Veracruz|Yucatán|Zacatecas")
        String estadoDireccion,
    @Pattern(regexp = "activo|inactivo|prospecto") String estado,
    String historialCompras,
    byte[] qrCode) {
}
