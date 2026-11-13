package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.OrdenCompraLineaRequest;
import com.ejemplo.proveedor.dto.OrdenCompraLineaResponse;
import com.ejemplo.proveedor.model.OrdenCompra;
import com.ejemplo.proveedor.model.OrdenCompraLinea;
import com.ejemplo.proveedor.repository.OrdenCompraLineaRepository;
import com.ejemplo.proveedor.repository.OrdenCompraRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorLineaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdenCompraLineaService {

  private static final BigDecimal MAX_ORDER_TOTAL = new BigDecimal("999999999999999.9999");

  private final OrdenCompraLineaRepository repository;
  private final OrdenCompraRepository ordenCompraRepository;
  private final FacturaProveedorLineaRepository facturaLineaRepository;
  private final JdbcTemplate jdbcTemplate;
  private final UuidV7Generator uuidV7Generator;

  public OrdenCompraLineaService(
      OrdenCompraLineaRepository repository,
      OrdenCompraRepository ordenCompraRepository,
      FacturaProveedorLineaRepository facturaLineaRepository,
      JdbcTemplate jdbcTemplate,
      UuidV7Generator uuidV7Generator) {
    this.repository = repository;
    this.ordenCompraRepository = ordenCompraRepository;
    this.facturaLineaRepository = facturaLineaRepository;
    this.jdbcTemplate = jdbcTemplate;
    this.uuidV7Generator = uuidV7Generator;
  }

  @Transactional(readOnly = true)
  public List<OrdenCompraLineaResponse> listar(UUID ordenCompraId) {
    verificarOrden(ordenCompraId);
    return repository.findAllByOrdenCompra_IdOrdenCompraOrderByNumeroLineaAsc(ordenCompraId)
        .stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<OrdenCompraLineaResponse> obtener(
      UUID ordenCompraId, UUID idOrdenCompraLinea) {
    verificarOrden(ordenCompraId);
    return repository.findByOrdenCompra_IdOrdenCompraAndIdOrdenCompraLinea(
        ordenCompraId, idOrdenCompraLinea).map(this::toResponse);
  }

  @Transactional
  public OrdenCompraLineaResponse crear(UUID ordenCompraId, OrdenCompraLineaRequest request) {
    OrdenCompra orden = obtenerOrden(ordenCompraId);
    validarRequest(ordenCompraId, request, null);
    OrdenCompraLinea linea = new OrdenCompraLinea();
    linea.setIdOrdenCompraLinea(uuidV7Generator.generate());
    aplicarRequest(linea, orden, request);
    OrdenCompraLinea guardada = repository.saveAndFlush(linea);
    recalcularTotal(orden);
    return toResponse(guardada);
  }

  @Transactional
  public Optional<OrdenCompraLineaResponse> actualizar(
      UUID ordenCompraId, UUID idOrdenCompraLinea, OrdenCompraLineaRequest request) {
    return repository.findByOrdenCompra_IdOrdenCompraAndIdOrdenCompraLinea(
        ordenCompraId, idOrdenCompraLinea).map(linea -> {
          validarRequest(ordenCompraId, request, idOrdenCompraLinea);
          OrdenCompra orden = linea.getOrdenCompra();
          aplicarRequest(linea, orden, request);
          OrdenCompraLinea guardada = repository.saveAndFlush(linea);
          recalcularTotal(orden);
          return toResponse(guardada);
        });
  }

  @Transactional
  public boolean eliminar(UUID ordenCompraId, UUID idOrdenCompraLinea) {
    return repository.findByOrdenCompra_IdOrdenCompraAndIdOrdenCompraLinea(
        ordenCompraId, idOrdenCompraLinea).map(linea -> {
          if (facturaLineaRepository.existsByOrdenCompraLineaId(idOrdenCompraLinea)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "No se puede eliminar una línea vinculada a facturas");
          }
          OrdenCompra orden = linea.getOrdenCompra();
          repository.delete(linea);
          repository.flush();
          recalcularTotal(orden);
          return true;
        }).orElse(false);
  }

  private void validarRequest(
      UUID ordenCompraId, OrdenCompraLineaRequest request, UUID idOrdenCompraLinea) {
    boolean numeroDuplicado = idOrdenCompraLinea == null
        ? repository.existsByOrdenCompra_IdOrdenCompraAndNumeroLinea(
            ordenCompraId, request.numeroLinea())
        : repository.existsByOrdenCompra_IdOrdenCompraAndNumeroLineaAndIdOrdenCompraLineaNot(
            ordenCompraId, request.numeroLinea(), idOrdenCompraLinea);
    if (numeroDuplicado) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe ese número de línea para la orden");
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
      OrdenCompraLinea linea, OrdenCompra orden, OrdenCompraLineaRequest request) {
    linea.setOrdenCompra(orden);
    linea.setNumeroLinea(request.numeroLinea());
    linea.setDescripcion(request.descripcion().trim());
    linea.setCantidadOrdenada(request.cantidadOrdenada());
    linea.setPrecioUnitario(request.precioUnitario());
    linea.setCantidadRecibida(
        request.cantidadRecibida() == null ? BigDecimal.ZERO : request.cantidadRecibida());
    linea.setCuentaContableId(request.cuentaContableId());
  }

  private void recalcularTotal(OrdenCompra orden) {
    BigDecimal total = BigDecimal.ZERO;
    for (OrdenCompraLinea linea : repository.findAllByOrdenCompra_IdOrdenCompraOrderByNumeroLineaAsc(
        orden.getIdOrdenCompra())) {
      total = total.add(linea.getCantidadOrdenada().multiply(linea.getPrecioUnitario()));
    }
    total = total.setScale(4, RoundingMode.HALF_UP);

    if (total.compareTo(MAX_ORDER_TOTAL) > 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El total de las líneas excede el máximo permitido");
    }

    orden.setMontoTotal(total);
    ordenCompraRepository.saveAndFlush(orden);
  }

  private OrdenCompra obtenerOrden(UUID ordenCompraId) {
    return ordenCompraRepository.findById(ordenCompraId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe la orden de compra indicada"));
  }

  private void verificarOrden(UUID ordenCompraId) {
    if (!ordenCompraRepository.existsById(ordenCompraId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "No existe la orden de compra indicada");
    }
  }

  private OrdenCompraLineaResponse toResponse(OrdenCompraLinea linea) {
    return new OrdenCompraLineaResponse(
        linea.getIdOrdenCompraLinea(),
        linea.getOrdenCompra().getIdOrdenCompra(),
        linea.getNumeroLinea(),
        linea.getDescripcion(),
        linea.getCantidadOrdenada(),
        linea.getPrecioUnitario(),
        linea.getCantidadRecibida(),
        linea.getCuentaContableId());
  }
}
