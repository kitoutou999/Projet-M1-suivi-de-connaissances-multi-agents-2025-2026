package hanabi;
import java.util.*;

public class Carte {
    private int numero;
    private String couleur;

    public Carte(int numero,String couleur){
        this.numero = numero;
        this.couleur = couleur;
    }

    public int getNumero(){
        return this.numero;
    }

    public String getCouleur(){
        return this.couleur;
    }

    @Override
    public String toString(){
        return "Carte : " + this.numero + "|" + this.couleur;
    }
}