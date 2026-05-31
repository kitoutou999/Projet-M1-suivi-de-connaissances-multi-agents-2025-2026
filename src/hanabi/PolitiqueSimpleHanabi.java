package hanabi;

import java.util.*;
import java.util.Random;
import core.*;

/**
* Classe qui implémente ce qu'est une Politique en une politique intelligente permetant à des agents d'obtenir de bons scores en jeu grâce à leurs croyances dans le cadre du jeu Hanabi 
*/

public class PolitiqueSimpleHanabi implements Politique {

    private List<Set> listDomainesCartes;
    private Set<Carte> domaineDefault;
    private boolean premierTour;
    private List<Carte> carteBase;
    private Random generateur;
    private boolean verbose;
    // Second ordre : estimation de ce que chaque autre agent croit avoir dans sa main
    private Map<Agent<?>, List<Set<Carte>>> domainesAutres;

    public PolitiqueSimpleHanabi(List<Carte> carteBase, Random generateur) {
        this(carteBase, generateur, false);
    }

    public PolitiqueSimpleHanabi(List<Carte> carteBase, Random generateur, boolean verbose) {
        this.verbose = verbose;
        this.generateur = generateur;
    	this.premierTour = true;
    	this.listDomainesCartes = new ArrayList<>();
    	this.domaineDefault = new HashSet<Carte>();
    	this.carteBase = carteBase;
        this.domainesAutres = new HashMap<>();
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

    //crée 5 domaine avec toute les cartes pour chaque agents
    private void initDomainesAutres(Map<Agent<?>, List<Carte>> autresMains){
        if(!domainesAutres.isEmpty()) return;
        for(Agent<?> autre : autresMains.keySet()){
            List<Set<Carte>> ds = new ArrayList<>();
            for(int i = 0; i < 5; i++) ds.add(new HashSet<>(carteBase));
            domainesAutres.put(autre, ds);
        }
    }


    
    //Donne un score pour l'indice donnée en fonction des mains des agents(pour plus tard prendre le meilleur indice qui reduit un max les domaines)
    private int scoreIndice(Action indice, Agent<?> cible, List<Carte> mainCible){
        List<Set<Carte>> base = domainesAutres.get(cible); // ce que la cible croit avoir dans sa main
        int score = 0;
        if(indice instanceof ActionIndiquerCouleur aic){
            String coul = aic.couleur();
            for(int i = 0; i < 5; i++){
                boolean match = mainCible.get(i).getCouleur().equals(coul);
                for(Carte cc : base.get(i))
                    if(match ? !cc.getCouleur().equals(coul) : cc.getCouleur().equals(coul)){
                        score++;
                    }
            }
        } else if(indice instanceof ActionIndiquerValeur aiv){
            int val = aiv.valeur();
            for(int i = 0; i < 5; i++){
                boolean match = mainCible.get(i).getNumero() == val;
                for(Carte cc : base.get(i))
                    if(match ? cc.getNumero() != val : cc.getNumero() == val){
                        score++;
                    }
            }
        }
        return score;
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
                            Set<Carte> domaine = listDomainesCartes.get(i);
                            if(mainAgent.get(i).getCouleur().equals(daic.couleur())){
                                domaine.removeIf(carte -> !carte.getCouleur().equals(daic.couleur()));
                            }
                            else{
                                domaine.removeIf(carte -> carte.getCouleur().equals(daic.couleur()));
                            }
                        }
                    }
                    //Met a jour le domaine cible avec l'observation de l'agent avec l'indice qu'a recu la cible
                    if(derAction instanceof ActionIndiquerCouleur daic && daic.agentCible() != agent){
                        // Second ordre : mise à jour des domaines estimés de la cible
                        EtatHanabi etatIndique = (EtatHanabi)daic.etat();
                        Agent<?> cible = daic.agentCible();
                        List<Carte> mainCible = etatIndique.getMainAgent(cible);
                        List<Set<Carte>> ds = domainesAutres.get(cible);
                        for(int i = 0; i < 5; i++){
                            if(mainCible.get(i).getCouleur().equals(daic.couleur()))
                                ds.get(i).removeIf(c -> !c.getCouleur().equals(daic.couleur()));
                            else
                                ds.get(i).removeIf(c -> c.getCouleur().equals(daic.couleur()));
                        }
                    }
                    if(derAction instanceof ActionIndiquerValeur daiv && daiv.agentCible() == agent){
                        EtatHanabi etatIndique = (EtatHanabi)daiv.etat();
                        List<Carte> mainAgent = etatIndique.getMainAgent(agent);

                        for(int i = 0;i < 5;i++){
                            Set<Carte> domaine = listDomainesCartes.get(i);
                            if(mainAgent.get(i).getNumero() == daiv.valeur()){
                                domaine.removeIf(carte -> carte.getNumero() != daiv.valeur());
                            }
                            else{
                                domaine.removeIf(carte -> carte.getNumero() == daiv.valeur());
                            }
                        }
                    }
                    //Met a jour le domaine cible avec l'observation de l'agent avec l'indice qu'a recu la cible

