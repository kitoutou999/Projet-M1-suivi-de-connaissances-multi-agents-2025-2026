package core;

import java.util.ArrayList;
import java.util.List;

public class BeliefState<E extends Etat> {
    private List<E> etatsCompatibles;
    private final BeliefStateFilter<E> filtre;
    private final Jeu jeu;
    private final List<Agent<?>> tousLesAgents;

    public BeliefState(List<E> etatsInitiaux, BeliefStateFilter<E> filtre, Jeu jeu, List<Agent<?>> tousLesAgents) {
        this.etatsCompatibles = new ArrayList<>(etatsInitiaux);
        this.filtre = filtre;
        this.jeu = jeu;
        this.tousLesAgents = tousLesAgents;
    }

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
