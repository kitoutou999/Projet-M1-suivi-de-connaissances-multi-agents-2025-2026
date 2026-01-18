package ville;

import core.*;

public class ObservationVille extends Observation{
    public Agent agent;
    public Etat nextState;
    public ActionJointe actions;
    public ObservationVille(Agent agent,Etat nextState,ActionJointe actions){
        this.agent = agent;
        this.nextState = nextState;
        this.actions = actions;
    }
}