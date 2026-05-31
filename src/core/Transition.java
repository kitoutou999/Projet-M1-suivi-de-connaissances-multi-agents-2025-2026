package core;

/**
 * Représente une transition entre deux états : l'état précédent, l'action jointe qui l'a causée,
 * et l'état suivant qui en résulte. Utilisée par le {@link BeliefStateFilter}
 * pour simuler et filtrer les croyances d'un agent.
 */
public record Transition<E extends Etat>(E precedent, ActionJointe action, E suivant) {}
