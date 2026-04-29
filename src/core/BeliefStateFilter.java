package core;

import java.util.List;
import java.util.Set;

public interface BeliefStateFilter<E extends Etat> {
    Set<Transition<E>> transitions(E etat, Jeu jeu, List<Agent> tousLesAgents);
    boolean estCompatible(Transition<E> transition, Agent observateur, Observation obsReelle, Jeu jeu, int step);
}
