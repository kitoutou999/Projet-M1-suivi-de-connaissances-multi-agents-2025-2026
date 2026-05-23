package core;

import java.util.*;

public class BeliefStateFilter<E extends Etat> {

    public Set<Transition<E>> transitions(E etat, Jeu jeu, List<Agent<?>> tousLesAgents, Agent<?> observateur, Action actionPropre) {
        List<List<Map.Entry<Agent<?>, Action>>> listes = new ArrayList<>();
        for (Agent<?> agent : tousLesAgents) {
            List<Map.Entry<Agent<?>, Action>> pairs = new ArrayList<>();
            if (agent.equals(observateur) && actionPropre != null) {
                pairs.add(Map.entry(agent, actionPropre));
            } else {
                for (Action a : jeu.getActionsPossibles(agent, etat))
                    pairs.add(Map.entry(agent, a));
            }
            listes.add(pairs);
        }
        Set<Transition<E>> result = new HashSet<>();
        for (ActionJointe aj : generateJointActions(listes)) {
            E succ = (E) jeu.nextState(etat, aj);
            result.add(new Transition<>(etat, aj, succ));
        }
        return result;
    }

    public boolean estCompatible(Transition<E> t, Agent<?> observateur, Observation obsReelle, Jeu jeu, int step) {
        Observation obsSim = jeu.simulerObservation(observateur, t, step);
        return obsSim.equals(obsReelle);
    }

    private List<ActionJointe> generateJointActions(List<List<Map.Entry<Agent<?>, Action>>> listes) {
        List<ActionJointe> resultat = new ArrayList<>();
        resultat.add(new ActionJointe());
        for (List<Map.Entry<Agent<?>, Action>> options : listes) {
            List<ActionJointe> nouveau = new ArrayList<>();
            for (ActionJointe aj : resultat) {
                for (Map.Entry<Agent<?>, Action> opt : options) {
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
        for (Agent<?> a : aj.getAgents()) copie.addAction(a, aj.getAction(a));
        return copie;
    }
}
