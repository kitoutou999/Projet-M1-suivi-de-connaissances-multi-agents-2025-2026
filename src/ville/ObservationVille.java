package ville;

import core.*;
import java.util.*;

public class ObservationVille extends Observation{
    private List<Message> message = new ArrayList<>();
    public ObservationVille(Agent agent,Etat nextState,ActionJointe actions,int step,int mode){
        EtatRDV nextStateRDV = (EtatRDV) nextState;
        String agentPos = nextStateRDV.getPosition(agent);
        Map<Agent, String> positions = nextStateRDV.getPositions();
        //Présence d'agent à la même pos que l'agent
        for (Map.Entry<Agent, String> entry : positions.entrySet()) {

            Agent agentFromMap = entry.getKey();
            String positionOfAgent = entry.getValue();

            if(positionOfAgent.equals(agentPos)){
                if(agentFromMap.equals(agent)== false){
                    message.add(new MessageAgentPresent(step,positionOfAgent,agentFromMap));
                }
            }
        }

        //Route fermée — lire actualPosition AVANT la mise à jour
        AgentVille agentV = (AgentVille) agent;
        String actualPosition = agentV.getPosition();
        if(agentPos.equals(actualPosition) && actions.getAction(agentV) instanceof ActionDeplacement ad){
            String destinationTentee = ad.destination();
            if(mode == 1){
                message.add(new MessageRouteFerme(step, destinationTentee));
            }else if(mode == 2){
                message.add(new MessageRouteHoraire(-1,step, destinationTentee,false));
            }
        }
        //Route ouverte
        if(!agentPos.equals(actualPosition) && actions.getAction(agentV) instanceof ActionDeplacement){
            if(mode == 2){
                message.add(new MessageRouteHoraire(step,-1,actualPosition,true));
            }
        }
        // Messages laissés par d'autres agents sur ce lieu
        for(Message m : nextStateRDV.getMessages(agentPos)){
            message.add(m);
        }
        // Synchronise la position de l'agent avec le nouvel état
        agentV.setPosition(agentPos);
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