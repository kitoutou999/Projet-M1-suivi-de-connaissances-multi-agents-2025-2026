package hanabi;
import java.util.*;
import core.*;

public class JeuHanabi extends Jeu {

    private final Hanabi hanabi;
    private final List<Agent> agents;
    private final EtatHanabi etatInitial;

    public JeuHanabi(Hanabi hanabi,List<Agent> agents,Map<Agent,List<Carte>> mainsAgents){
        this.hanabi = hanabi;
        this.agents = agents;
        this.etatInitial = new EtatHanabi(mainsAgents,new ArrayDeque<>(),0);
    }

    @Override
    public Etat getInitialState() {
        return etatInitial;
    }

    @Override
    public boolean isFinal(Etat etat) {
        return false;
    }

    @Override
    public Etat nextState(Etat etat, ActionJointe actions) {
        EtatHanabi e = (EtatHanabi) etat;
        return e;
    }

    @Override
    public List<Action> getActionsPossibles(Agent agent, Etat etat) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionMessage());
        actions.add(new ActionDefausse());
        actions.add(new ActionPoser());
        return actions;
    }


}