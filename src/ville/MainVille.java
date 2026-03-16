package ville;

import core.*;
import java.util.*;

public class MainVille {
    public static void main(String[] args) {
        Random rand = new Random();
        int nbLieux = 100;
        int nbRoutesSupp = 1;
        int nbLieuxFermes = 5;

        Ville ville = Ville.genererAleatoire(nbLieux, nbRoutesSupp, rand);


        List<String> lieux = new ArrayList<>(ville.getLieux());
        String lieuRDV = lieux.get(0);
        String departAlice = lieux.get(rand.nextInt(lieux.size()));
        String departBob = lieux.get(rand.nextInt(lieux.size()));

        PolitiquePCC politiqueAlice = new PolitiquePCC(lieuRDV, ville.copierVille());
        PolitiquePCC politiqueBob   = new PolitiquePCC(lieuRDV, ville.copierVille());

        AgentVille a1 = new AgentVille(politiqueAlice, "Alice", departAlice);
        AgentVille a2 = new AgentVille(politiqueBob,   "Bob",   departBob);
        List<Agent> agents = List.of(a1, a2);

        Map<Agent, String> positionsDebut = new HashMap<>();
        positionsDebut.put(a1, departAlice);
        positionsDebut.put(a2, departBob);

        // Fermer nbLieuxFermes route
        List<String> lieuxFermables = new ArrayList<>();
        for(String l : lieux){
            if(!l.equals(lieuRDV) && !l.equals(departAlice) && !l.equals(departBob)){
                lieuxFermables.add(l);
            }
        }
        Collections.shuffle(lieuxFermables, rand);
        for(int i = 0; i < nbLieuxFermes && i < lieuxFermables.size(); i++){
            ville.fermerLieu(lieuxFermables.get(i));
            System.out.println("Lieu fermé : " + lieuxFermables.get(i));
        }

        System.out.println("Lieu RDV : " + lieuRDV);
        System.out.println("Alice part de : " + departAlice);
        System.out.println("Bob part de : " + departBob);

        JeuRDV jeu = new JeuRDV(ville, lieuRDV, agents, positionsDebut);

        Map<Agent, Politique> politiques = new HashMap<>();
        politiques.put(a1, politiqueAlice);
        politiques.put(a2, politiqueBob);

        Simulateur simulateur = new Simulateur(jeu, politiques);
        simulateur.run(50);
    }
}
