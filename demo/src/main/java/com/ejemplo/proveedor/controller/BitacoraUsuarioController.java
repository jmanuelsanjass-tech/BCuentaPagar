package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.BitacoraUsuarioResponse;
import com.ejemplo.proveedor.service.BitacoraUsuarioService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bitacora/usuarios")
public class BitacoraUsuarioController {

  private final BitacoraUsuarioService service;

  public BitacoraUsuarioController(BitacoraUsuarioService service) {
    this.service = service;
  }

  @GetMapping
  public List<BitacoraUsuarioResponse> listar(
      @RequestParam(defaultValue = "100") int limite) {
    return service.listar(limite);
  }
}
