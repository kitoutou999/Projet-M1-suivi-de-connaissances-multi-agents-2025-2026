package ville;

import core.*;
import core.Message;
import java.util.*;

public class JeuRDV extends Jeu {

    private final Ville ville;
    private final String lieuRDV;
    private final List<Agent> agents;
    private final EtatRDV etatInitial;
    private final int mode;//1 pour simple avec route fermée définitivement.

    public JeuRDV(Ville ville, String lieuRDV, List<Agent> agents, Map<Agent, String> positionsInitiales) {
        this.ville = ville;
        this.lieuRDV = lieuRDV;
        this.agents = agents;
        this.etatInitial = new EtatRDV(positionsInitiales, new HashMap<>());
        this.mode = 1;
    }

    public int getMode() { return mode; }

    @Override
    public Etat getInitialState() {
        return etatInitial;
    }

    @Override
    public boolean isFinal(Etat etat) {
        EtatRDV e = (EtatRDV) etat;

        for (Agent a : agents) {
            if (!lieuRDV.equals(e.getPosition(a))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Etat nextState(Etat etat, ActionJointe actions) {
        EtatRDV e = (EtatRDV) etat;

        Map<Agent, String> nouvellesPositions = new HashMap<>(e.getPositions());
        Map<String, List<Message>> nouveauxMessages = new HashMap<>(e.getMessagesMap());
        // On recrée les listes pour éviter les effets de bord sur l'ancien état
        for(String key : new HashSet<>(nouveauxMessages.keySet())) {
            nouveauxMessages.put(key, new ArrayList<>(nouveauxMessages.get(key)));
        }

        for (Agent agent : actions.getAgents()) {
            Action action = actions.getAction(agent);
            String positionActuelle = e.getPosition(agent);

            if (action instanceof ActionDeplacement ad) {
                String destination = ad.destination();
                if (ville.estOuvert(destination)) {
                    nouvellesPositions.put(agent, destination);
                } else {
                    System.out.println("Lieu fermé : " + destination + " (Agent " + agent.getId() + " bloqué)");
                }
            } else if (action instanceof ActionMessage am) {
                nouveauxMessages.computeIfAbsent(positionActuelle, k -> new ArrayList<>()).add(am.contenu());
            }
            // ActionRester ne fait rien explicitement
        }

        return new EtatRDV(nouvellesPositions, nouveauxMessages);

    }

    @Override
    public List<Action> getActionsPossibles(Agent agent, Etat etat) {
        EtatRDV e = (EtatRDV) etat;

        String pos = e.getPosition(agent);
        List<Action> actions = new ArrayList<>();

        for (String voisin : ville.getVoisins(pos)) {
            actions.add(new ActionDeplacement(voisin));
        }

        actions.add(new ActionRester());

        return actions;
    }

    @Override
    public Observation makeObservation(Agent agent,ActionJointe actions,Etat nextState,int step){
        return new ObservationVille(agent,nextState,actions,step,this.mode);
    }
}
