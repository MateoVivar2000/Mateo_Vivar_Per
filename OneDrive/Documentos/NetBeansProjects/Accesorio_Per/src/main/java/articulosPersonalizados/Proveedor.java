package articulosPersonalizados;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ArticuloPersonalizado> articulos = new ArrayList<>();

    public Proveedor() {}

    public Proveedor(String nombre, String telefono, String email) {
        this.nombre   = nombre;
        this.telefono = telefono;
        this.email    = email;
    }

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }
    public String getNombre()                       { return nombre; }
    public void setNombre(String nombre)            { this.nombre = nombre; }
    public String getTelefono()                     { return telefono; }
    public void setTelefono(String telefono)        { this.telefono = telefono; }
    public String getEmail()                        { return email; }
    public void setEmail(String email)              { this.email = email; }
    public List<ArticuloPersonalizado> getArticulos()               { return articulos; }
    public void setArticulos(List<ArticuloPersonalizado> articulos)  { this.articulos = articulos; }

    @Override
    public String toString() {
        return id + " - " + nombre + " | Tel: " + telefono + " | Email: " + email;
    }
}