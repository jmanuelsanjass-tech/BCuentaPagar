package com.ejemplo.proveedor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ejemplo.proveedor.controller.ProductoController;
import com.ejemplo.proveedor.dto.ProductoBusquedaResponse;
import com.ejemplo.proveedor.model.Producto;
import com.ejemplo.proveedor.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;

class ProductoServiceTest {

  @Test
  void crearAplicaValoresPorDefectoDeInventario() {
    ProductoRepository repository = mock(ProductoRepository.class);
    when(repository.saveAndFlush(any(Producto.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    ProductoService service = new ProductoService(repository);

    Producto datos = new Producto();
    datos.setNombre("Arroz");
    datos.setPrecio(new BigDecimal("25.50"));
    datos.setCodigoBarras("7501234567890");

    Producto creado = service.crear(datos);

    assertEquals("Abarrotes", creado.getCategoria());
    assertEquals(0, creado.getStock());
    assertEquals("activo", creado.getEstado());
  }

  @Test
  void buscarPredictivoRecortaElNombreYLimitaResultados() {
    ProductoRepository repository = mock(ProductoRepository.class);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    ProductoBusquedaResponse producto = new ProductoBusquedaResponse(
        1L, "7501234567890", "Café", "Café molido", "Abarrotes",
        new BigDecimal("89.50"), 7);
    when(repository.buscarPredictivo(eq("cafe"), any(Pageable.class)))
        .thenReturn(List.of(producto));
    ProductoService service = new ProductoService(repository);

    List<ProductoBusquedaResponse> resultados = service.buscarPredictivo("  cafe  ");

    assertEquals(List.of(producto), resultados);
    verify(repository).buscarPredictivo(eq("cafe"), pageableCaptor.capture());
    assertEquals(10, pageableCaptor.getValue().getPageSize());
  }

  @Test
  void buscarPredictivoAceptaElParametroLegadoTermino() {
    ProductoService service = mock(ProductoService.class);
    ProductoController controller = new ProductoController(service);
    when(service.buscarPredictivo("AR")).thenReturn(List.of());

    var response = controller.buscarPredictivo(null, "AR");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(service).buscarPredictivo("AR");
  }
}
