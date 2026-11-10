package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.dto.VentaDetalleRequest;
import com.ejemplo.proveedor.dto.VentaDetalleResponse;
import com.ejemplo.proveedor.dto.VentaRequest;
import com.ejemplo.proveedor.dto.VentaResponse;
import com.ejemplo.proveedor.model.Cliente;
import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.model.Venta;
import com.ejemplo.proveedor.model.VentaDetalle;
import com.ejemplo.proveedor.repository.VentaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VentaService {

  private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");

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
    return repository.findWithDetailsByIdVenta(ventaId).map(this::toResponse);
  }

  @Transactional
  public VentaResponse crear(VentaRequest request) {
    Venta venta = new Venta();
    aplicarRequest(venta, request);
    Venta guardada = repository.saveAndFlush(venta);
    return cargarRespuesta(guardada.getIdVenta());
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
    BigDecimal subtotal = BigDecimal.ZERO.setScale(2);
    BigDecimal descuentoDetalles = BigDecimal.ZERO.setScale(2);
    BigDecimal iva = BigDecimal.ZERO.setScale(2);
    for (VentaDetalle detalle : detalles) {
      BigDecimal importe = BigDecimal.valueOf(detalle.getCantidad())
          .multiply(detalle.getPrecioUnitario())
          .setScale(2, RoundingMode.HALF_UP);
      if (detalle.getDescuento().compareTo(importe) > 0) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "El descuento no puede exceder el subtotal del detalle");
      }
      subtotal = subtotal.add(importe);
      descuentoDetalles = descuentoDetalles.add(detalle.getDescuento());
      iva = iva.add(importe.subtract(detalle.getDescuento())
          .multiply(new BigDecimal("0.16"))
          .setScale(2, RoundingMode.HALF_UP));
    }

    BigDecimal descuentoVenta = request.descuento() == null
        ? BigDecimal.ZERO.setScale(2)
        : request.descuento().setScale(2, RoundingMode.HALF_UP);
    BigDecimal descuento = descuentoVenta.add(descuentoDetalles).setScale(2, RoundingMode.HALF_UP);
    iva = iva.setScale(2, RoundingMode.HALF_UP);
    if (descuento.compareTo(subtotal) > 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "El descuento no puede exceder el subtotal");
    }
    BigDecimal total = subtotal.subtract(descuento).add(iva).setScale(2, RoundingMode.HALF_UP);

    if (subtotal.compareTo(MAX_AMOUNT) > 0
        || descuento.compareTo(MAX_AMOUNT) > 0
        || iva.compareTo(MAX_AMOUNT) > 0
        || total.compareTo(MAX_AMOUNT) > 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Los importes exceden el máximo permitido para una venta");
    }

    venta.setCliente(entityManager.getReference(Cliente.class, request.clienteId()));
    venta.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
    venta.setDescuento(descuento);
    venta.setIva(iva);
    venta.setTotal(total);
    if (request.estado() != null) {
      venta.setEstado(request.estado());
    }
    venta.setUsuarioAuditoria(usuarioAutenticado());
    venta.setQrCode(request.qrCode());
    venta.reemplazarDetalles(detalles);
  }

  private VentaDetalle toDetalle(VentaDetalleRequest request) {
    VentaDetalle detalle = new VentaDetalle();
    detalle.setProducto(entityManager.getReference(Producto.class, request.idProducto()));
    detalle.setCantidad(request.cantidad());
    detalle.setPrecioUnitario(request.precioUnitario());
    detalle.setDescuento(request.descuento() == null
        ? BigDecimal.ZERO.setScale(2)
        : request.descuento().setScale(2, RoundingMode.HALF_UP));
    detalle.setUsuarioAuditoria(usuarioAutenticado());
    return detalle;
  }

  private VentaResponse cargarRespuesta(Long ventaId) {
    entityManager.clear();
    return repository.findWithDetailsByIdVenta(ventaId)
        .map(this::toResponse)
        .orElseThrow(() -> new IllegalStateException("No se pudo recargar la venta guardada"));
  }

  private String usuarioAutenticado() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()
        || "anonymousUser".equals(authentication.getName())) {
      return null;
    }
    return authentication.getName();
  }

  private VentaResponse toResponse(Venta venta) {
    List<VentaDetalleResponse> detalles = venta.getDetalles().stream()
        .map(detalle -> new VentaDetalleResponse(
            detalle.getDetalleId(),
            detalle.getProducto().getIdProducto(),
            detalle.getProducto().getCodigoBarras(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.getSubtotal(),
            detalle.getDescuento(),
            detalle.getIva(),
            detalle.getTotal()))
        .toList();
    return new VentaResponse(
        venta.getIdVenta(),
        venta.getCliente().getIdCliente(),
        venta.getFechaVenta(),
        venta.getSubtotal(),
        venta.getDescuento(),
        venta.getIva(),
        venta.getTotal(),
        venta.getEstado(),
        venta.getUsuarioAuditoria(),
        venta.getFechaAuditoria(),
        venta.getQrCode(),
        detalles);
  }
}
