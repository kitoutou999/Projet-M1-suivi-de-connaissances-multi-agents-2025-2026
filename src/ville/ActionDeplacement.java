package ville;

public record ActionDeplacement(String destination) implements ActionVille {
    @Override
    public String toString() {
        return "Aller vers " + destination;
    }
}
