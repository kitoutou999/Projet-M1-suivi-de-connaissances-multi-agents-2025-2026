package core;
import java.util.*;

public class Historique{

    private HashMap<Action,Observation> liste;

    public Historique(){
        this.liste = new HashMap<>();
    }

    public void addToHistorique(Action action,Observation observation){
        this.liste.put(action,observation);
    }

    public Observation getFromHistorique(Action action){
        return this.liste.get(action);
    }
    @Override
    public String toString(){
        String chaine = "";
        for (Map.Entry<Action, Observation> entry : liste.entrySet()) {
            Action key = entry.getKey();
            Observation value = entry.getValue();
            chaine = chaine + key + " - " + value + " | ";
        }
        return chaine;
    }
}