package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.VentaDetalleRequest;
import com.ejemplo.proveedor.dto.VentaDetalleResponse;
import com.ejemplo.proveedor.dto.VentaRequest;
import com.ejemplo.proveedor.dto.VentaResponse;
import com.ejemplo.proveedor.model.Venta;
import com.ejemplo.proveedor.model.VentaDetalle;
import com.ejemplo.proveedor.repository.VentaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

  private static final BigDecimal MAX_TOTAL = new BigDecimal("9999999999.99");

  private final VentaRepository repository;

  @PersistenceContext
  private EntityManager entityManager;

  public VentaService(VentaRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<VentaResponse> listar() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public Optional<VentaResponse> obtener(Long ventaId) {
    return repository.findWithDetailsByVentaId(ventaId).map(this::toResponse);
  }

  @Transactional
  public VentaResponse crear(VentaRequest request) {
    Venta venta = new Venta();
    aplicarRequest(venta, request);
    Venta guardada = repository.saveAndFlush(venta);
    return cargarRespuesta(guardada.getVentaId());
  }

  @Transactional
  public Optional<VentaResponse> actualizar(Long ventaId, VentaRequest request) {
    return repository.findById(ventaId).map(venta -> {
      aplicarRequest(venta, request);
      repository.saveAndFlush(venta);
      return cargarRespuesta(ventaId);
    });
  }

  @Transactional
  public boolean eliminar(Long ventaId) {
    return repository.findById(ventaId).map(venta -> {
      repository.delete(venta);
      return true;
    }).orElse(false);
  }

  private void aplicarRequest(Venta venta, VentaRequest request) {
    List<VentaDetalle> detalles = request.detalles().stream()
        .map(this::toDetalle)
        .toList();
    BigDecimal total = BigDecimal.ZERO;
    for (VentaDetalle detalle : detalles) {
      BigDecimal importe = detalle.getCantidad()
          .multiply(detalle.getPrecioUnitario())
          .setScale(2, RoundingMode.HALF_UP);
      total = total.add(importe);
    }

    if (total.compareTo(MAX_TOTAL) > 0) {
      throw new IllegalArgumentException("El total excede el máximo permitido para una venta");
    }

    venta.setClienteId(request.clienteId());
    venta.setTotal(total);
    if (request.estado() != null) {
      venta.setEstado(request.estado());
    }
    if (request.formaPago() != null) {
      venta.setFormaPago(request.formaPago());
    }
    venta.reemplazarDetalles(detalles);
  }

  private VentaDetalle toDetalle(VentaDetalleRequest request) {
    VentaDetalle detalle = new VentaDetalle();
    detalle.setCodigoBarras(request.codigoBarras());
    detalle.setCantidad(request.cantidad());
    detalle.setPrecioUnitario(request.precioUnitario());
    return detalle;
  }

  private VentaResponse cargarRespuesta(Long ventaId) {
    entityManager.clear();
    return repository.findWithDetailsByVentaId(ventaId)
        .map(this::toResponse)
        .orElseThrow(() -> new IllegalStateException("No se pudo recargar la venta guardada"));
  }

  private VentaResponse toResponse(Venta venta) {
    List<VentaDetalleResponse> detalles = venta.getDetalles().stream()
        .map(detalle -> new VentaDetalleResponse(
            detalle.getDetalleId(),
            detalle.getCodigoBarras(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.getImporte()))
        .toList();
    return new VentaResponse(
        venta.getVentaId(),
        venta.getClienteId(),
        venta.getFechaVenta(),
        venta.getTotal(),
        venta.getEstado(),
        venta.getFormaPago(),
        venta.getUsuarioRegistro(),
        detalles);
  }
}
