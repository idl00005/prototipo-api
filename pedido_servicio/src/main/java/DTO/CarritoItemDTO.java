package DTO;

import java.math.BigDecimal;

public class CarritoItemDTO{

    private Long productoId;
    private Integer cantidad;
    private BigDecimal precio;

    public CarritoItemDTO(Long productoId, Integer cantidad, BigDecimal precio) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public CarritoItemDTO() {
    }

    public Long getProductoId() {
        return productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecio() { return precio; }

}

