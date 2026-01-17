package hanabi;
import java.util.*;
import core.*;

public class MainHanabi{
    public static void main(String[] args) {

        List<Integer> listNumero = new ArrayList<>(List.of(3,2,2,2,1));
        List<String> listCouleur = new ArrayList<>(List.of("rouge", "bleu", "vert","jaune","blanc"));

        Map<String,List<Integer>> nombreCarte = new HashMap<>();
        for(String c : listCouleur){
            nombreCarte.put(c,listNumero);
        }

        
        Hanabi hanabi = new Hanabi(nombreCarte);

        //System.out.println(hanabi.getPioche() + "\n");

        Agent a1 = new Agent("Alice");
        Agent a2 = new Agent("Bob");
        List<Agent> agents = List.of(a1, a2);


        Map<Agent,List<Carte>> mainsAgents = new HashMap<>();
        for(Agent agent : agents){
            List<Carte> mainAgent = new ArrayList<>();
            for(int i = 0;i < 5;i++){
                mainAgent.add(hanabi.retireCarte());
            }
            mainsAgents.put(agent,mainAgent);
        }

        //System.out.println(hanabi.getPioche() + "\n");

        JeuHanabi jeu = new JeuHanabi(hanabi,agents,mainsAgents);

        Map<Agent, Politique> politiques = new HashMap<>();
        politiques.put(a1, new PolitiqueRandom());
        politiques.put(a2, new PolitiqueRandom());


        Simulateur simulateur = new Simulateur(jeu, politiques);
        simulateur.run(20);

    }
}