package gui;

import gui.components.ModernButton;
import gui.components.ModernProgressBar;
import gui.components.RoundedPanel;
import gui.components.StatusBadge;
import gui.components.UIFactory;
import model.LearningPath;
import model.Skill;
import model.SkillStatus;
import model.Student;
import service.ProgressTracker;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.List;

/**
 * MODULE 7: Learning roadmap - shows every skill grouped into Beginner,
 * Intermediate and Advanced, and lets the student update each skill's status.
 */
public class LearningRoadmapPanel extends BasePanel {

    public LearningRoadmapPanel(DashboardFrame dashboard) {
        super(dashboard, "Learning Roadmap", "Follow the levels in order and update each skill as you learn.");
    }

    @Override
    public void refreshData() {
        body.removeAll();
        Student student = student();
        if (student == null) {
            return;
        }
        try {
            LearningPath path = services().getRecommendationService().generateLearningPath(student);
            if (path == null) {
                showNoCareerMessage();
            } else {
                setTitle(path.getCareer().getCareerName() + " Roadmap");
                addSection(createSummaryCard(path));
                String[] stepTitles = {"STEP 1", "STEP 2", "STEP 3"};
                List<String> levels = path.getLevels();
                for (int i = 0; i < levels.size(); i++) {
                    String step = i < stepTitles.length ? stepTitles[i] : "STEP " + (i + 1);
                    addSection(createLevelCard(step, levels.get(i), path.getSkillsForLevel(levels.get(i))));
                }
            }
        } catch (Exception e) {
            handleError(e);
        }
        reloadBody();
    }

    private void showNoCareerMessage() {
        setTitle("Learning Roadmap");
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        card.add(UIFactory.heading("No career selected yet"), BorderLayout.NORTH);
        card.add(UIFactory.secondary("Choose a career first and your learning roadmap will be generated automatically."),
                BorderLayout.CENTER);
        JPanel buttonRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        buttonRow.setOpaque(false);
        JButton choose = new ModernButton("Choose a Career");
        choose.addActionListener(e -> dashboard.showPanel(DashboardFrame.CAREER));
        buttonRow.add(choose);
        card.add(buttonRow, BorderLayout.SOUTH);
        limitHeight(card);
        addSection(card);
    }

    private RoundedPanel createSummaryCard(LearningPath path) {
        ProgressTracker tracker = services().getProgressTracker();
        List<Skill> skills = path.getAllSkills();
        int percent = tracker.calculatePercentage(skills);

        RoundedPanel card = new RoundedPanel(new BorderLayout(20, 10));
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        ModernProgressBar bar = new ModernProgressBar(UIConstants.PRIMARY, 12);
        bar.setValue(percent);
        UIFactory.stackVertically(left,
                UIFactory.label("Overall completion: " + percent + "%", UIConstants.FONT_SUBHEADING, UIConstants.TEXT),
                Box.createVerticalStrut(10), bar);
        card.add(left, BorderLayout.CENTER);

        JPanel legend = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        legend.setOpaque(false);
        for (SkillStatus status : SkillStatus.values()) {
            StatusBadge badge = new StatusBadge(status);
            badge.setText(status.getLabel() + ": " + tracker.countByStatus(skills, status));
            legend.add(badge);
        }
        card.add(legend, BorderLayout.EAST);
        limitHeight(card);
        return card;
    }

