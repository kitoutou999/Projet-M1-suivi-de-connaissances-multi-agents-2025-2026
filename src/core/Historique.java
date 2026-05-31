package core;
import java.util.*;

/**
 * Mémoire d'un agent : stocke la séquence ordonnée des paires (Action, Observation)
 * accumulées au fil des tours. Consultée par la politique pour prendre des décisions informées.
 */
public class Historique{

    private List<Map.Entry<Action,Observation>> liste;

    public Historique(){
        this.liste = new ArrayList<>();
    }

    /** Ajoute une paire (action, observation) à la fin de l'historique. */
    public void addToHistorique(Action action,Observation observation){
        this.liste.add(Map.entry(action,observation));
    }

    public List<Map.Entry<Action, Observation>> getListe(){
        return liste;
    }

    public Map.Entry<Action, Observation> getDerniere(){
        if (liste.isEmpty()) return null;
        return liste.get(liste.size() - 1);
    }

    /** Recherche en remontant l'historique la dernière observation associée à une action donnée. */
    public Observation getFromHistorique(Action action){
        for (int i = liste.size() - 1; i >= 0; i--) {
            if (liste.get(i).getKey().equals(action)) {
                return liste.get(i).getValue();
            }
        }
        return null;
    }

    @Override
    public String toString(){
        String chaine = "";
        for (Map.Entry<Action, Observation> entry : liste) {
            chaine = chaine + entry.getKey() + " - " + entry.getValue() + " | ";
        }
        return chaine;
    }
}