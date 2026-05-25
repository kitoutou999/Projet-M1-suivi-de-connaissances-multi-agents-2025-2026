package ville;

import core.*;
import java.util.*;

public class MainVille {
    public static void main(String[] args) {
        scenarioSecondOrdreVrai();
    }

    // Alice et Bob démarrent au même endroit, tentent tous les deux Carrefour.
    // Alice observe que Bob a échoué aussi -> second ordre : Bob sait déjà, pas de message.
    // Charlie a un chemin indépendant et ne passe pas par Carrefour.
    static void scenarioSecondOrdreVrai() {
        Ville ville = new Ville();

        ville.ajouterRoute("Shared", "Carrefour");
        ville.ajouterRoute("Carrefour", "RDV");
        ville.ajouterRoute("Shared", "Detour");
        ville.ajouterRoute("Detour", "RDV");
        ville.ajouterRoute("Charlie_Depart", "Pont_C");
        ville.ajouterRoute("Pont_C", "RDV");

        ville.fermerLieu("Carrefour");

        System.out.println("=== Scénario second ordre vrai ===");
        System.out.println("Lieu fermé : Carrefour");
        System.out.println("Alice et Bob démarrent à Shared | Charlie à Charlie_Depart | RDV : RDV");
        System.out.println("Attendu : Alice et Bob tentent Carrefour ensemble, Alice voit que Bob a échoué aussi -> pas de message\n");

        String lieuRDV = "RDV";

        Ville villeAlice   = ville.copierVille(); villeAlice.ouvrirLieu("Carrefour");
        Ville villeBob     = ville.copierVille(); villeBob.ouvrirLieu("Carrefour");
        Ville villeCharlie = ville.copierVille(); villeCharlie.ouvrirLieu("Carrefour");

        PolitiquePCC politiqueAlice   = new PolitiquePCC(lieuRDV, villeAlice);
        PolitiquePCC politiqueBob     = new PolitiquePCC(lieuRDV, villeBob);
        PolitiquePCC politiqueCharlie = new PolitiquePCC(lieuRDV, villeCharlie);

        AgentVille a1 = new AgentVille(politiqueAlice,   "Alice",   "Shared");
        AgentVille a2 = new AgentVille(politiqueBob,     "Bob",     "Shared");
        AgentVille a3 = new AgentVille(politiqueCharlie, "Charlie", "Charlie_Depart");
        List<Agent<?>> agents = List.of(a1, a2, a3);

        Map<Agent<?>, String> positionsDebut = new HashMap<>();
        positionsDebut.put(a1, "Shared");
        positionsDebut.put(a2, "Shared");
        positionsDebut.put(a3, "Charlie_Depart");

        JeuRDV jeu = new JeuRDV(ville, lieuRDV, agents, positionsDebut);

        List<EtatRDV> etatsInitiaux = new ArrayList<>();
        etatsInitiaux.add((EtatRDV) jeu.getInitialState());
        core.BeliefState<EtatRDV> bs = new core.BeliefState<>(etatsInitiaux, new core.BeliefStateFilter<>(), jeu, agents);
        a1.setBeliefState(bs);

        new Simulateur(jeu, agents).run(20);
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

    static void scenarioSecondOrdreMixte() {
        Ville ville = new Ville();

        ville.ajouterRoute("Alice_Depart", "Shared");
        ville.ajouterRoute("Shared","Carrefour");
        ville.ajouterRoute("Carrefour","RDV");
        ville.ajouterRoute("Shared","Detour_Long");
        ville.ajouterRoute("Detour_Long","Plus_Long");
        ville.ajouterRoute("Plus_Long","RDV");

        ville.ajouterRoute("Bob_Depart","Milieu_B1");
        ville.ajouterRoute("Milieu_B1","Milieu_B2");
        ville.ajouterRoute("Milieu_B2","Shared");

        ville.ajouterRoute("Charlie_Depart", "Pont_C");
        ville.ajouterRoute("Pont_C","RDV");

        ville.fermerLieu("Carrefour");
        System.out.println("Lieu fermé : Carrefour");
        System.out.println("Alice : Alice_Depart | Bob : Bob_Depart | Charlie : Charlie_Depart | RDV : RDV");

        String lieuRDV = "RDV";

        Ville villeAlice   = ville.copierVille(); villeAlice.ouvrirLieu("Carrefour");
        Ville villeBob     = ville.copierVille(); villeBob.ouvrirLieu("Carrefour");
        Ville villeCharlie = ville.copierVille(); villeCharlie.ouvrirLieu("Carrefour");

        PolitiquePCC politiqueAlice   = new PolitiquePCC(lieuRDV, villeAlice);
        PolitiquePCC politiqueBob     = new PolitiquePCC(lieuRDV, villeBob);
        PolitiquePCC politiqueCharlie = new PolitiquePCC(lieuRDV, villeCharlie);

        AgentVille a1 = new AgentVille(politiqueAlice,   "Alice",   "Alice_Depart");
        AgentVille a2 = new AgentVille(politiqueBob,     "Bob",     "Bob_Depart");
        AgentVille a3 = new AgentVille(politiqueCharlie, "Charlie", "Charlie_Depart");
        List<Agent<?>> agents = List.of(a1, a2, a3);

        Map<Agent<?>, String> positionsDebut = new HashMap<>();
        positionsDebut.put(a1, "Alice_Depart");
        positionsDebut.put(a2, "Bob_Depart");
        positionsDebut.put(a3, "Charlie_Depart");

        JeuRDV jeu = new JeuRDV(ville, lieuRDV, agents, positionsDebut);

        List<EtatRDV> etatsInitiaux = new ArrayList<>();
        etatsInitiaux.add((EtatRDV) jeu.getInitialState());
        core.BeliefState<EtatRDV> bs = new core.BeliefState<>(etatsInitiaux, new core.BeliefStateFilter<>(), jeu, agents);
        a1.setBeliefState(bs);


        new Simulateur(jeu, agents).run(20);
    }

    static void lancerSimulation(Ville ville, String lieuRDV, String departAlice, String departBob) {
        PolitiquePCC politiqueAlice = new PolitiquePCC(lieuRDV, ville.copierVille());
        PolitiquePCC politiqueBob   = new PolitiquePCC(lieuRDV, ville.copierVille());

        AgentVille a1 = new AgentVille(politiqueAlice, "Alice", departAlice);
        AgentVille a2 = new AgentVille(politiqueBob,   "Bob",   departBob);
        List<Agent<?>> agents = List.of(a1, a2);

        Map<Agent<?>, String> positionsDebut = new HashMap<>();
        positionsDebut.put(a1, departAlice);
        positionsDebut.put(a2, departBob);

        JeuRDV jeu = new JeuRDV(ville, lieuRDV, agents, positionsDebut);

        // Attacher un BeliefState à Alice
        List<EtatRDV> etatsInitiaux = new ArrayList<>();
        etatsInitiaux.add((EtatRDV) jeu.getInitialState());
        core.BeliefState<EtatRDV> bs = new core.BeliefState<>(etatsInitiaux, new core.BeliefStateFilter<>(), jeu, agents);
        a1.setBeliefState(bs);

        new Simulateur(jeu, agents).run(50);
    }
}
