package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.FacturaProveedorRequest;
import com.ejemplo.proveedor.dto.FacturaProveedorResponse;
import com.ejemplo.proveedor.model.FacturaProveedor;
import com.ejemplo.proveedor.model.OrdenCompra;
import com.ejemplo.proveedor.model.Proveedor;
import com.ejemplo.proveedor.model.RecepcionMercancia;
import com.ejemplo.proveedor.repository.FacturaProveedorRepository;
import com.ejemplo.proveedor.repository.OrdenCompraRepository;
import com.ejemplo.proveedor.repository.PagoRepository;
import com.ejemplo.proveedor.repository.ProveedorRepository;
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
public class FacturaProveedorService {

  private final FacturaProveedorRepository repository;
  private final ProveedorRepository proveedorRepository;
  private final OrdenCompraRepository ordenCompraRepository;
  private final RecepcionMercanciaRepository recepcionRepository;
  private final PagoRepository pagoRepository;
  private final UuidV7Generator uuidV7Generator;

  public FacturaProveedorService(
      FacturaProveedorRepository repository,
      ProveedorRepository proveedorRepository,
      OrdenCompraRepository ordenCompraRepository,
      RecepcionMercanciaRepository recepcionRepository,
      PagoRepository pagoRepository,
      UuidV7Generator uuidV7Generator) {
    this.repository = repository;
    this.proveedorRepository = proveedorRepository;
    this.ordenCompraRepository = ordenCompraRepository;
    this.recepcionRepository = recepcionRepository;
    this.pagoRepository = pagoRepository;
    this.uuidV7Generator = uuidV7Generator;
  }

  @Transactional(readOnly = true)
  public List<FacturaProveedorResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<FacturaProveedorResponse> obtener(UUID id) {
    return repository.findById(id).map(this::toResponse);
  }

  @Transactional
  public FacturaProveedorResponse crear(FacturaProveedorRequest request) {
    Relaciones relaciones = validarRequest(request, null);
    FacturaProveedor factura = new FacturaProveedor();
    factura.setId(uuidV7Generator.generate());
    aplicarRequest(factura, request, relaciones);
    return toResponse(repository.saveAndFlush(factura));
  }

