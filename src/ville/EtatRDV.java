package ville;

import core.Agent;
import core.Etat;
import core.Message;
import java.util.*;

/**
 * Représente un état du jeu de rendez-vous : la position de chaque agent,
 * les messages déposés sur chaque lieu, et les routes fermées connues de chaque agent.
 */
public class EtatRDV implements Etat {
    private final Map<Agent<?>, String> positions;
    private final Map<String, List<Message>> messages;
    private final Map<Agent<?>, Set<String>> routesFermeesConnues;

    public EtatRDV(Map<Agent<?>, String> positions, Map<String, List<Message>> messages, Map<Agent<?>, Set<String>> routesFermeesConnues) {
        this.positions = new HashMap<>(positions);
        this.messages = new HashMap<>();
        for (Map.Entry<String, List<Message>> entry : messages.entrySet())
            this.messages.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        this.routesFermeesConnues = new HashMap<>();
        for (Map.Entry<Agent<?>, Set<String>> entry : routesFermeesConnues.entrySet())
            this.routesFermeesConnues.put(entry.getKey(), new HashSet<>(entry.getValue()));
    }

    public EtatRDV(Map<Agent<?>, String> positions, Map<String, List<Message>> messages) {
        this(positions, messages, new HashMap<>());
    }

    public String getPosition(Agent<?> agent) {
        return positions.get(agent);
    }

    public List<Message> getMessages(String lieu) {
        return messages.getOrDefault(lieu, Collections.emptyList());
    }

    public Map<Agent<?>, String> getPositions() {
        return positions;
    }

    public Map<String, List<Message>> getMessagesMap() {
        return messages;
    }

    public Set<String> getRoutesConnues(Agent<?> agent) {
        return routesFermeesConnues.getOrDefault(agent, Collections.emptySet());
    }

    public Map<Agent<?>, Set<String>> getRoutesFermeesConnues() {
        return routesFermeesConnues;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EtatRDV other)) return false;
        if (!messages.equals(other.messages)) return false;
        if (!routesFermeesConnues.equals(other.routesFermeesConnues)) return false;
        if (positions.size() != other.positions.size()) return false;
        for (Map.Entry<Agent<?>, String> entry : positions.entrySet()) {
            boolean trouve = false;
            for (Map.Entry<Agent<?>, String> autreEntry : other.positions.entrySet()) {
                if (autreEntry.getKey().getId().equals(entry.getKey().getId())) {
                    if (!autreEntry.getValue().equals(entry.getValue())) return false;
                    trouve = true;
                    break;
                }
            }
            if (!trouve) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String posStr = "";
        for (Map.Entry<Agent<?>, String> e : positions.entrySet()) {
            posStr += e.getKey().getId() + "=" + e.getValue() + "  ";
        }
        posStr = posStr.trim();

        String msgStr = "";
        for (Map.Entry<String, List<Message>> e : messages.entrySet()) {
            if (!e.getValue().isEmpty()) {
                msgStr += e.getKey() + "(" + e.getValue().size() + ") ";
            }
        }

        if (!msgStr.isEmpty()) {
            return posStr + "  [msgs: " + msgStr.trim() + "]";
        }
        return posStr;
    }
}
