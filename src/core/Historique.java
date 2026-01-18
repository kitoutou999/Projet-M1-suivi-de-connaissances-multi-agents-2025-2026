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
}