package hanabi;
import java.util.*;
import core.*;

public class MainHanabi{
    public static void main(String[] args) {

        Random generateur;

        if (args.length > 0) {
            try {
                long seed = Long.parseLong(args[0]);
                generateur = new Random(seed);
                
            } catch (NumberFormatException e) {
                generateur = new Random(); 
            }
        } else {
            generateur = new Random();
        }




        //Créer un "Hanabi" (la boite de jeu)
        List<Integer> listNumero = new ArrayList<>(List.of(3,2,2,2,1)); // Vrai valeur pour Hanabi -> 3,2,2,2,1
        List<String> listCouleur = new ArrayList<>(List.of("rouge","bleu","vert","jaune","blanc")); //Vrai valeur pour Hanabi -> "rouge","bleu","vert","jaune","blanc"

        Map<String,Integer> valeurMax = new HashMap<>(); //Pour chaque couleur donne quelle valeur est la derniere possible 

        Map<String,List<Integer>> nombreCarte = new HashMap<>();
        for(String c : listCouleur){
            nombreCarte.put(c,listNumero);
            valeurMax.put(c,listNumero.size());
        }
        Hanabi hanabi = new Hanabi(nombreCarte,3,valeurMax,generateur);
        
        List<Carte> carteBase = new ArrayList<>(hanabi.getPioche());

        //System.out.println(hanabi.getPioche() + "\n");

        PolitiqueRandom politiqueRandom = new PolitiqueRandom();
        PolitiqueSimpleHanabi politiqueSimpleA1 = new PolitiqueSimpleHanabi(carteBase,generateur);
        PolitiqueSimpleHanabi politiqueSimpleA2 = new PolitiqueSimpleHanabi(carteBase,generateur);

        //Créer les agents
        
        AgentHanabi a1 = new AgentHanabi(politiqueSimpleA1,"Alice",carteBase);
        AgentHanabi a2 = new AgentHanabi(politiqueSimpleA2,"Bob",carteBase);
        List<Agent<?>> agents = List.of(a1, a2);


        //Distribue les cartes aux agents
        Map<Agent<?>, List<Carte>> mainsAgents = new HashMap<>();
        for(Agent<?> agent : agents){
            List<Carte> mainAgent = new ArrayList<>();
            for(int i = 0;i < 5;i++){
                mainAgent.add(hanabi.retireCarte());
            }
            for(Agent<?> a : agents){
            	if(a != agent && a instanceof AgentHanabi aH){
            		aH.getAutresMains().put(agent, mainAgent);
            	}
            }
            mainsAgents.put(agent,mainAgent);
        }

        //System.out.println(hanabi.getPioche() + "\n");

        //Créer le jeu
        JeuHanabi jeu = new JeuHanabi(hanabi,agents,mainsAgents);

        //Lance la simulation
        new Simulateur(jeu, agents).run(2000);

    }
}
