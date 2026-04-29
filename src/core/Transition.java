package core;

public record Transition<E extends Etat>(E precedent, ActionJointe action, E suivant) {}
