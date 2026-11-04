import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Headless test: plays thousands of battles and checks they always finish. */
public class Simulate {
    enum Policy { RANDOM, GREEDY }

    public static void main(String[] args) {
        for (Policy p : Policy.values()) run(p, 3000);
    }

    static void run(Policy policy, int games) {
        Random rng = new Random(42);
        long totalDefeated = 0, totalTurns = 0;
        int best = 0, maxTurns = 0;

        for (int g = 0; g < games; g++) {
            List<Jojomon> pool = new ArrayList<>(Roster.all());
            java.util.Collections.shuffle(pool, rng);
            Battle b = new Battle(pool.subList(0, 3), rng);

            int turns = 0;
            while (!b.isOver()) {
                if (++turns > 5000) throw new IllegalStateException("battle never ended");
                if (b.needsSwitch()) {
                    for (int i = 0; i < b.team().size(); i++) {
                        if (!b.team().get(i).fainted()) { b.switchTo(i); break; }
                    }
                } else {
                    b.useMove(pickMove(policy, b, rng));
                }
            }
            totalDefeated += b.defeated();
            totalTurns += turns;
            best = Math.max(best, b.defeated());
            maxTurns = Math.max(maxTurns, turns);
        }
        System.out.printf("%-6s games=%d  avg enemies beaten=%.2f  best=%d  avg turns=%.1f  longest=%d%n",
                policy, games, totalDefeated / (double) games, best, totalTurns / (double) games, maxTurns);
    }

    static int pickMove(Policy policy, Battle b, Random rng) {
        List<Move> moves = b.player().moves();
        if (policy == Policy.RANDOM) return rng.nextInt(moves.size());
        int bestIdx = 0;
        double bestScore = -1;
        for (int i = 0; i < moves.size(); i++) {
            Move m = moves.get(i);
            double score = m.isHeal()
                    ? (b.player().hp() < b.player().maxHp() / 2 ? 50 : 0)
                    : m.power() * m.accuracy() / 100.0 * m.type().multiplierAgainst(b.enemy().type());
            if (score > bestScore) { bestScore = score; bestIdx = i; }
        }
        return bestIdx;
    }
}
