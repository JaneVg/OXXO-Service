import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;

public class OxxoServiceATM extends JFrame {

    private Map<String, Servicio> baseDatosServicios;
    private JPanel panelPrincipal;
    private JLabel lblTitulo;
    private JLabel lblMensaje;
    private JTextField txtNumeroServicio;
    private JTextArea areaDetalles;
    private JButton btnAccionPrincipal;
    private JButton btnRegresar;
    private JLabel lblImagen;

    private static final Color COLOR_NARANJA_OSCURO = new Color(220, 80, 0);
    private static final Color COLOR_GRIS_CLARO = new Color(240, 240, 240);
    private static final Font FUENTE_TEXTO = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 18);

    private String servicioSeleccionado = "";

    private static class Servicio {
        String id;
        String nombreContratista;
        double monto;
        boolean pagado;
        String fechaCargo;
        String fechaPago;

        public Servicio(String id, String nombreContratista, double monto, boolean pagado, String fechaCargo) {
            this.id = id;
            this.nombreContratista = nombreContratista;
            this.monto = monto;
            this.pagado = pagado;
            this.fechaCargo = fechaCargo;
            this.fechaPago = null;
        }
    }

    public OxxoServiceATM() {
        super("Oxxo Service");
        inicializarBaseDatos();
        configurarVentana();
        mostrarMenuPrincipal();
    }

    private void inicializarBaseDatos() {
        baseDatosServicios = new HashMap<>();
        baseDatosServicios.put("AG00001", new Servicio("AG00001", "Ana Gonzalez", 450.75, false, "01/10/2025"));
        baseDatosServicios.put("LZ00001", new Servicio("LZ00001", "Beto Cruz", 890.50, false, "15/10/2025"));
        baseDatosServicios.put("PR00001", new Servicio("PR00001", "Carlos Diaz", 1200.00, false, "01/08/2025"));
        Servicio sPagado = new Servicio("AG00002", "Diana Flores", 320.10, true, "01/10/2025");
        sPagado.fechaPago = "28/10/2025";
        baseDatosServicios.put("AG00002", sPagado);
    }

    private void configurarVentana() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(750, 580);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        panelPrincipal = new JPanel(null);
        panelPrincipal.setBackground(COLOR_NARANJA_OSCURO);
        this.setContentPane(panelPrincipal);
        crearComponentesBase();
    }

    private void crearComponentesBase() {
        lblImagen = new JLabel("[LOGO OXXO]");
        lblImagen.setFont(new Font("Arial", Font.BOLD, 18));
        lblImagen.setForeground(Color.WHITE);
        lblImagen.setBounds(10, 10, 200, 40);
        panelPrincipal.add(lblImagen);

        lblTitulo = new JLabel("OXXO SERVICE ATM");
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(250, 20, 300, 30);
        panelPrincipal.add(lblTitulo);
    }

    private void limpiarContenido() {
        Component[] componentes = panelPrincipal.getComponents();
        for (Component c : componentes) {
            if (c != lblTitulo && c != lblImagen) {
                panelPrincipal.remove(c);
            }
        }
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void mostrarMenuPrincipal() {
        limpiarContenido();

        lblMensaje = crearEtiqueta("Seleccione el servicio a pagar:", 200, 80, 400, 30, Color.WHITE, FUENTE_TITULO.deriveFont(20f));
        panelPrincipal.add(lblMensaje);

        JButton btnAgua = crearBoton("PAGAR AGUA", 50, 160);
        btnAgua.addActionListener(e -> iniciarPago("AGUA"));
        panelPrincipal.add(btnAgua);

        JButton btnLuz = crearBoton("PAGAR LUZ", 270, 160);
        btnLuz.addActionListener(e -> iniciarPago("LUZ"));
        panelPrincipal.add(btnLuz);

        JButton btnPredio = crearBoton("PAGAR PREDIO", 490, 160);
        btnPredio.addActionListener(e -> iniciarPago("PREDIO"));
        panelPrincipal.add(btnPredio);

        JButton btnSalir = crearBoton("SALIR (X)", 270, 300);
        btnSalir.addActionListener(e -> System.exit(0));
        panelPrincipal.add(btnSalir);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void iniciarPago(String tipoServicio) {
        limpiarContenido();
        servicioSeleccionado = tipoServicio;

        lblMensaje = crearEtiqueta("Ingrese el numero de servicio para " + tipoServicio + ":", 50, 80, 600, 30, Color.WHITE, FUENTE_TEXTO.deriveFont(Font.BOLD, 18f));
        panelPrincipal.add(lblMensaje);

        txtNumeroServicio = new JTextField(15);
        txtNumeroServicio.setFont(FUENTE_TEXTO);
        txtNumeroServicio.setBorder(BorderFactory.createLineBorder(COLOR_NARANJA_OSCURO, 2));
        txtNumeroServicio.setBounds(200, 130, 350, 40);
        panelPrincipal.add(txtNumeroServicio);

        areaDetalles = new JTextArea();
        areaDetalles.setFont(FUENTE_TEXTO.deriveFont(Font.PLAIN, 15f));
        areaDetalles.setBackground(COLOR_GRIS_CLARO);
        areaDetalles.setBorder(new EmptyBorder(10, 10, 10, 10));
        areaDetalles.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(areaDetalles);
        scrollPane.setBounds(70, 200, 610, 170);
        panelPrincipal.add(scrollPane);

        btnAccionPrincipal = crearBoton("BUSCAR FACTURA", 220, 390);
        btnAccionPrincipal.addActionListener(e -> buscarServicio(txtNumeroServicio.getText().toUpperCase().trim()));
        panelPrincipal.add(btnAccionPrincipal);

        btnRegresar = crearBoton("« REGRESAR", 220, 460);
        btnRegresar.setBackground(Color.DARK_GRAY);
        btnRegresar.setForeground(Color.WHITE);
        agregarAnimacionHover(btnRegresar, btnRegresar.getBackground(), btnRegresar.getForeground());
        btnRegresar.addActionListener(e -> mostrarMenuPrincipal());
        panelPrincipal.add(btnRegresar);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
        txtNumeroServicio.requestFocusInWindow();
    }

    private void buscarServicio(String id) {
        areaDetalles.setText("");

        if (baseDatosServicios.containsKey(id)) {
            Servicio servicio = baseDatosServicios.get(id);
            if (servicio.pagado) {
                String mensaje = String.format("ATENCIÓN!\n\nEl pago de este servicio (%s) ya fue completado.\nFecha de Pago: %s", servicio.id, servicio.fechaPago);
                areaDetalles.setText(mensaje);
                areaDetalles.setForeground(new Color(0, 150, 0));
                actualizarBotonAccion("VOLVER A INICIO", e -> mostrarMenuPrincipal(), Color.BLUE);
            } else {
                String detalles = String.format("SERVICIO: %s\nID DE FACTURA: %s\nCONTRATISTA: %s\nFECHA DE CARGO: %s\n\nMONTO PENDIENTE: $%.2f",
                        servicioSeleccionado.toUpperCase(), servicio.id, servicio.nombreContratista, servicio.fechaCargo, servicio.monto);
                areaDetalles.setText(detalles);
                areaDetalles.setForeground(Color.BLACK);
                actualizarBotonAccion("PAGAR AHORA ($" + servicio.monto + ")", e -> realizarPago(id), new Color(0, 150, 0));
            }
        } else {
            String errorMsg = String.format("ERROR!\n\nNo se encontró la factura con ID: %s\nVerifique e inténtelo de nuevo.", id);
            areaDetalles.setText(errorMsg);
            areaDetalles.setForeground(Color.RED);
            actualizarBotonAccion("BUSCAR FACTURA", e -> buscarServicio(txtNumeroServicio.getText().toUpperCase().trim()), COLOR_NARANJA_OSCURO);
        }
    }

    private void realizarPago(String id) {
        Servicio servicio = baseDatosServicios.get(id);
        if (servicio != null && !servicio.pagado) {
            servicio.pagado = true;
            Date fechaActual = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            servicio.fechaPago = sdf.format(fechaActual);

            String mensaje = String.format("✅ PAGO EXITOSO!\n\nServicio: %s\nMonto: $%.2f\nFecha: %s\n\nGracias por usar OXXO SERVICE.",
                    servicio.id, servicio.monto, servicio.fechaPago);
            areaDetalles.setText(mensaje);
            areaDetalles.setForeground(new Color(0, 100, 0));
            actualizarBotonAccion("VOLVER A INICIO", e -> mostrarMenuPrincipal(), Color.BLUE);
        }
    }

    private JButton crearBoton(String texto, int x, int y) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON);
        boton.setBackground(Color.WHITE);
        boton.setForeground(COLOR_NARANJA_OSCURO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(COLOR_NARANJA_OSCURO, 3));
        boton.setBounds(x, y, 200, 50);
        agregarAnimacionHover(boton, boton.getBackground(), boton.getForeground());
        return boton;
    }

    private JLabel crearEtiqueta(String texto, int x, int y, int ancho, int alto, Color color, Font fuente) {
        JLabel label = new JLabel(texto);
        label.setBounds(x, y, ancho, alto);
        label.setForeground(color);
        label.setFont(fuente);
        return label;
    }

    private void actualizarBotonAccion(String texto, ActionListener listener, Color colorFondo) {
        for (ActionListener al : btnAccionPrincipal.getActionListeners()) {
            btnAccionPrincipal.removeActionListener(al);
        }
        btnAccionPrincipal.setText(texto);
        btnAccionPrincipal.addActionListener(listener);
        btnAccionPrincipal.setBackground(colorFondo);
        btnAccionPrincipal.setForeground(Color.WHITE);
        btnAccionPrincipal.setBounds(220, 390, 300, 50);
        agregarAnimacionHover(btnAccionPrincipal, colorFondo, Color.WHITE);
    }

    private void agregarAnimacionHover(JButton boton, Color originalBg, Color originalFg) {
        boton.addMouseListener(new MouseAdapter() {
            Color hoverBg = COLOR_NARANJA_OSCURO;
            Color hoverFg = Color.WHITE;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!boton.isEnabled()) return;
                boton.setBackground(hoverBg);
                boton.setForeground(hoverFg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(originalBg);
                boton.setForeground(originalFg);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OxxoServiceATM().setVisible(true));
    }
}
