import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Swing front end for Jojomon: pick 3 creatures, then survive as many challengers as you can. */
public class JojomonGame extends JFrame {
    static final Color BG = new Color(0x14121F);
    static final Color PANEL = new Color(0x1F1B30);
    static final Color TEXT = new Color(0xF2EEE3);
    static final Color MUTED = new Color(0xA9A3C2);
    static final Color GOLD = new Color(0xF2B705);

    static {
        // Keep disabled buttons readable on the dark theme.
        UIManager.put("Button.disabledText", new Color(0x8C86A6));
    }

    private final CardLayout screens = new CardLayout();
    private final JPanel root = new JPanel(screens);

    // team select
    private final List<Jojomon> roster = Roster.all();
    private final List<JToggleButton> pickButtons = new ArrayList<>();
    private final JLabel pickCount = new JLabel();
    private final JButton startButton = new JButton("Start battle");

    // battle
    private Battle battle;
    private final CombatantPanel enemyCard = new CombatantPanel("ENEMY");
    private final CombatantPanel playerCard = new CombatantPanel("YOU");
    private final JLabel scoreLabel = new JLabel();
    private final JTextArea logArea = new JTextArea();
    private final JButton[] moveButtons = new JButton[4];
    private final JButton[] switchButtons = new JButton[3];

    public JojomonGame() {
        super("Jojomon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        root.setBackground(BG);
        root.add(buildSelectScreen(), "select");
        root.add(buildBattleScreen(), "battle");
        setContentPane(root);
        setSize(820, 720);
        setMinimumSize(new Dimension(760, 680));
        setLocationRelativeTo(null);
    }

    // ---------------------------------------------------------------- team select

    private JPanel buildSelectScreen() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = styled(new JLabel("JOJOMON", SwingConstants.CENTER), 32, GOLD, true);
        JLabel sub = styled(new JLabel("Pick 3 fighters. Flair > Grit > Mystic > Flair.", SwingConstants.CENTER), 14, MUTED, false);
        JPanel head = new JPanel(new GridLayout(2, 1));
        head.setOpaque(false);
        head.add(title);
        head.add(sub);
        p.add(head, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 4, 10, 10));
        grid.setOpaque(false);
        for (Jojomon j : roster) {
            JToggleButton b = new JToggleButton(cardHtml(j));
            b.setFocusPainted(false);
            b.setBackground(PANEL);
            b.setForeground(TEXT);
            b.setBorder(BorderFactory.createLineBorder(j.type().color, 2));
            b.addActionListener(e -> onPick(b));
            pickButtons.add(b);
            grid.add(b);
        }
        p.add(grid, BorderLayout.CENTER);

