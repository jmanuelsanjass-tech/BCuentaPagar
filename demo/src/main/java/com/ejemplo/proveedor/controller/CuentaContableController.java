package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.CuentaContableResponse;
import com.ejemplo.proveedor.service.CuentaContableService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas-contables")
public class CuentaContableController {

  private final CuentaContableService service;

  public CuentaContableController(CuentaContableService service) {
    this.service = service;
  }

  @GetMapping
  public List<CuentaContableResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<CuentaContableResponse> obtener(@PathVariable UUID id) {
    return service.obtener(id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
