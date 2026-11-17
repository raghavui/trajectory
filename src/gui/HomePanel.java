package gui;

import gui.components.ModernButton;
import gui.components.ModernProgressBar;
import gui.components.RoundedPanel;
import gui.components.StatCard;
import gui.components.StatusBadge;
import gui.components.UIFactory;
import model.Activity;
import model.LearningPath;
import model.Skill;
import model.SkillStatus;
import model.Student;
import service.ProgressTracker;
import service.RecommendationService;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * MODULE 4: Personalised dashboard home screen.
 * Every number on this screen is calculated from the saved data each time the screen opens.
 */
public class HomePanel extends BasePanel {

    private final StatCard careerCard = new StatCard("Selected Career", UIConstants.PRIMARY);
    private final StatCard totalCard = new StatCard("Total Skills", UIConstants.NEUTRAL);
    private final StatCard completedCard = new StatCard("Completed Skills", UIConstants.SUCCESS);
    private final StatCard pendingCard = new StatCard("Pending Skills", UIConstants.WARNING);
    private final StatCard progressCard = new StatCard("Overall Progress", UIConstants.PRIMARY);
    private final StatCard goalsCard = new StatCard("Active Learning Goals", UIConstants.DANGER);

    private final JPanel roadmapContent = new JPanel();
    private final JPanel activityContent = new JPanel();

    public HomePanel(DashboardFrame dashboard) {
        super(dashboard, "Dashboard", "");
        buildLayout();
    }

    private void buildLayout() {
        // ---- statistic cards: 2 rows x 3 columns ----
        JPanel stats = new JPanel(new GridLayout(2, 3, UIConstants.GAP, UIConstants.GAP));
        stats.setOpaque(false);
        for (StatCard card : new StatCard[] {careerCard, totalCard, completedCard, pendingCard, progressCard, goalsCard}) {
            stats.add(card);
        }
        stats.setPreferredSize(new Dimension(800, 244));
        limitHeight(stats);
        addSection(stats);

        // ---- roadmap card (left) + activities card (right) ----
        roadmapContent.setOpaque(false);
        roadmapContent.setLayout(new BoxLayout(roadmapContent, BoxLayout.Y_AXIS));
        activityContent.setOpaque(false);
        activityContent.setLayout(new BoxLayout(activityContent, BoxLayout.Y_AXIS));

        JPanel middle = new JPanel(new GridBagLayout());
        middle.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        gc.gridx = 0;
        gc.weightx = 0.6;
        gc.insets = new Insets(0, 0, 0, UIConstants.GAP);
        middle.add(wrapInCard("Current Learning Roadmap", roadmapContent), gc);
        gc.gridx = 1;
        gc.weightx = 0.4;
        gc.insets = new Insets(0, 0, 0, 0);
        middle.add(wrapInCard("Recent Learning Activities", activityContent), gc);
        addSection(middle);

        // ---- quick actions ----
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        JButton roadmapBtn = new ModernButton("Update My Skills");
        JButton goalBtn = new ModernButton("Add a Goal", ModernButton.Style.SECONDARY);
        JButton careerBtn = new ModernButton("Choose / Change Career", ModernButton.Style.SECONDARY);
        JButton progressBtn = new ModernButton("View Progress Report", ModernButton.Style.SECONDARY);
        roadmapBtn.addActionListener(e -> dashboard.showPanel(DashboardFrame.ROADMAP));
        goalBtn.addActionListener(e -> dashboard.showPanel(DashboardFrame.GOALS));
        careerBtn.addActionListener(e -> dashboard.showPanel(DashboardFrame.CAREER));
        progressBtn.addActionListener(e -> dashboard.showPanel(DashboardFrame.PROGRESS));
        actions.add(roadmapBtn);
        actions.add(goalBtn);
        actions.add(careerBtn);
        actions.add(progressBtn);
        RoundedPanel actionsCard = wrapInCard("Quick Actions", actions);
        limitHeight(actionsCard);
        addSection(actionsCard);
    }

    private RoundedPanel wrapInCard(String title, JComponent content) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        card.add(UIFactory.label(title, UIConstants.FONT_SUBHEADING, UIConstants.TEXT), BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refreshData() {
        Student student = student();
        if (student == null) {
            return;
        }
        setTitle(getGreeting() + ", " + student.getFirstName() + "!");
        setSubtitle(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
                + "  \u2022  Here is an overview of your learning journey.");

        try {
            RecommendationService recommender = services().getRecommendationService();
            ProgressTracker tracker = services().getProgressTracker();
            LearningPath path = recommender.generateLearningPath(student);
            List<Skill> skills = path == null ? java.util.Collections.emptyList() : path.getAllSkills();

            int total = skills.size();
            int completed = tracker.countByStatus(skills, SkillStatus.COMPLETED);
            int inProgress = tracker.countByStatus(skills, SkillStatus.IN_PROGRESS);
            int percent = tracker.calculatePercentage(completed, total);
            int activeGoals = services().getGoalManager().countActiveGoals(student.getEmail());

            careerCard.setValue(student.hasSelectedCareer() ? student.getSelectedCareer() : "Not selected", true);
            careerCard.setSubtitle(path == null ? "Choose a career to begin" : "Duration: " + path.getCareer().getEstimatedDuration());
            totalCard.setValue(String.valueOf(total));
            totalCard.setSubtitle("in your learning path");
            completedCard.setValue(String.valueOf(completed));
            completedCard.setSubtitle("skills mastered");
            pendingCard.setValue(String.valueOf(total - completed));
            pendingCard.setSubtitle(inProgress + " in progress");
            progressCard.setValue(percent + "%");
            progressCard.setSubtitle(completed + " of " + total + " completed");
            goalsCard.setValue(String.valueOf(activeGoals));
            goalsCard.setSubtitle("pending goals");

            buildRoadmapSummary(path, percent, recommender, tracker);
            buildActivities(services().getActivityRepository().findRecent(student.getEmail(), 7));
        } catch (Exception e) {
            handleError(e);
        }
        reloadBody();
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) {
            return "Good morning";
        } else if (hour < 17) {
            return "Good afternoon";
        } else {
            return "Good evening";
        }
    }

