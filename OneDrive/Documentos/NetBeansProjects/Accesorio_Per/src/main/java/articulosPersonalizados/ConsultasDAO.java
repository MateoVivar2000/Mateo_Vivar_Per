package articulosPersonalizados;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.Collections;
import java.util.List;

public class ConsultasDAO {

    private SessionFactory factory;

    public ConsultasDAO() {
        try {
            factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Error al iniciar Hibernate: " + e.getMessage());
        }
    }

    // ── CONSULTA 1: Artículos por categoría, filtro de precio, ordenados ──
    public List<ArticuloPersonalizado> consultaArticulosPorCategoria(
            String nombreCategoria, double precioMax) {
        Session session = factory.openSession();
        try {
            String jpql =
                "SELECT a FROM ArticuloPersonalizado a " +
                "JOIN a.categoria c " +
                "WHERE c.nombre = :cat " +
                "AND a.precio <= :precioMax " +
                "ORDER BY a.precio ASC";
            return session.createQuery(jpql, ArticuloPersonalizado.class)
                    .setParameter("cat", nombreCategoria)
                    .setParameter("precioMax", precioMax)
                    .list();
        } catch (Exception e) {
            System.err.println("Error consulta 1: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    // ── CONSULTA 2: Pedidos de cliente por estado, con PAGINACIÓN ─────────
    public List<Pedido> consultaPedidosPorCliente(
            int clienteId, Pedido.EstadoPedido estado, int pagina, int tamanioPagina) {
        Session session = factory.openSession();
        try {
            String jpql =
                "SELECT p FROM Pedido p " +
                "JOIN p.cliente c " +
                "WHERE c.id = :clienteId " +
                "AND p.estadoPedido = :estado " +
                "ORDER BY p.fecha DESC";
            return session.createQuery(jpql, Pedido.class)
                    .setParameter("clienteId", clienteId)
                    .setParameter("estado", estado)
                    .setFirstResult((pagina - 1) * tamanioPagina)
                    .setMaxResults(tamanioPagina)
                    .list();
        } catch (Exception e) {
            System.err.println("Error consulta 2: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    // ── CONSULTA 3: Artículos con proveedor usando JOIN, ordenados ─────────
    public List<Object[]> consultaArticulosConProveedor(String nombreProveedor) {
        Session session = factory.openSession();
        try {
            String jpql =
                "SELECT a, p FROM ArticuloPersonalizado a " +
                "JOIN a.proveedor p " +
                "WHERE p.nombre = :proveedor " +
                "ORDER BY a.nombre ASC";
            return session.createQuery(jpql, Object[].class)
                    .setParameter("proveedor", nombreProveedor)
                    .list();
        } catch (Exception e) {
            System.err.println("Error consulta 3: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    public void cerrar() {
        if (factory != null) factory.close();
    }
}