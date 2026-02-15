package ville;

import core.*;
import java.util.*;

public class ObservationVille extends Observation{
    private List<Message> message = new ArrayList<>();
    public ObservationVille(Agent agent,Etat nextState,ActionJointe actions,int step){
        this.name = agent.toString() + nextState.toString() + actions.toString();
        EtatRDV nextStateRDV = (EtatRDV) nextState;
        String agentPos = nextStateRDV.getPosition(agent);
        Map<Agent, String> positions = nextStateRDV.getPositions();

        //Présence d'agent(s) à la même pos que l'agent
        for (Map.Entry<Agent, String> entry : positions.entrySet()) {

            Agent agentFromMap = entry.getKey();
            String positionOfAgent = entry.getValue();

            if(positionOfAgent.equals(agentPos)){
                if(agentFromMap.equals(agent)== false){
                    message.add(new MessageAgentPresent(step,positionOfAgent,agentFromMap));
                }
            }
        }

        //Route fermée
        AgentVille agentV = (AgentVille) agent;
        String actualPosition = agentV.getPosition();
        if(agentPos.equals(actualPosition) && actions.getAction(agentV) instanceof ActionDeplacement){
            message.add(new MessageRouteFerme(step,actualPosition));
        }
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