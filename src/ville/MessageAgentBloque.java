package ville;

import core.*;

public class MessageAgentBloque implements Message {
    private final int step;
    private final Agent<?> agent;
    private final String destination;

    public MessageAgentBloque(int step, Agent<?> agent, String destination) {
        this.step = step;
        this.agent = agent;
        this.destination = destination;
    }

    public Agent<?> getAgent() { return agent; }
    public String getDestination() { return destination; }
    public int getStep() { return step; }

    @Override
    public String toString() {
        return agent.getId() + " bloqué vers " + destination + " au pas " + step;
    }
}
