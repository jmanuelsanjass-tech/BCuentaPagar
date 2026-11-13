package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.PagoRequest;
import com.ejemplo.proveedor.dto.PagoResponse;
import com.ejemplo.proveedor.model.FacturaProveedor;
import com.ejemplo.proveedor.model.Pago;
import com.ejemplo.proveedor.model.Proveedor;
import com.ejemplo.proveedor.repository.FacturaProveedorRepository;
import com.ejemplo.proveedor.repository.PagoRepository;
import com.ejemplo.proveedor.repository.ProveedorRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PagoService {

  private final PagoRepository repository;
  private final ProveedorRepository proveedorRepository;
  private final FacturaProveedorRepository facturaRepository;
  private final FacturaProveedorTotalesService totalesService;
  private final UuidV7Generator uuidV7Generator;

  public PagoService(
      PagoRepository repository,
      ProveedorRepository proveedorRepository,
      FacturaProveedorRepository facturaRepository,
      FacturaProveedorTotalesService totalesService,
      UuidV7Generator uuidV7Generator) {
    this.repository = repository;
    this.proveedorRepository = proveedorRepository;
    this.facturaRepository = facturaRepository;
    this.totalesService = totalesService;
    this.uuidV7Generator = uuidV7Generator;
  }

  @Transactional(readOnly = true)
  public List<PagoResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<PagoResponse> obtener(UUID id) {
    return repository.findById(id).map(this::toResponse);
  }

  @Transactional
  public PagoResponse crear(PagoRequest request) {
    Relaciones relaciones = validarRequest(request, null);
    Pago pago = new Pago();
    pago.setId(uuidV7Generator.generate());
    aplicarRequest(pago, request, relaciones);
    Pago guardado = repository.saveAndFlush(pago);
    recalcularFacturas(relaciones.factura(), null);
    return toResponse(guardado);
  }

  @Transactional
  public Optional<PagoResponse> actualizar(UUID id, PagoRequest request) {
    return repository.findById(id).map(pago -> {
      UUID facturaAnteriorId =
          pago.getFactura() == null ? null : pago.getFactura().getId();
      Relaciones relaciones = validarRequest(request, id);
      aplicarRequest(pago, request, relaciones);
      Pago guardado = repository.saveAndFlush(pago);
      recalcularFacturas(relaciones.factura(), facturaAnteriorId);
      return toResponse(guardado);
    });
  }

  @Transactional
  public boolean eliminar(UUID id) {
    return repository.findById(id).map(pago -> {
      UUID facturaId = pago.getFactura() == null ? null : pago.getFactura().getId();
      repository.delete(pago);
      repository.flush();
      if (facturaId != null) {
        totalesService.recalcularPagos(facturaId);
      }
      return true;
    }).orElse(false);
  }

  private Relaciones validarRequest(PagoRequest request, UUID id) {
    if (request.ejecutadoPor() != null
        && request.ejecutadoPor().equals(request.aprobadoPor())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Quien aprueba el pago no puede ejecutarlo");
    }

    boolean numeroDuplicado = id == null
        ? repository.existsByEmpresaIdAndNumeroPago(
            request.empresaId(), request.numeroPago().trim())
        : repository.existsByEmpresaIdAndNumeroPagoAndIdNot(
            request.empresaId(), request.numeroPago().trim(), id);
    if (numeroDuplicado) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe ese número de pago para la empresa");
    }

    Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe el proveedor indicado"));
    FacturaProveedor factura = request.facturaId() == null
        ? null
        : facturaRepository.findById(request.facturaId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la factura indicada"));
    if (factura != null && (!factura.getEmpresaId().equals(request.empresaId())
        || !factura.getProveedor().getIdProveedor().equals(proveedor.getIdProveedor())
        || !factura.getMoneda().equals(request.moneda()))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "La factura no coincide con la empresa, proveedor o moneda");
    }
    if (factura != null && "CANCELADA".equals(factura.getEstado())
        && "CONFIRMADO".equals(request.estado())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "No se puede confirmar un pago de una factura cancelada");
    }
    return new Relaciones(proveedor, factura);
  }

  private void aplicarRequest(Pago pago, PagoRequest request, Relaciones relaciones) {
    pago.setEmpresaId(request.empresaId());
    pago.setNumeroPago(request.numeroPago().trim());
    pago.setProveedor(relaciones.proveedor());
    pago.setFactura(relaciones.factura());
    pago.setFechaPago(request.fechaPago());
    pago.setMonto(request.monto());
    pago.setMoneda(request.moneda());
    pago.setMetodoPago(request.metodoPago());
    pago.setReferencia(request.referencia());
    pago.setEstado(request.estado() == null ? "PENDIENTE" : request.estado());
    pago.setAprobadoPor(request.aprobadoPor());
    pago.setEjecutadoPor(request.ejecutadoPor());
  }

  private void recalcularFacturas(FacturaProveedor nueva, UUID anteriorId) {
    UUID nuevaId = nueva == null ? null : nueva.getId();
    if (anteriorId != null && !anteriorId.equals(nuevaId)) {
      totalesService.recalcularPagos(anteriorId);
    }
    if (nuevaId != null) {
      totalesService.recalcularPagos(nuevaId);
    }
  }

  private PagoResponse toResponse(Pago pago) {
    return new PagoResponse(
        pago.getId(),
        pago.getEmpresaId(),
        pago.getNumeroPago(),
        pago.getProveedor().getIdProveedor(),
        pago.getFactura() == null ? null : pago.getFactura().getId(),
        pago.getFechaPago(),
        pago.getMonto(),
        pago.getMoneda(),
        pago.getMetodoPago(),
        pago.getReferencia(),
        pago.getEstado(),
        pago.getAprobadoPor(),
        pago.getEjecutadoPor(),
        pago.getCreadoEn());
  }

  private record Relaciones(Proveedor proveedor, FacturaProveedor factura) {}
}
