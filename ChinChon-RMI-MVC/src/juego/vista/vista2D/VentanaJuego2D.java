package juego.vista.vista2D;

import juego.vista.base.VistaBase;
import juego.controlador.Controlador;
import juego.modelo.Carta;
import juego.modelo.Jugador;
import juego.modelo.Mano;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class VentanaJuego2D extends VistaBase implements IVista {
    private JPanel panelMano;
    private JPanel panelAcciones;
    private JLabel lblEstado;
    private JButton btnTomarMazo;
    private JButton btnTomarPila;
    private JButton btnDescartar;
    private JButton btnCerrar;
    private JButton btnListo;

    private int cartaSeleccionadaIndice = -1; // Guarda la carta cliqueada (1 a 8)
    private JButton botonCartaSeleccionada = null;

    public VentanaJuego2D(Controlador controlador) {
        super(controlador);
    }

    @Override
    public void logicaInicioAdicional() {
        setTitle("Chinchón 2D - Tablero de Juego");
        setSize(850, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Estado del turno y botón de inicio
        JPanel panelSuperior = new JPanel(new BorderLayout());
        lblEstado = new JLabel("Iniciá sesión y presioná 'Estoy Listo' para comenzar", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 15));

        btnListo = new JButton("¡Estoy Listo para Jugar!");
        btnListo.setBackground(new Color(46, 139, 87));
        btnListo.setForeground(Color.WHITE);
        btnListo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnListo.addActionListener(e -> {
            try {
                setListoParaJugar(true);
                btnListo.setEnabled(false);
                lblEstado.setText("Esperando a que los demás jugadores se preparen...");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        panelSuperior.add(lblEstado, BorderLayout.CENTER);
        panelSuperior.add(btnListo, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel Central: Cartas de la mano
        panelMano = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 20));
        panelMano.setBorder(BorderFactory.createTitledBorder("Tu Mano de Cartas"));
        panelMano.setBackground(new Color(34, 112, 62)); // Fondo verde tapete de cartas
        add(panelMano, BorderLayout.CENTER);

        // Panel Derecho: Mazo y Pila de Descarte
        JPanel panelTablero = new JPanel(new GridLayout(2, 1, 10, 10));
        panelTablero.setBorder(BorderFactory.createTitledBorder("Mazo / Pila"));

        btnTomarMazo = new JButton("<html><center><b>MAZO</b><br><font size=3>🎴 Robar</font></center></html>");
        btnTomarPila = new JButton("<html><center><b>DESCARTE</b><br><font size=3>📥 Tomar</font></center></html>");

        btnTomarMazo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnTomarPila.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnTomarMazo.addActionListener(e -> {
            try {
                tomarTopeMazo();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        btnTomarPila.addActionListener(e -> {
            try {
                tomarTopePilaDescarte();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        panelTablero.add(btnTomarMazo);
        panelTablero.add(btnTomarPila);
        add(panelTablero, BorderLayout.EAST);

        // Panel Inferior: Acciones de Juego
        panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnDescartar = new JButton("Descartar Seleccionada");
        btnCerrar = new JButton("Cerrar / Cortar Ronda");

        btnDescartar.addActionListener(e -> ejecutarDescarte(false));
        btnCerrar.addActionListener(e -> ejecutarDescarte(true));

        panelAcciones.add(btnDescartar);
        panelAcciones.add(btnCerrar);
        add(panelAcciones, BorderLayout.SOUTH);

        bloquearBotonesAccion();
    }

    private void bloquearBotonesAccion() {
        btnTomarMazo.setEnabled(false);
        btnTomarPila.setEnabled(false);
        btnDescartar.setEnabled(false);
        btnCerrar.setEnabled(false);
    }

    private void ejecutarDescarte(boolean intentarCerrar) {
        if (cartaSeleccionadaIndice == -1) {
            JOptionPane.showMessageDialog(this, "Por favor hacé clic en una carta de tu mano para seleccionarla.", "Seleccioná una carta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (intentarCerrar) {
                // En el backend, 0 es el código de intento de corte/cierre
                descartar(0);
            } else {
                descartar(cartaSeleccionadaIndice);
            }
            cartaSeleccionadaIndice = -1;
            botonCartaSeleccionada = null;
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    // Formateador HTML visual para las cartas estilo naipe
    private String formatearCartaHTML(Carta carta) {
        String texto = carta.toString();
        String colorHex = "#000000";
        String paloIcono = "♠";

        if (texto.contains("COPA")) {
            colorHex = "#D32F2F"; // Rojo
            paloIcono = "🍷";
        } else if (texto.contains("ORO")) {
            colorHex = "#F57F17"; // Amarillo/Dorado
            paloIcono = "🪙";
        } else if (texto.contains("BASTO")) {
            colorHex = "#388E3C"; // Verde
            paloIcono = "🪵";
        } else if (texto.contains("ESPADA")) {
            colorHex = "#1976D2"; // Azul
            paloIcono = "⚔️";
        } else if (texto.contains("COMODÍN")) {
            colorHex = "#8E24AA"; // Púrpura
            paloIcono = "🃏";
        }

        return "<html><center>"
                + "<font color='" + colorHex + "' size=4><b>" + paloIcono + "</b></font><br>"
                + "<font size=3 color='#212121'>" + texto.replace(" de ", "<br>") + "</font>"
                + "</center></html>";
    }

    @Override
    public void sesionIniciada() throws RemoteException {
        setVisible(true);
    }

    @Override
    public void bloquear() throws RemoteException {
        lblEstado.setText("Turno del oponente. Esperá tu turno...");
        bloquearBotonesAccion();
    }

    @Override
    public void actualizarManoYPila() throws RemoteException {
        panelMano.removeAll();
        Mano mano = getMano();

        if (mano != null) {
            for (int i = 1; i <= mano.getCantidadCartas(); i++) {
                Carta c = mano.getCarta(i);
                final int indiceActual = i;
                JButton btnCarta = new JButton(formatearCartaHTML(c));
                btnCarta.setPreferredSize(new Dimension(85, 125));
                btnCarta.setBackground(Color.WHITE);
                btnCarta.setFocusPainted(false);
                btnCarta.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

                btnCarta.addActionListener(e -> {
                    if (botonCartaSeleccionada != null) {
                        botonCartaSeleccionada.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                        botonCartaSeleccionada.setBackground(Color.WHITE);
                    }
                    cartaSeleccionadaIndice = indiceActual;
                    botonCartaSeleccionada = btnCarta;
                    btnCarta.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 4));
                    btnCarta.setBackground(new Color(255, 248, 220));
                });

                panelMano.add(btnCarta);
            }
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    @Override
    public void tomarDeMazoOPila() throws RemoteException {
        lblEstado.setText("¡Es tu turno! Robá del mazo o tomá del descarte.");
        btnTomarMazo.setEnabled(true);
        btnTomarPila.setEnabled(true);
        btnDescartar.setEnabled(false);
        btnCerrar.setEnabled(false);
    }

    @Override
    public void descartarOCerrar() throws RemoteException {
        lblEstado.setText("Hacé clic en una carta de tu mano y elegí 'Descartar' o 'Cerrar'.");
        btnTomarMazo.setEnabled(false);
        btnTomarPila.setEnabled(false);
        btnDescartar.setEnabled(true);
        btnCerrar.setEnabled(true);
    }

    @Override
    public void mostrarPuntos() throws RemoteException {
        StringBuilder sb = new StringBuilder("=== RESUMEN DE PUNTAJES ===\n\n");
        for (Jugador j : getJugadores()) {
            sb.append(j.getNombre()).append(": ").append(j.getPuntos()).append(" puntos\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Puntajes de la Ronda", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void perder() throws RemoteException {
        JOptionPane.showMessageDialog(this, "Has quedado eliminado o perdiste el juego.", "Derrota", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void ganar() throws RemoteException {
        JOptionPane.showMessageDialog(this, "¡Felicitaciones! ¡Ganaste la partida de Chinchón!", "¡Victoria!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void esperarNuevaRonda() throws RemoteException {
        lblEstado.setText("Ronda finalizada. Presioná 'Estoy Listo' para la siguiente.");
        btnListo.setEnabled(true);
        bloquearBotonesAccion();
    }
}