package com.ejemplo.proveedor.controller;

import com.ejemplo.proveedor.dto.FacturaProveedorLineaRequest;
import com.ejemplo.proveedor.dto.FacturaProveedorLineaResponse;
import com.ejemplo.proveedor.service.FacturaProveedorLineaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
@RequestMapping("/api/facturas-proveedor/{facturaId}/lineas")
public class FacturaProveedorLineaController {

  private final FacturaProveedorLineaService service;

  public FacturaProveedorLineaController(FacturaProveedorLineaService service) {
    this.service = service;
  }

  @GetMapping
  public List<FacturaProveedorLineaResponse> listar(@PathVariable UUID facturaId) {
    return service.listar(facturaId);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FacturaProveedorLineaResponse> obtener(
      @PathVariable UUID facturaId, @PathVariable UUID id) {
    return service.obtener(facturaId, id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<FacturaProveedorLineaResponse> crear(
      @PathVariable UUID facturaId, @Valid @RequestBody FacturaProveedorLineaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.crear(facturaId, request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<FacturaProveedorLineaResponse> actualizar(
      @PathVariable UUID facturaId,
      @PathVariable UUID id,
      @Valid @RequestBody FacturaProveedorLineaRequest request) {
    return service.actualizar(facturaId, id, request).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable UUID facturaId, @PathVariable UUID id) {
    return service.eliminar(facturaId, id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
