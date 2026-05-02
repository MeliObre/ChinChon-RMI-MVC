package Juego.modelo;

import Juego.enumerados.Palo;
import java.io.Serializable; // Necesario para que la carta viaje por la red

public class Carta implements Serializable{
    // Es buena práctica poner un ID de versión para la serialización
    private static final long serialVersionUID = 1L;

    private final Palo palo;
    private final int numero;

    public Carta(Palo palo, int numero) {
        this.palo = palo;
        this.numero = numero;
    }

    public Palo getPalo() {
        return palo;
    }

    public int getNumero() {
        return numero;
    }

    @Override
    public String toString() {
        return this.numero + " de " + this.palo;
    }

}
