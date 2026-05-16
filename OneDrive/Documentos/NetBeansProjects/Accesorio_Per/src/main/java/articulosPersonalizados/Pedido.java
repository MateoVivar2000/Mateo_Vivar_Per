package articulosPersonalizados;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "total", nullable = false)
    private double total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido")
    private EstadoPedido estadoPedido;

    public enum EstadoPedido {
        PENDIENTE, EN_PROCESO, ENTREGADO, CANCELADO
    }

    // Muchos pedidos → un cliente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Muchos pedidos ↔ muchos artículos
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Pedido_Articulos",
        joinColumns        = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "articulo_id")
    )
    private List<ArticuloPersonalizado> articulos = new ArrayList<>();

    public Pedido() {}

    public Pedido(LocalDate fecha, double total, EstadoPedido estadoPedido, Cliente cliente) {
        this.fecha        = fecha;
        this.total        = total;
        this.estadoPedido = estadoPedido;
        this.cliente      = cliente;
    }

    public int getId()                                           { return id; }
    public void setId(int id)                                    { this.id = id; }
    public LocalDate getFecha()                                  { return fecha; }
    public void setFecha(LocalDate fecha)                        { this.fecha = fecha; }
    public double getTotal()                                     { return total; }
    public void setTotal(double total)                           { this.total = total; }
    public EstadoPedido getEstadoPedido()                        { return estadoPedido; }
    public void setEstadoPedido(EstadoPedido e)                  { this.estadoPedido = e; }
    public Cliente getCliente()                                  { return cliente; }
    public void setCliente(Cliente cliente)                      { this.cliente = cliente; }
    public List<ArticuloPersonalizado> getArticulos()            { return articulos; }
    public void setArticulos(List<ArticuloPersonalizado> arts)   { this.articulos = arts; }

    @Override
    public String toString() {
        return id + " | Fecha: " + fecha + " | Total: $" + total + " | Estado: " + estadoPedido;
    }
}