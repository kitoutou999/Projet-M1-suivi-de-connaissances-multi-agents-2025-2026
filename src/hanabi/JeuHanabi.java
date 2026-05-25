package hanabi;
import java.util.*;
import core.*;

public class JeuHanabi extends Jeu {

    private final Hanabi hanabi;
    private final List<Agent<?>> agents;
    private final EtatHanabi etatInitial;
    private int boucleJeu;

    public JeuHanabi(Hanabi hanabi, List<Agent<?>> agents, Map<Agent<?>, List<Carte>> mainsAgents){
        this.hanabi = hanabi;
        this.agents = agents;
        this.boucleJeu = 0;
        this.etatInitial = new EtatHanabi(mainsAgents, creerJeu(), 0, agents.get(boucleJeu),hanabi.getPioche().size());
    }


    //Initialise une pile pour chaque couleur
    private Map<String,Deque<Carte>> creerJeu(){
        Map<String,Deque<Carte>> jeu = new HashMap<>();

        for(String c : hanabi.getListCouleur()){
            jeu.put(c,new ArrayDeque<>());
        }
        return jeu;
    }

    private boolean estGagne(Map<String,Deque<Carte>> jeu){
        for(Map.Entry<String,Deque<Carte>> entry : jeu.entrySet()){
            String couleur = entry.getKey();
            Deque<Carte> pile = entry.getValue();

            if(pile.isEmpty() || pile.peek().getNumero() < hanabi.getValeurMaxDeC(couleur)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Etat getInitialState() {
        return etatInitial;
    }

    @Override
    public boolean isFinal(Etat etat) {
        EtatHanabi e = (EtatHanabi) etat;
        boolean estGagne = estGagne(e.getJeu());
        if (hanabi.getPioche().size() <= 0 || e.getNbErreur() >= hanabi.getNbErreurMax() || estGagne){  //Si la pioche est vide ou Si il y a eu trop d'erreur ou Si la partie est gagné (chaque couleur est complete)
            return true;
        }
        return false;
    }

    @Override
    public Etat nextState(Etat etat, ActionJointe actions) {
        EtatHanabi e = (EtatHanabi) etat;

        Map<Agent<?>, List<Carte>> newMainsAgents = new HashMap<>(e.getMainsAgents());
        for(Agent<?> key : new HashSet<>(newMainsAgents.keySet())) {
            newMainsAgents.put(key, new ArrayList<>(newMainsAgents.get(key)));
        }

        Map<String,Deque<Carte>> newJeu = new HashMap<>(e.getJeu()); //Créer une copie du jeu pour faire le nouveau
        for(String key : new HashSet<>(newJeu.keySet())) {
            newJeu.put(key, new ArrayDeque<>(newJeu.get(key)));
        }

        int newNbErreur = e.getNbErreur(); //Créer une copie du nombre d'erreur pour mettre à jour

        for (Agent<?> agent : actions.getAgents()) {
            Action action = actions.getAction(agent);

            if (action instanceof ActionDefausse ad) { //Si défausse
                int posCarte = ad.posCarte();
                if (!hanabi.getPioche().isEmpty()) {
                    (newMainsAgents.get(agent)).set(posCarte, hanabi.retireCarte());
                } else {
                    (newMainsAgents.get(agent)).remove(posCarte);
                }
            } else if (action instanceof ActionPoser ap) { //Si poser une carte
                int posCarte = ap.posCarte();
                Carte carte = (newMainsAgents.get(agent)).get(posCarte);
                String couleurCarte = carte.getCouleur();

                Deque<Carte> pile = newJeu.get(couleurCarte);

                if(pile.isEmpty() && carte.getNumero() == 1){ //Pour pouvoir Jouer les carte 1
                    pile.push(carte);
                    if (!hanabi.getPioche().isEmpty()) {
                        (newMainsAgents.get(agent)).set(posCarte, hanabi.retireCarte());
                    } else {
                        (newMainsAgents.get(agent)).remove(posCarte);
                    }
                }
                else if (!pile.isEmpty() && carte.getNumero() == (pile.peek()).getNumero() + 1){ //Pour pouvoir jouer les autres cartes que 1
                    pile.push(carte);
                    if (!hanabi.getPioche().isEmpty()) {
                        (newMainsAgents.get(agent)).set(posCarte, hanabi.retireCarte());
                    } else {
                        (newMainsAgents.get(agent)).remove(posCarte);
                    }
                }
                else{ //Si l'agent s'est trompé
                    if (!hanabi.getPioche().isEmpty()) {
                        (newMainsAgents.get(agent)).set(posCarte, hanabi.retireCarte());
                    } else {
                        (newMainsAgents.get(agent)).remove(posCarte);
                    }
                    newNbErreur += 1;
                }



            }
        }

        //Détermine le prochain agent qui joue
        boucleJeu++;
        if(boucleJeu >= agents.size()){ 
            boucleJeu = 0;
        }
        
        return new EtatHanabi(newMainsAgents,newJeu,newNbErreur,agents.get(boucleJeu),this.hanabi.getPioche().size());
    }

    @Override
    public List<Action> getActionsPossibles(Agent<?> agent, Etat etat) {
        EtatHanabi e = (EtatHanabi) etat;
        List<Action> actions = new ArrayList<>();

        if(agent.getId() == (e.getAgentQuiJoue()).getId()){
            for(int i = 0;i < 5; i++){
                actions.add(new ActionDefausse(i));
                actions.add(new ActionPoser(i));
            }
            for (Agent<?> a : agents) {
                if (a != agent){
                    Set<Integer> valeursVues = new HashSet<>();
                    Set<String> couleursVues = new HashSet<>();
                    for (Carte c : e.getMainAgent(a)) {
                        if (valeursVues.add(c.getNumero())) {
                            actions.add(new ActionIndiquerValeur(a, c.getNumero(), e));
                        }
                        if (couleursVues.add(c.getCouleur())) {
                            actions.add(new ActionIndiquerCouleur(a, c.getCouleur(), e));
                        }
                    }
                }
            }
        }
        else{
            actions.add(new ActionVide());
        }
        return actions;
    }

    @Override
    public Observation makeObservation(Agent<?> agent, ActionJointe actions, Etat nextState, int step){
        return new ObservationHanabi(agent, nextState, actions);
    }

    @Override
    public Observation simulerObservation(Agent<?> agent, Transition<? extends Etat> t, int step) {
        return new ObservationHanabi(agent, t.suivant(), t.action());
    }
}
