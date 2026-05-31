package hanabi;

import java.util.*;
import core.Agent;
import core.Politique;

/**
 * Classe qui hérite d'un Agent et qui représente se qu'est un Agent jouant à Hanabi,il possède une liste repésentant ses propres cartes ainsi qu'un dictionnaire pour les cartes des autres agents.
 */

public class AgentHanabi extends Agent<EtatHanabi>{
    private List<Carte> carteBase = new ArrayList<>();
    private Map<Agent<?>, List<Carte>> autresMains;
    
    public AgentHanabi(Politique politique,String id,List<Carte> carteBase){
        super(politique,id);
        this.carteBase = carteBase;
        this.autresMains = new HashMap<Agent<?>, List<Carte>>();
    }

    public List<Carte> getCarteBase(){
        return this.carteBase;
    }
    
    public Map<Agent<?>, List<Carte>> getAutresMains(){
    	return this.autresMains;
    }
}
