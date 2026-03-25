package hanabi;
import java.util.*;

public record ActionIndiquer() implements ActionHanabi{
    @Override
    public String toString() {
        return "Action indique carte";
    }

}