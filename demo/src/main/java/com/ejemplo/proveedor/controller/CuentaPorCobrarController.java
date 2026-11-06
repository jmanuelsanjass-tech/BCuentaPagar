package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.CuentaPorCobrarRequest;
import com.ejemplo.proveedor.dto.CuentaPorCobrarResponse;
import com.ejemplo.proveedor.service.CuentaPorCobrarService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas-por-cobrar")
public class CuentaPorCobrarController {

  private final CuentaPorCobrarService service;

  public CuentaPorCobrarController(CuentaPorCobrarService service) {
    this.service = service;
  }

  @GetMapping
  public List<CuentaPorCobrarResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<CuentaPorCobrarResponse> obtener(@PathVariable Long id) {
    return service.obtener(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<CuentaPorCobrarResponse> crear(
      @Valid @RequestBody CuentaPorCobrarRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CuentaPorCobrarResponse> actualizar(
      @PathVariable Long id, @Valid @RequestBody CuentaPorCobrarRequest request) {
    return service.actualizar(id, request)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    return service.eliminar(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
