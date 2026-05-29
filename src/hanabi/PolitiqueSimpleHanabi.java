package hanabi;

import java.util.*;
import java.util.Random;
import core.*;


public class PolitiqueSimpleHanabi implements Politique {

    private List<Set> listDomainesCartes;
    private Set<Carte> domaineDefault;
    private boolean premierTour;
    private List<Carte> carteBase;
    private Random generateur;
    private boolean verbose;

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
                            Set<Carte> domaine = listDomainesCartes.get(i);
                            if(mainAgent.get(i).getCouleur().equals(daic.couleur())){
                                domaine.removeIf(carte -> !carte.getCouleur().equals(daic.couleur()));
                            }
                            else{
                                domaine.removeIf(carte -> carte.getCouleur().equals(daic.couleur()));
                            } 
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
                }

            }
            
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
                        int choixIndiceResult = this.choixIndice(agentCible,agent,historique,carte,agentHanabi.getAutresMains(),i);
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
                    if(isThisDomainNull){
                        listDomainesCartes.set(i,new HashSet<Carte>(this.domaineDefault));
                        return actionDefausse.get(i);
                    }
                }

                //Je donne un indice sur une carte qui ne peut pas être jouer tt de suite mais potentiellement plus tard.
                for(Map.Entry<Agent<?>,List<Carte>> entry : lastMains.entrySet()){
                    Agent agentCible = entry.getKey();
                    List<Carte> cartes = entry.getValue();
                    int i = 0;
                    for(Carte carte: cartes){
                        Deque<Carte> pile = lastJeu.get(carte.getCouleur());
                        int choixIndiceResult = this.choixIndice(agentCible,agent,historique,carte,agentHanabi.getAutresMains(),i);
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
                        int choixIndiceResult = this.choixIndice(agentCible,agent,historique,carte,agentHanabi.getAutresMains(),i);
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
    public int choixIndice(Agent agent,Agent agentSelf,Historique historique,Carte carte,Map<Agent<?>, List<Carte>>  mainAutre,int idCarte){
        if(verbose) System.out.println("CHOIX INDICE ------------------------------------------------------------------");
        List<Carte> domaineCarte = new ArrayList<>(this.carteBase);//domaine par défaut qu'on utilise pour construire des domaines
        List<Map.Entry<Action,Observation>> listeHisto = historique.getListe();
        Set<Action> setOfIndice = new HashSet<>();//tout les indices qui portent sur la carte.
        List<Carte> listCarteConnues = new ArrayList<>();//Toutes les cartes connues. Défausse,piles,mains.
        //Observation lastObss = historique.getDerniere().getValue();
        ObservationHanabi lastObs = (ObservationHanabi) historique.getDerniere().getValue();


        for(Map.Entry<Action, Observation> entry : listeHisto){
            ObservationHanabi obs = (ObservationHanabi) entry.getValue();
            Action actionCible = obs.getActions().getActions().get(agent);
            if(actionCible instanceof ActionDefausse act && act.getPosCarte() == idCarte){
                setOfIndice.clear();
                continue;
            }
            if(actionCible instanceof ActionPoser act && act.getPosCarte() == idCarte){
                setOfIndice.clear();
                continue;
            }
            for(Action action : obs.getActions().getActions().values()){
                if(action instanceof ActionIndiquerCouleur acti && acti.getAgentCible().equals(agent) && acti.getCouleur().equals(carte.getCouleur())){
                    setOfIndice.add(action);
                }
                if(action instanceof ActionIndiquerValeur actii && actii.getAgentCible().equals(agent) && actii.getNumero() == carte.getNumero()){
                    setOfIndice.add(action);
                }
            }
        }
        //On stock les cartes des mains des autres agents.
        for(Map.Entry<Agent<?>,List<Carte>> entry : mainAutre.entrySet()){
            AgentHanabi agenth = (AgentHanabi)entry.getKey();
            List<Carte> main = entry.getValue();
            if(!agenth.equals(agent) && !agenth.equals(agentSelf)){
                for(Carte c : main){
                    listCarteConnues.add(c);
                }
            }
        }
        //On stock les cartes de la défausse.
        for(Map.Entry<Action, Observation> entry : listeHisto){
            ObservationHanabi obs = (ObservationHanabi) entry.getValue();
            if(obs.getAgent().equals(agent)){
                List<Carte> defausse = obs.getDefausse();
                for(Carte c : defausse){
                    listCarteConnues.add(c);
                }
            }
        }
        //On stock les cartes de la piles.
        Map<String, Deque<Carte>> etatPiles = lastObs.getEtatPiles();
        for(Map.Entry<String, Deque<Carte>> entry : etatPiles.entrySet()){
            Deque<Carte> deque = entry.getValue();
            for(Carte c : deque){
                listCarteConnues.add(c);
            }
        }
        if(verbose) System.out.println("cartes connues");
        if(verbose) System.out.println(listCarteConnues);
        if(verbose) System.out.println("indices qui portent sur la carte : "+carte);
        if(verbose) System.out.println(setOfIndice);
        if(verbose) System.out.println("Defausse");
        if(verbose) System.out.println(lastObs.getDefausse());

        //On supprimer du domaine de défaut les cartes qui sont connues.
        for(Carte c : listCarteConnues){
            domaineCarte.remove(c);
        }

        for(Action act : setOfIndice){
            if(act instanceof ActionIndiquerCouleur){
                Iterator<Carte> iterator1 = domaineCarte.iterator();
                while(iterator1.hasNext()){
                    Carte c = iterator1.next();
                    if(c.getCouleur() != carte.getCouleur()){
                        iterator1.remove();
                    }
                }

            }
            if(act instanceof ActionIndiquerValeur){
                Iterator<Carte> iterator2 = domaineCarte.iterator();
                while(iterator2.hasNext()){
                    Carte c = iterator2.next();
                    if(c.getNumero() != carte.getNumero()){
                        iterator2.remove();
                    }
                }
            }
        }
        List<Carte> domaineNum = new ArrayList<>(domaineCarte);
        List<Carte> domaineCouleur = new ArrayList<>(domaineCarte);
        //on applique au deux domaines respectivement un indice sur le numéro et un indice sur la couleur.
        Iterator<Carte> iterator = domaineNum.iterator();
        while(iterator.hasNext()){
            Carte c = iterator.next();
            if(c.getNumero() != carte.getNumero()){
                iterator.remove();
            }
        }
        iterator = domaineCouleur.iterator();
        while(iterator.hasNext()){
            Carte c = iterator.next();
            if(c.getCouleur() != carte.getCouleur()){
                iterator.remove();
            }
        }
        //on regarde lequel a subi la plus grosse reduction.
        int diffNum = domaineCarte.size() - domaineNum.size();
        int diffCouleur = domaineCarte.size() - domaineCouleur.size();
        if(verbose) System.out.println("Domaine de la carte,couleur,numero");
        if(verbose) System.out.println(domaineCarte);
        if(verbose) System.out.println(domaineCouleur);
        if(verbose) System.out.println(domaineNum);
        if(domaineCarte.equals(domaineNum) && domaineCarte.equals(domaineCouleur)){
            if(verbose) System.out.println("pas de reduction de domaine");
            return 2;
        }
        if(diffNum > diffCouleur){
            if(verbose) System.out.println("on donne indice numéro");
            return 0;
        }else{
            if(verbose) System.out.println("on donne indice couleur");
            return 1;
        }
        //0 quand indice sur le numéro reduit plus le domaine, sinon 1 pour couleur
        
    }
}
