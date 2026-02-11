package core;

import java.util.Map;


public class Simulateur {
    private final Jeu jeu;
    private final Map<Agent, Politique> politiques;

    public Simulateur(Jeu jeu, Map<Agent, Politique> politiques) {
        this.jeu = jeu;
        this.politiques = politiques;
    }


    public void run(int maxStep) {
        Etat etatCourant = jeu.getInitialState();
        int step = 0;

        System.out.println("État initial : " + etatCourant);

        while (!jeu.isFinal(etatCourant) && step < maxStep) {
            step++;
            ActionJointe actionJointe = new ActionJointe();

            // Phase 1 : tous les agents choisissent leur action
            for (Map.Entry<Agent, Politique> entry : politiques.entrySet()) {
                Agent agent = entry.getKey();
                Politique politique = entry.getValue();

                var actionsPossibles = jeu.getActionsPossibles(agent, etatCourant);
                Action action = politique.chooseAction(agent, etatCourant, actionsPossibles);
                actionJointe.addAction(agent, action);
            }

            // Phase 2 : calcul du nouvel état
            etatCourant = jeu.nextState(etatCourant, actionJointe);

            // Phase 3 : chaque agent observe le nouvel état
            for (Map.Entry<Agent, Politique> entry : politiques.entrySet()) {
                Agent agent = entry.getKey();
                Observation obs = jeu.makeObservation(agent, actionJointe, etatCourant, step);
                agent.inform(obs, actionJointe.getAction(agent));
                System.out.println(agent.getHistorique());
            }
            
            System.out.println("Tour " + step + " : " + actionJointe);
            System.out.println("Nouvel état : " + etatCourant);
        }

        System.out.println("Simulation terminée.");
    }
}
