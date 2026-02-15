package hanabi;
import java.util.*;

public record ActionVide() implements ActionHanabi{
    @Override
    public String toString() {
        return "Joue pas";
    }

}