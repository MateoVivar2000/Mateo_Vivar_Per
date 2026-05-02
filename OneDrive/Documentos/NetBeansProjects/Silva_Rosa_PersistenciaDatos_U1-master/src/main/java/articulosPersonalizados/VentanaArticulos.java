package articulosPersonalizados;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaArticulos extends JFrame {

    // Conexión a la base de datos
    private ArticuloPersonalizadoDAO dao = new ArticuloPersonalizadoDAO();

    // Tabla para mostrar los artículos
    private DefaultTableModel modelo = new DefaultTableModel(
        new String[]{"ID", "Nombre", "Precio", "Estado"}, 0
    );
    private JTable tabla = new JTable(modelo);

    // Campos del formulario
    private JTextField txtNombre = new JTextField();
    private JTextField txtPrecio = new JTextField();
    private JTextField txtBuscar = new JTextField();
    private JTextField txtFiltro = new JTextField();
    private JLabel lblTotal     = new JLabel("Total: 0");

    // ID del artículo seleccionado
    private int idSeleccionado = -1;

    public VentanaArticulos() {
        // Configuración básica de la ventana
        setTitle("Artículos Personalizados - JPA");
        setSize(750, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Agregar secciones a la ventana
        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(crearPanelBusqueda(), BorderLayout.SOUTH);

        // Cargar datos al iniciar
        cargarTabla(dao.listar());
        actualizarTotal();

        // Cuando el usuario hace clic en una fila
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                idSeleccionado = (int) modelo.getValueAt(fila, 0);
                txtNombre.setText(modelo.getValueAt(fila, 1).toString());
                txtPrecio.setText(modelo.getValueAt(fila, 2).toString());
            }
        });

        setVisible(true);
    }

    // Panel superior: formulario para agregar/editar
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del artículo"));
        panel.setBackground(new Color(245, 247, 250));

        // Campos
        panel.add(new JLabel("  Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("  Precio ($):"));
        panel.add(txtPrecio);

        // Botones CRUD
        JButton btnAgregar    = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnLimpiar    = new JButton("Limpiar");

        // Colores de botones
        btnAgregar.setBackground(new Color(34, 197, 94));
        btnAgregar.setForeground(Color.WHITE);
        btnActualizar.setBackground(new Color(59, 130, 246));
        btnActualizar.setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(239, 68, 68));
        btnEliminar.setForeground(Color.WHITE);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        panel.add(botones);

        // Acciones de los botones
        btnAgregar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                if (nombre.isEmpty()) throw new Exception("El nombre no puede estar vacío.");
                dao.guardar(new ArticuloPersonalizado(nombre, precio));
                cargarTabla(dao.listar());
                limpiar();
                actualizarTotal();
                JOptionPane.showMessageDialog(this, "Artículo agregado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnActualizar.addActionListener(e -> {
            if (idSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un artículo de la tabla.");
                return;
            }
            try {
                ArticuloPersonalizado a = new ArticuloPersonalizado(
                    txtNombre.getText().trim(),
                    Double.parseDouble(txtPrecio.getText().trim())
                );
                a.setId(idSeleccionado);
                dao.actualizar(a);
                cargarTabla(dao.listar());
                limpiar();
                actualizarTotal();
                JOptionPane.showMessageDialog(this, "Artículo actualizado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnEliminar.addActionListener(e -> {
            if (idSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un artículo de la tabla.");
                return;
            }
            int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar este artículo?");
            if (conf == JOptionPane.YES_OPTION) {
                dao.eliminar(idSeleccionado);
                cargarTabla(dao.listar());
                limpiar();
                actualizarTotal();
            }
        });

        btnLimpiar.addActionListener(e -> {
            limpiar();
            cargarTabla(dao.listar());
        });

        return panel;
    }

    // Panel inferior: búsquedas JPQL
    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Consultas JPQL"));
        panel.setBackground(new Color(245, 247, 250));

        JButton btnBuscar  = new JButton("Buscar por nombre");
        JButton btnFiltrar = new JButton("Filtrar por precio");
        JButton btnContar  = new JButton("Contar artículos");
        JButton btnListar  = new JButton("Listar todos");

        panel.add(new JLabel("  Nombre a buscar:"));
        panel.add(txtBuscar);
        panel.add(new JLabel("  Precio máximo ($):"));
        panel.add(txtFiltro);
        panel.add(btnBuscar);
        panel.add(btnFiltrar);
        panel.add(btnContar);
        panel.add(btnListar);

        btnBuscar.addActionListener(e -> {
            String nombre = txtBuscar.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Escribe un nombre para buscar.");
                return;
            }
            List<ArticuloPersonalizado> resultado = dao.buscarPorNombre(nombre);
            cargarTabla(resultado);
            lblTotal.setText("Resultados: " + resultado.size());
        });

        btnFiltrar.addActionListener(e -> {
            try {
                double max = Double.parseDouble(txtFiltro.getText().trim());
                List<ArticuloPersonalizado> resultado = dao.filtrarPorPrecio(max);
                cargarTabla(resultado);
                lblTotal.setText("Artículos con precio <= $" + max + ": " + resultado.size());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingresa un precio válido.");
            }
        });

        btnContar.addActionListener(e -> {
            long total = dao.contarArticulos();
            lblTotal.setText("Total en base de datos: " + total);
            JOptionPane.showMessageDialog(this, "Total de artículos: " + total);
        });

        btnListar.addActionListener(e -> {
            cargarTabla(dao.listar());
            actualizarTotal();
        });

        // Barra de estado
        JPanel barraEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraEstado.setOpaque(false);
        barraEstado.add(lblTotal);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(panel, BorderLayout.CENTER);
        contenedor.add(barraEstado, BorderLayout.SOUTH);
        return contenedor;
    }

    // Cargar datos en la tabla
    private void cargarTabla(List<ArticuloPersonalizado> lista) {
        modelo.setRowCount(0);
        for (ArticuloPersonalizado a : lista) {
            modelo.addRow(new Object[]{a.getId(), a.getNombre(), a.getPrecio(), a.getEstado()});
        }
    }

    // Limpiar campos del formulario
    private void limpiar() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtBuscar.setText("");
        txtFiltro.setText("");
        idSeleccionado = -1;
        tabla.clearSelection();
    }

    // Actualizar contador inferior
    private void actualizarTotal() {
        lblTotal.setText("Total de artículos: " + dao.contarArticulos());
    }

    public static void main(String[] args) {
        new VentanaArticulos();
    }
}