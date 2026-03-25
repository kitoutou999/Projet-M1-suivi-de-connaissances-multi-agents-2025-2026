package hanabi;

import java.util.*;
import java.util.Random;
import core.*;


public class PolitiqueSimpleHanabi implements Politique {

    @Override
    public Action chooseAction(Agent agent, Historique historique, List<Action> actionsPossibles) {
        Action actionChoisi = null;
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
            else if(action instanceof ActionIndiquer ai){
                actionInfo.add(ai);
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


        List<Set> listDomainesCartes = new ArrayList<>(); //Liste d'ensemble de carte
        for(int i = 0; i < 5;i++){ //Créer 5 ensemble, 1 pour chaque postion de la main de l'agent
            Set<Carte> domaineCarte = new HashSet();
            listDomainesCartes.add(domaineCarte);
        }

        Random random = new Random();
        
        for(Map.Entry<Action, Observation> entree : historique.getListe()){
			ObservationHanabi obs = (ObservationHanabi) entree.getValue();
		}

        /*
            Si j'ai au moins une carte jouable dans au moins un domaine -> actionJouer (l'une des cartes en questions)
             Sinon -> on passe à la suite
        */
        if(false){
            actionChoisi = actionPoser.get(0);
        }

        /*
            Pour chaque agent a:
                Pour chaque carte c de l'agent a
                    regarder si la carte c et jouable dans le jeu (regarder la pile de la couleur de la carte c)
                        Si oui -> actionIndiquer
                        Sinon -> on passe à la suite
        */
        else if(false){
            actionChoisi = actionInfo.get(0);
        }
        else{ //Action défausse (choix de carte random pour l'instant)
            actionChoisi = actionDefausse.get(random.nextInt(actionDefausse.size())); 
        }
        
        return actionChoisi;
    }
}