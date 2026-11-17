package gui;

import gui.components.ModernButton;
import gui.components.ModernProgressBar;
import gui.components.RoundedPanel;
import gui.components.StatCard;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * MODULE 8: Progress tracking.
 * Progress % = (Completed Skills / Total Skills) x 100, calculated by ProgressTracker.
 */
public class ProgressPanel extends BasePanel {

    private final StatCard totalCard = new StatCard("Total Skills", UIConstants.NEUTRAL);
    private final StatCard completedCard = new StatCard("Completed", UIConstants.SUCCESS);
    private final StatCard inProgressCard = new StatCard("In Progress", UIConstants.WARNING);
    private final StatCard notStartedCard = new StatCard("Not Started", UIConstants.NEUTRAL);
    private final StatCard percentCard = new StatCard("Completion", UIConstants.PRIMARY);

    private final ModernProgressBar overallBar = new ModernProgressBar(UIConstants.PRIMARY, 16);
    private final JLabel overallLabel = UIFactory.label("", UIConstants.FONT_SUBHEADING, UIConstants.TEXT);
    private final JLabel formulaLabel = UIFactory.label("", UIConstants.FONT_SMALL, UIConstants.TEXT_SECONDARY);
    private final JPanel levelRows = new JPanel();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[] {"Skill ID", "Skill Name", "Level", "Duration", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable skillTable = new JTable(tableModel);
    private LearningPath currentPath;

    public ProgressPanel(DashboardFrame dashboard) {
        super(dashboard, "My Progress", "Your skill statistics are calculated automatically from your roadmap.");
        buildLayout();
    }

    private void buildLayout() {
        JPanel stats = new JPanel(new GridLayout(1, 5, UIConstants.GAP, 0));
        stats.setOpaque(false);
        stats.add(totalCard);
        stats.add(completedCard);
        stats.add(inProgressCard);
        stats.add(notStartedCard);
        stats.add(percentCard);
        stats.setPreferredSize(new Dimension(800, 108));
        limitHeight(stats);
        addSection(stats);

        // ---- overall + level progress ----
        RoundedPanel progressCard = new RoundedPanel(null);
        progressCard.setLayout(new BoxLayout(progressCard, BoxLayout.Y_AXIS));
        progressCard.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        levelRows.setOpaque(false);
        levelRows.setLayout(new GridLayout(1, 3, 24, 0));
        levelRows.setPreferredSize(new Dimension(600, 34));
        UIFactory.stackVertically(progressCard, overallLabel, Box.createVerticalStrut(10), overallBar,
                Box.createVerticalStrut(6), formulaLabel, Box.createVerticalStrut(20), levelRows);
        progressCard.setPreferredSize(new Dimension(800, 170));
        limitHeight(progressCard);
        addSection(progressCard);

        // ---- skill table ----
        UIFactory.styleTable(skillTable);
        skillTable.getColumnModel().getColumn(4).setCellRenderer(UIFactory.statusRenderer());
        skillTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        skillTable.getColumnModel().getColumn(1).setPreferredWidth(280);
        RoundedPanel tableCard = UIFactory.tableCard(skillTable);
        tableCard.setPreferredSize(new Dimension(800, 380));
        tableCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(UIFactory.heading("All Skills"), BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttons.setOpaque(false);
        JButton updateButton = new ModernButton("Update Selected Skill");
        buttons.add(updateButton);
        tableHeader.add(buttons, BorderLayout.EAST);
        limitHeight(tableHeader);
        addSection(tableHeader);
        addSection(tableCard);

        updateButton.addActionListener(e -> updateSelectedSkill());
        // double-clicking a row also opens the update dialog
        skillTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    updateSelectedSkill();
                }
            }
        });
    }

    @Override
    public void refreshData() {
        Student student = student();
        if (student == null) {
            return;
        }
        try {
            currentPath = services().getRecommendationService().generateLearningPath(student);
        } catch (Exception e) {
            handleError(e);
            return;
        }
        ProgressTracker tracker = services().getProgressTracker();
        List<Skill> skills = currentPath == null ? java.util.Collections.emptyList() : currentPath.getAllSkills();

        int total = skills.size();
        int completed = tracker.countByStatus(skills, SkillStatus.COMPLETED);
        int inProgress = tracker.countByStatus(skills, SkillStatus.IN_PROGRESS);
        int notStarted = tracker.countByStatus(skills, SkillStatus.NOT_STARTED);
        int percent = tracker.calculatePercentage(completed, total);

        totalCard.setValue(String.valueOf(total));
        completedCard.setValue(String.valueOf(completed));
        inProgressCard.setValue(String.valueOf(inProgress));
        notStartedCard.setValue(String.valueOf(notStarted));
        percentCard.setValue(percent + "%");

        overallBar.setValue(percent);
        overallLabel.setText(currentPath == null
                ? "No career selected - choose a career to start tracking progress"
                : "Overall progress in " + currentPath.getCareer().getCareerName() + ": " + percent + "%");
        formulaLabel.setText("Formula: (Completed " + completed + " \u00F7 Total " + total + ") \u00D7 100 = " + percent + "%");

        levelRows.removeAll();
        for (Map.Entry<String, Integer> entry : tracker.getLevelProgress(currentPath).entrySet()) {
            JPanel cell = new JPanel();
            cell.setOpaque(false);
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            ModernProgressBar bar = new ModernProgressBar(UIConstants.PRIMARY, 8);
            bar.setValue(entry.getValue());
            UIFactory.stackVertically(cell,
                    UIFactory.label(entry.getKey() + "  \u2022  " + entry.getValue() + "%",
                            UIConstants.FONT_SMALL_BOLD, UIConstants.TEXT),
                    Box.createVerticalStrut(8), bar);
            levelRows.add(cell);
        }

        tableModel.setRowCount(0);
        for (Skill skill : skills) {
            tableModel.addRow(new Object[] {skill.getSkillId(), skill.getSkillName(), skill.getLevel(),
                    skill.getEstimatedDuration(), skill.getStatus().getLabel()});
        }
        reloadBody();
    }

    private void updateSelectedSkill() {
        int row = skillTable.getSelectedRow();
        if (currentPath == null || row < 0) {
            handleError(new util.ValidationException("Please select a skill in the table first."));
            return;
        }
        Skill skill = currentPath.findSkill((String) tableModel.getValueAt(row, 0));
        SkillStatus chosen = LearningRoadmapPanel.askForStatus(dashboard, skill);
        if (chosen == null) {
            return;
        }
        try {
            services().getProgressTracker().updateSkillStatus(student().getEmail(), skill, chosen);
            refreshData();
            skillTable.setRowSelectionInterval(row, row);
        } catch (Exception e) {
            handleError(e);
        }
    }
}
