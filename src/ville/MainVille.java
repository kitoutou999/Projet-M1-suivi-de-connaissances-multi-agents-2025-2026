package ville;

import core.*;
import java.util.*;

public class MainVille {
    public static void main(String[] args) {
        Ville ville = new Ville();
        ville.ajouterRoute("Gare", "Centre");
        ville.ajouterRoute("Centre", "Parc");
        ville.ajouterRoute("Gare", "Port");
        

        AgentVille a1 = new AgentVille(new PolitiqueRandom(),"Alice",null);
        AgentVille a2 = new AgentVille(new PolitiqueRandom(),"Bob",null);
        List<Agent> agents = List.of(a1, a2);


        Map<Agent, String> positionsDebut = new HashMap<>();
        positionsDebut.put(a1, "Gare");
        positionsDebut.put(a2, "Parc");
        //changer la façon dont on place au début les agents dans la ville.
        a1.setPosition("Gare");
        a2.setPosition("Parc");


        JeuRDV jeu = new JeuRDV(ville, "Centre", agents, positionsDebut);


        Map<Agent, Politique> politiques = new HashMap<>();
        politiques.put(a1, new PolitiqueRandom());
        politiques.put(a2, new PolitiqueRandom());


        Simulateur simulateur = new Simulateur(jeu, politiques);
        simulateur.run(20);
        
    }
}
