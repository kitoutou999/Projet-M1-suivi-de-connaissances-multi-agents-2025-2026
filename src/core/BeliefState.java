package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente l'état de croyance d'un agent : l'ensemble des états du monde
 * que l'agent juge compatibles avec ses observations passées.
 * Mis à jour à chaque tour via {@link #mettreAJour} en filtrant les états
 * incompatibles avec la nouvelle observation reçue.
 *
 * @param <E> le type d'état du jeu
 */
public class BeliefState<E extends Etat> {
    private List<E> etatsCompatibles;
    private final BeliefStateFilter<E> filtre;
    private final Jeu jeu;
    private final List<Agent<?>> tousLesAgents;

    /** Crée un BeliefState avec un ensemble d'états initiaux jugés possibles. */
    public BeliefState(List<E> etatsInitiaux, BeliefStateFilter<E> filtre, Jeu jeu, List<Agent<?>> tousLesAgents) {
        this.etatsCompatibles = new ArrayList<>(etatsInitiaux);
        this.filtre = filtre;
        this.jeu = jeu;
        this.tousLesAgents = tousLesAgents;
    }

    /**
     * Met à jour l'ensemble des états compatibles après un tour.
     * Pour chaque état croyable, génère toutes les transitions possibles et
     * ne conserve que ceux dont l'observation simulée correspond à l'observation réelle.
     */
    public void mettreAJour(Agent<?> observateur, Observation obsReelle, Action actionPropre, int step) {
        List<E> nouvelEnsemble = new ArrayList<>();
        for (E etat : etatsCompatibles)
            for (Transition<E> t : filtre.transitions(etat, jeu, tousLesAgents, observateur, actionPropre))
                if (filtre.estCompatible(t, observateur, obsReelle, jeu, step) && !nouvelEnsemble.contains(t.suivant()))
                    nouvelEnsemble.add(t.suivant());
        if (!nouvelEnsemble.isEmpty()) etatsCompatibles = nouvelEnsemble;
    }

    public List<E> getEtatsCompatibles() {
        return etatsCompatibles;
    }
}
