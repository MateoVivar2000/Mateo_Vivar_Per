package articulosPersonalizados;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    public Cliente() {}

    public Cliente(String nombre, String apellido, String email, String telefono) {
        this.nombre   = nombre;
        this.apellido = apellido;
        this.email    = email;
        this.telefono = telefono;
    }

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }
    public String getNombre()                    { return nombre; }
    public void setNombre(String nombre)         { this.nombre = nombre; }
    public String getApellido()                  { return apellido; }
    public void setApellido(String apellido)     { this.apellido = apellido; }
    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }
    public String getTelefono()                  { return telefono; }
    public void setTelefono(String telefono)     { this.telefono = telefono; }
    public List<Pedido> getPedidos()             { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }

    @Override
    public String toString() {
        return id + " - " + nombre + " " + apellido + " | " + email;
    }
}