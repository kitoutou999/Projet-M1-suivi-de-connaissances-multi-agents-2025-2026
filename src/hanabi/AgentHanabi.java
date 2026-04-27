package hanabi;

import java.util.*;
import core.Agent;
import core.Politique;

public class AgentHanabi extends Agent{
    private List<Carte> carteBase = new ArrayList<>();
    private Map<Agent, List<Carte>> autresMains;
    
    public AgentHanabi(Politique politique,String id,List<Carte> carteBase){
        super(politique,id);
        this.carteBase = carteBase;
        this.autresMains = new HashMap<Agent, List<Carte>>();
    }

    public List<Carte> getCarteBase(){
        return this.carteBase;
    }
    
    public Map<Agent, List<Carte>> getAutresMains(){
    	return this.autresMains;
    }
}
