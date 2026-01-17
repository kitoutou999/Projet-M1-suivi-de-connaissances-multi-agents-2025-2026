package hanabi;
import java.util.*;
import core.*;

public class EtatHanabi implements Etat {
    private Map<Agent,List<Carte>> mainsAgents; 
    private Deque<Carte> jeu; 
    private int nbErreur;

    public EtatHanabi(Map<Agent,List<Carte>> mainsAgents,Deque<Carte> jeu,int nbErreur){
        this.mainsAgents = new HashMap<>(mainsAgents);
        this.jeu = new ArrayDeque<>(jeu);
        this.nbErreur = nbErreur;
    }

    public List<Carte> getMainAgent(Agent agent){
        return mainsAgents.get(agent);
    }

    public int getNbErreur(){
        return nbErreur;
    }

    public Deque<Carte> GetJeu(){
        return jeu;
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
        return res;
    }

}

