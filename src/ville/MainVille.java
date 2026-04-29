package ville;

import core.*;
import java.util.*;

public class MainVille {
    public static void main(String[] args) {
        scenarioMessage();
    }

    // Scénario principal : deux arbres binaires, Bob contourne un pont fermé
    static void scenarioPrincipal() {
        int profondeur = 4;
        int nbPonts = 3;

        Ville ville = Ville.genererDeuxArbres(profondeur, nbPonts);

        String lieuRDV = "B0";

        int premiereFeuilleA = (int) Math.pow(2, profondeur - 1) - 1;
        int derniereFeuilleA = (int) Math.pow(2, profondeur) - 2;
        String departAlice = "A" + premiereFeuilleA;
        String departBob   = "A" + derniereFeuilleA;

        ville.fermerLieu("A" + (premiereFeuilleA + 1));
        System.out.println("Lieu fermé : A" + (premiereFeuilleA + 1));
        System.out.println("RDV : " + lieuRDV + " | Alice : " + departAlice + " | Bob : " + departBob);

        lancerSimulation(ville, lieuRDV, departAlice, departBob);
    }

    static void scenarioMessage() {
        Ville ville = new Ville();

        ville.ajouterRoute("B", "AvantA");
        ville.ajouterRoute("AvantA",   "A");
        ville.ajouterRoute("A",        "Court");
        ville.ajouterRoute("A",        "Long");
        ville.ajouterRoute("Court",    "RDV");
        ville.ajouterRoute("Long",     "Detour");
        ville.ajouterRoute("Detour",   "RDV");

        ville.fermerLieu("Court");
        System.out.println("Lieu fermé : Court");
        System.out.println("RDV : RDV | Alice : A | Bob : B");

        lancerSimulation(ville, "RDV", "A", "B");
    }

    static void lancerSimulation(Ville ville, String lieuRDV, String departAlice, String departBob) {
        PolitiquePCC politiqueAlice = new PolitiquePCC(lieuRDV, ville.copierVille());
        PolitiquePCC politiqueBob   = new PolitiquePCC(lieuRDV, ville.copierVille());

        AgentVille a1 = new AgentVille(politiqueAlice, "Alice", departAlice);
        AgentVille a2 = new AgentVille(politiqueBob,   "Bob",   departBob);
        List<Agent> agents = List.of(a1, a2);

        Map<Agent, String> positionsDebut = new HashMap<>();
        positionsDebut.put(a1, departAlice);
        positionsDebut.put(a2, departBob);

        JeuRDV jeu = new JeuRDV(ville, lieuRDV, agents, positionsDebut);

        // Attacher un BeliefState à Alice
        BeliefStateFilterVille filtre = new BeliefStateFilterVille(jeu.getMode());
        List<EtatRDV> etatsInitiaux = new ArrayList<>();
        etatsInitiaux.add((EtatRDV) jeu.getInitialState());
        core.BeliefState<EtatRDV> bs = new core.BeliefState<>(etatsInitiaux, filtre, jeu, agents);
        a1.setBeliefState(bs);

        Map<Agent, Politique> politiques = new HashMap<>();
        politiques.put(a1, politiqueAlice);
        politiques.put(a2, politiqueBob);

        new Simulateur(jeu, politiques).run(50);
    }
}
