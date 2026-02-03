package ville;

import core.*;
import java.util.*;

public class MessageAgentPresent implements Message {
    private int step;
    private String name;
    private Agent agent;
    public MessageAgentPresent(int step,String name,Agent agent){
        this.step = step;
        this.name = name;
        this.agent = agent;
    }
    @Override
    public String toString(){
        String info = "L'agent "+this.agent.toString()+" était présent sur le lieu "+this.name+" au pas de temps "+this.step+".";
        return info;
    }
    public int getStep(){
        return this.step;
    }
    public String getName(){
        return this.name;
    }
    public Agent getAgent(){
        return this.agent;
    }
}
