package core;
import java.util.*;

public class Agent<E extends Etat>{
    private Politique politique;
    private String id;
    private Historique historique;
    private BeliefState<E> beliefState = null;
    public Agent(Politique politique,String id){
        this.politique = politique;
        this.id = id;
        this.historique = new Historique();
    }
    @Override
    public String toString() {
        return "Agent[" + this.id + "]";
    }
    public Action chooseAction(List<Action> actionsPossibles){
        Action action = this.politique.chooseAction(this,this.historique,actionsPossibles);
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
    public void setBeliefState(BeliefState<E> bs) { this.beliefState = bs; }
    public BeliefState<E> getBeliefState() { return this.beliefState; }
}
