package core;

import java.util.List;
import java.util.Random;

/**
 * Implémentation de {@link Politique} qui choisit une action au hasard parmi les actions disponibles.
 * Ne consulte pas l'historique de l'agent. Sert principalement de référence de base (baseline)
 * pour comparer avec des politiques plus intelligentes.
 */
public class PolitiqueRandom implements Politique {
    private final Random random = new Random();

    /** Choisit aléatoirement une action parmi les actions possibles. */
    @Override
    public Action chooseAction(Agent<?> agent, Historique historique, List<Action> actionsPossibles) {
        if (actionsPossibles.isEmpty()) {
            return null;
        }
        return actionsPossibles.get(random.nextInt(actionsPossibles.size()));
    }
}