    private void buildRoadmapSummary(LearningPath path, int percent, RecommendationService recommender,
                                     ProgressTracker tracker) {
        roadmapContent.removeAll();
        if (path == null) {
            JLabel message = UIFactory.secondary("You have not selected a career yet.");
            JButton choose = new ModernButton("Choose a Career");
            choose.addActionListener(e -> dashboard.showPanel(DashboardFrame.CAREER));
            UIFactory.stackVertically(roadmapContent, message, Box.createVerticalStrut(14), choose);
            choose.setMaximumSize(choose.getPreferredSize());
            return;
        }

        JLabel careerLabel = UIFactory.label(path.getCareer().getCareerName(), UIConstants.FONT_HEADING, UIConstants.TEXT);
        JLabel levelLabel = UIFactory.secondary("Current level: " + recommender.getCurrentLevel(path)
                + "   \u2022   Overall progress: " + percent + "%");
        ModernProgressBar overall = new ModernProgressBar(UIConstants.PRIMARY, 12);
        overall.setValue(percent);
        UIFactory.stackVertically(roadmapContent, careerLabel, Box.createVerticalStrut(4), levelLabel,
                Box.createVerticalStrut(10), overall, Box.createVerticalStrut(16));

        // one small progress bar per level
        for (Map.Entry<String, Integer> entry : tracker.getLevelProgress(path).entrySet()) {
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            JLabel name = UIFactory.label(entry.getKey(), UIConstants.FONT_SMALL_BOLD, UIConstants.TEXT);
            name.setPreferredSize(new Dimension(100, 20));
            ModernProgressBar bar = new ModernProgressBar(UIConstants.PRIMARY, 8);
            bar.setValue(entry.getValue());
            JLabel value = UIFactory.label(entry.getValue() + "%", UIConstants.FONT_SMALL, UIConstants.TEXT_SECONDARY);
            value.setPreferredSize(new Dimension(40, 20));
            row.add(name, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            row.add(value, BorderLayout.EAST);
            UIFactory.stackVertically(roadmapContent, row, Box.createVerticalStrut(8));
        }

        roadmapContent.add(Box.createVerticalStrut(10));
        JLabel nextLabel = UIFactory.label("RECOMMENDED NEXT", UIConstants.FONT_SMALL_BOLD, UIConstants.TEXT_SECONDARY);
        UIFactory.stackVertically(roadmapContent, nextLabel, Box.createVerticalStrut(6));
        List<Skill> next = recommender.getNextRecommendedSkills(path, 3);
        if (next.isEmpty()) {
            UIFactory.stackVertically(roadmapContent,
                    UIFactory.label("All skills completed. Congratulations!", UIConstants.FONT_BODY, UIConstants.SUCCESS));
        }
        for (Skill skill : next) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            row.add(UIFactory.label(skill.getSkillName() + "  \u2022  " + skill.getLevel(),
                    UIConstants.FONT_BODY, UIConstants.TEXT), BorderLayout.CENTER);
            JPanel badgeHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            badgeHolder.setOpaque(false);
            badgeHolder.add(new StatusBadge(skill.getStatus()));
            row.add(badgeHolder, BorderLayout.EAST);
            UIFactory.stackVertically(roadmapContent, row);
        }
    }

    private void buildActivities(List<Activity> activities) {
        activityContent.removeAll();
        if (activities.isEmpty()) {
            UIFactory.stackVertically(activityContent, UIFactory.secondary("No activity yet."));
            return;
        }
        for (Activity activity : activities) {
            JPanel item = new JPanel();
            item.setOpaque(false);
            item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
            item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 2, 0, 0, UIConstants.PRIMARY_LIGHT),
                    BorderFactory.createEmptyBorder(2, 12, 2, 0)));
            JLabel message = UIFactory.label(shorten(activity.getMessage(), 46), UIConstants.FONT_BODY, UIConstants.TEXT);
            message.setToolTipText(activity.getMessage());
            JLabel time = UIFactory.label(activity.getFormattedTime(), UIConstants.FONT_SMALL, UIConstants.TEXT_SECONDARY);
            message.setAlignmentX(Component.LEFT_ALIGNMENT);
            time.setAlignmentX(Component.LEFT_ALIGNMENT);
            item.add(message);
            item.add(time);
            UIFactory.stackVertically(activityContent, item, Box.createVerticalStrut(10));
        }
    }

    /**
     * String handling: cuts long text and adds "..." so it fits in the card.
     */
    private static String shorten(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
