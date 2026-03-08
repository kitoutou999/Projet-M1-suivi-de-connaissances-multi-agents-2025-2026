package ville;

import core.*;
import java.util.*;
/**
 * Message pour une route qui est fermée définitivement.
 * Comporte un entier step qui représente le moment où l'information de la route fermée est obtenue.
 */
public class MessageRouteFerme implements Message {
    private int step;
    private String name;
    public MessageRouteFerme(int step,String name){
        this.step = step;
        this.name = name;
    }
    @Override
    public String toString(){
        String info = "La route "+this.name+" est fermée, cela a été vu au pas de temps "+this.step+".";
        return info;
    }
    public int getStep(){
        return this.step;
    }
    public String getName(){
        return this.name;
    }
}
