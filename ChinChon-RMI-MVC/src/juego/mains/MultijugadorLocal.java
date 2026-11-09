package juego.mains;

import juego.controlador.Controlador;
import juego.modelo.Juego;
import juego.vista.vista2D.IVista;
import juego.vista.vista2D.VentanaJuego2D; // Importamos la nueva vista 2D

import java.rmi.RemoteException;

public class MultijugadorLocal {
    public static void main(String[] args) throws RemoteException {
        // 1. Un solo modelo en memoria para ambos jugadores
        Juego modelo = new Juego();

        // 2. Creamos la primera vista gráfica (Jugador 1)
        Controlador controlador1 = new Controlador();
        controlador1.setModeloRemoto(modelo);
        IVista vista1 = new VentanaJuego2D(controlador1); // Usamos VentanaJuego2D

        // 3. Creamos la segunda vista gráfica (Jugador 2)
        Controlador controlador2 = new Controlador();
        controlador2.setModeloRemoto(modelo);
        IVista vista2 = new VentanaJuego2D(controlador2); // Usamos VentanaJuego2D

        // 4. Iniciamos las interfaces (esto abrirá las VentanaInicioSesion)
        vista1.iniciar();
        vista2.iniciar();
    }
}
