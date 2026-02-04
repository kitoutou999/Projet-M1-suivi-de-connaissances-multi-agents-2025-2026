package hanabi;
import java.util.*;
import core.*;

public class MainHanabi{
    public static void main(String[] args) {


        //Créer un "Hanabi" (la boite de jeu)
        List<Integer> listNumero = new ArrayList<>(List.of(3,2,2,2,1)); // Vrai valeur pour Hanabi -> 3,2,2,2,1
        List<String> listCouleur = new ArrayList<>(List.of("rouge","bleu","vert","jaune","blanc")); //Vrai valeur pour Hanabi -> "rouge","bleu","vert","jaune","blanc"

        Map<String,Integer> valeurMax = new HashMap<>(); //Pour chaque couleur donne quelle valeur est la derniere possible 

        Map<String,List<Integer>> nombreCarte = new HashMap<>();
        for(String c : listCouleur){
            nombreCarte.put(c,listNumero);
            valeurMax.put(c,listNumero.size());
        }
        Hanabi hanabi = new Hanabi(nombreCarte,3,valeurMax);

        //System.out.println(hanabi.getPioche() + "\n");

        Map<Agent, Politique> politiques = new HashMap<>();

        PolitiqueRandom politiqueRandom = new PolitiqueRandom();

        //Créer les agents
        Agent a1 = new Agent(politiqueRandom,"Alice");
        Agent a2 = new Agent(politiqueRandom,"Bob");
        List<Agent> agents = List.of(a1, a2);


        //Distribue les cartes aux agents 
        Map<Agent,List<Carte>> mainsAgents = new HashMap<>();
        for(Agent agent : agents){
            List<Carte> mainAgent = new ArrayList<>();
            for(int i = 0;i < 5;i++){
                mainAgent.add(hanabi.retireCarte());
            }
            mainsAgents.put(agent,mainAgent);
        }

        //System.out.println(hanabi.getPioche() + "\n");

        //Créer le jeu
        JeuHanabi jeu = new JeuHanabi(hanabi,agents,mainsAgents);

        //Créer les politiques
        Map<Agent, Politique> politiques = new HashMap<>();
        politiques.put(a1, politiqueRandom);
        politiques.put(a2, politiqueRandom);


        //Lance la simulation
        Simulateur simulateur = new Simulateur(jeu, politiques);
        simulateur.run(20);

    }
}