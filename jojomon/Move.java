/**
 * A move is either an attack (power > 0) or a heal (healPct > 0).
 * accuracy is a percentage from 1 to 100.
 */
public record Move(String name, Type type, int power, int accuracy, int healPct) {

    public static Move attack(String name, Type type, int power, int accuracy) {
        return new Move(name, type, power, accuracy, 0);
    }

    public static Move heal(String name, int healPct) {
        return new Move(name, Type.MYSTIC, 0, 100, healPct);
    }

    public boolean isHeal() {
        return healPct > 0;
    }

    /** Short text for buttons, e.g. "GRIT - pwr 60 - 100%". */
    public String summary() {
        if (isHeal()) return "Heal " + healPct + "% HP";
        return type + " - pwr " + power + " - " + accuracy + "%";
    }
}
