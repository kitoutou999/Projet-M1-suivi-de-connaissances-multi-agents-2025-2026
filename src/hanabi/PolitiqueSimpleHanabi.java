package hanabi;

import java.util.*;
import java.util.Random;
import core.*;


public class PolitiqueSimpleHanabi implements Politique {
    
    private List<Set> listDomainesCartes;
    private Set<Carte> domaineDefault;
    private boolean premierTour;
    private List<Carte> carteBase;
    
    public PolitiqueSimpleHanabi(List<Carte> carteBase){
    	this.premierTour = true;
    	this.listDomainesCartes = new ArrayList<>();
    	this.domaineDefault = new HashSet<Carte>();
    	this.carteBase = carteBase;
        for(int i = 0; i < 5;i++){
            Set<Carte> domaineCarte = new HashSet();
            for(Carte carte : carteBase){
            	if(i == 0){
            		domaineDefault.add(carte);
            	}
                domaineCarte.add(carte);
            }
            listDomainesCartes.add(domaineCarte);
        }
    }

    @Override
    public Action chooseAction(Agent<?> agent, Historique historique, List<Action> actionsPossibles) {
        //Divise les action possibles en groupes
        if(agent instanceof AgentHanabi agentHanabi){
            ObservationHanabi lastObs = null;
            Map<Agent<?>, List<Carte>> autresMains = null;

            if (this.premierTour){
            	autresMains = agentHanabi.getAutresMains();
                this.premierTour = false;
            }
            else{
            	Map.Entry<Action, Observation> der = historique.getDerniere();
            	lastObs = (ObservationHanabi)der.getValue();
            	autresMains = lastObs.getMainsAutresAgents();
                //Filtre les domaines par rapport aux indiquations données par les autres joueurs
                ActionJointe derActionsJointes = lastObs.getActions();
                for(Map.Entry<Agent<?>,Action> actions : derActionsJointes.getActions().entrySet()){
                    Agent<?> agentAction = actions.getKey();
                    Action derAction = actions.getValue();
                    if(derAction instanceof ActionIndiquerCouleur daic && daic.agentCible() == agent){
                        EtatHanabi etatIndique = (EtatHanabi)daic.etat();
                        List<Carte> mainAgent = etatIndique.getMainAgent(agent);

                        for(int i = 0;i < 5;i++){
                            if(mainAgent.get(i).getCouleur() == daic.couleur()){
                                Set<Carte> domaine = listDomainesCartes.get(i);
                                domaine.removeIf(carte -> carte.getCouleur() != daic.couleur());
                            }
                        }

                        
                    }
                    if(derAction instanceof ActionIndiquerValeur daiv && daiv.agentCible() == agent){
                        EtatHanabi etatIndique = (EtatHanabi)daiv.etat();
                        List<Carte> mainAgent = etatIndique.getMainAgent(agent);

                        for(int i = 0;i < 5;i++){
                            if(mainAgent.get(i).getNumero() == daiv.valeur()){
                                Set<Carte> domaine = listDomainesCartes.get(i);
                                domaine.removeIf(carte -> carte.getNumero() != daiv.valeur());
                            }
                        }
                    }
                }

            }
            
            //Filtre les domaines par rapport aux mains des autres joueurs
            for(Map.Entry<Agent<?>, List<Carte>> mains : autresMains.entrySet()){
                List<Carte> mainAgent = mains.getValue();
                for(Carte carte : mainAgent){
                    this.domaineDefault.remove(carte);
                    for(Set domaine :listDomainesCartes){
                        boolean test = domaine.remove(carte);
                        if(test){
                            System.out.println("Carte supprimée : " + carte);
                        }
                    }
                }
            }

            
            
        
            if (actionsPossibles.isEmpty()) {
                return null;
            }

            //Cas ou ce n'est pas le tour de l'agent
            else if (actionsPossibles.size() == 1 && actionsPossibles.get(0) instanceof ActionVide ){
                return actionsPossibles.get(0);
            }


            //Cas ou c'est au tour de l'agent
            List<Action> actionDefausse = new ArrayList<>();
            List<Action> actionPoser = new ArrayList<>();
            List<Action> actionInfo = new ArrayList<>();

            //Divise les 3 types d'actions possibles 
            for(Action action : actionsPossibles){
                if(action instanceof ActionDefausse ad){
                    actionDefausse.add(ad);
                }
                else if(action instanceof ActionPoser ap){
                    actionPoser.add(ap);
                }
                else if(action instanceof ActionIndiquerCouleur aic){
                    actionInfo.add(aic);
                }
                else if(action instanceof ActionIndiquerValeur aiv){
                    actionInfo.add(aiv);
                }
            }


            /*
                Quand faire quoi ?
                    Si je sais que j'ai une carte jouable ->actionJouer
                    Sinon si je sais que l'autre à une carte jouable -> actionIndiquer
                        Ajouter une info dans ses observation 
                        Info incomplète (carte 1 mais connais pas la couleur etc...)
                    Sinon -> Action défausse (choix de carte random pour l'instant)
            */

            Random random = new Random();
            
            System.out.println("Les DOMAINES des cartes :\n");
            for(int i =0;i<5;i++){
                System.out.println("Domaine " + i + " : " + listDomainesCartes.get(i) + "\n");
            }




            /*
                Si j'ai au moins une carte jouable dans au moins un domaine -> actionJouer (l'une des cartes en questions)
                Sinon -> on passe à la suite
            */

            
            if(lastObs != null){
                Map<String,Deque<Carte>> lastJeu = lastObs.getEtatPiles(); 
                Map<Agent<?>, List<Carte>> lastMains = lastObs.getMainsAutresAgents();
                boolean allDomainesEmpty = true;
                for(int i = 0;i < 5; i++){
                    Set<Carte> domaine = listDomainesCartes.get(i);
                    if(!domaine.isEmpty()){
                        allDomainesEmpty = false;
                    }
                }
                //Pour décider de si l'agent pose une carte(un agent ne pose une carte que si l'un de ses domaines ne contient que des cartes jouables)
                for(int i = 0;i < 5; i++){
                    boolean domaineSur = true;
                    Set<Carte> domaine = listDomainesCartes.get(i);
                    for(Carte carte : domaine){
                        String couleurCarte = carte.getCouleur();
                        Deque<Carte> pile = lastJeu.get(couleurCarte);
                        //Ajout pour l'amélioration du début de la politique mais normalement cette partie là était déja faite.
                        if(allDomainesEmpty && carte.getNumero() == 1){
                            continue;
                        }
                        else if(pile.isEmpty() && carte.getNumero() == 1){ //Pour pouvoir Jouer les carte 1
                            continue;
                        }
                        else if (!pile.isEmpty() && carte.getNumero() == (pile.peek()).getNumero() + 1){ //Pour pouvoir jouer les autres cartes que 1
                            continue;
                        }
                        domaineSur = false;
                    
                    }
                    if(domaineSur){
                    	listDomainesCartes.set(i, new HashSet<Carte>(this.domaineDefault));
                        return actionPoser.get(i);
                    }
                }


                //Pour décider de si l'agent donne une indication à un autre agent (Indique à un agent ag si ag a une carte jouable dans sa main)(choix entre indiqueValeur et IndiqueCouleur aléatoire pour l'instant)
                for(Map.Entry<Agent<?>,List<Carte>> entry : lastMains.entrySet()){
                    Agent agentCible = entry.getKey();
                    List<Carte> cartes = entry.getValue();
                    for(Carte carte: cartes){
                        Deque<Carte> pile = lastJeu.get(carte.getCouleur());

                        if(pile.isEmpty() && carte.getNumero() == 1){ //Pour pouvoir Jouer les carte 1
                            for(Action a : actionInfo){ 
                                if (random.nextBoolean() || allDomainesEmpty){ 
                                    if(a instanceof ActionIndiquerValeur aiv && aiv.valeur() == carte.getNumero() && aiv.agentCible() == agentCible){ 
                                        return aiv;
                                    }
                                }
                                else{ 
                                    if(a instanceof ActionIndiquerCouleur aic && aic.couleur() == carte.getCouleur() && aic.agentCible() == agentCible){ 
                                        return aic;
                                    }
                                }
                            }

                        }

                        else if (!pile.isEmpty() && carte.getNumero() == (pile.peek()).getNumero() + 1){ //Pour pouvoir jouer les autres cartes que 1
                            for(Action a : actionInfo){ 
                                if (random.nextBoolean()){ 
                                    if(a instanceof ActionIndiquerValeur aiv && aiv.valeur() == carte.getNumero() && aiv.agentCible() == agentCible){ 
                                        return aiv;
                                    }
                                }
                                else{ 
                                    if(a instanceof ActionIndiquerCouleur aic && aic.couleur() == carte.getCouleur() && aic.agentCible() == agentCible){ 
                                        return aic;
                                    }
                                }
                            }
                        }
                    }

                }


            }
            int posRandom = random.nextInt(actionDefausse.size());
            listDomainesCartes.set(posRandom, new HashSet<Carte>(this.domaineDefault));
            return actionDefausse.get(posRandom);  //Action défausse (choix de carte random pour l'instant)
        }
        return null;
    }
}
