package hanabi;

import java.util.*;
import core.Agent;
import core.Politique;

public class AgentHanabi extends Agent{
    private List<Carte> carteBase = new ArrayList<>();
    
    public AgentHanabi(Politique politique,String id,List<Carte> carteBase){
        super(politique,id);
        this.carteBase = carteBase;
    }

    public List<Carte> getCarteBase(){
        return this.carteBase;
    }
}