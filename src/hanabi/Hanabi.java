package hanabi;
import java.util.*;

public class Hanabi {
    private List<Carte> pioche;
    private Map<String,List<Integer>> nombreCarte = new HashMap<>();
    private int nbErreurMax;

    public Hanabi(Map<String,List<Integer>> nombreCarte,int nbErreurMax){
        this.nombreCarte = nombreCarte;
        this.pioche = creerPioche();
        this.nbErreurMax = nbErreurMax; 
    }

    //Créer une pioche avec toute les cartes mélangées
    private List<Carte> creerPioche(){
        List<Carte> piocheTempo = new ArrayList<>();

        for(Map.Entry<String,List<Integer>> entry : nombreCarte.entrySet()){
            String couleur = entry.getKey();
            List<Integer> listNumero = entry.getValue();

            int numero = 1;

            for(Integer n : listNumero){
                for(int i = 0;i < n;i++){
                    Carte carte = new Carte(numero,couleur);
                    piocheTempo.add(carte);
                }
                numero++;
            }
        }
        Collections.shuffle(piocheTempo);
        return piocheTempo;
    }

    public List<Carte> getPioche(){
        return pioche;
    }

    public Carte retireCarte(){
        return pioche.remove(0);
    }

    public int getNbErreurMax(){
        return nbErreurMax;
    }

    public List<String> getListCouleur(){
        return new ArrayList<>(nombreCarte.keySet());
    } 



    
}