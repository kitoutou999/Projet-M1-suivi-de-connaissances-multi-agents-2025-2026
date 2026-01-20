package hanabi;
import java.util.*;

public record ActionPoser(int posCarte) implements ActionHanabi{
    @Override
    public String toString() {
        return "Action poser la carte à la position " + posCarte;
    }

}