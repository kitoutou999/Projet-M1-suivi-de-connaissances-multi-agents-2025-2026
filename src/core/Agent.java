package core;
import java.util.*;

/**
 * Représente un agent autonome dans le système multi-agents.
 * Un agent possède une politique de décision, un historique de ses actions et observations,
 * et optionnellement un état de croyance (BeliefState) pour raisonner sous incertitude.
 */
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

    /** Délègue le choix d'une action à la politique de l'agent. */
    public Action chooseAction(List<Action> actionsPossibles){
        return this.politique.chooseAction(this, this.historique,actionsPossibles);
    }

    /** Enregistre la paire (action, observation) dans l'historique de l'agent après chaque tour. */
    public void inform(Observation observation,Action action){
        this.historique.addToHistorique(action,observation);
    }

    public String getId(){
        return this.id;
    }

    public Historique getHistorique(){
        return this.historique;
    }

    /** Associe un BeliefState à cet agent pour lui permettre de raisonner sous incertitude. */
    public void setBeliefState(BeliefState<E> bs) { this.beliefState = bs; }

    public BeliefState<E> getBeliefState() { return this.beliefState; }
}
