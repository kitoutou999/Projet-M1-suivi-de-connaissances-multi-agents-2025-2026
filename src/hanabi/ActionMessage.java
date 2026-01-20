package hanabi;
import java.util.*;

public record ActionMessage(String message) implements ActionHanabi{
    @Override
    public String toString() {
        return "Action laisse le message suivant: " + message;
    }

}