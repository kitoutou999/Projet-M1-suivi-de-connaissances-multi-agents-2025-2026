package core;

import java.util.List;

/**
 * Classe abstraite représentant le moteur d'un jeu.
 * Chaque scénario doit fournir une implémentation concrète de cette classe
 * en définissant l'état initial, les conditions de fin, les transitions d'état,
 * les actions possibles et la génération des observations.
 */
public abstract class Jeu {

    /** Retourne l'état initial du jeu avant le premier tour. */
    public abstract Etat getInitialState();

    /** Détermine si un état est terminal et doit arrêter la simulation. */
    public abstract boolean isFinal(Etat etat);

    /** Calcule l'état suivant à partir de l'état courant et des actions jouées par tous les agents. */
    public abstract Etat nextState(Etat etat, ActionJointe actions);

    /** Retourne la liste des actions qu'un agent peut effectuer depuis un état donné. */
    public abstract List<Action> getActionsPossibles(Agent<?> agent, Etat etat);

    /** Génère l'observation réelle reçue par un agent après l'exécution d'un tour. */
    public abstract Observation makeObservation(Agent<?> agent, ActionJointe actions, Etat nextState, int step);

    /**
     * Simule l'observation qu'un agent aurait reçue pour une transition donnée.
     * Utilisé par le {@link BeliefStateFilter} pour filtrer les états compatibles.
     */
    public abstract Observation simulerObservation(Agent<?> agent, Transition<? extends Etat> t, int step);
}
