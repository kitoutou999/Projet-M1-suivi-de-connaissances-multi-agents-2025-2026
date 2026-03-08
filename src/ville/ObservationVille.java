package ville;

import core.*;
import java.util.*;

public class ObservationVille extends Observation{
    private List<Message> message = new ArrayList<>();
    public ObservationVille(Agent agent,Etat nextState,ActionJointe actions,int step,int mode){
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
            if(mode == 1){
                message.add(new MessageRouteFerme(step,actualPosition));
            }else if(mode == 2){
                message.add(new MessageRouteHoraire(-1,step,actualPosition,false));
            }
        }
        //Route ouverte
        if(!agentPos.equals(actualPosition) && actions.getAction(agentV) instanceof ActionDeplacement){
            if(mode == 2){
                message.add(new MessageRouteHoraire(step,-1,actualPosition,true));
            }
        }
        this.name = agent.toString() + message.toString();
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