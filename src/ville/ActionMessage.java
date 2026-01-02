package ville;

public record ActionMessage(String contenu) implements ActionVille {
    @Override
    public String toString() {
        return "Laisser msg: '" + contenu + "'";
    }
}
