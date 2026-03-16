package core;
import java.util.*;

public class Historique{

    private List<Map.Entry<Action,Observation>> liste;

    public Historique(){
        this.liste = new ArrayList<>();
    }

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