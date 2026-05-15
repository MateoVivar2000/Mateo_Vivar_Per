package articulosPersonalizados;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ArticuloPersonalizadoDAO daoArticulo = new ArticuloPersonalizadoDAO();
        ConsultasDAO             daoConsulta = new ConsultasDAO();

        // ── Crear Categorías ───────────────────────────────────────────────
        Categoria catRopa  = new Categoria("Ropa",       "Prendas de vestir personalizadas");
        Categoria catHogar = new Categoria("Hogar",      "Artículos para el hogar");
        Categoria catAcces = new Categoria("Accesorios", "Complementos y accesorios");

        // ── Crear Proveedores ──────────────────────────────────────────────
        Proveedor prov1 = new Proveedor("TextilesPro",   "0991234567", "ventas@textilespro.com");
        Proveedor prov2 = new Proveedor("AccesoriosSur", "0987654321", "info@accesoriossur.com");

        // ── Crear Artículos con Categoría y Proveedor ─────────────────────
        ArticuloPersonalizado camiseta = new ArticuloPersonalizado("Camiseta", 15.00, catRopa,  prov1);
        ArticuloPersonalizado taza     = new ArticuloPersonalizado("Taza",      8.50, catHogar, prov2);
        ArticuloPersonalizado gorra    = new ArticuloPersonalizado("Gorra",    12.00, catAcces, prov1);
        ArticuloPersonalizado almohada = new ArticuloPersonalizado("Almohada", 22.00, catHogar, prov2);
        ArticuloPersonalizado llavero  = new ArticuloPersonalizado("Llavero",   5.00, catAcces, prov2);

        daoArticulo.guardar(camiseta);
        daoArticulo.guardar(taza);
        daoArticulo.guardar(gorra);
        daoArticulo.guardar(almohada);
        daoArticulo.guardar(llavero);

        // ── Crear Clientes ─────────────────────────────────────────────────
        Cliente cliente1 = new Cliente("Ana",    "García", "ana@mail.com",    "0991111111");
        Cliente cliente2 = new Cliente("Carlos", "López",  "carlos@mail.com", "0992222222");

        // ── Crear Pedidos ──────────────────────────────────────────────────
        Pedido pedido1 = new Pedido(LocalDate.now(), 23.50, Pedido.EstadoPedido.ENTREGADO, cliente1);
        pedido1.getArticulos().add(camiseta);
        pedido1.getArticulos().add(taza);

        Pedido pedido2 = new Pedido(LocalDate.now(), 17.00, Pedido.EstadoPedido.PENDIENTE, cliente1);
        pedido2.getArticulos().add(gorra);
        pedido2.getArticulos().add(llavero);

        Pedido pedido3 = new Pedido(LocalDate.now(), 22.00, Pedido.EstadoPedido.EN_PROCESO, cliente2);
        pedido3.getArticulos().add(almohada);

        // ── CONSULTA 1: Artículos por categoría y precio ───────────────────
        System.out.println("\n===== CONSULTA 1: Artículos de 'Accesorios' con precio <= $15 =====");
        List<ArticuloPersonalizado> r1 =
            daoConsulta.consultaArticulosPorCategoria("Accesorios", 15.00);
        r1.forEach(System.out::println);

        // ── CONSULTA 2: Pedidos con paginación ────────────────────────────
        System.out.println("\n===== CONSULTA 2: Pedidos PENDIENTES del cliente 1 (pág 1) =====");
        List<Pedido> r2 =
            daoConsulta.consultaPedidosPorCliente(cliente1.getId(), Pedido.EstadoPedido.PENDIENTE, 1, 5);
        r2.forEach(System.out::println);

        // ── CONSULTA 3: Artículos con su proveedor ────────────────────────
        System.out.println("\n===== CONSULTA 3: Artículos del proveedor 'TextilesPro' =====");
        List<Object[]> r3 = daoConsulta.consultaArticulosConProveedor("TextilesPro");
        for (Object[] fila : r3) {
            ArticuloPersonalizado art  = (ArticuloPersonalizado) fila[0];
            Proveedor             prov = (Proveedor)             fila[1];
            System.out.println("  Artículo: " + art.getNombre() + " | Proveedor: " + prov.getNombre());
        }

        daoArticulo.cerrar();
        daoConsulta.cerrar();
        System.out.println("\n===== FIN =====");
    }
}