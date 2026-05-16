package articulosPersonalizados;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VentanaArticulos extends JFrame {

    private ArticuloPersonalizadoDAO daoArticulo = new ArticuloPersonalizadoDAO();
    private ConsultasDAO             daoConsulta = new ConsultasDAO();

    // Paleta simple — naranja impresión 3D + gris
    private static final Color NARANJA  = new Color(255, 111, 0);
    private static final Color GRIS     = new Color(80, 80, 80);
    private static final Color GRIS_CLR = new Color(230, 230, 230);
    private static final Color ROJO     = new Color(200, 50, 50);
    private static final Color FONDO    = new Color(250, 250, 250);

    // PESTAÑA 1 — ARTÍCULOS
    private DefaultTableModel modeloArticulos = new DefaultTableModel(
        new String[]{"ID", "Nombre", "Precio", "Estado", "Categoría", "Proveedor"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable tablaArticulos  = new JTable(modeloArticulos);
    private JTextField txtNombre   = new JTextField();
    private JTextField txtPrecio   = new JTextField();
    private JComboBox<String> cbEstado    = new JComboBox<>(new String[]{"NUEVO","ACTIVO","DESCONTINUADO"});
    private JComboBox<String> cbCategoria = new JComboBox<>();
    private JComboBox<String> cbProveedor = new JComboBox<>();
    private JLabel lblTotal = new JLabel("Total: 0 artículos");
    private int idArticuloSel = -1;

    // PESTAÑA 2 — CLIENTES
    private DefaultTableModel modeloClientes = new DefaultTableModel(
        new String[]{"ID", "Nombre", "Apellido", "Email", "Teléfono"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable tablaClientes     = new JTable(modeloClientes);
    private JTextField txtCliNombre   = new JTextField();
    private JTextField txtCliApellido = new JTextField();
    private JTextField txtCliEmail    = new JTextField();
    private JTextField txtCliTelefono = new JTextField();
    private int idClienteSel = -1;

    // PESTAÑA 3 — PEDIDOS
    private DefaultTableModel modeloPedidos = new DefaultTableModel(
        new String[]{"ID", "Fecha", "Total ($)", "Estado", "Cliente"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable tablaPedidos = new JTable(modeloPedidos);
    private JTextField txtPedTotal          = new JTextField();
    private JComboBox<String> cbPedCliente  = new JComboBox<>();
    private JComboBox<String> cbPedEstado   = new JComboBox<>(
        new String[]{"PENDIENTE","EN_PROCESO","ENTREGADO","CANCELADO"});
    private JComboBox<String> cbPedArticulo = new JComboBox<>();
    private DefaultListModel<String> listModelArts = new DefaultListModel<>();
    private JList<String> listArts = new JList<>(listModelArts);

    // PESTAÑA 4 — CONSULTAS
    private JTextArea areaResultados = new JTextArea();

    public VentanaArticulos() {
        setTitle("Artículos Personalizados 3D — Sistema de Gestión");
        setSize(860, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout());

        // Header sencillo
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NARANJA);
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel titulo = new JLabel("🖨 Artículos Personalizados en Impresión 3D");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Sistema de gestión — Persistencia de Datos");
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(new Color(255, 220, 180));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(sub);
        header.add(textos, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));
        tabs.addTab("Artículos 3D",    panelArticulos());
        tabs.addTab("Clientes",        panelClientes());
        tabs.addTab("Pedidos",         panelPedidos());
        tabs.addTab("Consultas JPQL",  panelConsultas());
        add(tabs, BorderLayout.CENTER);

        cargarDropdownsArticulos();
        cargarTablaArticulos();
        cargarTablaClientes();
        cargarTablaPedidos();
        cargarDropdownsPedidos();

        setVisible(true);
    }

    // ── PANEL ARTÍCULOS ───────────────────────────────────────────────────────
    private JPanel panelArticulos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(FONDO);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(NARANJA, 1), "Datos del Artículo 3D"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        String[] labels = {"Nombre:", "Precio ($):", "Estado:", "Categoría:", "Proveedor:"};
        JComponent[] campos = {txtNombre, txtPrecio, cbEstado, cbCategoria, cbProveedor};

        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            form.add(new JLabel(labels[i]), g);
            g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
            campos[i].setPreferredSize(new Dimension(200, 26));
            form.add(campos[i], g);
        }

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        botones.setOpaque(false);
        JButton btnGuardar    = btnSimple("Guardar",    NARANJA);
        JButton btnActualizar = btnSimple("Actualizar", GRIS);
        JButton btnEliminar   = btnSimple("Eliminar",   ROJO);
        JButton btnLimpiar    = btnSimple("Limpiar",    GRIS_CLR);
        btnLimpiar.setForeground(Color.DARK_GRAY);
        botones.add(btnGuardar); botones.add(btnActualizar);
        botones.add(btnEliminar); botones.add(btnLimpiar);

        g.gridx = 0; g.gridy = labels.length; g.gridwidth = 2; g.weightx = 1;
        form.add(botones, g);
        g.gridy = labels.length + 1;
        form.add(lblTotal, g);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaArticulos), BorderLayout.CENTER);
        estilizarTabla(tablaArticulos);

        // Selección de fila
        tablaArticulos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaArticulos.getSelectedRow();
            if (fila >= 0) {
                idArticuloSel = (int) modeloArticulos.getValueAt(fila, 0);
                txtNombre.setText(modeloArticulos.getValueAt(fila, 1).toString());
                txtPrecio.setText(modeloArticulos.getValueAt(fila, 2).toString());
                cbEstado.setSelectedItem(modeloArticulos.getValueAt(fila, 3).toString());
                cbCategoria.setSelectedItem(modeloArticulos.getValueAt(fila, 4).toString());
                cbProveedor.setSelectedItem(modeloArticulos.getValueAt(fila, 5).toString());
            }
        });

        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) throw new Exception("El nombre no puede estar vacío.");
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                Categoria cat  = buscarOCrearCategoria(cbCategoria.getSelectedItem().toString());
                Proveedor prov = buscarOCrearProveedor(cbProveedor.getSelectedItem().toString());
                ArticuloPersonalizado a = new ArticuloPersonalizado(nombre, precio, cat, prov);
                a.setEstado(ArticuloPersonalizado.EstadoArticulo.valueOf(cbEstado.getSelectedItem().toString()));
                daoArticulo.guardar(a);
                cargarTablaArticulos(); cargarDropdownsPedidos(); limpiarArticulos();
                JOptionPane.showMessageDialog(this, "Artículo guardado correctamente.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        btnActualizar.addActionListener(e -> {
            if (idArticuloSel == -1) { JOptionPane.showMessageDialog(this, "Selecciona un artículo de la tabla."); return; }
            try {
                Categoria cat  = buscarOCrearCategoria(cbCategoria.getSelectedItem().toString());
                Proveedor prov = buscarOCrearProveedor(cbProveedor.getSelectedItem().toString());
                ArticuloPersonalizado a = new ArticuloPersonalizado(
                    txtNombre.getText().trim(), Double.parseDouble(txtPrecio.getText().trim()), cat, prov);
                a.setId(idArticuloSel);
                a.setEstado(ArticuloPersonalizado.EstadoArticulo.valueOf(cbEstado.getSelectedItem().toString()));
                daoArticulo.actualizar(a);
                cargarTablaArticulos(); limpiarArticulos();
                JOptionPane.showMessageDialog(this, "Artículo actualizado.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        btnEliminar.addActionListener(e -> {
            if (idArticuloSel == -1) { JOptionPane.showMessageDialog(this, "Selecciona un artículo."); return; }
            if (JOptionPane.showConfirmDialog(this, "¿Eliminar este artículo?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                daoArticulo.eliminar(idArticuloSel);
                cargarTablaArticulos(); limpiarArticulos();
            }
        });

        btnLimpiar.addActionListener(e -> { limpiarArticulos(); cargarTablaArticulos(); });
        return panel;
    }

    // ── PANEL CLIENTES ────────────────────────────────────────────────────────
    private JPanel panelClientes() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(FONDO);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(NARANJA, 1), "Datos del Cliente"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        String[] labels = {"Nombre:", "Apellido:", "Email:", "Teléfono:"};
        JTextField[] campos = {txtCliNombre, txtCliApellido, txtCliEmail, txtCliTelefono};
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            form.add(new JLabel(labels[i]), g);
            g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
            campos[i].setPreferredSize(new Dimension(200, 26));
            form.add(campos[i], g);
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        botones.setOpaque(false);
        JButton btnAgregar  = btnSimple("Agregar",  NARANJA);
        JButton btnEliminar = btnSimple("Eliminar", ROJO);
        JButton btnLimpiar  = btnSimple("Limpiar",  GRIS_CLR);
        btnLimpiar.setForeground(Color.DARK_GRAY);
        botones.add(btnAgregar); botones.add(btnEliminar); botones.add(btnLimpiar);

        g.gridx = 0; g.gridy = labels.length; g.gridwidth = 2;
        form.add(botones, g);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        estilizarTabla(tablaClientes);

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaClientes.getSelectedRow();
            if (fila >= 0) {
                idClienteSel = (int) modeloClientes.getValueAt(fila, 0);
                txtCliNombre.setText(modeloClientes.getValueAt(fila, 1).toString());
                txtCliApellido.setText(modeloClientes.getValueAt(fila, 2).toString());
                txtCliEmail.setText(modeloClientes.getValueAt(fila, 3).toString());
                txtCliTelefono.setText(modeloClientes.getValueAt(fila, 4).toString());
            }
        });

        btnAgregar.addActionListener(e -> {
            try {
                String nom = txtCliNombre.getText().trim();
                String ape = txtCliApellido.getText().trim();
                if (nom.isEmpty() || ape.isEmpty()) throw new Exception("Nombre y apellido son obligatorios.");
                guardarEntidad(new Cliente(nom, ape, txtCliEmail.getText().trim(), txtCliTelefono.getText().trim()));
                cargarTablaClientes(); cargarDropdownsPedidos(); limpiarClientes();
                JOptionPane.showMessageDialog(this, "Cliente agregado.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        btnEliminar.addActionListener(e -> {
            if (idClienteSel == -1) { JOptionPane.showMessageDialog(this, "Selecciona un cliente."); return; }
            if (JOptionPane.showConfirmDialog(this, "¿Eliminar este cliente?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                eliminarEntidad(Cliente.class, idClienteSel);
                cargarTablaClientes(); cargarDropdownsPedidos(); limpiarClientes();
            }
        });

        btnLimpiar.addActionListener(e -> limpiarClientes());
        return panel;
    }

    // ── PANEL PEDIDOS ─────────────────────────────────────────────────────────
    private JPanel panelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(FONDO);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(NARANJA, 1), "Registrar Pedido"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Cliente:"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        cbPedCliente.setPreferredSize(new Dimension(200, 26)); form.add(cbPedCliente, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Total ($):"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        txtPedTotal.setPreferredSize(new Dimension(200, 26)); form.add(txtPedTotal, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Estado:"), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        form.add(cbPedEstado, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Artículo:"), g);
        JPanel rowArt = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        rowArt.setOpaque(false);
        cbPedArticulo.setPreferredSize(new Dimension(180, 26));
        JButton btnAddArt = btnSimple("+ Añadir", GRIS);
        rowArt.add(cbPedArticulo); rowArt.add(btnAddArt);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        form.add(rowArt, g);

        // Lista de artículos del pedido
        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBackground(FONDO);
        panelLista.setBorder(BorderFactory.createTitledBorder("Artículos seleccionados"));
        panelLista.setPreferredSize(new Dimension(220, 110));
        listArts.setFont(new Font("Arial", Font.PLAIN, 12));
        panelLista.add(new JScrollPane(listArts));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        botones.setOpaque(false);
        JButton btnCrear   = btnSimple("Registrar Pedido", NARANJA);
        JButton btnLimpiar = btnSimple("Limpiar",          GRIS_CLR);
        btnLimpiar.setForeground(Color.DARK_GRAY);
        botones.add(btnCrear); botones.add(btnLimpiar);

        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        form.add(botones, g);

        JPanel norte = new JPanel(new BorderLayout(8, 0));
        norte.setBackground(FONDO);
        norte.add(form, BorderLayout.CENTER);
        norte.add(panelLista, BorderLayout.EAST);

        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);
        estilizarTabla(tablaPedidos);

        btnAddArt.addActionListener(e -> {
            if (cbPedArticulo.getSelectedItem() != null) {
                String art = cbPedArticulo.getSelectedItem().toString();
                if (!listModelArts.contains(art)) listModelArts.addElement(art);
            }
        });

        btnCrear.addActionListener(e -> {
            try {
                if (cbPedCliente.getSelectedItem() == null) throw new Exception("Selecciona un cliente.");
                double total = Double.parseDouble(txtPedTotal.getText().trim());
                int cliId = Integer.parseInt(cbPedCliente.getSelectedItem().toString().split(" - ")[0]);
                Cliente cliente = buscarPorId(Cliente.class, cliId);
                Pedido.EstadoPedido estado = Pedido.EstadoPedido.valueOf(cbPedEstado.getSelectedItem().toString());
                Pedido pedido = new Pedido(LocalDate.now(), total, estado, cliente);
                for (int i = 0; i < listModelArts.size(); i++) {
                    int artId = Integer.parseInt(listModelArts.get(i).split(" - ")[0]);
                    ArticuloPersonalizado art = daoArticulo.buscarPorId(artId);
                    if (art != null) pedido.getArticulos().add(art);
                }
                guardarEntidad(pedido);
                cargarTablaPedidos(); listModelArts.clear(); txtPedTotal.setText("");
                JOptionPane.showMessageDialog(this, "Pedido registrado correctamente.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        btnLimpiar.addActionListener(e -> { listModelArts.clear(); txtPedTotal.setText(""); });
        return panel;
    }

    // ── PANEL CONSULTAS JPQL ──────────────────────────────────────────────────
    private JPanel panelConsultas() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controles = new JPanel(new GridLayout(3, 1, 4, 4));
        controles.setBackground(FONDO);
        controles.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(NARANJA, 1), "Consultas JPQL"));

        // Consulta 1
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        p1.setBackground(new Color(255, 245, 235));
        JTextField txtCat    = new JTextField("Accesorios", 10);
        JTextField txtPreMax = new JTextField("15", 5);
        JButton btn1 = btnSimple("Consulta 1", NARANJA);
        JLabel desc1 = new JLabel("Artículos por categoría con precio máximo, ordenados");
        desc1.setFont(new Font("Arial", Font.ITALIC, 11));
        desc1.setForeground(GRIS);
        p1.add(btn1); p1.add(new JLabel("Categoría:")); p1.add(txtCat);
        p1.add(new JLabel("Precio máx $:")); p1.add(txtPreMax); p1.add(desc1);
        controles.add(p1);

        // Consulta 2
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        p2.setBackground(new Color(245, 245, 255));
        JTextField txtCliId  = new JTextField("1", 4);
        JTextField txtPagina = new JTextField("1", 3);
        JComboBox<String> cbEstPed = new JComboBox<>(
            new String[]{"PENDIENTE","EN_PROCESO","ENTREGADO","CANCELADO"});
        JButton btn2 = btnSimple("Consulta 2", GRIS);
        JLabel desc2 = new JLabel("Pedidos de un cliente con filtro de estado y paginación");
        desc2.setFont(new Font("Arial", Font.ITALIC, 11));
        desc2.setForeground(GRIS);
        p2.add(btn2); p2.add(new JLabel("Cliente ID:")); p2.add(txtCliId);
        p2.add(new JLabel("Estado:")); p2.add(cbEstPed);
        p2.add(new JLabel("Pág:")); p2.add(txtPagina); p2.add(desc2);
        controles.add(p2);

        // Consulta 3
        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        p3.setBackground(new Color(235, 255, 245));
        JTextField txtProv = new JTextField("TextilesPro", 12);
        JButton btn3 = btnSimple("Consulta 3", new Color(0, 140, 80));
        JLabel desc3 = new JLabel("Artículos con JOIN al proveedor, ordenados por nombre");
        desc3.setFont(new Font("Arial", Font.ITALIC, 11));
        desc3.setForeground(GRIS);
        p3.add(btn3); p3.add(new JLabel("Proveedor:")); p3.add(txtProv); p3.add(desc3);
        controles.add(p3);

        panel.add(controles, BorderLayout.NORTH);

        // Área de resultados — simple, como consola
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaResultados.setBackground(Color.WHITE);
        areaResultados.setForeground(new Color(40, 40, 40));
        areaResultados.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GRIS_CLR), "Resultados"),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        areaResultados.setText("Presiona uno de los botones para ver los resultados de la consulta JPQL...");
        panel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        btn1.addActionListener(e -> {
            try {
                double pmax = Double.parseDouble(txtPreMax.getText().trim());
                List<ArticuloPersonalizado> res =
                    daoConsulta.consultaArticulosPorCategoria(txtCat.getText().trim(), pmax);
                StringBuilder sb = new StringBuilder();
                sb.append("CONSULTA 1 — SELECT a FROM ArticuloPersonalizado a JOIN a.categoria c\n");
                sb.append("             WHERE c.nombre = '").append(txtCat.getText())
                  .append("' AND a.precio <= ").append(pmax).append(" ORDER BY a.precio ASC\n\n");
                sb.append(String.format("%-5s %-22s %-10s %-15s%n","ID","Nombre","Precio","Estado"));
                sb.append("-".repeat(56)).append("\n");
                if (res.isEmpty()) sb.append("(Sin resultados)\n");
                for (ArticuloPersonalizado a : res)
                    sb.append(String.format("%-5d %-22s $%-9.2f %-15s%n",
                        a.getId(), a.getNombre(), a.getPrecio(), a.getEstado()));
                sb.append("\nTotal: ").append(res.size()).append(" artículo(s).");
                areaResultados.setText(sb.toString());
            } catch (Exception ex) { areaResultados.setText("Error: " + ex.getMessage()); }
        });

        btn2.addActionListener(e -> {
            try {
                int cliId  = Integer.parseInt(txtCliId.getText().trim());
                int pagina = Integer.parseInt(txtPagina.getText().trim());
                Pedido.EstadoPedido est =
                    Pedido.EstadoPedido.valueOf(cbEstPed.getSelectedItem().toString());
                List<Pedido> res = daoConsulta.consultaPedidosPorCliente(cliId, est, pagina, 5);
                StringBuilder sb = new StringBuilder();
                sb.append("CONSULTA 2 — SELECT p FROM Pedido p JOIN p.cliente c\n");
                sb.append("             WHERE c.id = ").append(cliId)
                  .append(" AND p.estadoPedido = '").append(est).append("'\n");
                sb.append("             ORDER BY p.fecha DESC  [Paginación: página ").append(pagina).append(", 5 por página]\n\n");
                sb.append(String.format("%-5s %-12s %-10s %-15s%n","ID","Fecha","Total","Estado"));
                sb.append("-".repeat(46)).append("\n");
                if (res.isEmpty()) sb.append("(Sin resultados)\n");
                for (Pedido p : res)
                    sb.append(String.format("%-5d %-12s $%-9.2f %-15s%n",
                        p.getId(), p.getFecha(), p.getTotal(), p.getEstadoPedido()));
                sb.append("\nResultados en esta página: ").append(res.size());
                areaResultados.setText(sb.toString());
            } catch (Exception ex) { areaResultados.setText("Error: " + ex.getMessage()); }
        });

        btn3.addActionListener(e -> {
            List<Object[]> res = daoConsulta.consultaArticulosConProveedor(txtProv.getText().trim());
            StringBuilder sb = new StringBuilder();
            sb.append("CONSULTA 3 — SELECT a, p FROM ArticuloPersonalizado a JOIN a.proveedor p\n");
            sb.append("             WHERE p.nombre = '").append(txtProv.getText())
              .append("' ORDER BY a.nombre ASC\n\n");
            sb.append(String.format("%-5s %-22s %-10s %-20s%n","ID","Artículo","Precio","Proveedor"));
            sb.append("-".repeat(60)).append("\n");
            if (res.isEmpty()) sb.append("(Sin resultados)\n");
            for (Object[] fila : res) {
                ArticuloPersonalizado a = (ArticuloPersonalizado) fila[0];
                Proveedor             p = (Proveedor)             fila[1];
                sb.append(String.format("%-5d %-22s $%-9.2f %-20s%n",
                    a.getId(), a.getNombre(), a.getPrecio(), p.getNombre()));
            }
            sb.append("\nTotal: ").append(res.size()).append(" artículo(s).");
            areaResultados.setText(sb.toString());
        });

        return panel;
    }

    // ── OPERACIONES BD ────────────────────────────────────────────────────────
    private org.hibernate.SessionFactory sf() {
        return new org.hibernate.cfg.Configuration()
                .configure("hibernate.cfg.xml").buildSessionFactory();
    }
    private void guardarEntidad(Object obj) {
        try (org.hibernate.Session s = sf().openSession()) {
            s.beginTransaction(); s.save(obj); s.getTransaction().commit();
        }
    }
    private <T> void eliminarEntidad(Class<T> cls, int id) {
        try (org.hibernate.Session s = sf().openSession()) {
            s.beginTransaction();
            T obj = s.get(cls, id);
            if (obj != null) s.delete(obj);
            s.getTransaction().commit();
        }
    }
    private <T> T buscarPorId(Class<T> cls, int id) {
        try (org.hibernate.Session s = sf().openSession()) { return s.get(cls, id); }
    }
    private List<Cliente> listarClientes() {
        try (org.hibernate.Session s = sf().openSession()) {
            return s.createQuery("FROM Cliente ORDER BY apellido ASC", Cliente.class).list();
        } catch (Exception e) { return java.util.Collections.emptyList(); }
    }
    private List<Pedido> listarPedidos() {
        try (org.hibernate.Session s = sf().openSession()) {
            return s.createQuery("FROM Pedido ORDER BY fecha DESC", Pedido.class).list();
        } catch (Exception e) { return java.util.Collections.emptyList(); }
    }
    private Categoria buscarOCrearCategoria(String nombre) {
        try (org.hibernate.Session s = sf().openSession()) {
            List<Categoria> res = s.createQuery("FROM Categoria WHERE nombre = :n", Categoria.class)
                .setParameter("n", nombre).list();
            if (!res.isEmpty()) return res.get(0);
            Categoria c = new Categoria(nombre, "");
            s.beginTransaction(); s.save(c); s.getTransaction().commit();
            return c;
        }
    }
    private Proveedor buscarOCrearProveedor(String nombre) {
        try (org.hibernate.Session s = sf().openSession()) {
            List<Proveedor> res = s.createQuery("FROM Proveedor WHERE nombre = :n", Proveedor.class)
                .setParameter("n", nombre).list();
            if (!res.isEmpty()) return res.get(0);
            Proveedor p = new Proveedor(nombre, "", "");
            s.beginTransaction(); s.save(p); s.getTransaction().commit();
            return p;
        }
    }

    // ── CARGAR TABLAS Y DROPDOWNS ─────────────────────────────────────────────
    private void cargarTablaArticulos() {
        modeloArticulos.setRowCount(0);
        for (ArticuloPersonalizado a : daoArticulo.listar()) {
            String cat  = a.getCategoria() != null ? a.getCategoria().getNombre() : "-";
            String prov = a.getProveedor() != null ? a.getProveedor().getNombre() : "-";
            modeloArticulos.addRow(new Object[]{
                a.getId(), a.getNombre(), a.getPrecio(), a.getEstado(), cat, prov});
        }
        lblTotal.setText("Total: " + modeloArticulos.getRowCount() + " artículos");
    }
    private void cargarTablaClientes() {
        modeloClientes.setRowCount(0);
        for (Cliente c : listarClientes())
            modeloClientes.addRow(new Object[]{
                c.getId(), c.getNombre(), c.getApellido(), c.getEmail(), c.getTelefono()});
    }
    private void cargarTablaPedidos() {
        modeloPedidos.setRowCount(0);
        for (Pedido p : listarPedidos()) {
            String cli = p.getCliente() != null ?
                p.getCliente().getNombre() + " " + p.getCliente().getApellido() : "-";
            modeloPedidos.addRow(new Object[]{
                p.getId(), p.getFecha(), p.getTotal(), p.getEstadoPedido(), cli});
        }
    }
    private void cargarDropdownsArticulos() {
        cbCategoria.removeAllItems(); cbProveedor.removeAllItems();
        try (org.hibernate.Session s = sf().openSession()) {
            s.createQuery("FROM Categoria ORDER BY nombre ASC", Categoria.class)
             .list().forEach(c -> cbCategoria.addItem(c.getNombre()));
            s.createQuery("FROM Proveedor ORDER BY nombre ASC", Proveedor.class)
             .list().forEach(p -> cbProveedor.addItem(p.getNombre()));
        } catch (Exception ignored) {}
        if (cbCategoria.getItemCount() == 0) {
            cbCategoria.addItem("Figuras"); cbCategoria.addItem("Prototipos");
            cbCategoria.addItem("Accesorios"); cbCategoria.addItem("Repuestos");
        }
        if (cbProveedor.getItemCount() == 0) {
            cbProveedor.addItem("FilamentoPro"); cbProveedor.addItem("Resinas3D");
        }
    }
    private void cargarDropdownsPedidos() {
        cbPedCliente.removeAllItems(); cbPedArticulo.removeAllItems();
        listarClientes().forEach(c ->
            cbPedCliente.addItem(c.getId() + " - " + c.getNombre() + " " + c.getApellido()));
        daoArticulo.listar().forEach(a ->
            cbPedArticulo.addItem(a.getId() + " - " + a.getNombre() + " ($" + a.getPrecio() + ")"));
    }

    // ── LIMPIEZA ──────────────────────────────────────────────────────────────
    private void limpiarArticulos() {
        txtNombre.setText(""); txtPrecio.setText("");
        idArticuloSel = -1; tablaArticulos.clearSelection();
    }
    private void limpiarClientes() {
        txtCliNombre.setText(""); txtCliApellido.setText("");
        txtCliEmail.setText(""); txtCliTelefono.setText("");
        idClienteSel = -1; tablaClientes.clearSelection();
    }

    // ── HELPERS UI ────────────────────────────────────────────────────────────
    private JButton btnSimple(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return b;
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(NARANJA);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(255, 220, 180));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(GRIS_CLR);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaArticulos::new);
    }
}