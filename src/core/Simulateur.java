package core;

import java.util.List;


public class Simulateur {
    private final Jeu jeu;
    private final List<Agent<?>> agents;

    public Simulateur(Jeu jeu, List<Agent<?>> agents) {
        this.jeu = jeu;
        this.agents = agents;
    }

    public void run(int maxStep) {
        Etat etatCourant = jeu.getInitialState();
        int step = 0;

        System.out.println("État initial : " + etatCourant + "\n");

        while (!jeu.isFinal(etatCourant) && step < maxStep) {
            System.out.println("\033[4m\033[1;34mTour " + step + "\033[0m");
            step++;
            ActionJointe actionJointe = new ActionJointe();

            // Phase 1 : tous les agents choisissent leur action
            for (Agent<?> agent : agents) {
                var actionsPossibles = jeu.getActionsPossibles(agent, etatCourant);
                Action action = agent.chooseAction(actionsPossibles);
                actionJointe.addAction(agent, action);
            }

            // Phase 2 : calcul du nouvel état
            etatCourant = jeu.nextState(etatCourant, actionJointe);

            // Phase 3 : chaque agent observe le nouvel état
            for (Agent<?> agent : agents) {
                Observation obs = jeu.makeObservation(agent, actionJointe, etatCourant, step);
                agent.inform(obs, actionJointe.getAction(agent));

                if (agent.getBeliefState() != null)
                    agent.getBeliefState().mettreAJour(agent, obs, actionJointe.getAction(agent), step);
            }

            String ligneAction = actionJointe.toString().replace("Rester sur place", "\033[1;31mRester sur place\033[0m");
            System.out.println("  " + ligneAction);
            System.out.println("  \033[1mEtat\033[0m : " + etatCourant);
            System.out.println();
        }

        System.out.println("Simulation terminée.");
    }
}
