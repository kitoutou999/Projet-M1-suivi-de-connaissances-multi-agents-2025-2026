package core;
import java.util.*;

public class Agent{
    private Politique politique;
    private String id;
    private Historique historique;
    public Agent(Politique politique,String id){
        this.politique = politique;
        this.id = id;
        this.historique = new Historique();
    }
    @Override
    public String toString() {
        return "Agent[" + this.id + "]";
    }
    public Action chooseAction(Etat etat,List<Action> actionsPossibles){
        Action action = this.politique.chooseAction(this,etat,actionsPossibles);
        return action;
    }
    public void inform(Observation observation,Action action){
        this.historique.addToHistorique(action,observation);
    }
    public String getId(){
        return this.id;
    }
    public Historique getHistorique(){
        return this.historique;
    }
}
