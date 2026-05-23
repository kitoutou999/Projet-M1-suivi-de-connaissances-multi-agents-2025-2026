package hanabi;
import java.util.*;
import core.*;

public class ObservationHanabi extends Observation {

    private final Map<Agent<?>, List<Carte>> mainsAutresAgents;
    private final Map<String, Deque<Carte>> etatPiles;
    private final int nbErreurs;
    private final Agent<?> agent;
    private final ActionJointe actions;

    public ObservationHanabi(Agent<?> agent, Etat nextState, ActionJointe actions) {
        EtatHanabi etat = (EtatHanabi) nextState;
        this.agent = agent;
        this.actions = actions;

        // Mains des autres agents (pas la sienne)
        this.mainsAutresAgents = new HashMap<>();
        for (Map.Entry<Agent<?>, List<Carte>> entry : etat.getMainsAgents().entrySet()) {
            if (!entry.getKey().equals(agent)) {
                mainsAutresAgents.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }

        // État des piles
        this.etatPiles = new HashMap<>();
        for (Map.Entry<String, Deque<Carte>> entry : etat.getJeu().entrySet()) {
            etatPiles.put(entry.getKey(), new ArrayDeque<>(entry.getValue()));
        }

        // Nombre d'erreurs
        this.nbErreurs = etat.getNbErreur();
    }

    public Map<Agent<?>, List<Carte>> getMainsAutresAgents() {
        return mainsAutresAgents;
    }

    public Map<String, Deque<Carte>> getEtatPiles() {
        return etatPiles;
    }

    public int getNbErreurs() {
        return nbErreurs;
    }

    public ActionJointe getActions(){
        return actions;
    }

    @Override
    public String toString() {
        String chaine = "Observations de " + this.agent + " :\n";
        chaine += "\tMains visibles : ";
        for (Map.Entry<Agent<?>, List<Carte>> entry : mainsAutresAgents.entrySet()) {
            chaine += entry.getKey() + " -> " + entry.getValue() + " | ";
        }
        chaine += ":\n\tPiles : " + etatPiles + " | ";
        chaine += "\n\tErreurs : " + nbErreurs + "\n";
        return chaine;
    }
}
