package core;

import java.util.List;
import java.util.Random;



public class PolitiqueRandom implements Politique {
    private final Random random = new Random();

    @Override
    public Action chooseAction(Agent agent, Historique historique, List<Action> actionsPossibles) {
        if (actionsPossibles.isEmpty()) {
            return null;
        }
        return actionsPossibles.get(random.nextInt(actionsPossibles.size()));
    }
}
