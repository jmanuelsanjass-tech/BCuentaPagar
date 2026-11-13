package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.FacturaProveedorLineaRequest;
import com.ejemplo.proveedor.dto.FacturaProveedorLineaResponse;
import com.ejemplo.proveedor.model.FacturaProveedor;
import com.ejemplo.proveedor.model.FacturaProveedorLinea;
import com.ejemplo.proveedor.repository.FacturaProveedorLineaRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FacturaProveedorLineaService {

  private final FacturaProveedorLineaRepository repository;
  private final FacturaProveedorRepository facturaRepository;
  private final FacturaProveedorTotalesService totalesService;
  private final JdbcTemplate jdbcTemplate;
  private final UuidV7Generator uuidV7Generator;

  public FacturaProveedorLineaService(
      FacturaProveedorLineaRepository repository,
      FacturaProveedorRepository facturaRepository,
      FacturaProveedorTotalesService totalesService,
      JdbcTemplate jdbcTemplate,
      UuidV7Generator uuidV7Generator) {
    this.repository = repository;
    this.facturaRepository = facturaRepository;
    this.totalesService = totalesService;
    this.jdbcTemplate = jdbcTemplate;
    this.uuidV7Generator = uuidV7Generator;
  }

  @Transactional(readOnly = true)
  public List<FacturaProveedorLineaResponse> listar(UUID facturaId) {
    verificarFactura(facturaId);
    return repository.findAllByFactura_IdOrderByNumeroLineaAsc(facturaId)
        .stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<FacturaProveedorLineaResponse> obtener(UUID facturaId, UUID id) {
    verificarFactura(facturaId);
    return repository.findByFactura_IdAndId(facturaId, id).map(this::toResponse);
  }

  @Transactional
  public FacturaProveedorLineaResponse crear(UUID facturaId, FacturaProveedorLineaRequest request) {
    FacturaProveedor factura = obtenerFactura(facturaId);
    validarRequest(facturaId, request, null);
    FacturaProveedorLinea linea = new FacturaProveedorLinea();
    linea.setId(uuidV7Generator.generate());
    aplicarRequest(linea, factura, request);
    FacturaProveedorLinea guardada = repository.saveAndFlush(linea);
    totalesService.recalcularDesdeLineas(facturaId);
    return toResponse(guardada);
  }

  @Transactional
  public Optional<FacturaProveedorLineaResponse> actualizar(
      UUID facturaId, UUID id, FacturaProveedorLineaRequest request) {
    return repository.findByFactura_IdAndId(facturaId, id).map(linea -> {
      validarRequest(facturaId, request, id);
      aplicarRequest(linea, linea.getFactura(), request);
      FacturaProveedorLinea guardada = repository.saveAndFlush(linea);
      totalesService.recalcularDesdeLineas(facturaId);
      return toResponse(guardada);
    });
  }

  @Transactional
  public boolean eliminar(UUID facturaId, UUID id) {
    return repository.findByFactura_IdAndId(facturaId, id).map(linea -> {
      repository.delete(linea);
      repository.flush();
      totalesService.recalcularDesdeLineas(facturaId);
      return true;
    }).orElse(false);
  }

  private void validarRequest(UUID facturaId, FacturaProveedorLineaRequest request, UUID id) {
    boolean numeroDuplicado = id == null
        ? repository.existsByFactura_IdAndNumeroLinea(facturaId, request.numeroLinea())
        : repository.existsByFactura_IdAndNumeroLineaAndIdNot(
            facturaId, request.numeroLinea(), id);
    if (numeroDuplicado) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe ese número de línea para la factura");
    }

    if (request.ordenCompraLineaId() != null) {
      Boolean ordenLineaValida = jdbcTemplate.queryForObject("""
          SELECT EXISTS (
              SELECT 1
              FROM public.factura_proveedor f
              JOIN public.orden_compra_linea ol
                ON ol.id = ?
               AND ol.orden_compra_id = f.orden_compra_id
              WHERE f.id = ?
          )
          """, Boolean.class, request.ordenCompraLineaId(), facturaId);
      if (!Boolean.TRUE.equals(ordenLineaValida)) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "La línea de orden no corresponde a la orden de la factura");
      }
    }

    Boolean cuentaContableExiste = jdbcTemplate.queryForObject(
        "SELECT EXISTS (SELECT 1 FROM public.cuenta_contable WHERE id = ?)",
        Boolean.class,
        request.cuentaContableId());
    if (!Boolean.TRUE.equals(cuentaContableExiste)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "No existe la cuenta contable indicada");
    }
  }

  private void aplicarRequest(
      FacturaProveedorLinea linea,
      FacturaProveedor factura,
      FacturaProveedorLineaRequest request) {
    linea.setFactura(factura);
    linea.setNumeroLinea(request.numeroLinea());
    linea.setOrdenCompraLineaId(request.ordenCompraLineaId());
    linea.setDescripcion(request.descripcion().trim());
    linea.setCantidad(request.cantidad());
    linea.setPrecioUnitario(request.precioUnitario());
    linea.setMontoNeto(request.montoNeto());
    linea.setMontoImpuestos(
        request.montoImpuestos() == null ? BigDecimal.ZERO : request.montoImpuestos());
    linea.setCuentaContableId(request.cuentaContableId());
  }

  private FacturaProveedor obtenerFactura(UUID facturaId) {
    return facturaRepository.findById(facturaId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe la factura indicada"));
  }

  private void verificarFactura(UUID facturaId) {
    if (!facturaRepository.existsById(facturaId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "No existe la factura indicada");
    }
  }

  private FacturaProveedorLineaResponse toResponse(FacturaProveedorLinea linea) {
    return new FacturaProveedorLineaResponse(
        linea.getId(),
        linea.getFactura().getId(),
        linea.getNumeroLinea(),
        linea.getOrdenCompraLineaId(),
        linea.getDescripcion(),
        linea.getCantidad(),
        linea.getPrecioUnitario(),
        linea.getMontoNeto(),
        linea.getMontoImpuestos(),
        linea.getCuentaContableId());
  }
}
