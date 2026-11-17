package gui;

import gui.components.ModernButton;
import gui.components.RoundedPanel;
import gui.components.UIFactory;
import model.Student;
import util.UIConstants;
import util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

/**
 * MODULE 10: Profile management - view and update the student's details.
 * Changing the email requires the current password (proper validation).
 */
public class ProfilePanel extends BasePanel {

    // ---- summary card ----
    private final Avatar avatar = new Avatar();
    private final JLabel nameValue = UIFactory.label("", UIConstants.FONT_HEADING, UIConstants.TEXT);
    private final JLabel emailValue = UIFactory.secondary("");
    private final JLabel collegeValue = UIFactory.label(" ", UIConstants.FONT_BODY, UIConstants.TEXT);
    private final JLabel yearValue = UIFactory.label(" ", UIConstants.FONT_BODY, UIConstants.TEXT);
    private final JLabel careerValue = UIFactory.label(" ", UIConstants.FONT_BODY, UIConstants.TEXT);

    // ---- edit profile form ----
    private final JTextField nameField = UIFactory.textField();
    private final JTextField collegeField = UIFactory.textField();
    private final JComboBox<String> yearCombo = UIFactory.comboBox(ValidationUtil.ACADEMIC_YEARS);

    // ---- change email form ----
    private final JTextField newEmailField = UIFactory.textField();
    private final JPasswordField emailPasswordField = UIFactory.passwordField();

    // ---- change password form ----
    private final JPasswordField currentPasswordField = UIFactory.passwordField();
    private final JPasswordField newPasswordField = UIFactory.passwordField();
    private final JPasswordField confirmPasswordField = UIFactory.passwordField();

    public ProfilePanel(DashboardFrame dashboard) {
        super(dashboard, "My Profile", "View and update your personal and account details.");
        buildLayout();
    }

    private void buildLayout() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTH;

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        gc.insets = new Insets(0, 0, 0, UIConstants.GAP);
        wrapper.add(createSummaryCard(), gc);

        JPanel forms = new JPanel();
        forms.setOpaque(false);
        forms.setLayout(new BoxLayout(forms, BoxLayout.Y_AXIS));
        UIFactory.stackVertically(forms, createProfileForm(), Box.createVerticalStrut(UIConstants.GAP),
                createEmailForm(), Box.createVerticalStrut(UIConstants.GAP), createPasswordForm());
        // keep the preferred width small so GridBagLayout never falls back to minimum sizes
        forms.setPreferredSize(new Dimension(500, forms.getPreferredSize().height));
        gc.gridx = 1;
        gc.weightx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        wrapper.add(forms, gc);

