package ville;

public record ActionRester() implements ActionVille {
    @Override
    public String toString() {
        return "Rester sur place";
    }
}
