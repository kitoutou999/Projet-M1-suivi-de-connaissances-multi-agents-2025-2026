package ville;

import core.Agent;
import core.Etat;
import core.Message;
import java.util.*;

public class EtatRDV implements Etat {
    private final Map<Agent, String> positions;
    private final Map<String, List<Message>> messages; // Lieu -> liste de messages

    public EtatRDV(Map<Agent, String> positions, Map<String, List<Message>> messages) {
        this.positions = new HashMap<>(positions);
        this.messages = new HashMap<>();
        for (Map.Entry<String, List<Message>> entry : messages.entrySet()) {
            this.messages.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    public String getPosition(Agent agent) {
        return positions.get(agent);
    }

    public List<Message> getMessages(String lieu) {
        return messages.getOrDefault(lieu, Collections.emptyList());
    }

    public Map<Agent, String> getPositions() {
        return positions;
    }

    public Map<String, List<Message>> getMessagesMap() {
        return messages;
    }

    @Override
    public String toString() {
        return "EtatRDV{Pos=" + positions + ", Msgs=" + messages + "}";
    }
}
