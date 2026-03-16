package ville;

import core.Message;

public record ActionMessage(Message contenu) implements ActionVille {
    @Override
    public String toString() {
        return "Laisser msg: '" + contenu + "'";
    }
}