  @Transactional
  public Optional<FacturaProveedorResponse> actualizar(
      UUID id, FacturaProveedorRequest request) {
    return repository.findById(id).map(factura -> {
      Relaciones relaciones = validarRequest(request, id);
      if (request.montoTotal().compareTo(factura.getMontoPagado()) < 0) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "El total no puede ser menor que el monto pagado");
      }
      if (pagoRepository.existsByFactura_Id(id)
          && (!request.empresaId().equals(factura.getEmpresaId())
              || !request.proveedorId().equals(factura.getProveedor().getIdProveedor())
              || !request.moneda().equals(factura.getMoneda()))) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "No se puede cambiar empresa, proveedor o moneda con pagos");
      }
      if ("PAGADA".equals(request.estado())
          && factura.getMontoPagado().compareTo(request.montoTotal()) != 0) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Solo una factura pagada en su totalidad puede estar PAGADA");
      }
      aplicarRequest(factura, request, relaciones);
      return toResponse(repository.saveAndFlush(factura));
    });
  }

  @Transactional
  public boolean eliminar(UUID id) {
    return repository.findById(id).map(factura -> {
      if (pagoRepository.existsByFactura_Id(id)) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "No se puede eliminar una factura vinculada a pagos");
      }
      repository.delete(factura);
      return true;
    }).orElse(false);
  }

  private Relaciones validarRequest(FacturaProveedorRequest request, UUID id) {
    if (request.fechaVencimiento().isBefore(request.fechaFactura())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "La fecha de vencimiento no puede preceder a la fecha factura");
    }
    if (request.montoNeto().add(request.montoImpuestos()).compareTo(request.montoTotal()) != 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El total debe ser igual al neto más impuestos");
    }
    if (id == null && "PAGADA".equals(request.estado())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El estado PAGADA se asigna al confirmar el pago total");
    }
    boolean numeroDuplicado = id == null
        ? repository.existsByEmpresaIdAndProveedor_IdProveedorAndNumeroFactura(
            request.empresaId(), request.proveedorId(), request.numeroFactura().trim())
        : repository.existsByEmpresaIdAndProveedor_IdProveedorAndNumeroFacturaAndIdNot(
            request.empresaId(), request.proveedorId(), request.numeroFactura().trim(), id);
    if (numeroDuplicado) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe ese número de factura para el proveedor y empresa");
    }

    Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No existe el proveedor indicado"));
    OrdenCompra orden = request.ordenCompraId() == null
        ? null
        : ordenCompraRepository.findById(request.ordenCompraId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la orden de compra indicada"));
    RecepcionMercancia recepcion = request.recepcionId() == null
        ? null
        : recepcionRepository.findById(request.recepcionId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la recepción indicada"));

    if (orden != null && (!orden.getEmpresaId().equals(request.empresaId())
        || !orden.getProveedor().getIdProveedor().equals(proveedor.getIdProveedor())
        || !orden.getMoneda().equals(request.moneda()))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "La orden no coincide con la empresa, proveedor o moneda");
    }
    if (recepcion != null && (!recepcion.getEmpresaId().equals(request.empresaId())
        || (orden != null && !recepcion.getOrdenCompra().getIdOrdenCompra()
            .equals(orden.getIdOrdenCompra())))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "La recepción no corresponde a la empresa u orden indicada");
    }
    if (recepcion != null && orden == null) {
      orden = recepcion.getOrdenCompra();
      if (!orden.getProveedor().getIdProveedor().equals(proveedor.getIdProveedor())
          || !orden.getMoneda().equals(request.moneda())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "La recepción no coincide con el proveedor o moneda");
      }
    }
    return new Relaciones(proveedor, orden, recepcion);
  }

  private void aplicarRequest(
      FacturaProveedor factura,
      FacturaProveedorRequest request,
      Relaciones relaciones) {
    factura.setEmpresaId(request.empresaId());
    factura.setNumeroFactura(request.numeroFactura().trim());
    factura.setProveedor(relaciones.proveedor());
    factura.setFechaFactura(request.fechaFactura());
    factura.setFechaVencimiento(request.fechaVencimiento());
    factura.setMoneda(request.moneda());
    factura.setOrdenCompra(relaciones.orden());
    factura.setRecepcion(relaciones.recepcion());
    factura.setMontoNeto(request.montoNeto());
    factura.setMontoImpuestos(request.montoImpuestos());
    factura.setMontoTotal(request.montoTotal());
    factura.setEstado(request.estado() == null ? "RECIBIDA" : request.estado());
    factura.setEstadoConciliacion(
        request.estadoConciliacion() == null ? "PENDIENTE" : request.estadoConciliacion());
    factura.setAprobadoPagoPor(request.aprobadoPagoPor());
    factura.setAprobadoPagoEn(request.aprobadoPagoEn());
    factura.setCreadoPor(request.creadoPor());
  }

  private FacturaProveedorResponse toResponse(FacturaProveedor factura) {
    return new FacturaProveedorResponse(
        factura.getId(),
        factura.getEmpresaId(),
        factura.getNumeroFactura(),
        factura.getProveedor().getIdProveedor(),
        factura.getFechaFactura(),
        factura.getFechaVencimiento(),
        factura.getMoneda(),
        factura.getOrdenCompra() == null ? null : factura.getOrdenCompra().getIdOrdenCompra(),
        factura.getRecepcion() == null ? null : factura.getRecepcion().getId(),
        factura.getMontoNeto(),
        factura.getMontoImpuestos(),
        factura.getMontoTotal(),
        factura.getMontoPagado(),
        factura.getSaldoPendiente(),
        factura.getEstado(),
        factura.getEstadoConciliacion(),
        factura.getAprobadoPagoPor(),
        factura.getAprobadoPagoEn(),
        factura.getCreadoEn(),
        factura.getCreadoPor());
  }

  private record Relaciones(
      Proveedor proveedor, OrdenCompra orden, RecepcionMercancia recepcion) {}
}
