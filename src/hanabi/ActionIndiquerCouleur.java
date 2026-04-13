package hanabi;
import java.util.*;
import core.*;

public record ActionIndiquerCouleur(Agent agentCible,String couleur,EtatHanabi etat) implements ActionHanabi{
    @Override
    public String toString() {
        return "Action indique couleur des cartes " + couleur;
    }

}