                    if(derAction instanceof ActionIndiquerValeur daiv && daiv.agentCible() != agent){
                        // Second ordre : mise à jour des domaines estimés de la cible
                        EtatHanabi etatIndique = (EtatHanabi)daiv.etat();
                        Agent<?> cible = daiv.agentCible();
                        List<Carte> mainCible = etatIndique.getMainAgent(cible);
                        List<Set<Carte>> ds = domainesAutres.get(cible);
                        for(int i = 0; i < 5; i++){
                            if(mainCible.get(i).getNumero() == daiv.valeur())
                                ds.get(i).removeIf(c -> c.getNumero() != daiv.valeur());
                            else
                                ds.get(i).removeIf(c -> c.getNumero() == daiv.valeur());
                        }
                    }
                    // Si un autre agent pose/défausse, sa nouvelle carte est inconnue. On reconstruit le domaine en retirant ce que cet agent peut voir : les piles (visibles par tous) et ses propres autres cartes (visibles par lui).
                    if((derAction instanceof ActionDefausse || derAction instanceof ActionPoser) && agentAction != agent){
                        int pos = derAction instanceof ActionDefausse ad ? ad.posCarte() : ((ActionPoser)derAction).posCarte();
                        List<Set<Carte>> ds = domainesAutres.get(agentAction);
                        Set<Carte> nouveauDomaine = new HashSet<>(carteBase);
                        // retirer les cartes sur les piles
                        for(Deque<Carte> pile : lastObs.getEtatPiles().values())
                            nouveauDomaine.removeAll(pile);
                        // retirer les autres cartes visibles de cet agent (ses 4 autres positions)
                        List<Carte> mainAutreAgent = lastObs.getMainsAutresAgents().get(agentAction);

                        for(int p = 0; p < mainAutreAgent.size(); p++) {
                            if (p != pos) nouveauDomaine.remove(mainAutreAgent.get(p));
                        }
                        ds.set(pos, nouveauDomaine);
                    }
                }

            }
            
            initDomainesAutres(autresMains);

            //Filtre les domaines par rapport aux mains des autres joueurs
            for(Map.Entry<Agent<?>, List<Carte>> mains : autresMains.entrySet()){
                List<Carte> mainAgent = mains.getValue();
                for(Carte carte : mainAgent){
                    this.domaineDefault.remove(carte);
                    int indexDomaine = 0;
                    for(Set domaine :listDomainesCartes){
                        boolean test = domaine.remove(carte);
                        if(test){
                            if(verbose) System.out.println("Carte " + carte+" supprimée du domaine "+indexDomaine);
                        }
                        indexDomaine+=1;
                    }
                }
            }


            //Cas ou ce n'est pas le tour de l'agent
            if (actionsPossibles.size() == 1 && actionsPossibles.get(0) instanceof ActionVide ){
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
            
            if(verbose) System.out.println("Les DOMAINES des cartes de "+ agent +" :\n");
            for(int i =0;i<5;i++){
                List<Carte> listAffichage = new ArrayList<>();
                Set<Carte> sc = listDomainesCartes.get(i);
                for(Carte c : sc){
                    listAffichage.add(c);
                }
                Collections.sort(listAffichage);
                if(verbose) System.out.println("Domainee " + i + " : " + listAffichage + "\n");
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
                    int i = 0;
                    for(Carte carte: cartes){
                        Deque<Carte> pile = lastJeu.get(carte.getCouleur());
                        int sv = scoreIndice(new ActionIndiquerValeur(agentCible, carte.getNumero(), null), agentCible, cartes);
                        int sc = scoreIndice(new ActionIndiquerCouleur(agentCible, carte.getCouleur(), null), agentCible, cartes);
                        int choixIndiceResult = (sv == 0 && sc == 0) ? 2 : (sv > sc ? 0 : 1);
                        if(pile.isEmpty() && carte.getNumero() == 1){ //Pour pouvoir Jouer les carte 1
                            for(Action a : actionInfo){ 
                                if (choixIndiceResult == 0 || allDomainesEmpty){ 
                                    if(a instanceof ActionIndiquerValeur aiv && aiv.valeur() == carte.getNumero() && aiv.agentCible() == agentCible){ 
                                        return aiv;
                                    }
                                }
                                if (choixIndiceResult == 1 || allDomainesEmpty){ 
                                    if(a instanceof ActionIndiquerCouleur aic && aic.couleur() == carte.getCouleur() && aic.agentCible() == agentCible){ 
                                        return aic;
                                    }
                                }
                            }

                        }

                        else if (!pile.isEmpty() && carte.getNumero() == (pile.peek()).getNumero() + 1){ //Pour pouvoir jouer les autres cartes que 1
                            for(Action a : actionInfo){ 
                                if (choixIndiceResult == 0){ 
                                    if(a instanceof ActionIndiquerValeur aiv && aiv.valeur() == carte.getNumero() && aiv.agentCible() == agentCible){ 
                                        return aiv;
                                    }
                                }
                                if(choixIndiceResult == 1){ 
                                    if(a instanceof ActionIndiquerCouleur aic && aic.couleur() == carte.getCouleur() && aic.agentCible() == agentCible){ 
                                        return aic;
                                    }
                                }
                            }
                        }
                        i++;
                    }

                }

                //Je me défausse d'une carte que je ne peux pas jouer.
                for(int i = 0;i<5;i++){
                    boolean isThisDomainNull = true;
                    Set<Carte> domaine = listDomainesCartes.get(i);
                    for(Carte carte : domaine){
                        if(carte.isPlayable(lastObs.getEtatPiles())){
                            isThisDomainNull = false;
                        }
                    }
                }

                //Je donne un indice sur une carte qui ne peut pas être jouer tt de suite mais potentiellement plus tard.
                for(Map.Entry<Agent<?>,List<Carte>> entry : lastMains.entrySet()){
                    Agent agentCible = entry.getKey();
                    List<Carte> cartes = entry.getValue();
                    int i = 0;
                    for(Carte carte: cartes){
                        Deque<Carte> pile = lastJeu.get(carte.getCouleur());
                        int sv = scoreIndice(new ActionIndiquerValeur(agentCible, carte.getNumero(), null), agentCible, cartes);
                        int sc = scoreIndice(new ActionIndiquerCouleur(agentCible, carte.getCouleur(), null), agentCible, cartes);
                        int choixIndiceResult = (sv == 0 && sc == 0) ? 2 : (sv > sc ? 0 : 1);
                        if(!pile.isEmpty() && carte.getNumero() >= (pile.peek()).getNumero() + 1){
                            for(Action a : actionInfo){
                                //Enlever l'aléatoire et utiliser les croyances.
                                if(choixIndiceResult == 0){ 
                                    if(a instanceof ActionIndiquerValeur aiv && aiv.valeur() == carte.getNumero() && aiv.agentCible() == agentCible){ 
                                        return aiv;
                                    }
                                }
                                if(choixIndiceResult == 1){ 
                                    if(a instanceof ActionIndiquerCouleur aic && aic.couleur() == carte.getCouleur() && aic.agentCible() == agentCible){ 
                                        return aic;
                                    }
                                }
                            }
                        }
                    }
                    i++;
                }
                //Je donne un indice sur une carte injouable
                for(Map.Entry<Agent<?>,List<Carte>> entry : lastMains.entrySet()){
                    Agent agentCible = entry.getKey();
                    List<Carte> cartes = entry.getValue();
                    int i = 0;
                    for(Carte carte: cartes){
                        Deque<Carte> pile = lastJeu.get(carte.getCouleur());
                        int sv = scoreIndice(new ActionIndiquerValeur(agentCible, carte.getNumero(), null), agentCible, cartes);
                        int sc = scoreIndice(new ActionIndiquerCouleur(agentCible, carte.getCouleur(), null), agentCible, cartes);
                        int choixIndiceResult = (sv == 0 && sc == 0) ? 2 : (sv > sc ? 0 : 1);
                        if(!pile.isEmpty() && carte.getNumero() <= (pile.peek()).getNumero()){
                            for(Action a : actionInfo){
                                //Enlever l'aléatoire et utiliser les croyances.
                                if(choixIndiceResult == 0){ 
                                    if(a instanceof ActionIndiquerValeur aiv && aiv.valeur() == carte.getNumero() && aiv.agentCible() == agentCible){ 
                                        return aiv;
                                    }
                                }
                                if(choixIndiceResult == 1){ 
                                    if(a instanceof ActionIndiquerCouleur aic && aic.couleur() == carte.getCouleur() && aic.agentCible() == agentCible){ 
                                        return aic;
                                    }
                                }
                            }
                        }
                    }
                    i++;
                }
                //je me défausse d'une carte aléatoire.
                if(lastObs.getSizePioche()>1){
                    int posRandom = generateur.nextInt(actionDefausse.size());
                    listDomainesCartes.set(posRandom, new HashSet<Carte>(this.domaineDefault));
                    return actionDefausse.get(posRandom);  //Action défausse (choix de carte random pour l'instant)
                }
            }
            int posRandom = generateur.nextInt(actionDefausse.size());
            listDomainesCartes.set(posRandom, new HashSet<Carte>(this.domaineDefault));
            return actionDefausse.get(posRandom);  //Action défausse (choix de carte random pour l'instant)
            
        }
        return null;
    }
    /**
     * Pour une carte d'un agent on regarde quel indice va réduire un maximum son domaine et on renvoie 0 ou 1 en fonction du plus intéressant.
     */
}
