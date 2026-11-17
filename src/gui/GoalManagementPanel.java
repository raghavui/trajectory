package gui;

import gui.components.ModernButton;
import gui.components.RoundedPanel;
import gui.components.UIFactory;
import model.Goal;
import model.Student;
import util.UIConstants;
import util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MODULE 9: Learning goal management - add, view, update, complete and delete goals.
 */
public class GoalManagementPanel extends BasePanel {

    private static final String FILTER_ALL = "All Goals";
    private static final String FILTER_PENDING = "Pending Goals";
    private static final String FILTER_COMPLETED = "Completed Goals";
    private static final DateTimeFormatter TABLE_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final JTextField titleField = UIFactory.textField();
    private final JTextField dateField = UIFactory.textField();
    private final JTextArea descriptionArea = UIFactory.textArea(3);
    private final JLabel formTitle = UIFactory.heading("Add a New Goal");
    private final JButton saveButton = new ModernButton("Add Goal");
    private final JLabel summaryLabel = UIFactory.secondary("");
    private final JComboBox<String> filterCombo =
            UIFactory.comboBox(new String[] {FILTER_ALL, FILTER_PENDING, FILTER_COMPLETED});

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[] {"Goal ID", "Title", "Description", "Target Date", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable goalTable = new JTable(tableModel);

    private String editingGoalId;   // null = adding a new goal, otherwise = updating this goal

    public GoalManagementPanel(DashboardFrame dashboard) {
        super(dashboard, "My Learning Goals", "Set personal goals with target dates and track them to completion.");
        buildLayout();
    }

    private void buildLayout() {
        addSection(createFormCard());

        // ---- table toolbar ----
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        filterCombo.setPreferredSize(new Dimension(170, 38));
        left.add(filterCombo);
        left.add(summaryLabel);
        toolbar.add(left, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton editButton = new ModernButton("Edit Selected", ModernButton.Style.SECONDARY);
        JButton completeButton = new ModernButton("Mark as Completed", ModernButton.Style.SUCCESS);
        JButton deleteButton = new ModernButton("Delete", ModernButton.Style.DANGER);
        actions.add(editButton);
        actions.add(completeButton);
        actions.add(deleteButton);
        toolbar.add(actions, BorderLayout.EAST);
        limitHeight(toolbar);
        addSection(toolbar);

        // ---- table ----
        UIFactory.styleTable(goalTable);
        goalTable.getColumnModel().getColumn(4).setCellRenderer(UIFactory.statusRenderer());
        goalTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        goalTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        goalTable.getColumnModel().getColumn(2).setPreferredWidth(320);
        goalTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        goalTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        RoundedPanel tableCard = UIFactory.tableCard(goalTable);
        tableCard.setPreferredSize(new Dimension(800, 300));
        tableCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        addSection(tableCard);

        // ---- event handling ----
        saveButton.addActionListener(e -> saveGoal());
        filterCombo.addActionListener(e -> refreshData());
        editButton.addActionListener(e -> loadSelectedGoalIntoForm());
        completeButton.addActionListener(e -> completeSelectedGoal());
        deleteButton.addActionListener(e -> deleteSelectedGoal());
    }

    private RoundedPanel createFormCard() {
        RoundedPanel card = new RoundedPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(0, 0, 12, 14);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        card.add(formTitle, gc);

        gc.gridwidth = 1;
        gc.gridy = 1;
        gc.weightx = 0.65;
        card.add(labelled("Goal Title *", titleField), gc);
        gc.gridx = 1;
        gc.weightx = 0.35;
        gc.insets = new Insets(0, 0, 12, 0);
        card.add(labelled("Target Date * (YYYY-MM-DD)", dateField), gc);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 2;
        JScrollPane descriptionScroll = UIFactory.scrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(400, 74));
        card.add(labelled("Description (optional)", descriptionScroll), gc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.setOpaque(false);
        JButton clearButton = new ModernButton("Clear", ModernButton.Style.SECONDARY);
        clearButton.addActionListener(e -> clearForm());
        buttons.add(saveButton);
        buttons.add(javax.swing.Box.createHorizontalStrut(10));
        buttons.add(clearButton);
        gc.gridy = 3;
        gc.insets = new Insets(4, 0, 0, 0);
        card.add(buttons, gc);

        clearForm();
        limitHeight(card);
        return card;
    }

    private JPanel labelled(String text, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(UIFactory.fieldLabel(text), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void refreshData() {
        Student student = student();
        if (student == null) {
            return;
        }
        try {
            List<Goal> allGoals = services().getGoalManager().getGoals(student.getEmail());
            String filter = (String) filterCombo.getSelectedItem();
            int pending = 0;
            int completed = 0;
            int overdue = 0;
            tableModel.setRowCount(0);
            for (Goal goal : allGoals) {
                if (goal.isCompleted()) {
                    completed++;
                } else {
                    pending++;
                }
                if (goal.isOverdue()) {
                    overdue++;
                }
                boolean show = FILTER_ALL.equals(filter)
                        || (FILTER_PENDING.equals(filter) && !goal.isCompleted())
                        || (FILTER_COMPLETED.equals(filter) && goal.isCompleted());
                if (show) {
                    tableModel.addRow(new Object[] {goal.getGoalId(), goal.getTitle(),
                            goal.getDescription().replace('\n', ' '),
                            goal.getTargetDate().format(TABLE_DATE), goal.getDisplayStatus()});
                }
            }
            summaryLabel.setText(allGoals.size() + " total  \u2022  " + pending + " pending  \u2022  "
                    + completed + " completed" + (overdue > 0 ? "  \u2022  " + overdue + " overdue" : ""));
        } catch (Exception e) {
            handleError(e);
        }
        reloadBody();
    }

    private void saveGoal() {
        String email = student().getEmail();
        try {
            if (editingGoalId == null) {
                services().getGoalManager().addGoal(email, titleField.getText(), descriptionArea.getText(),
                        dateField.getText());
                showInfo("Goal added successfully.");
            } else {
                services().getGoalManager().updateGoal(email, editingGoalId, titleField.getText(),
                        descriptionArea.getText(), dateField.getText());
                showInfo("Goal updated successfully.");
            }
            clearForm();
            refreshData();
        } catch (Exception e) {
            handleError(e);
        }
    }

    /**
     * Returns the goal ID of the selected table row, or shows a message if nothing is selected.
     */
    private String getSelectedGoalId() {
        int row = goalTable.getSelectedRow();
        if (row < 0) {
            handleError(new ValidationException("Please select a goal in the table first."));
            return null;
        }
        return (String) tableModel.getValueAt(row, 0);
    }

    private void loadSelectedGoalIntoForm() {
        String goalId = getSelectedGoalId();
        if (goalId == null) {
            return;
        }
        try {
            for (Goal goal : services().getGoalManager().getGoals(student().getEmail())) {
                if (goal.getGoalId().equals(goalId)) {
                    editingGoalId = goalId;
                    titleField.setText(goal.getTitle());
                    dateField.setText(goal.getTargetDate().toString());
                    descriptionArea.setText(goal.getDescription());
                    formTitle.setText("Update Goal " + goalId);
                    saveButton.setText("Update Goal");
                    titleField.requestFocusInWindow();
                    return;
                }
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void completeSelectedGoal() {
        String goalId = getSelectedGoalId();
        if (goalId == null) {
            return;
        }
        try {
            services().getGoalManager().markCompleted(student().getEmail(), goalId);
            showInfo("Well done! Goal marked as completed.");
            refreshData();
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void deleteSelectedGoal() {
        String goalId = getSelectedGoalId();
        if (goalId == null || !confirm("Delete goal " + goalId + "? This cannot be undone.")) {
            return;
        }
        try {
            services().getGoalManager().deleteGoal(student().getEmail(), goalId);
            if (goalId.equals(editingGoalId)) {
                clearForm();
            }
            refreshData();
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void clearForm() {
        editingGoalId = null;
        titleField.setText("");
        descriptionArea.setText("");
        dateField.setText(LocalDate.now().plusWeeks(2).toString());   // sensible default date
        formTitle.setText("Add a New Goal");
        saveButton.setText("Add Goal");
    }
}
