package ville;

import core.*;
import java.util.*;

public class ObservationVille extends Observation{
    private List<Message> message = new ArrayList<>();

    public ObservationVille(Agent agent, Etat nextState, ActionJointe actions, int step, int mode) {
        EtatRDV nextStateRDV = (EtatRDV) nextState;
        AgentVille agentV = (AgentVille) agent;
        String positionAvant = agentV.getPosition();
        construireMessages(agent, nextStateRDV, actions, positionAvant, step, mode);
        agentV.setPosition(nextStateRDV.getPosition(agent));
        this.name = agent.toString() + message.toString();
    }

    private ObservationVille(Agent agent, EtatRDV etatHyp, ActionJointe actionsHyp, String positionAvant, int step, int mode) {
        construireMessages(agent, etatHyp, actionsHyp, positionAvant, step, mode);
        this.name = agent.toString() + message.toString();
    }

    private void construireMessages(Agent agent, EtatRDV etat, ActionJointe actions, String positionAvant, int step, int mode) {
        String agentPos = etat.getPosition(agent);
        for (Map.Entry<Agent, String> entry : etat.getPositions().entrySet()) {
            if (entry.getValue().equals(agentPos) && !entry.getKey().equals(agent))
                message.add(new MessageAgentPresent(step, agentPos, entry.getKey()));
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

    public static ObservationVille simuler(Agent agent, EtatRDV etatHyp, ActionJointe actionsHyp, String positionAvant, int step, int mode) {
        return new ObservationVille(agent, etatHyp, actionsHyp, positionAvant, step, mode);
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