# Jojomon

A turn-based creature battler in Java Swing, in the spirit of Pokémon with a flamboyant, JoJo-inspired flavour. All creatures, moves and names are original.

Pick 3 fighters, then see how many challengers you can beat. A win doesn't end the fight: your team catches its breath (+20% HP) and a tougher challenger steps in. The run ends when your whole team has fainted.

## Run it

Needs JDK 17 or newer.

```
javac *.java
java JojomonGame
```

To run the headless balance test (plays 3,000 battles per strategy and checks they always finish):

```
java Simulate
```

## How it plays

- **Types:** Flair beats Grit, Grit beats Mystic, Mystic beats Flair (1.5x damage, or 0.67x the other way).
- **Turn order:** the faster creature moves first.
- **Moves:** each creature has up to 4 moves. Some attack, some heal 30% of max HP. Attacks can miss.
- **Switching:** swapping on purpose costs your turn. Swapping because your creature fainted is free.
- **Difficulty:** every enemy defeated makes the next one 6% tougher.

## Code layout

| File | Job |
| --- | --- |
| `Type.java` | The type triangle and damage multipliers |
| `Move.java` | Attack and heal moves |
| `Jojomon.java` | A creature: stats, moves, current HP |
| `Roster.java` | The 8 playable creatures and the enemy generator |
| `Battle.java` | All battle rules, no GUI code, so it can be tested on its own |
| `JojomonGame.java` | Swing screens: team select and battle |
| `Simulate.java` | Headless test and balance check |

The rules live in `Battle` and the window lives in `JojomonGame`, so you can change one without breaking the other.

## Credits
Me, Myself and I
