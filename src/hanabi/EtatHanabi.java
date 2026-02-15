package hanabi;
import java.util.*;
import core.*;

public class EtatHanabi implements Etat {
    private Map<Agent,List<Carte>> mainsAgents; 
    private Map<String,Deque<Carte>> jeu; 
    private int nbErreur;
    private List<String> listMessage;
    private Agent agentQuiJoue;

    public EtatHanabi(Map<Agent,List<Carte>> mainsAgents,Map<String,Deque<Carte>> jeu,int nbErreur,List<String> listMessage,Agent agentQuiJoue){
        this.mainsAgents = new HashMap<>(mainsAgents);
        this.jeu = new HashMap<>(jeu);
        this.nbErreur = nbErreur;
        this.listMessage = new ArrayList<>(listMessage);
        this.agentQuiJoue = agentQuiJoue;
    }

    public Map<Agent,List<Carte>> getMainsAgents(){
        return mainsAgents;
    }

    public List<Carte> getMainAgent(Agent agent){
        return mainsAgents.get(agent);
    }

    public int getNbErreur(){
        return nbErreur;
    }

    public Map<String,Deque<Carte>> getJeu(){
        return jeu;
    }

    public List<String> getListMessage(){
        return listMessage;
    }

    public Agent getAgentQuiJoue(){
        return agentQuiJoue;
    }

    @Override
    public String toString() {
        String res = "Etat Hanabi :\n" ;
        for(Map.Entry<Agent,List<Carte>> entry : mainsAgents.entrySet()){
            Agent agent = entry.getKey();
            List<Carte> mainAgent = entry.getValue();

            res += "    " + agent + ": ";
            for(Carte c : mainAgent){
                res += c + " / ";
            }
            res += "\n";
        }
        res += "C'est l'agent " + agentQuiJoue + " qui joue\n";

        if(!listMessage.isEmpty()){
            res += "Les messages "+listMessage + "\n";
        }

        res += "Le jeu : "+jeu + "\n";
        res += "Le nombre d'erreur est de " + nbErreur + "\n";
    
        return res;
    }

}

