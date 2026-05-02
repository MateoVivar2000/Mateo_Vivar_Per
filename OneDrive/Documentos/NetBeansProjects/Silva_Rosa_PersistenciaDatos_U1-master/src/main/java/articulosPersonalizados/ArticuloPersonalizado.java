package articulosPersonalizados;

import javax.persistence.*;

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

    // Enum para demostrar ciclo de vida
    public enum EstadoArticulo {
        NUEVO, ACTIVO, DESCONTINUADO
    }

    // Callbacks del ciclo de vida JPA
    @PrePersist
    public void antesDeGuardar() {
        System.out.println(">> [JPA] Estado: NEW → MANAGED (guardando: " + nombre + ")");
        if (this.estado == null) this.estado = EstadoArticulo.NUEVO;
    }

    @PostPersist
    public void despuesDeGuardar() {
        System.out.println(">> [JPA] Entidad MANAGED con ID: " + id);
    }

    @PreUpdate
    public void antesDeActualizar() {
        System.out.println(">> [JPA] Estado: MANAGED → actualizando: " + nombre);
    }

    @PreRemove
    public void antesDeEliminar() {
        System.out.println(">> [JPA] Estado: MANAGED → REMOVED (eliminando ID: " + id + ")");
    }

    @PostLoad
    public void despuesDeCarga() {
        System.out.println(">> [JPA] Entidad cargada (MANAGED): " + nombre);
    }

    public ArticuloPersonalizado(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public ArticuloPersonalizado() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public EstadoArticulo getEstado() { return estado; }
    public void setEstado(EstadoArticulo estado) { this.estado = estado; }

    @Override
    public String toString() {
        return id + " - " + nombre + " - $" + precio + " [" + estado + "]";
    }
}