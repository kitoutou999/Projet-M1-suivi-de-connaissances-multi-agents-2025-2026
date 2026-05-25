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
}