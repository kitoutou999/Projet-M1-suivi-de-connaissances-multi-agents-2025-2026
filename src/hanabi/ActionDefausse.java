package hanabi;
import java.util.*;
import core.*;

public record ActionDefausse(int posCarte) implements ActionHanabi{
    @Override
    public String toString() {
        return "Action défausser la carte à la position " + posCarte;
    }

}