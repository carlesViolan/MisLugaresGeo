package mislugares.example.carles.mislugares;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by carles on 20/05/2016.
 */
public enum TipoLugar implements Serializable {
    OTROS ("Otros",R.drawable.otros),
    RESTAURANTE ("Restaurante",R.drawable.restaurante),
    BAR("Bar",R.drawable.bar),
    COPAS("Copas",R.drawable.bar),
    ESPECTACULO("Espectáculo",R.drawable.espectaculos),
    HOTEL ("Hotel",R.drawable.hotel),
    COMPRAS("Compras",R.drawable.compras),
    EDUCACION("Educación",R.drawable.educacion),
    NATURALEZA("Naturaleza",R.drawable.naturaleza),
    DEPORTE("Deporte", R.drawable.deporte),
    GASOLINERA("Gasolinera",R.drawable.gasolinera);

    private String texto;
    private int recurso;

    TipoLugar(String texto, int recurso){

        this.texto = texto;
        this.recurso = recurso;

    }


    public String getTexto(){ return texto; }
    public int getRecurso(){ return recurso; }

    static TipoLugar getTipo (String s) {
        s = s.toUpperCase();

        switch (s) {
            case "RESTAURANTE":
                return RESTAURANTE;
            case "BAR":
                return BAR;
            case "COPAS":
                return COPAS;
            case "ESPECTACULO":
                return ESPECTACULO;
            case "HOTEL":
                return HOTEL;
            case "EDUCACION":
                return EDUCACION;
            case "NATURALEZA":
                return NATURALEZA;
            case "DEPORTE":
                return DEPORTE;
            case "GASOLINERA":
                return GASOLINERA;
            default:
                return OTROS;
        }
        //return OTROS;
    }

    public static String[] getNombres(){

        String[] resultado = new String[TipoLugar.values().length];

        for (TipoLugar tipo : TipoLugar.values()){

            resultado[tipo.ordinal()] = tipo.texto;
        }
        return resultado;
    }

}
