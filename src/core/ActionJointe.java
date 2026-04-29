package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ActionJointe {
    private final Map<Agent, Action> actions = new HashMap<>();

    public void addAction(Agent agent, Action action) {
        actions.put(agent, action);
    }

    public Action getAction(Agent agent) {
        return actions.get(agent);
    }

    public Set<Agent> getAgents() {
        return actions.keySet();
    }

    public Map<Agent, Action> getActions(){
        return actions;
    }

    @Override
    public String toString() {
        return "ActionJointe" + actions;
    }
}
