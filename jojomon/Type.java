import java.awt.Color;

/** Three-way type triangle: FLAIR beats GRIT, GRIT beats MYSTIC, MYSTIC beats FLAIR. */
public enum Type {
    FLAIR(new Color(0xE4572E)),
    GRIT(new Color(0x8D99AE)),
    MYSTIC(new Color(0x7B5EA7));

    public final Color color;

    Type(Color color) {
        this.color = color;
    }

    /** Damage multiplier when an attack of this type hits a defender of type {@code defender}. */
    public double multiplierAgainst(Type defender) {
        if (beats(this, defender)) return 1.5;
        if (beats(defender, this)) return 0.67;
        return 1.0;
    }

    private static boolean beats(Type a, Type b) {
        return (a == FLAIR && b == GRIT)
                || (a == GRIT && b == MYSTIC)
                || (a == MYSTIC && b == FLAIR);
    }
}
