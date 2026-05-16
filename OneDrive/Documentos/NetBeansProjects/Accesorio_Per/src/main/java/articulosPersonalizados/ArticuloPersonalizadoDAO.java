package articulosPersonalizados;

import java.util.Collections;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ArticuloPersonalizadoDAO {

    private SessionFactory factory;

    public ArticuloPersonalizadoDAO() {
        try {
            factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Error al iniciar Hibernate: " + e.getMessage());
        }
    }

    // CREATE
    public void guardar(ArticuloPersonalizado a) {
        Session session = factory.openSession();
        try {
            session.beginTransaction();
            session.save(a);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("Error al guardar: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    // READ - listar todos
    public List<ArticuloPersonalizado> listar() {
        Session session = factory.openSession();
        try {
            return session.createQuery(
                "FROM ArticuloPersonalizado ORDER BY nombre ASC",
                ArticuloPersonalizado.class
            ).list();
        } catch (Exception e) {
            System.err.println("Error al listar: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    // JPQL - buscar por nombre
    public List<ArticuloPersonalizado> buscarPorNombre(String nombre) {
        Session session = factory.openSession();
        try {
            return session.createQuery(
                "FROM ArticuloPersonalizado a WHERE a.nombre LIKE :nombre",
                ArticuloPersonalizado.class
            ).setParameter("nombre", "%" + nombre + "%").list();
        } catch (Exception e) {
            System.err.println("Error al buscar: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    // JPQL - filtrar por precio máximo
    public List<ArticuloPersonalizado> filtrarPorPrecio(double precioMax) {
        Session session = factory.openSession();
        try {
            return session.createQuery(
                "FROM ArticuloPersonalizado a WHERE a.precio <= :precio ORDER BY a.precio ASC",
                ArticuloPersonalizado.class
            ).setParameter("precio", precioMax).list();
        } catch (Exception e) {
            System.err.println("Error al filtrar: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    // JPQL - contar artículos
    public long contarArticulos() {
        Session session = factory.openSession();
        try {
            return session.createQuery(
                "SELECT COUNT(a) FROM ArticuloPersonalizado a", Long.class
            ).uniqueResult();
        } catch (Exception e) {
            System.err.println("Error al contar: " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }

    // UPDATE
    public void actualizar(ArticuloPersonalizado a) {
        Session session = factory.openSession();
        try {
            session.beginTransaction();
            session.update(a);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("Error al actualizar: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    // DELETE
    public void eliminar(int id) {
        Session session = factory.openSession();
        try {
            session.beginTransaction();
            ArticuloPersonalizado a = session.get(ArticuloPersonalizado.class, id);
            if (a != null) session.delete(a);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("Error al eliminar: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public void cerrar() {
        if (factory != null) factory.close();
    }
    public ArticuloPersonalizado buscarPorId(int id) {
        Session session = factory.openSession();
        try {
            return session.get(ArticuloPersonalizado.class, id);
        } catch (Exception e) {
            System.err.println("Error al buscar por id: " + e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }
}