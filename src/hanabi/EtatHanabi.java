package hanabi;
import java.util.*;
import core.*;

public class EtatHanabi implements Etat {
    private Map<Agent<?>, List<Carte>> mainsAgents;
    private Map<String, Deque<Carte>> jeu;
    private int nbErreur;
    private Agent<?> agentQuiJoue;

    public EtatHanabi(Map<Agent<?>, List<Carte>> mainsAgents, Map<String, Deque<Carte>> jeu, int nbErreur, Agent<?> agentQuiJoue){
        this.mainsAgents = new HashMap<>(mainsAgents);
        this.jeu = new HashMap<>(jeu);
        this.nbErreur = nbErreur;
        this.agentQuiJoue = agentQuiJoue;
    }

    public Map<Agent<?>, List<Carte>> getMainsAgents(){
        return mainsAgents;
    }

    public List<Carte> getMainAgent(Agent<?> agent){
        return mainsAgents.get(agent);
    }

    public int getNbErreur(){
        return nbErreur;
    }

    public Map<String, Deque<Carte>> getJeu(){
        return jeu;
    }

    public Agent<?> getAgentQuiJoue(){
        return agentQuiJoue;
    }

    public int getScore(){
        int score = 0;

        for(Deque<Carte> deque : jeu.values()){
            score += deque.size();
        }

        return score;
    }

    @Override
    public String toString() {
        String res = "Etat Hanabi :\n";
        for(Map.Entry<Agent<?>, List<Carte>> entry : mainsAgents.entrySet()){
            Agent<?> agent = entry.getKey();
            List<Carte> mainAgent = entry.getValue();
            res += "    " + agent + ": ";
            for(Carte c : mainAgent){
                res += c + " / ";
            }
            res += "\n";
        }
        res += "C'est l'agent " + agentQuiJoue + " qui joue\n";
        res += "Le jeu : " + jeu + "\n";
        res += "Score : " + this.getScore() + "\n";
        res += "Le nombre d'erreur est de " + nbErreur + "\n";
        return res;
    }
}
