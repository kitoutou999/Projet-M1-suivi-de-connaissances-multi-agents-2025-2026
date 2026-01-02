package core;

import java.util.List;

public interface Politique {

    Action chooseAction(Agent agent, Etat etat, List<Action> actionsPossibles);
}
