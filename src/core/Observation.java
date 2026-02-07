package core;
import java.util.Objects;

public abstract class Observation{
    protected String name;

    @Override
    public boolean equals(Object obs){
        Observation obss = (Observation) obs;
        if(this.name != obss.getName()){
            return false;
        }else{
            return true;
        }

    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName(){
        return this.name;
    }
}