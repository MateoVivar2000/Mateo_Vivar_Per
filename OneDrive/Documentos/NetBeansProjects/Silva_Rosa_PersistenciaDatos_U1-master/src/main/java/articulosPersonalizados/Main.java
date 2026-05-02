package articulosPersonalizados;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        ArticuloPersonalizadoDAO dao = new ArticuloPersonalizadoDAO();

        System.out.println("\n===== CREATE =====");
        dao.guardar(new ArticuloPersonalizado("Camiseta", 15.00));
        dao.guardar(new ArticuloPersonalizado("Taza", 8.50));
        dao.guardar(new ArticuloPersonalizado("Gorra", 12.00));

        System.out.println("\n===== READ - Listar todos =====");
        for (ArticuloPersonalizado a : dao.listar()) {
            System.out.println(a);
        }

        System.out.println("\n===== JPQL - Buscar por nombre =====");
        for (ArticuloPersonalizado a : dao.buscarPorNombre("Ca")) {
            System.out.println(a);
        }

        System.out.println("\n===== JPQL - Filtrar precio <= $12 =====");
        for (ArticuloPersonalizado a : dao.filtrarPorPrecio(12.00)) {
            System.out.println(a);
        }

        System.out.println("\n===== JPQL - Total artículos =====");
        System.out.println("Total: " + dao.contarArticulos());

        System.out.println("\n===== UPDATE =====");
        List<ArticuloPersonalizado> lista = dao.listar();
        if (!lista.isEmpty()) {
            ArticuloPersonalizado primero = lista.get(0);
            primero.setPrecio(20.00);
            dao.actualizar(primero);
            System.out.println("Actualizado: " + primero);
        }

        System.out.println("\n===== DELETE =====");
        if (!lista.isEmpty()) {
            dao.eliminar(lista.get(lista.size() - 1).getId());
            System.out.println("Eliminado correctamente.");
        }

        dao.cerrar();
        System.out.println("\n===== FIN - CRUD + JPQL completado =====");
    }
}