    private RoundedPanel createLevelCard(String step, String level, List<Skill> skills) {
        int percent = services().getProgressTracker().calculatePercentage(skills);
        RoundedPanel card = new RoundedPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 12, 22));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(UIFactory.label(step, UIConstants.FONT_SMALL_BOLD, UIConstants.PRIMARY));
        titles.add(UIFactory.label(level + " Level", UIConstants.FONT_HEADING, UIConstants.TEXT));
        header.add(titles, BorderLayout.WEST);
        header.add(UIFactory.label(percent + "% complete  \u2022  " + skills.size() + " skills",
                UIConstants.FONT_SMALL_BOLD, UIConstants.TEXT_SECONDARY), BorderLayout.EAST);
        UIFactory.stackVertically(card, header, Box.createVerticalStrut(12));

        int number = 1;
        for (Skill skill : skills) {
            UIFactory.stackVertically(card, createSkillRow(number++, skill));
        }
        limitHeight(card);
        return card;
    }

    /**
     * One row: [number] name + description | duration | status badge | Update Status button
     */
    private JPanel createSkillRow(int number, Skill skill) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.NEUTRAL_LIGHT),
                BorderFactory.createEmptyBorder(10, 0, 10, 0)));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, 0, 0, 14);

        gc.gridx = 0;
        row.add(new NumberCircle(number, skill.getStatus()), gc);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(UIFactory.label(skill.getSkillName(), UIConstants.FONT_SUBHEADING, UIConstants.TEXT));
        text.add(Box.createVerticalStrut(2));
        text.add(UIFactory.label(skill.getDescription(), UIConstants.FONT_SMALL, UIConstants.TEXT_SECONDARY));
        gc.gridx = 1;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        row.add(text, gc);

        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.gridx = 2;
        JLabel duration = UIFactory.label(skill.getEstimatedDuration(), UIConstants.FONT_SMALL, UIConstants.TEXT_SECONDARY);
        duration.setPreferredSize(new Dimension(70, 20));
        row.add(duration, gc);

        gc.gridx = 3;
        StatusBadge badge = new StatusBadge(skill.getStatus());
        badge.setPreferredSize(new Dimension(108, 26));
        row.add(badge, gc);

        gc.gridx = 4;
        gc.insets = new Insets(0, 0, 0, 0);
        JButton updateButton = new ModernButton("Update Status", ModernButton.Style.SECONDARY);
        updateButton.setFont(UIConstants.FONT_SMALL_BOLD);
        updateButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        updateButton.addActionListener(e -> updateStatus(skill));
        row.add(updateButton, gc);
        return row;
    }

    /**
     * Asks the student for the new status and saves it.
     */
    private void updateStatus(Skill skill) {
        SkillStatus chosen = askForStatus(dashboard, skill);
        if (chosen == null) {
            return;   // user pressed Cancel
        }
        try {
            services().getProgressTracker().updateSkillStatus(student().getEmail(), skill, chosen);
            refreshData();
        } catch (Exception e) {
            handleError(e);
        }
    }

    /**
     * Shared dialog used by the roadmap and the progress screens.
     * Returns the chosen status, or null if the user cancelled.
     */
    static SkillStatus askForStatus(java.awt.Component parent, Skill skill) {
        Object choice = JOptionPane.showInputDialog(parent,
                "Select the new status for \"" + skill.getSkillName() + "\":",
                "Update Skill Status", JOptionPane.PLAIN_MESSAGE, null,
                SkillStatus.values(), skill.getStatus());
        return choice == null ? null : SkillStatus.fromText(choice.toString());
    }

    /**
     * Small round number on the left of each skill. It becomes a green tick when completed.
     */
    private static class NumberCircle extends JComponent {
        private final int number;
        private final SkillStatus status;

        NumberCircle(int number, SkillStatus status) {
            this.number = number;
            this.status = status;
            setPreferredSize(new Dimension(32, 32));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Color fill = StatusBadge.getBackgroundFor(status);
            Color text = StatusBadge.getForegroundFor(status);
            g2.setColor(fill);
            g2.fillOval(0, 0, 31, 31);
            g2.setColor(text);
            if (status == SkillStatus.COMPLETED) {
                // draw a tick mark with two lines
                g2.setStroke(new java.awt.BasicStroke(2.2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g2.drawLine(10, 16, 14, 20);
                g2.drawLine(14, 20, 22, 11);
                g2.dispose();
                return;
            }
            g2.setFont(UIConstants.FONT_SMALL_BOLD);
            String label = String.valueOf(number);
            int x = (32 - g2.getFontMetrics().stringWidth(label)) / 2;
            int y = (32 + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
            g2.drawString(label, x, y);
            g2.dispose();
        }
    }
}
