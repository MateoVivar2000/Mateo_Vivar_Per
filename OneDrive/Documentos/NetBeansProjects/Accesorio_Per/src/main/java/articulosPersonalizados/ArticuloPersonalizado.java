package articulosPersonalizados;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Articulos")
public class ArticuloPersonalizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private double precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoArticulo estado;

    public enum EstadoArticulo {
        NUEVO, ACTIVO, DESCONTINUADO
    }

    // Muchos artículos → una categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Muchos artículos → un proveedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    // Lado inverso del ManyToMany con Pedido
    @ManyToMany(mappedBy = "articulos", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    @PrePersist
    public void antesDeGuardar() {
        System.out.println(">> [JPA] NEW → MANAGED (guardando: " + nombre + ")");
        if (this.estado == null) this.estado = EstadoArticulo.NUEVO;
    }

    @PostPersist
    public void despuesDeGuardar() {
        System.out.println(">> [JPA] Entidad MANAGED con ID: " + id);
    }

    @PreUpdate
    public void antesDeActualizar() {
        System.out.println(">> [JPA] MANAGED → actualizando: " + nombre);
    }

    @PreRemove
    public void antesDeEliminar() {
        System.out.println(">> [JPA] MANAGED → REMOVED (eliminando ID: " + id + ")");
    }

    @PostLoad
    public void despuesDeCarga() {
        System.out.println(">> [JPA] Entidad cargada (MANAGED): " + nombre);
    }

    public ArticuloPersonalizado() {}

    public ArticuloPersonalizado(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public ArticuloPersonalizado(String nombre, double precio,
                                  Categoria categoria, Proveedor proveedor) {
        this.nombre    = nombre;
        this.precio    = precio;
        this.categoria = categoria;
        this.proveedor = proveedor;
    }

    public int getId()                                    { return id; }
    public void setId(int id)                             { this.id = id; }
    public String getNombre()                             { return nombre; }
    public void setNombre(String nombre)                  { this.nombre = nombre; }
    public double getPrecio()                             { return precio; }
    public void setPrecio(double precio)                  { this.precio = precio; }
    public EstadoArticulo getEstado()                     { return estado; }
    public void setEstado(EstadoArticulo estado)          { this.estado = estado; }
    public Categoria getCategoria()                       { return categoria; }
    public void setCategoria(Categoria categoria)         { this.categoria = categoria; }
    public Proveedor getProveedor()                       { return proveedor; }
    public void setProveedor(Proveedor proveedor)         { this.proveedor = proveedor; }
    public List<Pedido> getPedidos()                      { return pedidos; }
    public void setPedidos(List<Pedido> pedidos)          { this.pedidos = pedidos; }

    @Override
    public String toString() {
        return id + " - " + nombre + " - $" + precio + " [" + estado + "]";
    }
}