package ville;

import core.*;
import java.util.*;
/**
 * Message pour une route qui est fermée ou ouverte selon des horaires(de step1 à step2).
 * Si il nous manque une information que ça soit de début ou de fin alors cette information est à -1.
 * @param step1 début de l'horaire.
 * @param step2 fin de l'horaire.
 * @param ouvert Si à true alors la route est ouverte de step1 à step2 sinon elle est fermée de step1 à step2.
 */
public class MessageRouteHoraire implements Message {
    private int step1;
    private int step2;
    private String name;
    private boolean ouvert;
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
        if(this.step1 == -1){
            info = "La route "+this.name+"est "+openClose+" du pas de temps (?) à "+this.step2+".";
        }else if(this.step2 == -1){
            info = "La route "+this.name+"est "+openClose+" du pas de temps "+this.step1+" à (?).";
        }
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
    public Boolean getStatus(){//ne renvoie pas l'état actuel mais uniquement si l'info porte sur le fait que la route soit fermée ou ouverte
        return this.ouvert;
    }
}
