package hanabi;

import java.util.*;
import java.util.Random;
import core.*;


public class PolitiqueSimpleHanabi implements Politique {

    @Override
    public Action chooseAction(Agent agent, Historique historique, List<Action> actionsPossibles) {
        //Divise les action possibles en groupes
        if(agent instanceof AgentHanabi agentHanabi){
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

            //Créer les domaines pour chaque emplacement avec toute les carte possibles
            List<Set> listDomainesCartes = new ArrayList<>(); //Liste d'ensemble de carte
            List<Carte> carteBase = agentHanabi.getCarteBase();
            for(int i = 0; i < 5;i++){ //Créer 5 ensemble, 1 pour chaque postion de la main de l'agent
                Set<Carte> domaineCarte = new HashSet();
                for(Carte carte : carteBase){
                    domaineCarte.add(carte);
                }
                listDomainesCartes.add(domaineCarte);
            }

            Random random = new Random();
            
            //Filtre les domaines en fonction de l'historique
            for(Map.Entry<Action, Observation> entree : historique.getListe()){
                ObservationHanabi obs = (ObservationHanabi) entree.getValue();
                Map<Agent, List<Carte>> mainsAutresAgents = obs.getMainsAutresAgents();

                //Enlève les cartes des autres agents de mes domaines
                for(Map.Entry<Agent, List<Carte>> mains : mainsAutresAgents.entrySet()){
                    List<Carte> mainAgent = mains.getValue();
                    for(Carte carte : mainAgent){
                        for(Set domaine :listDomainesCartes){
                            domaine.remove(carte);
                        }
                    }
                }

                //Enlève les cartes qui ont été jouées de mes domaines
                Map<String, Deque<Carte>> etatPiles = obs.getEtatPiles();
                for(Map.Entry<String, Deque<Carte>> piles : etatPiles.entrySet()){
                    Deque<Carte> pile = piles.getValue();
                    for(Carte carte : pile){
                        for(Set domaine :listDomainesCartes){
                            domaine.remove(carte);
                        }
                    }
                }


            }
            System.out.println("Les DOMAINES des cartes " + listDomainesCartes + "\n");
            /*
                Si j'ai au moins une carte jouable dans au moins un domaine -> actionJouer (l'une des cartes en questions)
                Sinon -> on passe à la suite
            */

            
            if(historique.getDerniere() != null){
                ObservationHanabi lastObs = (ObservationHanabi) historique.getDerniere().getValue();
                Map<String,Deque<Carte>> lastJeu = lastObs.getEtatPiles(); 
                Map<Agent, List<Carte>> lastMains = lastObs.getMainsAutresAgents();
        
                //Pour décider de si l'agent pose une carte(un agent ne pose une carte que si l'un de ses domaines ne contient que des cartes joaubles)
                for(int i = 0;i < 5; i++){
                    boolean domaineSur = true;
                    Set<Carte> domaine = listDomainesCartes.get(i);
                    for(Carte carte : domaine){
                        String couleurCarte = carte.getCouleur();
                        Deque<Carte> pile = lastJeu.get(couleurCarte);
                        
                        if(pile.isEmpty() && carte.getNumero() == 1){ //Pour pouvoir Jouer les carte 1
                            continue;
                        }
                        else if (!pile.isEmpty() && carte.getNumero() == (pile.peek()).getNumero() + 1){ //Pour pouvoir jouer les autres cartes que 1
                            continue;
                        }
                        domaineSur = false;
                    
                    }
                    if(domaineSur){
                        return actionPoser.get(i);
                    }
                }


                //Pour décider de si l'agent donne une indication à un autre agent (Indique à un agent ag si ag a une carte jouable dans sa main)(choix entre indiqueValeur et IndiqueCouleur aléatoire pour l'instant)
                for(Map.Entry<Agent,List<Carte>> entry : lastMains.entrySet()){
                    Agent agentCible = entry.getKey();
                    List<Carte> cartes = entry.getValue();
                    for(Carte carte: cartes){
                        Deque<Carte> pile = lastJeu.get(carte.getCouleur());

                        if(pile.isEmpty() && carte.getNumero() == 1){ //Pour pouvoir Jouer les carte 1
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
            return actionDefausse.get(random.nextInt(actionDefausse.size()));  //Action défausse (choix de carte random pour l'instant)
        }
        return null;
    }
}