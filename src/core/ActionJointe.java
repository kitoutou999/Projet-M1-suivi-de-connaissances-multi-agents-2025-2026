package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Regroupe les actions choisies par tous les agents pour un même tour.
 * Passée à {@link Jeu#nextState} pour calculer l'état suivant.
 */
public class ActionJointe {
    private final Map<Agent<?>, Action> actions = new HashMap<>();

    /** Associe une action à un agent pour ce tour. */
    public void addAction(Agent<?> agent, Action action) {
        actions.put(agent, action);
    }

    public Action getAction(Agent<?> agent) {
        return actions.get(agent);
    }

    public Set<Agent<?>> getAgents() {
        return actions.keySet();
    }

    public Map<Agent<?>, Action> getActions(){
        return actions;
    }

    @Override
    public String toString() {
        return "\033[1mActionJointe\033[0m" + actions;
    }
}
