package ville;

import core.Agent;
import core.Etat;
import java.util.*;

public class EtatRDV implements Etat {
    private final Map<Agent, String> positions;
    private final Map<String, List<String>> messages; // Lieu, Message

    public EtatRDV(Map<Agent, String> positions, Map<String, List<String>> messages) {
        this.positions = new HashMap<>(positions);
        this.messages = new HashMap<>();
        // Copie profonde des listes de messages
        for (var entry : messages.entrySet()) {
            this.messages.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    public String getPosition(Agent agent) {
        return positions.get(agent);
    }

    public List<String> getMessages(String lieu) {
        return messages.getOrDefault(lieu, Collections.emptyList());
    }
    
    public Map<Agent, String> getPositions() {
        return positions;
    }

    public Map<String, List<String>> getMessagesMap() {
        return messages;
    }

    @Override
    public String toString() {
        return "EtatRDV{Pos=" + positions + ", Msgs=" + messages + "}";
    }
}
