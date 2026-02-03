package ville;

import core.*;
import java.util.*;

public class ObservationVille extends Observation{
    private List<Message> message = new ArrayList<>();
    public ObservationVille(Agent agent,Etat nextState,ActionJointe actions){

        EtatRDV nextStateRDV = (EtatRDV) nextState;
        String agentPos = nextStateRDV.getPosition(agent);
        Map<Agent, String> positions = nextStateRDV.getPositions();
        
        for (Map.Entry<Agent, String> entry : positions.entrySet()) {

            Agent agentFromMap = entry.getKey();
            String positionOfAgent = entry.getValue();

            if(positionOfAgent == agentPos){
                if(agentFromMap.equals(agent)== false){
                    //faire en sorte qu'on connaisse le pas de temps actuel et le mettre à la place du 0
                    message.add(new MessageAgentPresent(0,positionOfAgent,agentFromMap));
                }
            }
        }
    }

    public List<Message> getMessage(){
        return this.message;
    }
}