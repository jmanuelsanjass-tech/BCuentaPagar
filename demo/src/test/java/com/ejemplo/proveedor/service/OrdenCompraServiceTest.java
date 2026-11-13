package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.dto.OrdenCompraRequest;
import com.ejemplo.proveedor.dto.OrdenCompraResponse;
import com.ejemplo.proveedor.model.OrdenCompra;
import com.ejemplo.proveedor.model.Proveedor;
import com.ejemplo.proveedor.repository.OrdenCompraRepository;
import com.ejemplo.proveedor.repository.ProveedorRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorLineaRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorRepository;
import com.ejemplo.proveedor.repository.RecepcionMercanciaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class OrdenCompraServiceTest {

  @Test
  void crearAplicaValoresPorDefectoYEnlazaProveedor() {
    OrdenCompraRepository repository = mock(OrdenCompraRepository.class);
    ProveedorRepository proveedorRepository = mock(ProveedorRepository.class);
    RecepcionMercanciaRepository recepcionRepository = mock(RecepcionMercanciaRepository.class);
    FacturaProveedorRepository facturaRepository = mock(FacturaProveedorRepository.class);
    FacturaProveedorLineaRepository facturaLineaRepository =
        mock(FacturaProveedorLineaRepository.class);
    UuidV7Generator uuidV7Generator = mock(UuidV7Generator.class);
    Proveedor proveedor = new Proveedor();
    proveedor.setIdProveedor(4L);
    UUID empresaId = UUID.randomUUID();
    UUID solicitadoPor = UUID.randomUUID();
    when(uuidV7Generator.generate()).thenReturn(UUID.randomUUID());

    when(repository.existsByEmpresaIdAndNumeroOc(empresaId, "OC-100")).thenReturn(false);
    when(proveedorRepository.existsById(4L)).thenReturn(true);
    when(proveedorRepository.getReferenceById(4L)).thenReturn(proveedor);
    when(repository.saveAndFlush(any(OrdenCompra.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    OrdenCompraService service =
        new OrdenCompraService(
            repository, proveedorRepository, recepcionRepository, facturaRepository,
            facturaLineaRepository, uuidV7Generator);
    OrdenCompraRequest request = new OrdenCompraRequest(
        empresaId, " OC-100 ", 4L, LocalDate.parse("2026-11-12"), null, "MXN",
        null, solicitadoPor, null, null);

    OrdenCompraResponse creada = service.crear(request);

    assertEquals("OC-100", creada.numeroOc());
    assertEquals(4L, creada.proveedorId());
    assertEquals("BORRADOR", creada.estado());
    assertEquals("0", creada.montoTotal().toPlainString());
    assertEquals(solicitadoPor, creada.solicitadoPor());
  }

  @Test
  void rechazaQueElSolicitanteApruebeSuPropiaOrden() {
    OrdenCompraRepository repository = mock(OrdenCompraRepository.class);
    ProveedorRepository proveedorRepository = mock(ProveedorRepository.class);
    RecepcionMercanciaRepository recepcionRepository = mock(RecepcionMercanciaRepository.class);
    FacturaProveedorRepository facturaRepository = mock(FacturaProveedorRepository.class);
    FacturaProveedorLineaRepository facturaLineaRepository =
        mock(FacturaProveedorLineaRepository.class);
    UuidV7Generator uuidV7Generator = mock(UuidV7Generator.class);
    UUID solicitadoPor = UUID.randomUUID();
    OrdenCompraService service =
        new OrdenCompraService(
            repository, proveedorRepository, recepcionRepository, facturaRepository,
            facturaLineaRepository, uuidV7Generator);
    OrdenCompraRequest request = new OrdenCompraRequest(
        UUID.randomUUID(), "OC-100", 4L, LocalDate.parse("2026-11-12"), "APROBADA",
        "MXN", BigDecimal.TEN, solicitadoPor, solicitadoPor, null);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> service.crear(request));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }
}
