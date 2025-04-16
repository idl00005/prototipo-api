package Integracion;

import Cliente.ProductoClient;
import DTO.ProductoDTO;
import Entidades.CarritoItem;
import Recursos.CarritoResource;
import Recursos.CarritoResource.AgregarProductoRequest;
import Repositorios.CarritoItemRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CarritoResourceIT {

    @InjectMock
    ProductoClient productoClient;

    @InjectMock
    CarritoItemRepository carritoItemRepository;

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testAgregarEliminarProducto() {
        // Mock del cliente de producto
        ProductoDTO productoMock = new ProductoDTO(1L, "Producto Test", BigDecimal.valueOf(100), 10);
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(productoMock);
        // Agregar producto
        AgregarProductoRequest request = new AgregarProductoRequest();
        request.productoId = 1L;
        request.cantidad = 2;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/carrito/agregar")
                .then()
                .statusCode(200)
                .body("nombreProducto", notNullValue())
                .body("cantidad", equalTo(2))
                .body("precio", notNullValue());
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testObtenerCarrito() {
        // Configura el mock del repositorio
        CarritoItem item = new CarritoItem();
        item.userId = "user"; // Debe coincidir con el userId del SecurityContext
        item.productoId = 1L;
        item.nombreProducto = "Producto Test";
        item.precio = BigDecimal.valueOf(100);
        item.cantidad = 2;

        when(carritoItemRepository.findByUserId("user")).thenReturn(List.of(item));

        // Realiza la solicitud y verifica la respuesta
        given()
                .when()
                .get("/carrito/")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].nombreProducto", equalTo("Producto Test"))
                .body("[0].cantidad", equalTo(2));
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testEliminarProducto() {
        // Mock del repositorio para la búsqueda
        CarritoItem item = new CarritoItem();
        item.userId = "user"; // Debe coincidir con el userId del SecurityContext
        item.productoId = 1L;
        item.nombreProducto = "Producto Test";
        item.precio = BigDecimal.valueOf(100);
        item.cantidad = 2;
        when(carritoItemRepository.findByUserAndProducto("user", 1L)).thenReturn(Optional.of(item));

        // Eliminar producto
        given()
                .when()
                .delete("/carrito/1")
                .then()
                .statusCode(204); // No Content
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testVaciarCarrito() {
        CarritoItem item = new CarritoItem();
        item.userId = "user";
        item.productoId = 1L;
        item.nombreProducto = "Producto Test";
        item.precio = BigDecimal.valueOf(100);
        item.cantidad = 1;
        carritoItemRepository.persist(item);

        given()
                .when()
                .delete("/carrito/")
                .then()
                .statusCode(204); // No Content
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testActualizarCantidad() {
        // Mock del cliente de producto
        ProductoDTO productoMock = new ProductoDTO(1L, "Producto Test", BigDecimal.valueOf(100), 10);
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(productoMock);

        // Mock del repositorio para la búsqueda
        CarritoItem item = new CarritoItem();
        item.userId = "user"; // Debe coincidir con el userId del SecurityContext
        item.productoId = 1L;
        item.nombreProducto = "Producto Test";
        item.precio = BigDecimal.valueOf(100);
        item.cantidad = 1;

        when(carritoItemRepository.findByUserAndProducto("user", 1L)).thenReturn(Optional.of(item));

        // Configurar la solicitud para actualizar la cantidad
        CarritoResource.ActualizarCantidadRequest request = new CarritoResource.ActualizarCantidadRequest();
        request.setCantidad(5);

        // Realizar la solicitud y verificar la respuesta
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/carrito/1")
                .then()
                .statusCode(200)
                .body("cantidad", equalTo(5));
    }

}