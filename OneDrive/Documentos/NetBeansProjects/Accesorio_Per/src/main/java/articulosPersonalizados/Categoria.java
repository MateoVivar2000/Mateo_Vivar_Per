package articulosPersonalizados;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ArticuloPersonalizado> articulos = new ArrayList<>();

    public Categoria() {}

    public Categoria(String nombre, String descripcion) {
        this.nombre      = nombre;
        this.descripcion = descripcion;
    }

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }
    public String getNombre()                       { return nombre; }
    public void setNombre(String nombre)            { this.nombre = nombre; }
    public String getDescripcion()                  { return descripcion; }
    public void setDescripcion(String descripcion)  { this.descripcion = descripcion; }
    public List<ArticuloPersonalizado> getArticulos()               { return articulos; }
    public void setArticulos(List<ArticuloPersonalizado> articulos)  { this.articulos = articulos; }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + descripcion + ")";
    }
}