package core;


public record Agent(String id) {
    @Override
    public String toString() {
        return "Agent[" + id + "]";
    }
}
