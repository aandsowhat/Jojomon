import java.util.List;

/** One creature. Species stats are fixed; hp changes during a fight. */
public class Jojomon {
    private final String name;
    private final Type type;
    private final int maxHp;
    private final int atk;
    private final int def;
    private final int spd;
    private final List<Move> moves;
    private int hp;

    public Jojomon(String name, Type type, int maxHp, int atk, int def, int spd, List<Move> moves) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.moves = List.copyOf(moves);
        this.hp = maxHp;
    }

    /** A fresh, full-health copy with hp/atk/def multiplied by factor (speed stays the same). */
    public Jojomon scaled(double factor) {
        return new Jojomon(name, type,
                (int) Math.round(maxHp * factor),
                (int) Math.round(atk * factor),
                (int) Math.round(def * factor),
                spd, moves);
    }

    public Jojomon copy() {
        return scaled(1.0);
    }

    public void damage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    /** Heals up to maxHp and returns the amount actually recovered. */
    public int heal(int amount) {
        int before = hp;
        hp = Math.min(maxHp, hp + amount);
        return hp - before;
    }

    public boolean fainted() {
        return hp <= 0;
    }

    public String name() { return name; }
    public Type type() { return type; }
    public int hp() { return hp; }
    public int maxHp() { return maxHp; }
    public int atk() { return atk; }
    public int def() { return def; }
    public int spd() { return spd; }
    public List<Move> moves() { return moves; }
}
