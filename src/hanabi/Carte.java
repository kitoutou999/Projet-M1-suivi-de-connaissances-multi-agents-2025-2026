package hanabi;
import java.util.*;

/**
 * Classe qui représente se qu'est une carte du jeu Hanabi,possède une valeur et une couleur
 */

public class Carte implements Comparable<Carte> {
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
        String couleurStr = "\u001B[1;31m";
        if(this.couleur.equals("vert")){
            couleurStr = "\u001B[1;32m";
        }
        if(this.couleur.equals("bleu")){
            couleurStr = "\u001B[1;34m";
        }
        if(this.couleur.equals("jaune")){
            couleurStr = "\u001B[1;33m";
        }
        if(this.couleur.equals("blanc")){
            couleurStr = "\u001B[1;37m";
        }
        return couleurStr+this.numero + "\u001B[0m";
    }

    public boolean isPlayable(Map<String, Deque<Carte>> pile){
        for (Map.Entry<String, Deque<Carte>> entry : pile.entrySet()) {
            String cle = entry.getKey();
            Deque<Carte> deque = entry.getValue();
            if(cle.equals(this.couleur)){
                if(deque.contains(this.numero)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int compareTo(Carte other) {

        int cmp = this.couleur.compareTo(other.getCouleur());

        if (cmp != 0) {
            return cmp;
        }

        return Integer.compare(this.numero, other.getNumero());
    }
}