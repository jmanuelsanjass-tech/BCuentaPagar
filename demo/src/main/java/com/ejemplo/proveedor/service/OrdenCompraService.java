package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.OrdenCompraRequest;
import com.ejemplo.proveedor.dto.OrdenCompraResponse;
import com.ejemplo.proveedor.model.OrdenCompra;
import com.ejemplo.proveedor.repository.OrdenCompraRepository;
import com.ejemplo.proveedor.repository.ProveedorRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorLineaRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorRepository;
import com.ejemplo.proveedor.repository.RecepcionMercanciaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdenCompraService {

  private final OrdenCompraRepository repository;
  private final ProveedorRepository proveedorRepository;
  private final RecepcionMercanciaRepository recepcionRepository;
  private final FacturaProveedorRepository facturaRepository;
  private final FacturaProveedorLineaRepository facturaLineaRepository;
  private final UuidV7Generator uuidV7Generator;

  public OrdenCompraService(
      OrdenCompraRepository repository,
      ProveedorRepository proveedorRepository,
      RecepcionMercanciaRepository recepcionRepository,
      FacturaProveedorRepository facturaRepository,
      FacturaProveedorLineaRepository facturaLineaRepository,
      UuidV7Generator uuidV7Generator) {
    this.repository = repository;
    this.proveedorRepository = proveedorRepository;
    this.recepcionRepository = recepcionRepository;
    this.facturaRepository = facturaRepository;
    this.facturaLineaRepository = facturaLineaRepository;
    this.uuidV7Generator = uuidV7Generator;
  }

  @Transactional(readOnly = true)
  public List<OrdenCompraResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<OrdenCompraResponse> obtener(UUID idOrdenCompra) {
    return repository.findById(idOrdenCompra).map(this::toResponse);
  }

  @Transactional
  public OrdenCompraResponse crear(OrdenCompraRequest request) {
    validarRequest(request, null);
    OrdenCompra orden = new OrdenCompra();
    orden.setIdOrdenCompra(uuidV7Generator.generate());
    aplicarRequest(orden, request);
    return toResponse(repository.saveAndFlush(orden));
  }

  @Transactional
  public Optional<OrdenCompraResponse> actualizar(
      UUID idOrdenCompra, OrdenCompraRequest request) {
    return repository.findById(idOrdenCompra).map(orden -> {
      validarRequest(request, idOrdenCompra);
      aplicarRequest(orden, request);
      return toResponse(repository.saveAndFlush(orden));
    });
  }

  @Transactional
  public boolean eliminar(UUID idOrdenCompra) {
    return repository.findById(idOrdenCompra).map(orden -> {
      if (recepcionRepository.existsByOrdenCompra_IdOrdenCompra(idOrdenCompra)
          || facturaRepository.existsByOrdenCompra_IdOrdenCompra(idOrdenCompra)
          || facturaLineaRepository.existsReferencingOrdenCompra(idOrdenCompra)) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "No se puede eliminar una orden con recepciones o facturas");
      }
      repository.delete(orden);
      return true;
    }).orElse(false);
  }

  private void validarRequest(OrdenCompraRequest request, UUID idOrdenCompra) {
    if (request.aprobadoPor() != null
        && request.aprobadoPor().equals(request.solicitadoPor())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El solicitante no puede aprobar su propia orden");
    }

    String numeroOc = request.numeroOc().trim();
    boolean numeroDuplicado = idOrdenCompra == null
        ? repository.existsByEmpresaIdAndNumeroOc(request.empresaId(), numeroOc)
        : repository.existsByEmpresaIdAndNumeroOcAndIdOrdenCompraNot(
            request.empresaId(), numeroOc, idOrdenCompra);
    if (numeroDuplicado) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe ese número de orden para la empresa");
    }

    if (!proveedorRepository.existsById(request.proveedorId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el proveedor indicado");
    }
  }

  private void aplicarRequest(OrdenCompra orden, OrdenCompraRequest request) {
    orden.setEmpresaId(request.empresaId());
    orden.setNumeroOc(request.numeroOc().trim());
    orden.setProveedor(proveedorRepository.getReferenceById(request.proveedorId()));
    orden.setFechaOrden(request.fechaOrden());
    orden.setEstado(request.estado() == null ? "BORRADOR" : request.estado());
    orden.setMoneda(request.moneda());
    orden.setMontoTotal(
        request.montoTotal() == null ? BigDecimal.ZERO : request.montoTotal());
    orden.setSolicitadoPor(request.solicitadoPor());
    orden.setAprobadoPor(request.aprobadoPor());
    orden.setAprobadoEn(request.aprobadoEn());
  }

  private OrdenCompraResponse toResponse(OrdenCompra orden) {
    return new OrdenCompraResponse(
        orden.getIdOrdenCompra(),
        orden.getEmpresaId(),
        orden.getNumeroOc(),
        orden.getProveedor().getIdProveedor(),
        orden.getFechaOrden(),
        orden.getEstado(),
        orden.getMoneda(),
        orden.getMontoTotal(),
        orden.getSolicitadoPor(),
        orden.getAprobadoPor(),
        orden.getAprobadoEn(),
        orden.getCreadoEn());
  }
}
