package core;

import java.util.List;

/**
 * Interface représentant la stratégie de décision d'un agent.
 * Toute politique concrète doit implémenter {@code chooseAction} pour déterminer
 * quelle action l'agent effectue à chaque tour.
 */
public interface Politique {

    /** Choisit une action parmi les actions disponibles pour un agent à un instant donné. */
    Action chooseAction(Agent<?> agent, Historique historique, List<Action> actionsPossibles);
}
