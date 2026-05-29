package hanabi;
import java.util.*;

public record ActionVide() implements ActionHanabi{
    @Override
    public String toString() {
        return "Joue pas";
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        return this.toString().equals(obj.toString());
    }

}