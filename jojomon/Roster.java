import java.util.List;
import java.util.Random;

/** The creatures you can pick from, and the enemy generator. All names and designs are original. */
public final class Roster {
    private Roster() {}

    private static final List<Jojomon> ALL = List.of(
        new Jojomon("Scarlet Gale", Type.FLAIR, 90, 70, 55, 80, List.of(
            Move.attack("Ember Pose", Type.FLAIR, 45, 100),
            Move.attack("Dramatic Gust", Type.FLAIR, 70, 90),
            Move.attack("Quick Jab", Type.GRIT, 40, 100),
            Move.heal("Second Wind", 30))),

        new Jojomon("Ironclad Dandy", Type.GRIT, 110, 65, 80, 40, List.of(
            Move.attack("Steel Strut", Type.GRIT, 60, 100),
            Move.attack("Anvil Drop", Type.GRIT, 85, 80),
            Move.attack("Fabric Whip", Type.MYSTIC, 40, 100),
            Move.heal("Brace Up", 30))),

        new Jojomon("Moonlit Oracle", Type.MYSTIC, 85, 80, 50, 65, List.of(
            Move.attack("Whisper Hex", Type.MYSTIC, 55, 100),
            Move.attack("Starfall", Type.MYSTIC, 80, 85),
            Move.attack("Spark Flick", Type.FLAIR, 40, 100),
            Move.heal("Moon Mend", 30))),

        new Jojomon("Gilded Brawler", Type.GRIT, 100, 85, 60, 55, List.of(
            Move.attack("Golden Haymaker", Type.GRIT, 75, 85),
            Move.attack("Flurry", Type.GRIT, 45, 100),
            Move.attack("Heckle", Type.MYSTIC, 40, 100),
            Move.attack("Victory Flex", Type.FLAIR, 50, 95))),

        new Jojomon("Neon Phantom", Type.MYSTIC, 80, 75, 55, 90, List.of(
            Move.attack("Glow Slash", Type.MYSTIC, 60, 95),
            Move.attack("Haunt", Type.MYSTIC, 45, 100),
            Move.attack("Flash Bulb", Type.FLAIR, 55, 95),
            Move.heal("Fade Out", 30))),

        new Jojomon("Sunburst Duelist", Type.FLAIR, 95, 80, 60, 60, List.of(
            Move.attack("Radiant Lunge", Type.FLAIR, 70, 90),
            Move.attack("Showboat", Type.FLAIR, 50, 100),
            Move.attack("Gauntlet Slap", Type.GRIT, 45, 100),
            Move.attack("Lens Flare", Type.MYSTIC, 40, 100))),

        new Jojomon("Marble Colossus", Type.GRIT, 125, 60, 85, 30, List.of(
            Move.attack("Statue Slam", Type.GRIT, 80, 85),
            Move.attack("Chisel", Type.GRIT, 50, 100),
            Move.attack("Echo Boom", Type.MYSTIC, 45, 100),
            Move.heal("Stand Firm", 30))),

        new Jojomon("Twilight Courier", Type.MYSTIC, 90, 65, 70, 70, List.of(
            Move.attack("Dusk Delivery", Type.MYSTIC, 60, 100),
            Move.attack("Shadow Parcel", Type.MYSTIC, 70, 90),
            Move.attack("Torch Toss", Type.FLAIR, 45, 100),
            Move.heal("Rest Stop", 30)))
    );

    /** Fresh copies of every playable creature. */
    public static List<Jojomon> all() {
        return ALL.stream().map(Jojomon::copy).toList();
    }

    /** A random enemy, 6% tougher for every enemy already defeated. */
    public static Jojomon randomEnemy(Random rng, int defeated) {
        Jojomon base = ALL.get(rng.nextInt(ALL.size()));
        return base.scaled(1.0 + 0.06 * defeated);
    }
}
