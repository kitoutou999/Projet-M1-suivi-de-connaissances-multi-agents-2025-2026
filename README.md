# Projet Suivi de Connaissances Multi-Agents

Academic project (M1) — a multi-agent framework where each agent maintains a belief state and tracks knowledge of other agents.

Applied to two games: **Hanabi** (cooperative card game) and a **city simulation**.

## Architecture

| Component | Description |
|---|---|
| `Simulateur` | Runs the game loop and dispatches observations |
| `Agent` | Holds a `BeliefState` and chooses actions via a `Politique` |
| `BeliefStateFilter` | Updates beliefs based on observations |
| `Jeu` | Defines initial state, transitions, and terminal conditions |

## Adding a New Game

1. Implement `Jeu`: define `getInitialState`, `isFinal`, `nextState`, `getActionsPossibles`, `makeObservation`
2. Define an `Etat` class
3. Define `Action` and `Observation` (with `equals()`)
4. Implement a `Politique`: `chooseAction(Agent, Historique, List<Action>)`
5. Create a `Main` that instantiates agents and the `Simulateur`

## Run

```bash
# Hanabi (good score)
./run_hanabi.sh

# City simulation
./run_ville.sh
```

## Requirements

- Java 17+
- Maven (`pom.xml` provided)

## Authors

Tom David, Titouan Pasquier, Marius Guillais, Maxim Chepy
