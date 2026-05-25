package ville;

import core.*;
import java.util.*;

public class ObservationVille extends Observation{
    private List<Message> message = new ArrayList<>();

    private ObservationVille(Agent<?> agent, EtatRDV etat, ActionJointe actions, String positionAvant, int step, int mode) {
        construireMessages(agent, etat, actions, positionAvant, step, mode);
        this.name = agent.toString() + message.toString();
    }

    public static ObservationVille creer(Agent<?> agent, Etat nextState, ActionJointe actions, int step, int mode) {
        EtatRDV nextStateRDV = (EtatRDV) nextState;
        AgentVille agentV = (AgentVille) agent;
        String positionAvant = agentV.getPosition();
        ObservationVille obs = new ObservationVille(agent, nextStateRDV, actions, positionAvant, step, mode);
        agentV.setPosition(nextStateRDV.getPosition(agent));
        return obs;
    }

    private void construireMessages(Agent<?> agent, EtatRDV etat, ActionJointe actions, String positionAvant, int step, int mode) {
        String agentPos = etat.getPosition(agent);
        for (Map.Entry<Agent<?>, String> entry : etat.getPositions().entrySet()) {
            Agent<?> autreAgent = entry.getKey();
            if (entry.getValue().equals(agentPos) && !autreAgent.equals(agent)) {
                message.add(new MessageAgentPresent(step, agentPos, autreAgent));
                Action autreAction = actions.getAction(autreAgent);
                if (autreAction instanceof ActionDeplacement ad && !entry.getValue().equals(ad.destination()))
                    message.add(new MessageAgentBloque(step, autreAgent, ad.destination()));
            }
        }
        if (agentPos.equals(positionAvant) && actions.getAction(agent) instanceof ActionDeplacement ad) {
            if (mode == 1) message.add(new MessageRouteFerme(step, ad.destination()));
            else if (mode == 2) message.add(new MessageRouteHoraire(-1, step, ad.destination(), false));
        }
        if (!agentPos.equals(positionAvant) && actions.getAction(agent) instanceof ActionDeplacement) {
            if (mode == 2) message.add(new MessageRouteHoraire(step, -1, positionAvant, true));
        }
        for (Message m : etat.getMessages(agentPos)) message.add(m);
    }

    public static ObservationVille simuler(Agent<?> agent, EtatRDV etatHyp, ActionJointe actionsHyp, String positionAvant, int step, int mode) {
        return new ObservationVille(agent, etatHyp, actionsHyp, positionAvant, step, mode);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ObservationVille other)) return false;
        return messagesCles().equals(other.messagesCles());
    }

    @Override
    public int hashCode() {
        return messagesCles().hashCode();
    }

    private Set<String> messagesCles() {
        Set<String> keys = new HashSet<>();
        for (Message m : message) {
            if (m instanceof MessageAgentPresent map)
                keys.add("present:" + map.getAgent().getId());
            else if (m instanceof MessageRouteFerme mrf)
                keys.add("ferme:" + mrf.getName());
            else if (m instanceof MessageAgentBloque mab)
                keys.add("bloque:" + mab.getAgent().getId() + ":" + mab.getDestination());
        }
        return keys;
    }

    public List<Message> getMessage(){
        return this.message;
    }

    @Override
    public String toString(){
        String chaine = "";
        for (Message m : message) {
            
            chaine = chaine + m.toString() + " | ";
        }
        if(chaine.equals("")){
            return " vide ";
        }
        return chaine;
    }
}