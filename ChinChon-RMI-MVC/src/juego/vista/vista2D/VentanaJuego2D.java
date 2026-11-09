package juego.vista.vista2D;

import juego.vista.base.VistaBase;
import juego.controlador.Controlador;
import juego.modelo.Carta;
import juego.modelo.Jugador;
import juego.modelo.Mano;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class VentanaJuego2D extends VistaBase implements IVista {
    private JPanel panelMano;
    private JPanel panelAcciones;
    private JLabel lblEstado;
    private JLabel lblMazo;
    private JLabel lblPila;
    private JButton btnTomarMazo;
    private JButton btnTomarPila;
    private JButton btnDescartar;
    private JButton btnCerrar;
    private JTextArea txtResumenPuntos;

    public VentanaJuego2D(Controlador controlador) {
        super(controlador);
    }

    @Override
    public void logicaInicioAdicional() {
        setTitle("Chinchón 2D - Tablero de Juego");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Estado del turno
        lblEstado = new JLabel("Esperando inicio del juego...", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblEstado, BorderLayout.NORTH);

        // Panel Central: Cartas de la mano
        panelMano = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelMano.setBorder(BorderFactory.createTitledBorder("Tu Mano"));
        add(panelMano, BorderLayout.CENTER);

        // Panel Derecho: Mazo y Pila de Descarte
        JPanel panelTablero = new JPanel(new GridLayout(2, 1, 10, 10));
        panelTablero.setBorder(BorderFactory.createTitledBorder("Mazo / Pila"));

        btnTomarMazo = new JButton("Robar del Mazo");
        btnTomarPila = new JButton("Robar del Descarte");

        panelTablero.add(btnTomarMazo);
        panelTablero.add(btnTomarPila);
        add(panelTablero, BorderLayout.EAST);

        // Panel Inferior: Acciones de Juego
        panelAcciones = new JPanel(new FlowLayout());
        btnDescartar = new JButton("Descartar Seleccionada");
        btnCerrar = new JButton("Cerrar / Cortar Ronda");

        panelAcciones.add(btnDescartar);
        panelAcciones.add(btnCerrar);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    @Override
    public void sesionIniciada() throws RemoteException {
        setVisible(true);
    }

    @Override
    public void bloquear() throws RemoteException {
        lblEstado.setText("Espera tu turno...");
        btnTomarMazo.setEnabled(false);
        btnTomarPila.setEnabled(false);
        btnDescartar.setEnabled(false);
        btnCerrar.setEnabled(false);
    }

    @Override
    public void actualizarManoYPila() throws RemoteException {
        panelMano.removeAll();
        Mano mano = getMano();
        if (mano != null) {
            for (int i = 1; i <= mano.getCantidadCartas(); i++) {
                Carta c = mano.getCarta(i);
                JButton btnCarta = new JButton(c.toString());
                btnCarta.setPreferredSize(new Dimension(90, 130));
                btnCarta.setBackground(new Color(230, 240, 250));
                panelMano.add(btnCarta);
            }
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    @Override
    public void tomarDeMazoOPila() throws RemoteException {
        lblEstado.setText("¡Es tu turno! Elige de dónde tomar carta.");
        btnTomarMazo.setEnabled(true);
        btnTomarPila.setEnabled(true);
        btnDescartar.setEnabled(false);
        btnCerrar.setEnabled(false);
    }

    @Override
    public void descartarOCerrar() throws RemoteException {
        lblEstado.setText("Selecciona una carta para descartar o intenta cerrar.");
        btnTomarMazo.setEnabled(false);
        btnTomarPila.setEnabled(false);
        btnDescartar.setEnabled(true);
        btnCerrar.setEnabled(true);
    }

    @Override
    public void mostrarPuntos() throws RemoteException {
        StringBuilder sb = new StringBuilder("=== RESUMEN DE PUNTAJES ===\n");
        for (Jugador j : getJugadores()) {
            sb.append(j.getNombre()).append(": ").append(j.getPuntos()).append(" pts\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Fin de la Ronda", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void perder() throws RemoteException {
        JOptionPane.showMessageDialog(this, "¡Has sido eliminado o perdiste el juego!", "Derrota", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void ganar() throws RemoteException {
        JOptionPane.showMessageDialog(this, "¡Felicitaciones! ¡Ganaste la partida de Chinchón!", "¡Victoria!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void esperarNuevaRonda() throws RemoteException {
        lblEstado.setText("Preparando la siguiente ronda...");
    }
}