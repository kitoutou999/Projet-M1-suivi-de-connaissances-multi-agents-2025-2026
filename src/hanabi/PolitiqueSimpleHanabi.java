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

        else if (actionsPossibles.size() == 1 && actionsPossibles.get(0) instanceof ActionVide ){
            return actionsPossibles.get(0);
        }

        List<Action> actionDefausse = new ArrayList<>();
        List<Action> actionPoser = new ArrayList<>();
        List<Action> actionInfo = new ArrayList<>();

        for(Action action : actionsPossibles){
            if(action instanceof ActionDefausse ad){
                actionDefausse.add(ad);
            }
            else if(action instanceof ActionPoser ap){
                actionPoser.add(ap);
            }
            else if(action instanceof ActionMessage am){
                actionInfo.add(am);
            }
        }

        actionChoisi = actionDefausse.get(0); //Défausse la carte la plus à gauche


        return actionChoisi;
    }
}