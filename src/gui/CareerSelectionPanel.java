package gui;

import gui.components.ModernButton;
import gui.components.RoundedPanel;
import gui.components.UIFactory;
import model.Career;
import model.Skill;
import model.Student;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MODULE 5 + 6: Career selection and skill recommendations.
 *
 * Left: list of the 7 careers. Right: details of the highlighted career and the
 * skills the rule-based recommendation system suggests for it.
 */
public class CareerSelectionPanel extends BasePanel {

    private final Map<String, RoundedPanel> careerItems = new HashMap<>();
    private final Map<String, JLabel> currentTags = new HashMap<>();
    private String highlightedCareer;

    private final JLabel detailName = UIFactory.label("", UIConstants.FONT_TITLE, UIConstants.TEXT);
    private final JTextArea detailDescription = BaseFrame.createWrappedText("", UIConstants.FONT_BODY,
            UIConstants.TEXT_SECONDARY);
    private final JLabel durationValue = UIFactory.label("", UIConstants.FONT_SUBHEADING, UIConstants.TEXT);
    private final JLabel skillsValue = UIFactory.label("", UIConstants.FONT_SUBHEADING, UIConstants.TEXT);
    private final JLabel levelsValue = UIFactory.label("", UIConstants.FONT_SUBHEADING, UIConstants.TEXT);
    private final DefaultTableModel skillTableModel =
            new DefaultTableModel(new String[] {"#", "Recommended Skill", "Level", "Duration"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;   // table is read-only
                }
            };
    private final JButton selectButton = new ModernButton("Select This Career");

    public CareerSelectionPanel(DashboardFrame dashboard) {
        super(dashboard, "Choose Your Career",
                "Pick a career to automatically generate its Beginner \u2192 Advanced learning path.");
        buildLayout();
    }

    private void buildLayout() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.NORTH;
        gc.weighty = 1;

        // ---- left: career list ----
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        for (Career career : services().getCareerService().getAllCareers()) {
            list.add(createCareerItem(career));
            list.add(Box.createVerticalStrut(10));
        }
        list.add(Box.createVerticalGlue());
        JPanel listHolder = new JPanel(new BorderLayout());
        listHolder.setOpaque(false);
        listHolder.add(list, BorderLayout.NORTH);
        listHolder.setPreferredSize(new Dimension(300, 10));
        gc.gridx = 0;
        gc.weightx = 0;
        gc.insets = new Insets(0, 0, 0, UIConstants.GAP);
        wrapper.add(listHolder, gc);

        // ---- right: details ----
        gc.gridx = 1;
        gc.weightx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        wrapper.add(createDetailsCard(), gc);

