package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.CuentaPorCobrarHistoriaRequest;
import com.ejemplo.proveedor.dto.CuentaPorCobrarHistoriaResponse;
import com.ejemplo.proveedor.service.CuentaPorCobrarHistoriaService;
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
@RequestMapping("/api/cuentas-por-cobrar-historia")
public class CuentaPorCobrarHistoriaController {

  private final CuentaPorCobrarHistoriaService service;

  public CuentaPorCobrarHistoriaController(CuentaPorCobrarHistoriaService service) {
    this.service = service;
  }

  @GetMapping
  public List<CuentaPorCobrarHistoriaResponse> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public ResponseEntity<CuentaPorCobrarHistoriaResponse> obtener(@PathVariable Long id) {
    return service.obtener(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<CuentaPorCobrarHistoriaResponse> crear(
      @Valid @RequestBody CuentaPorCobrarHistoriaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CuentaPorCobrarHistoriaResponse> actualizar(
      @PathVariable Long id, @Valid @RequestBody CuentaPorCobrarHistoriaRequest request) {
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
