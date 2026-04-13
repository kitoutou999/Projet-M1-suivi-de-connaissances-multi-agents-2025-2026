package hanabi;
import java.util.*;
import core.*;

public record ActionIndiquerValeur(Agent agentCible,int valeur,EtatHanabi etat) implements ActionHanabi{
    @Override
    public String toString() {
        return "Action indique valeur des cartes " + String.valueOf(valeur);
    }

}