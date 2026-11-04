import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * All the battle rules, with no Swing in sight so it can be tested headless.
 *
 * The fight never ends on a win: when the enemy faints, your team recovers a little
 * and a tougher challenger steps in. The run ends only when your whole team has fainted.
 */
public class Battle {
    private final List<Jojomon> team = new ArrayList<>();
    private final Random rng;
    private int activeIdx = 0;
    private Jojomon enemy;
    private int defeated = 0;
    private boolean over = false;
    private boolean needsSwitch = false;

    public Battle(List<Jojomon> chosenTeam, Random rng) {
        for (Jojomon j : chosenTeam) team.add(j.copy());
        this.rng = rng;
        this.enemy = Roster.randomEnemy(rng, 0);
    }

    public List<Jojomon> team() { return team; }
    public int activeIndex() { return activeIdx; }
    public Jojomon player() { return team.get(activeIdx); }
    public Jojomon enemy() { return enemy; }
    public int defeated() { return defeated; }
    public boolean isOver() { return over; }
    /** True when the active creature fainted and the player must pick a replacement (free switch). */
    public boolean needsSwitch() { return needsSwitch; }

    /** Player uses one of the active creature's moves; the enemy answers. Returns log lines. */
    public List<String> useMove(int moveIdx) {
        List<String> log = new ArrayList<>();
        if (over || needsSwitch) return log;

        Move playerMove = player().moves().get(moveIdx);
        Move enemyMove = pickEnemyMove();
        boolean playerFirst = player().spd() > enemy.spd()
                || (player().spd() == enemy.spd() && rng.nextBoolean());

        if (playerFirst) {
            act(player(), enemy, playerMove, true, log);
            if (enemy.fainted()) { onEnemyFaint(log); return log; }
            act(enemy, player(), enemyMove, false, log);
            if (player().fainted()) onPlayerFaint(log);
        } else {
            act(enemy, player(), enemyMove, false, log);
            if (player().fainted()) { onPlayerFaint(log); return log; }
            act(player(), enemy, playerMove, true, log);
            if (enemy.fainted()) onEnemyFaint(log);
        }
        return log;
    }

    /**
     * Swap in the creature at idx. Switching on purpose costs your turn (the enemy attacks);
     * switching because your creature fainted is free.
     */
    public List<String> switchTo(int idx) {
        List<String> log = new ArrayList<>();
        if (over || idx < 0 || idx >= team.size() || idx == activeIdx || team.get(idx).fainted()) {
            return log;
        }
        boolean free = needsSwitch;
        activeIdx = idx;
        needsSwitch = false;
        log.add("Go, " + player().name() + "!");
        if (!free) {
            act(enemy, player(), pickEnemyMove(), false, log);
            if (player().fainted()) onPlayerFaint(log);
        }
        return log;
    }

    private Move pickEnemyMove() {
        return enemy.moves().get(rng.nextInt(enemy.moves().size()));
    }

    private void act(Jojomon user, Jojomon target, Move m, boolean playerSide, List<String> log) {
        String who = (playerSide ? "" : "Enemy ") + user.name();

        if (m.isHeal()) {
            int healed = user.heal(user.maxHp() * m.healPct() / 100);
            log.add(who + " used " + m.name() + " and recovered " + healed + " HP.");
            return;
        }
        if (rng.nextInt(100) >= m.accuracy()) {
            log.add(who + " used " + m.name() + " but missed!");
            return;
        }
        double mult = m.type().multiplierAgainst(target.type());
        double base = (m.power() * (double) user.atk() / target.def()) / 3.5 + 2;
        double roll = 0.9 + rng.nextDouble() * 0.2;
        int dmg = Math.max(1, (int) Math.round(base * mult * roll));
        target.damage(dmg);

        String note = mult > 1 ? " It's super effective!" : mult < 1 ? " Not very effective..." : "";
        log.add(who + " used " + m.name() + " for " + dmg + " damage." + note);
    }

    private void onEnemyFaint(List<String> log) {
        defeated++;
        log.add(enemy.name() + " was defeated! (" + defeated + " down)");
        for (Jojomon j : team) {
            if (!j.fainted()) j.heal(j.maxHp() / 5);
        }
        log.add("Your team catches its breath (+20% HP).");
        enemy = Roster.randomEnemy(rng, defeated);
        log.add("A new challenger appears: " + enemy.name() + "!");
    }

    private void onPlayerFaint(List<String> log) {
        log.add(player().name() + " fainted!");
        boolean allDown = team.stream().allMatch(Jojomon::fainted);
        if (allDown) {
            over = true;
            log.add("Your team is out. Final score: " + defeated);
        } else {
            needsSwitch = true;
        }
    }
}
