package ville;

import core.*;
import java.util.*;

public class BeliefStateFilterVille implements BeliefStateFilter<EtatRDV> {

    private final int mode;

    public BeliefStateFilterVille(int mode) {
        this.mode = mode;
    }

    /**
     * Génère toutes les transitions possibles depuis un état.
     * Pour chaque combinaison d'actions possibles de tous les agents, calcule l'état suivant via nextState.
     */
    @Override
    public Set<Transition<EtatRDV>> transitions(EtatRDV etat, Jeu jeu, List<Agent> tousLesAgents) {
        List<List<Map.Entry<Agent, Action>>> listes = new ArrayList<>();
        for (Agent agent : tousLesAgents) {
            List<Action> actions = jeu.getActionsPossibles(agent, etat);
            List<Map.Entry<Agent, Action>> pairs = new ArrayList<>();
            for (Action a : actions){
                pairs.add(Map.entry(agent, a));
            }
            listes.add(pairs);
        }
        Set<Transition<EtatRDV>> result = new HashSet<>();
        for (ActionJointe aj : generateJointActions(listes)) {
            EtatRDV succ = (EtatRDV) jeu.nextState(etat, aj);
            result.add(new Transition<>(etat, aj, succ));
        }
        return result;
    }

    /**
     * Vérifie si une transition est cohérente avec l'observation réelle.
     * Simule ce qu'il aurait observé dans cette transition.
     */
    @Override
    public boolean estCompatible(Transition<EtatRDV> t, Agent observateur, Observation obsReelle, Jeu jeu, int step) {
        ObservationVille obsV = (ObservationVille) obsReelle;
        String posAvant = t.precedent().getPosition(observateur);
        ObservationVille obsSim = ObservationVille.simuler(observateur, t.suivant(), t.action(), posAvant, step, mode);
        return messagesCompatibles(obsV, obsSim);
    }

    private boolean messagesCompatibles(ObservationVille obsReelle, ObservationVille obsSim) {
        return messagesCles(obsReelle).equals(messagesCles(obsSim));
    }

    private Set<String> messagesCles(ObservationVille obs) {
        Set<String> keys = new HashSet<>();
        for (Message m : obs.getMessage()) {
            if (m instanceof MessageAgentPresent map)
                keys.add("present:" + map.getAgent().getId());
            else if (m instanceof MessageRouteFerme mrf)
                keys.add("ferme:" + mrf.getName());
        }
        return keys;
    }

    private List<ActionJointe> generateJointActions(List<List<Map.Entry<Agent, Action>>> listes) {
        List<ActionJointe> resultat = new ArrayList<>();
        resultat.add(new ActionJointe());
        for (List<Map.Entry<Agent, Action>> options : listes) {
            List<ActionJointe> nouveau = new ArrayList<>();
            for (ActionJointe aj : resultat) {
                for (Map.Entry<Agent, Action> opt : options) {
                    ActionJointe copie = copier(aj);
                    copie.addAction(opt.getKey(), opt.getValue());
                    nouveau.add(copie);
                }
            }
            resultat = nouveau;
        }
        return resultat;
    }

    private ActionJointe copier(ActionJointe aj) {
        ActionJointe copie = new ActionJointe();
        for (Agent a : aj.getAgents()) copie.addAction(a, aj.getAction(a));
        return copie;
    }
}
