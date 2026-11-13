package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.dto.OrdenCompraLineaRequest;
import com.ejemplo.proveedor.dto.OrdenCompraLineaResponse;
import com.ejemplo.proveedor.model.OrdenCompra;
import com.ejemplo.proveedor.model.OrdenCompraLinea;
import com.ejemplo.proveedor.repository.OrdenCompraLineaRepository;
import com.ejemplo.proveedor.repository.OrdenCompraRepository;
import com.ejemplo.proveedor.repository.FacturaProveedorLineaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class OrdenCompraLineaServiceTest {

  @Test
  void crearEnlazaOrdenYRecalculaElTotal() {
    OrdenCompraLineaRepository repository = mock(OrdenCompraLineaRepository.class);
    OrdenCompraRepository ordenCompraRepository = mock(OrdenCompraRepository.class);
    FacturaProveedorLineaRepository facturaLineaRepository =
        mock(FacturaProveedorLineaRepository.class);
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    UuidV7Generator uuidV7Generator = mock(UuidV7Generator.class);
    UUID ordenId = UUID.randomUUID();
    UUID cuentaId = UUID.randomUUID();
    when(uuidV7Generator.generate()).thenReturn(UUID.randomUUID());
    OrdenCompra orden = new OrdenCompra();
    orden.setIdOrdenCompra(ordenId);
    AtomicReference<OrdenCompraLinea> lineaGuardada = new AtomicReference<>();

    when(ordenCompraRepository.findById(ordenId)).thenReturn(Optional.of(orden));
    when(repository.existsByOrdenCompra_IdOrdenCompraAndNumeroLinea(ordenId, 1))
        .thenReturn(false);
    when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(cuentaId)))
        .thenReturn(true);
    when(repository.saveAndFlush(any(OrdenCompraLinea.class))).thenAnswer(invocation -> {
      OrdenCompraLinea linea = invocation.getArgument(0);
      lineaGuardada.set(linea);
      return linea;
    });
    when(repository.findAllByOrdenCompra_IdOrdenCompraOrderByNumeroLineaAsc(ordenId))
        .thenAnswer(invocation -> List.of(lineaGuardada.get()));
    when(ordenCompraRepository.saveAndFlush(any(OrdenCompra.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    OrdenCompraLineaService service =
        new OrdenCompraLineaService(
            repository, ordenCompraRepository, facturaLineaRepository, jdbcTemplate,
            uuidV7Generator);
    OrdenCompraLineaRequest request = new OrdenCompraLineaRequest(
        1, "Producto", new BigDecimal("2.5000"), new BigDecimal("10.0000"),
        null, cuentaId);

    OrdenCompraLineaResponse creada = service.crear(ordenId, request);

    assertEquals(ordenId, creada.ordenCompraId());
    assertEquals(cuentaId, creada.cuentaContableId());
    assertEquals(BigDecimal.ZERO, creada.cantidadRecibida());
    assertEquals(new BigDecimal("25.0000"), orden.getMontoTotal());
  }
}
