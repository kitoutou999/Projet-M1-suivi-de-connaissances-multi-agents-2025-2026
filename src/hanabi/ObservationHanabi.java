package hanabi;
import java.util.*;
import core.*;

public class ObservationHanabi extends Observation{

    private List<Message> message = new ArrayList<>();

    public ObservationHanabi(Agent agent,Etat nextState,ActionJointe actions){
        this.name = agent.toString() + nextState.toString() + actions.toString();
    }

    public List<Message> getMessage(){
        return this.message;
    }

}