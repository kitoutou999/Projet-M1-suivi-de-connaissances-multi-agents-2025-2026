package ville;

import core.*;
import java.util.*;

public class MainVille {
    public static void main(String[] args) {

        Ville ville = new Ville();
        ville.ajouterRoute("Gare", "Centre");
        ville.ajouterRoute("Centre", "Parc");
        ville.ajouterRoute("Gare", "Port");
        

        Agent a1 = new Agent("Alice");
        Agent a2 = new Agent("Bob");
        List<Agent> agents = List.of(a1, a2);


        Map<Agent, String> positionsDebut = new HashMap<>();
        positionsDebut.put(a1, "Gare");
        positionsDebut.put(a2, "Parc");


        JeuRDV jeu = new JeuRDV(ville, "Centre", agents, positionsDebut);


        Map<Agent, Politique> politiques = new HashMap<>();
        politiques.put(a1, new PolitiqueRandom());
        politiques.put(a2, new PolitiqueRandom());


        Simulateur simulateur = new Simulateur(jeu, politiques);
        simulateur.run(20);
        
    }
}
