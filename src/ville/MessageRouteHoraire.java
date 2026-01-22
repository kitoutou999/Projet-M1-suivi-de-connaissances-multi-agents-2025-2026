package core;

import core.*;
import java.util.*;

public class MessageRouteHoraire implements Message {
    private int step1;//début
    private int step2;//fin
    private String name;
    private boolean ouvert;//si à true alors la route est ouverte de step1 à step2 sinon elle est fermée de step1 à step2
    public MessageRouteHoraire(int step1,int step2,String name,Boolean ouvert){
        this.step1 = step1;
        this.step2 = step2;
        this.name = name;
        this.ouvert = ouvert;
    }
    @Override
    public String toString(){
        String openClose = "ouverte";
        if(this.ouvert == false){
            openClose = "fermée";
        }
        String info = "La route "+this.name+"est "+openClose+" du pas de temps "+this.step1+" à "+this.step2+".";
        return info;
    }
    public int getStep1(){
        return this.step1;
    }
    public int getStep2(){
        return this.step2;
    }
    public String getName(){
        return this.name;
    }
    public Boolean getStatus(){//ne renvoie pas l'état acutel mais uniquement si l'info porte sur le fait que la route soit fermée ou ouverte
        return this.ouvert;
    }
}
