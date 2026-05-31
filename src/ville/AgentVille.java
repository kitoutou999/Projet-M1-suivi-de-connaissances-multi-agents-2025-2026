package ville;

import core.Agent;
import core.Politique;

/**
 * Spécialisation d'Agent pour le scénario Ville.
 * Maintient la position courante de l'agent sur la carte.
 */
public class AgentVille extends Agent<EtatRDV>{
    private String position;
    public AgentVille(Politique politique,String id,String position){
        super(politique,id);
        this.position = position;
    }

    public String getPosition(){
        return this.position;
    }

    public void setPosition(String newPosition){
        this.position = newPosition;
    }
}