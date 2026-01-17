package hanabi;
import java.util.*;

public class Hanabi {
    private List<Carte> pioche;
    private Map<String,List<Integer>> nombreCarte = new HashMap<>();

    public Hanabi(Map<String,List<Integer>> nombreCarte){
        this.nombreCarte = nombreCarte;
        this.pioche = creerPioche();
    }

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
        return this.pioche;
    }

    public Carte retireCarte(){
        return pioche.remove(0);
    }



    
}