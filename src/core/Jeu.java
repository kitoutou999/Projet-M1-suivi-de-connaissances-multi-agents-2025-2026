package core;

import java.util.List;


public abstract class Jeu {

    public abstract Etat getInitialState();

    public abstract boolean isFinal(Etat etat);

    public abstract Etat nextState(Etat etat, ActionJointe actions);

    public abstract List<Action> getActionsPossibles(Agent agent, Etat etat);

    public abstract void makeObservation(Agent agent,ActionJointe actions,Etat nextState);
}