        addSection(wrapper);
        selectButton.addActionListener(e -> selectHighlightedCareer());
    }

    private RoundedPanel createCareerItem(Career career) {
        RoundedPanel item = new RoundedPanel(new BorderLayout(0, 4));
        item.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        top.add(UIFactory.label(career.getCareerName(), UIConstants.FONT_SUBHEADING, UIConstants.TEXT), BorderLayout.WEST);
        JLabel currentTag = UIFactory.label("", UIConstants.FONT_SMALL_BOLD, UIConstants.SUCCESS);
        top.add(currentTag, BorderLayout.EAST);
        item.add(top, BorderLayout.NORTH);
        item.add(UIFactory.label(career.getSkillCount() + " skills  \u2022  " + career.getEstimatedDuration(),
                UIConstants.FONT_SMALL, UIConstants.TEXT_SECONDARY), BorderLayout.CENTER);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        item.setAlignmentX(LEFT_ALIGNMENT);

        // Event handling: clicking a career shows its details
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCareerDetails(career.getCareerName());
            }
        });
        careerItems.put(career.getCareerName(), item);
        currentTags.put(career.getCareerName(), currentTag);
        return item;
    }

    private RoundedPanel createDetailsCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel facts = new JPanel(new GridLayout(1, 3, 12, 0));
        facts.setOpaque(false);
        facts.add(fact("ESTIMATED DURATION", durationValue));
        facts.add(fact("REQUIRED SKILLS", skillsValue));
        facts.add(fact("LEARNING LEVELS", levelsValue));

        UIFactory.stackVertically(top, detailName, Box.createVerticalStrut(6), detailDescription,
                Box.createVerticalStrut(18), facts, Box.createVerticalStrut(18),
                UIFactory.label("Recommended Skills (rule-based)", UIConstants.FONT_SUBHEADING, UIConstants.TEXT));
        card.add(top, BorderLayout.NORTH);

        JTable table = new JTable(skillTableModel);
        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        RoundedPanel tableCard = UIFactory.tableCard(table);
        tableCard.setPreferredSize(new Dimension(400, 330));
        card.add(tableCard, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(selectButton);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JPanel fact(String title, JLabel value) {
        RoundedPanel box = new RoundedPanel(new BorderLayout(0, 4), UIConstants.BACKGROUND, null, 10);
        box.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        box.add(UIFactory.label(title, UIConstants.FONT_SMALL_BOLD, UIConstants.TEXT_SECONDARY), BorderLayout.NORTH);
        box.add(value, BorderLayout.CENTER);
        return box;
    }

    /**
     * Fills the right-hand card with the chosen career's information and recommended skills.
     */
    private void showCareerDetails(String careerName) {
        Career career = services().getCareerService().getCareer(careerName);
        if (career == null) {
            return;
        }
        highlightedCareer = careerName;
        detailName.setText(career.getCareerName());
        detailDescription.setText(career.getDescription());
        durationValue.setText(career.getEstimatedDuration());
        skillsValue.setText(String.valueOf(career.getSkillCount()));
        levelsValue.setText(career.getLearningLevels().size() + " (Beginner \u2192 Advanced)");

        skillTableModel.setRowCount(0);
        List<Skill> skills = services().getRecommendationService().getRecommendedSkills(careerName);
        int number = 1;
        for (Skill skill : skills) {
            skillTableModel.addRow(new Object[] {number++, skill.getSkillName(), skill.getLevel(),
                    skill.getEstimatedDuration()});
        }

        // highlight the selected item in the list
        for (Map.Entry<String, RoundedPanel> entry : careerItems.entrySet()) {
            entry.getValue().setFillColor(entry.getKey().equals(careerName) ? UIConstants.PRIMARY_LIGHT : UIConstants.CARD);
        }

        Student student = student();
        boolean isCurrent = student != null && careerName.equals(student.getSelectedCareer());
        selectButton.setEnabled(!isCurrent);
        selectButton.setText(isCurrent ? "This is your current career" : "Select This Career");
    }

    private void selectHighlightedCareer() {
        Student student = student();
        if (student == null || highlightedCareer == null) {
            return;
        }
        if (student.hasSelectedCareer()) {
            boolean ok = confirm("Change your career from " + student.getSelectedCareer() + " to "
                    + highlightedCareer + "?\n\nYour progress in " + student.getSelectedCareer()
                    + " is kept and will come back if you switch again later.");
            if (!ok) {
                return;
            }
        }
        try {
            services().getCareerService().selectCareer(student, highlightedCareer);
            showInfo(highlightedCareer + " selected!\nYour personalised learning path has been generated.");
            refreshData();
            dashboard.showPanel(DashboardFrame.ROADMAP);
        } catch (Exception e) {
            handleError(e);
        }
    }

    @Override
    public void refreshData() {
        Student student = student();
        if (student == null) {
            return;
        }
        for (Map.Entry<String, JLabel> entry : currentTags.entrySet()) {
            entry.getValue().setText(entry.getKey().equals(student.getSelectedCareer()) ? "CURRENT" : "");
        }
        String toShow = highlightedCareer;
        if (toShow == null) {
            toShow = student.hasSelectedCareer() ? student.getSelectedCareer()
                    : services().getCareerService().getAllCareers().get(0).getCareerName();
        }
        showCareerDetails(toShow);
        reloadBody();
    }
}
