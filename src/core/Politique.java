package core;

import java.util.List;

public interface Politique {

    Action chooseAction(Agent<?> agent, Historique historique, List<Action> actionsPossibles);
}