        JPanel foot = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        foot.setOpaque(false);
        styled(pickCount, 14, TEXT, false);
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startBattle());
        foot.add(pickCount);
        foot.add(startButton);
        p.add(foot, BorderLayout.SOUTH);

        updatePickCount();
        return p;
    }

    private static String cardHtml(Jojomon j) {
        StringBuilder sb = new StringBuilder("<html><div style='text-align:center;width:150px'>");
        sb.append("<b style='font-size:13px'>").append(j.name()).append("</b><br>");
        sb.append("<span style='color:").append(hex(j.type().color)).append("'>").append(j.type()).append("</span><br><br>");
        sb.append("HP ").append(j.maxHp()).append(" &nbsp; ATK ").append(j.atk()).append("<br>");
        sb.append("DEF ").append(j.def()).append(" &nbsp; SPD ").append(j.spd()).append("<br><br>");
        for (Move m : j.moves()) sb.append("<span style='font-size:10px'>").append(m.name()).append("</span><br>");
        return sb.append("</div></html>").toString();
    }

    private void onPick(JToggleButton clicked) {
        if (selectedCount() > 3) clicked.setSelected(false);
        updatePickCount();
    }

    private int selectedCount() {
        int n = 0;
        for (JToggleButton b : pickButtons) if (b.isSelected()) n++;
        return n;
    }

    private void updatePickCount() {
        int n = selectedCount();
        pickCount.setText("Selected: " + n + " / 3");
        startButton.setEnabled(n == 3);
    }

    private void startBattle() {
        List<Jojomon> chosen = new ArrayList<>();
        for (int i = 0; i < roster.size(); i++) {
            if (pickButtons.get(i).isSelected()) chosen.add(roster.get(i));
        }
        battle = new Battle(chosen, new Random());
        logArea.setText("");
        append(List.of("A challenger appears: " + battle.enemy().name() + "!"));
        screens.show(root, "battle");
        refresh();
    }

    // ---------------------------------------------------------------- battle screen

    private JPanel buildBattleScreen() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        styled(scoreLabel, 14, GOLD, true);
        top.add(scoreLabel, BorderLayout.NORTH);
        top.add(enemyCard, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(PANEL);
        logArea.setForeground(TEXT);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(PANEL.brighter()));
        p.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(playerCard, BorderLayout.NORTH);

        JPanel moves = new JPanel(new GridLayout(2, 2, 8, 8));
        moves.setOpaque(false);
        for (int i = 0; i < moveButtons.length; i++) {
            final int idx = i;
            JButton b = new JButton();
            b.setFocusPainted(false);
            b.setPreferredSize(new Dimension(100, 52));
            b.addActionListener(e -> { append(battle.useMove(idx)); refresh(); });
            moveButtons[i] = b;
            moves.add(b);
        }
        bottom.add(moves, BorderLayout.CENTER);

        JPanel team = new JPanel(new GridLayout(1, 3, 8, 0));
        team.setOpaque(false);
        for (int i = 0; i < switchButtons.length; i++) {
            final int idx = i;
            JButton b = new JButton();
            b.setFocusPainted(false);
            b.setFont(b.getFont().deriveFont(Font.PLAIN));
            b.setBackground(new Color(0x2C2745));
            b.setForeground(TEXT);
            b.addActionListener(e -> { append(battle.switchTo(idx)); refresh(); });
            switchButtons[i] = b;
            team.add(b);
        }
        bottom.add(team, BorderLayout.SOUTH);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void append(List<String> lines) {
        for (String s : lines) logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /** Re-draw everything from the Battle state. */
    private void refresh() {
        Jojomon you = battle.player();
        enemyCard.show(battle.enemy());
        playerCard.show(you);
        scoreLabel.setText("Challengers defeated: " + battle.defeated());

        boolean canAct = !battle.isOver() && !battle.needsSwitch();
        for (int i = 0; i < moveButtons.length; i++) {
            JButton b = moveButtons[i];
            if (i < you.moves().size()) {
                Move m = you.moves().get(i);
                b.setFont(b.getFont().deriveFont(Font.PLAIN));
                b.setText("<html><center><font size='4'><b>" + m.name() + "</b></font><br><font size='2'>" + m.summary() + "</font></center></html>");
                b.setBackground(m.isHeal() ? new Color(0x2E6F57) : m.type().color.darker());
                b.setForeground(Color.WHITE);
                b.setEnabled(canAct);
                b.setVisible(true);
            } else {
                b.setVisible(false);
            }
        }

        for (int i = 0; i < switchButtons.length; i++) {
            JButton b = switchButtons[i];
            if (i < battle.team().size()) {
                Jojomon j = battle.team().get(i);
                boolean active = i == battle.activeIndex();
                b.setText("<html><center>" + (active ? "&#9733; " : "") + j.name() + (j.fainted() ? " (fainted)" : "")
                        + "<br><font size='2'>" + j.hp() + "/" + j.maxHp() + "</font></center></html>");
                b.setEnabled(!battle.isOver() && !active && !j.fainted());
                b.setVisible(true);
            } else {
                b.setVisible(false);
            }
        }

        if (battle.needsSwitch()) append(List.of(">> Pick who goes in next (free switch)."));
        if (battle.isOver()) onGameOver();
    }

    private void onGameOver() {
        int choice = JOptionPane.showOptionDialog(this,
                "Your team is out.\nChallengers defeated: " + battle.defeated(),
                "Game over", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new Object[]{"Play again", "Quit"}, "Play again");
        if (choice == 0) {
            for (JToggleButton b : pickButtons) b.setSelected(false);
            updatePickCount();
            screens.show(root, "select");
        } else {
            dispose();
            System.exit(0);
        }
    }

    // ---------------------------------------------------------------- helpers

    /** Name, type tag and HP bar for one side of the fight. */
    static class CombatantPanel extends JPanel {
        private final JLabel name = new JLabel();
        private final JLabel meta = new JLabel();
        private final HpBar bar = new HpBar();

        CombatantPanel(String tag) {
            super(new BorderLayout(10, 0));
            setBackground(PANEL);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            JPanel text = new JPanel(new GridLayout(2, 1));
            text.setOpaque(false);
            text.add(styled(name, 16, TEXT, true));
            text.add(styled(meta, 12, MUTED, false));
            add(styled(new JLabel(tag), 11, MUTED, true), BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
            add(bar, BorderLayout.EAST);
        }

        void show(Jojomon j) {
            name.setText(j.name());
            meta.setText("<html><span style='color:" + hex(j.type().color) + "'>" + j.type() + "</span>"
                    + " &nbsp; ATK " + j.atk() + " &nbsp; DEF " + j.def() + " &nbsp; SPD " + j.spd() + "</html>");
            bar.set(j.hp(), j.maxHp());
        }
    }

    /** Rounded health bar that goes green, yellow, red. */
    static class HpBar extends JComponent {
        private double frac = 1;
        private String label = "";

        HpBar() {
            setPreferredSize(new Dimension(260, 24));
        }

        void set(int hp, int max) {
            frac = max == 0 ? 0 : (double) hp / max;
            label = hp + " / " + max;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(0x2C2745));
            g.fillRoundRect(0, 0, w, h, h, h);
            if (frac > 0) {
                Color c = frac > 0.5 ? new Color(0x4CC38A) : frac > 0.2 ? new Color(0xF2B705) : new Color(0xE4572E);
                g.setColor(c);
                g.fillRoundRect(0, 0, Math.max(h, (int) (w * frac)), h, h, h);
            }
            g.setColor(Color.WHITE);
            g.setFont(getFont().deriveFont(Font.BOLD, 12f));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(label, (w - fm.stringWidth(label)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }

    private static JLabel styled(JLabel l, int size, Color color, boolean bold) {
        l.setForeground(color);
        l.setFont(l.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN, (float) size));
        return l;
    }

    private static String hex(Color c) {
        return String.format("#%06X", c.getRGB() & 0xFFFFFF);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Metal look-and-feel so button colours show up on every OS.
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new JojomonGame().setVisible(true);
        });
    }
}
