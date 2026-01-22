package core;

import core.*;
import java.util.*;

public class MessageRouteFerme implements Message {
    private int step;
    private String name;
    public MessageRouteFerme(int step,String name){
        this.step = step;
        this.name = name;
    }
    @Override
    public String toString(){
        String info = "La route était fermée au pas de temps "+this.step+".";
        return info;
    }
    public int getStep(){
        return this.step;
    }
    public String getName(){
        return this.name;
    }
}