        addSection(wrapper);
    }

    private RoundedPanel createSummaryCard() {
        RoundedPanel card = new RoundedPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(26, 24, 26, 24));
        card.setPreferredSize(new Dimension(300, 420));
        card.setMinimumSize(new Dimension(300, 420));
        UIFactory.stackVertically(card, avatar, Box.createVerticalStrut(14), nameValue, Box.createVerticalStrut(2),
                emailValue, Box.createVerticalStrut(22),
                infoRow("COLLEGE", collegeValue), Box.createVerticalStrut(14),
                infoRow("ACADEMIC YEAR", yearValue), Box.createVerticalStrut(14),
                infoRow("SELECTED CAREER", careerValue));
        return card;
    }

    private JPanel infoRow(String title, JLabel value) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setOpaque(false);
        row.add(UIFactory.label(title, UIConstants.FONT_SMALL_BOLD, UIConstants.TEXT_SECONDARY), BorderLayout.NORTH);
        row.add(value, BorderLayout.CENTER);
        return row;
    }

    private RoundedPanel formCard(String title, String subtitle, JComponent fields, JButton button) {
        RoundedPanel card = new RoundedPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(button);
        UIFactory.stackVertically(card, UIFactory.heading(title), Box.createVerticalStrut(2),
                UIFactory.label(subtitle, UIConstants.FONT_SMALL, UIConstants.TEXT_SECONDARY),
                Box.createVerticalStrut(14), fields, Box.createVerticalStrut(14), buttonRow);
        return card;
    }

    private JPanel labelled(String text, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(UIFactory.fieldLabel(text), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel row(JComponent... items) {
        JPanel row = new JPanel(new GridLayout(1, items.length, 14, 0));
        row.setOpaque(false);
        for (JComponent item : items) {
            row.add(item);
        }
        return row;
    }

    private RoundedPanel createProfileForm() {
        JButton saveButton = new ModernButton("Save Profile");
        saveButton.addActionListener(e -> saveProfile());
        return formCard("Edit Profile", "Update your name, college and current academic year.",
                row(labelled("Full Name", nameField), labelled("College Name", collegeField),
                        labelled("Current Year", yearCombo)), saveButton);
    }

    private RoundedPanel createEmailForm() {
        JButton changeButton = new ModernButton("Change Email", ModernButton.Style.SECONDARY);
        changeButton.addActionListener(e -> changeEmail());
        return formCard("Change Registered Email",
                "For security, confirm your current password. Your progress and goals move to the new email.",
                row(labelled("New Email Address", newEmailField), labelled("Current Password", emailPasswordField)),
                changeButton);
    }

    private RoundedPanel createPasswordForm() {
        JButton changeButton = new ModernButton("Change Password", ModernButton.Style.SECONDARY);
        changeButton.addActionListener(e -> changePassword());
        return formCard("Change Password", "At least 6 characters, including a letter and a number.",
                row(labelled("Current Password", currentPasswordField), labelled("New Password", newPasswordField),
                        labelled("Confirm New Password", confirmPasswordField)), changeButton);
    }

    @Override
    public void refreshData() {
        Student student = student();
        if (student == null) {
            return;
        }
        avatar.setFullName(student.getFullName());
        nameValue.setText(student.getFullName());
        emailValue.setText(student.getEmail());
        collegeValue.setText(student.getCollegeName());
        yearValue.setText(student.getCurrentYear());
        careerValue.setText(student.hasSelectedCareer() ? student.getSelectedCareer() : "Not selected yet");

        nameField.setText(student.getFullName());
        collegeField.setText(student.getCollegeName());
        yearCombo.setSelectedItem(student.getCurrentYear());
        reloadBody();
    }

    private void saveProfile() {
        try {
            services().getAuthenticationService().updateProfile(nameField.getText(), collegeField.getText(),
                    (String) yearCombo.getSelectedItem());
            showInfo("Profile updated successfully.");
            refreshData();
            dashboard.refreshUserInfo();
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void changeEmail() {
        try {
            services().getAuthenticationService().changeEmail(newEmailField.getText(),
                    new String(emailPasswordField.getPassword()));
            newEmailField.setText("");
            showInfo("Email changed successfully. Use your new email the next time you log in.");
            refreshData();
            dashboard.refreshUserInfo();
        } catch (Exception e) {
            handleError(e);
        } finally {
            emailPasswordField.setText("");   // never leave a password sitting in a field
        }
    }

    private void changePassword() {
        try {
            services().getAuthenticationService().changePassword(new String(currentPasswordField.getPassword()),
                    new String(newPasswordField.getPassword()), new String(confirmPasswordField.getPassword()));
            showInfo("Password changed successfully.");
        } catch (Exception e) {
            handleError(e);
        } finally {
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        }
    }

    /**
     * Round avatar showing the student's initials, e.g. "RS" for Riya Sharma.
     */
    private static class Avatar extends JComponent {
        private String initials = "";

        Avatar() {
            setPreferredSize(new Dimension(72, 72));
            setMaximumSize(new Dimension(72, 72));
            setMinimumSize(new Dimension(72, 72));
        }

        void setFullName(String fullName) {
            StringBuilder builder = new StringBuilder();
            for (String part : fullName.trim().split("\\s+")) {
                if (!part.isEmpty() && builder.length() < 2) {
                    builder.append(Character.toUpperCase(part.charAt(0)));
                }
            }
            initials = builder.toString();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(UIConstants.PRIMARY);
            g2.fillOval(0, 0, 71, 71);
            g2.setColor(java.awt.Color.WHITE);
            g2.setFont(UIConstants.FONT_HEADING.deriveFont(24f));
            int x = (72 - g2.getFontMetrics().stringWidth(initials)) / 2;
            int y = (72 + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
            g2.drawString(initials, x, y);
            g2.dispose();
        }
    }
}
